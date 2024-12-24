const mockedUsers = require("./mocks/users.json");
const mockedHotels = require("./mocks/hotels.json");
const mockedBookings = require("./mocks/bookings.json");
const axios = require("axios");
const { jwtDecode } = require("jwt-decode");

// Modo
const args = process.argv;
const isProduction = args.includes("--prod");

const authUrl = "http://localhost:8101/auth";
const hotelsUrl = "http://localhost:8301/hotels";
const bookingUrl = "http://localhost:8401/bookings";

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
			console.error("ERROR 1");
			return await axios.post(second, data);
		}
	} catch (error) {
		console.error("ERROR 2");
		process.exit(-1);
	}
};

async function register(user) {
	const { data } = await savePost(
		user,
		`${authUrl}/register`,
		`${authUrl}/login`
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
	const { data } = await axios.post(hotelsUrl, {
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
	console.log({ booking });
	const { data } = await axios.post(bookingUrl, booking);
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
	const { managers, clients } = await addUsers();
	const hotels = await addHotels(managers, 3);
	await addBookings(clients, hotels);
}

console.log("MODE:", isProduction ? "PRODUCTION" : "DEVELOPMENT");
init();
