import { Room } from "./Room";
import { User } from "./User";

export interface Booking {
	startDate: Date;
	endDate: Date;
	user: User;
	room: Room;
}
