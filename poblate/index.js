const mockedUsers = require("./mocks/users.json");
const mockedHotels = require("./mocks/hotels.json");
const axios = require("axios");
const { jwtDecode } = require("jwt-decode");
const dev = require("./environments/env");
const prod = require("./environments/env.production");

// Environments consts
const args = process.argv;
const isProduction = args.includes("--prod");
const DEBUG = args.includes("--debug") || args.includes("-d");
const FORCE = args.includes("--force") || args.includes("-f");
const ERROR = args.includes("--error") || args.includes("-e");

const env = (isProduction ? prod : dev).env;
const { authApi, hotelsApi, bookingsApi } = env;

const debug = (...values) => {
	if (DEBUG) console.log(...values);
};

const loj = (data) => {
	console.log(JSON.stringify(data, null, 2));
};

const showError = (error) => {
	debug(
		"ERROR:",
		ERROR
			? error
			: error.response?.data ?? error.response?.error ?? error.cause ?? error,
		"\n"
	);
};
// Función para calcular fechas pareadas
function genDates(ref = new Date()) {
	// before
	const beforeStart = new Date(ref);
	beforeStart.setDate(ref.getDate() - 14); // Restar 2 semanas

	const beforeEnd = new Date(beforeStart);
	beforeEnd.setDate(beforeStart.getDate() + 3);

	// After
	const afterStart = new Date(ref);
	afterStart.setDate(ref.getDate() + 14); // Sumar 2 semanas

	const afterEnd = new Date(afterStart);
	afterEnd.setDate(afterStart.getDate() + 2);

	return [
		{
			start: beforeStart,
			end: beforeEnd,
		},
		{
			start: afterStart,
			end: afterEnd,
		},
	];
}

function getRandomItem(a = []) {
	return a[Math.floor(Math.random() * a.length)];
}

const savePost = async (data, first, second = "") => {
	try {
		try {
			debug("Trying to register user", data);
			return await axios.post(first, data);
		} catch (error) {
			debug("Trying to log user", data);
			showError(error);
			debug("ERROR Al REGISTRO, SE PROCEDE A INTENTAR ACCEDER");
			const response = await axios.post(second, data);
			if (!FORCE) {
				console.log("Parece que ya hay datos en el sistema");
				process.exit(0);
			}
			return response;
		}
	} catch (error) {
		console.error("ERROR Al LOGIN");
		console.error("\nNo se ha podido comunicar con el servicio de auth");
		showError(error);
		process.exit(-1);
	}
};

async function register(user) {
	const { data } = await savePost(
		user,
		`${authApi}/register`,
		`${authApi}/login`
	);
	const decoded = jwtDecode(data.token);
	debug(
		`User identified successful with id=${decoded.id} and token={${data.token}}`
	);
	user.id = decoded.id;
	user.token = data.token;
	return user;
}

const addUsers = async () => {
	const users = [];
	for await (const user of mockedUsers) {
		users.push(await register(user));
	}

	const admins = users.filter((u) => u.rol === "ADMIN");
	const managers = users.filter((u) => u.rol === "MANAGER");
	const clients = users.filter((u) => u.rol === "CLIENT");

	return { admins, managers, clients };
};

const insertHotel = async ({ manager, hotel }) => {
	try {
		const body = {
			...hotel,
			managerId: manager.id,
		};
		debug("Trying to add booking", body);
		const { data } = await axios.post(hotelsApi, body, {
			headers: {
				Authorization: `Bearer ${manager.token}`,
			},
		});
		debug("Hotel added successful");
		return data;
	} catch (error) {
		console.error("ERROR Al INSERTAR HOTEL");
		showError(error);
		process.exit(-1);
	}
};

async function addHotels(managers) {
	const hotels = [];
	for await (const hotel of mockedHotels) {
		const select = getRandomItem(managers);

		hotels.push(await insertHotel({ hotel, manager: select }));
	}
	return hotels;
}

const insertBookings = async (booking, token) => {
	try {
		debug("Trying to add booking", booking);
		const { data } = await axios.post(bookingsApi, booking, {
			headers: {
				Authorization: `Bearer ${token}`,
			},
		});
		debug("Booking added successful");
		return data;
	} catch (error) {
		console.error("ERROR Al INSERTAR RESERVA");
		showError(error);
		process.exit(-1);
	}
};

async function addBookings(clients, hotels) {
	var i = 0;
	const t = hotels.length;
	for await (const hotel of hotels) {
		const roomId = getRandomItem(hotel.rooms.filter((r) => r.available)).id;
		const client = getRandomItem(clients);
		const des = (i - t / 2) * 15;
		const date = new Date();
		date.setDate(date.getDate() + des);
		for await (const dates of genDates(date)) {
			await insertBookings(
				{
					managerId: hotel.managerId,
					userId: client.id,
					hotelId: hotel.id,
					roomId,
					...dates,
				},
				client.token
			);
		}
	}
}

function sleep(ms) {
	return new Promise((resolve) => setTimeout(resolve, ms));
}

async function init() {
	debug("MODE:", isProduction ? "PRODUCTION" : "DEVELOPMENT");
	debug("ENV:", env, "\n");
	const { managers, clients } = await addUsers();
	const time = 2;
	debug("USUARIOS REGISTRADOS O IDENTIFICADOS\n\n");
	if (DEBUG) {
		await sleep(time * 1000);
	}
	const hotels = await addHotels(managers, 3);
	debug("HOTELES REGISTRADOS\n\n");
	if (DEBUG) {
		await sleep(time * 1000);
	}
	await addBookings(clients, hotels);
	console.log("POBLACIÓN COMPLETADA EXITOSAMENTE");
}

init();
