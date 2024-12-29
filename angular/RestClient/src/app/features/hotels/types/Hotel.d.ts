import { Address } from './Address';
import { Room } from './Room.interface';

export interface Hotel {
  id: number;
  name: string;
  address: Address;
  rooms: Room[];
  managerId: number;
}
