import { Address } from "./Address";
import { Room } from "./Room";

export interface Hotel {
	name: string;
	address: Address;
	rooms: Room[];
}
