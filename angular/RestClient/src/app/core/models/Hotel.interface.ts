import { Address } from './Address.interface';
import { Room } from './Room.interface';

export interface Hotel {
  id: number;
  name: string;
  address: Address;
  rooms: Room[];
}
