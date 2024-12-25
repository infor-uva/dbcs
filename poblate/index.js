const mockedUsers = require("./mocks/users.json");
const mockedHotels = require("./mocks/hotels.json");
const axios = require("axios");
const { jwtDecode } = require("jwt-decode");
const dev = require("./environments/env");
const prod = require("./environments/env.production");

// Modo
const args = process.argv;
const isProduction = args.includes("--prod");
const DEBUG = args.includes("--debug");

const env = isProduction ? prod : dev;
const { authApi, hotelsApi, bookingsApi } = env;

const debug = (...values) => {
	if (DEBUG) console.log(...values);
};

const loj = (data) => {
	console.log(JSON.stringify(data, null, 2));
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
	afterStart.setDate(ref.getDate() + 14); // Restar 2 semanas

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
			return await axios.post(first, data);
		} catch (error) {
			console.error("ERROR Al REGISTRO");
			return await axios.post(second, data);
		}
	} catch (error) {
		console.error("ERROR Al LOGIN");
		console.error("\nNo se ha podido comunicar con el servicio de auth");
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
	user.id = decoded.id;
	return user;
}

const addUsers = async () => {
	const users = [];
	for await (const user of mockedUsers) {
		users.push(await register(user));
	}

	const admins = users.filter((u) => u.rol === "ADMIN");
	const managers = users.filter((u) => u.rol === "HOTEL_ADMIN");
	const clients = users.filter((u) => u.rol === "CLIENT");

	return { admins, managers, clients };
};

const insertHotel = async ({ manager, hotel }) => {
	const { data } = await axios.post(hotelsApi, {
		...hotel,
		managerId: manager.id,
	});
	return data;
};

async function addHotels(managers) {
	const hotels = [];
	for await (const hotel of mockedHotels) {
		const select = getRandomItem(managers);
		hotels.push(await insertHotel({ hotel, manager: select }));
	}
	return hotels;
}

const insertBookings = async (booking) => {
	const { data } = await axios.post(bookingsApi, booking);
	return data;
};

async function addBookings(clients, hotels) {
	var i = 0;
	const t = hotels.length;
	for await (const hotel of hotels) {
		const roomId = getRandomItem(hotel.rooms.filter((r) => r.available)).id;
		const userId = getRandomItem(clients).id;
		const des = (i - t / 2) * 15;
		const date = new Date();
		date.setDate(date.getDate() + des);
		for await (const dates of genDates(date)) {
			await insertBookings({
				managerId: hotel.managerId,
				userId,
				hotelId: hotel.id,
				roomId,
				...dates,
			});
		}
	}
}

async function init() {
	debug("MODE:", isProduction ? "PRODUCTION" : "DEVELOPMENT");
	debug("ENV:", env.env, "\n");
	const { managers, clients } = await addUsers();
	const hotels = await addHotels(managers, 3);
	await addBookings(clients, hotels);
	console.log("POBLACIÓN COMPLETADA EXITOSAMENTE");
}

init();
