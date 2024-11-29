import { Address } from './Address';
import { Room } from './Room';

export interface Hotel {
  id: number;
  name: string;
  address: Address;
  rooms: Room[];
}
