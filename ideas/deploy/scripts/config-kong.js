const fs = require('fs');
const path = require('path');

const KONG_ADMIN_URL = 'http://localhost:8001';
const CONFIG_FILE = path.join(__dirname, '../kong_config_3.json');

const config = JSON.parse(fs.readFileSync(CONFIG_FILE, 'utf8'));

// Map old IDs to new IDs/Names to resolve references
const serviceMap = new Map(); // oldId -> name
const routeMap = new Map();   // oldId -> name

async function request(method, endpoint, data = null) {
    try {
        const url = `${KONG_ADMIN_URL}${endpoint}`;
        const options = {
            method,
            headers: { 'Content-Type': 'application/json' },
        };
        if (data) options.body = JSON.stringify(data);
        
        const res = await fetch(url, options);
        if (!res.ok) {
            // If 409 Conflict, it means it already exists, which is fine for idempotency
            if (res.status === 409) {
                return null; 
            }
            const text = await res.text();
            throw new Error(`Request failed: ${method} ${url} ${res.status} - ${text}`);
        }
        return res.json();
    } catch (e) {
        console.error(e.message);
        process.exit(1);
    }
}

async function configure() {
    console.log('Starting Kong Configuration...');

    // 1. Services
    if (config.data.services) {
        for (const service of config.data.services) {
            console.log(`Processing Service: ${service.name}`);
            serviceMap.set(service.id, service.name);
            
            // Clean up payload
            const payload = { ...service };
            delete payload.id;
            delete payload.created_at;
            delete payload.updated_at;
            delete payload.extras;
            delete payload.tags; // optional, but sometimes cause issues if empty

            // Check if exists or update
            await request('PUT', `/services/${service.name}`, payload);
        }
    }

    // 2. Routes
    if (config.data.routes) {
        for (const route of config.data.routes) {
            console.log(`Processing Route: ${route.name}`);
            routeMap.set(route.id, route.name);

            const payload = { ...route };
            delete payload.id;
            delete payload.created_at;
            delete payload.updated_at;
            delete payload.service; // we set service via url or name reference

            // Resolve Service
            let serviceName = null;
            if (route.service && route.service.id) {
                serviceName = serviceMap.get(route.service.id);
            }

            if (!serviceName) {
                console.warn(`Skipping route ${route.name} because service not found`);
                continue;
            }

            // Create/Update Route
            await request('PUT', `/services/${serviceName}/routes/${route.name}`, payload);
        }
    }

    // 3. Consumers
    if (config.data.consumers) {
        for (const consumer of config.data.consumers) {
            console.log(`Processing Consumer: ${consumer.username}`);
            
            const payload = { ...consumer };
            const credentials = payload.credentials;
            delete payload.credentials;
            delete payload.id;
            delete payload.created_at;
            delete payload.updated_at;
            delete payload.custom_id; // prevent null issues if not set

            await request('PUT', `/consumers/${consumer.username}`, payload);

            // Credentials (JWT)
            if (credentials && credentials.jwts) {
                for (const jwt of credentials.jwts) {
                    const jwtPayload = { ...jwt };
                    delete jwtPayload.id;
                    delete jwtPayload.created_at;
                    delete jwtPayload.consumer;

                    // For JWT, we can't easily PUT by name/key in one go as ID is internal, 
                    // but we can try POST and ignore 409 (Conflict).
                    // Or check if exists.
                    // Let's use POST and catch 409.
                    console.log(`  Adding JWT credential for ${consumer.username}`);
                    await request('POST', `/consumers/${consumer.username}/jwt`, jwtPayload);
                }
            }
        }
    }

    // 4. Plugins
    if (config.data.plugins) {
        for (const plugin of config.data.plugins) {
            console.log(`Processing Plugin: ${plugin.name}`);
            
            const payload = { ...plugin };
            delete payload.id;
            delete payload.created_at;
            delete payload.updated_at;
            
            // Resolve Scope
            if (payload.service && payload.service.id) {
                const sName = serviceMap.get(payload.service.id);
                if (sName) {
                   payload.service = { name: sName }; // Kong allows referencing by name? Should verify. 
                   // Actually easier to just not map if we can't.
                   // But typically plugin payload expects `service_id` or `route_id` if we POST to global /plugins.
                   // However, standard Admin API allows `name` in some versions, but safer to use PUT /services/{name}/plugins? No that's not standard.
                   // Let's rely on looking up the NEW Service ID if needed.
                   // Wait, we PUT services by name, so their ID might have changed? 
                   // Yes. So we need the new IDs.
                   // BUT, if we use `PUT /services/{name}` it returns the object with ID.
                }
            }
            // For now, let's skip plugins scope resolution optimization and just assume global or try our best.
            // Actually, `kong_config_3.json` has plugins attached to routes mostly.
            // "route":{"id":"..."}
            
            // Let's re-fetch the route ID? Or just use the Route Name if Kong supports it?
            // Kong 3.x supports `route: { name: "..." }` ? 
            // Documentation says `route: { id: "..." }`. I don't think `name` works for reference in payload.
            
            // So we need to fetch the new IDs. 
            // Revision: simpler approach for plugins. 
            // 1. Fetch all routes and services to get name->id map.
            // 2. Replace IDs in plugin payload.
        }
    }
}

