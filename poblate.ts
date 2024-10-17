interface Date {
	day: number;
	month: number;
	year: number;
}

interface Address {
	streetKind: string;
	streetName: string;
	number: number;
	postCode: string;
	otherInfo?: string;
}

interface User {
	name: string;
	email: String;
	// status: "noBookings" | "withActiveBookings" | "withInactiveBookings";
	status: "NO_BOOKINGS" | "WITH_ACTIVE_BOOKINGS" | "WITH_INACTIVE_BOOKINGS";
}

interface Booking {
	startDate: Date;
	endDate: Date;
	user: User;
	room: Room;
}

interface Room {
	roomNumber: String;
	roomType: "single" | "double" | "suite";
	// roomType: "SINGLE" | "DOUBLE" | "SUITE"
	available: boolean;
}

interface Hotel {
	name: string;
	address: Address;
	rooms: Room[];
}

// Insert data
// 1. User
// 2. Room
// 3. Hotel with address
// 4. Booking

const users: User[] = [
	{
		name: "John Doe",
		email: "john.doe@example.com",
		status: "NO_BOOKINGS",
	},
	{
		name: "Pepe",
		email: "pepe@example.com",
		status: "WITH_ACTIVE_BOOKINGS",
	},
];

const rooms: Room[] = [
	{
		roomNumber: "101",
		roomType: "single",
		available: true,
	},
	{
		roomNumber: "102",
		roomType: "double",
		available: false,
	},
	{
		roomNumber: "103",
		roomType: "suite",
		available: true,
	},
];

const hotels: Hotel[] = [
	{
		name: "Hotel 1",
		address: {
			streetName: "Aca al lao",
			streetKind: "Alargada",
			number: 12,
			postCode: "12345",
		},
		rooms: [...rooms.slice(0, -1)],
	},
	{
		name: "Hotel 2",
		address: {
			streetName: "Calle de la plaza",
			streetKind: "Alargada",
			number: 12,
			postCode: "12345",
		},
		rooms: [...rooms.slice(-1)],
	},
];

const bookings: Booking[] = [
	{
		user: users[1],
		room: rooms[0],
		startDate: new Date("2024-03-01"),
		endDate: new Date("2024-03-08"),
	},
	{
		user: users[1],
		room: rooms[1],
		startDate: new Date("2024-03-15"),
		endDate: new Date("2024-03-22"),
	},
];

const data: { name: string; value: User[] | Room[] | Hotel[] | Booking[] }[] = [
	{
		name: "users",
		value: users,
	},
	{
		name: "rooms",
		value: rooms,
	},
	{
		name: "hotels",
		value: hotels,
	},
	{
		name: "bookings",
		value: bookings,
	},
];

const save = async ({ name, value }) => {
	const result = await fetch(`http://localhost:8080/${name}`, {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
		},
		body: JSON.stringify(value),
	});
	const response = await result.json();
	if (response?.status) {
		console.error({
			table: name,
			request: JSON.stringify(value, null, 2),
			response,
			// response: JSON.stringify(response, null, 2),
		});
	}
};
for (const object of data) {
	save(object);
}
