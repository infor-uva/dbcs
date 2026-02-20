This are my target class at least from now

package com.uva.api.hotels.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import com.uva.api.hotels.models.external.jwt.JwtData;
import com.uva.api.hotels.models.external.jwt.Service;
import com.uva.api.hotels.models.external.users.UserRol;
import com.uva.api.hotels.services.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.Filter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter implements Filter {

    @Autowired
    private TokenService service;

    private String getTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }

    private JwtData validateAndDecodeToken(String token) {
        try {
            return service.decodeToken(token);
        } catch (Exception ex) {
            System.err.println(
                    "[" + LocalDateTime.now().toString() + "] Error de verificación del token\n");
            ex.printStackTrace(System.err);
            return null;
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String token = getTokenFromRequest(httpRequest);

        System.out.println("[" + LocalDateTime.now().toString() + "] TOKEN: " + token + "\n");

        if (token != null) {
            JwtData jwt = validateAndDecodeToken(token);
            if (jwt != null) {
                String email = jwt.getEmail();
                UserRol role = jwt.getRol();
                Service service = jwt.getService();
                String audience = jwt.getAudience();

                System.out.println("[" + LocalDateTime.now().toString() + "] email=" + email + " role=" + role
                        + " service=" + service + " audience=" + audience + "\n");

                if (audience != null) {
                    // Definimos la autoridad
                    String authorityValue = null;
                    if (audience.equals("INTERNAL") && service != null) {
                        authorityValue = service.toString();
                    } else if (audience.equals("EXTERNAL") && role != null) {
                        authorityValue = String.format("ROLE_%s", role);
                    }

                    if (authorityValue != null &&
                            SecurityContextHolder.getContext().getAuthentication() == null) {

                        // Crear la autoridad con la autoridad oportuna
                        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(authorityValue);

                        // Crear autenticación
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                email,
                                null, Collections.singletonList(authority));
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));

                        // Establecer autenticación en el contexto de seguridad
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        }

        // Continuar con el resto de filtros
        chain.doFilter(request, response);
    }

}

package com.uva.api.hotels.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.uva.api.hotels.apis.TokenAPI;
import com.uva.api.hotels.models.external.jwt.JwtData;
import com.uva.api.hotels.models.external.users.UserRol;

@Service
public class TokenService {

@Autowired
private TokenAPI api;

private JwtData ownToken;
private Map<String, JwtData> cache = new HashMap<>();

private boolean expireSoon(JwtData decoded) {
return (decoded.getExpiresAt().getTime() - System.currentTimeMillis()) / 1000 <= 10;
}

public String getServiceToken() {
if (ownToken == null || expireSoon(ownToken)) {
System.out.println("\nGenerando token");
long s = System.currentTimeMillis();
ownToken = api.getServiceToken();
long t = System.currentTimeMillis() - s;
System.out.println("Token Generando en " + t + " ms\n");
}
return ownToken.getToken();
}

public JwtData decodeToken(String token) {
JwtData decoded;
if (cache.containsKey(token)) {
decoded = cache.get(token);
if (!expireSoon(decoded))
return cache.get(token);
}
System.out.println("\nActualizando token");
long s = System.currentTimeMillis();
decoded = api.decodeToken(token);
long t = System.currentTimeMillis() - s;
System.out.println("Actualizando token en " + t + " ms\n");
cache.put(token, decoded);
return decoded;
}

/**

* Valida que la entidad representada con el token tenga permisos de
* administrador, sea un servicio o sea el dueño del recurso (idExpected)
*
* @param token
* @param idExpected
  */
  public void assertPermission(String token, int idExpected) {
  JwtData decoded = decodeToken(token);
  boolean isOwner = decoded.getId() == idExpected;
  if (!isOwner)
  assertPermission(token);
  }

/**

* Valida que la entidad representada con el token tenga permisos de
* administrador o sea un servicio
*
* @param token
  */
  public void assertPermission(String token) {
  JwtData decoded = decodeToken(token);
  boolean isAdmin = decoded.isAdmin();
  boolean isService = decoded.getService() != null && decoded.getAudience().equals("INTERNAL");
  if (!isAdmin && !isService)
  throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
  }

public boolean hasAnyRole(String token, UserRol... roles) {
JwtData decoded = decodeToken(token);
for (UserRol role : roles)
if (decoded.getRol() == role)
return true;
return false;
}

}

package com.uva.api.hotels.apis;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.uva.api.hotels.models.external.jwt.JwtData;

@Component
public class TokenAPI {

private final RestTemplate restTemplate;

public TokenAPI(@Qualifier("simpleRestTemplate") RestTemplate restTemplate) {
this.restTemplate = restTemplate;
}

@Value("${spring.application.name}")
private String service;

@Value("${services.internal.token.url}")
private String TOKEN_API_URL;

public JwtData getServiceToken() {
String url = TOKEN_API_URL + "/service";
Map<String, String> body = new HashMap<>();
body.put("service", service);
String token = restTemplate.postForObject(url, body, JsonNode.class)
.get("token").asText();
return decodeToken(token);
}

public JwtData decodeToken(String token) {
String url = TOKEN_API_URL + "/info";
Map<String, String> body = new HashMap<>();
body.put("token", token);
JwtData response = restTemplate.postForObject(url, body, JwtData.class);
response.setToken(token);
return response;
}

}


