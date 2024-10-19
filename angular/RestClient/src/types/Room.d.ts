export interface Room {
	roomNumber: String;
	// type: "single" | "double" | "suite";
	type: "SINGLE" | "DOUBLE" | "SUITE";
	available: boolean;
}
