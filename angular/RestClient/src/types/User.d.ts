export interface User {
	name: string;
	email: String;
	// status: "noBookings" | "withActiveBookings" | "withInactiveBookings";
	status: "NO_BOOKINGS" | "WITH_ACTIVE_BOOKINGS" | "WITH_INACTIVE_BOOKINGS";
}