// Helper to refresh ID maps
async function refreshIdMaps(maps) {
    // Services
    const sRes = await request('GET', '/services');
    const sMap = new Map();
    if(sRes && sRes.data) sRes.data.forEach(s => sMap.set(s.name, s.id));

    // Routes
    const rRes = await request('GET', '/routes');
    const rMap = new Map();
    if(rRes && rRes.data) rRes.data.forEach(r => rMap.set(r.name, r.id));
    
    return { sMap, rMap };
}

async function configurePlugins() {
    // Need current IDs
    const { sMap, rMap } = await refreshIdMaps();

    if (config.data.plugins) {
        for (const plugin of config.data.plugins) {
            console.log(`Processing Plugin: ${plugin.name}`);
            const payload = { ...plugin };
            delete payload.id;
            delete payload.created_at;
            delete payload.updated_at;

            // Resolve Service
            if (payload.service && payload.service.id) {
                const oldId = payload.service.id;
                const name = serviceMap.get(oldId);
                const newId = sMap.get(name);
                if (newId) payload.service = { id: newId };
                else {
                    console.warn(`Skipping plugin ${plugin.name} - Service ${name} not found`);
                    continue;
                }
            }

            // Resolve Route
            if (payload.route && payload.route.id) {
                const oldId = payload.route.id;
                const name = routeMap.get(oldId);
                const newId = rMap.get(name);
                if (newId) payload.route = { id: newId };
                else {
                    console.warn(`Skipping plugin ${plugin.name} - Route ${name} not found`);
                    continue;
                }
            }
            
            // For plugins, we can't easily idempotent PUT because they don't have natural keys.
            // we should check if a plugin with same name/config exists on the scope.
            // Too complex for now?
            // Hack: Just POST and if it's duplicated... well.
            // Better: Iterate existing plugins and check if match?
            // Let's just try POST. If identical one exists, Kong allows or duplicates? 
            // Kong usually allows duplicates.
            // Let's NOT duplicate. 
            
            // Check existing plugins on the route/service
            let existingPlugins = [];
            if (payload.route) {
                 const res = await request('GET', `/routes/${payload.route.id}/plugins`);
                 if(res) existingPlugins = res.data;
            } else if (payload.service) {
                 const res = await request('GET', `/services/${payload.service.id}/plugins`);
                 if(res) existingPlugins = res.data;
            }
            
            const alreadyExists = existingPlugins.some(p => p.name === payload.name);
            if(alreadyExists) {
                console.log(`  Plugin ${plugin.name} already exists on scope. Skipping.`);
                continue;
            }

            await request('POST', '/plugins', payload);
        }
    }
}

(async () => {
    try {
        await configure();
        await configurePlugins();
        console.log('Configuration Complete.');
    } catch(e) {
        console.error(e);
        process.exit(1);
    }
})();
