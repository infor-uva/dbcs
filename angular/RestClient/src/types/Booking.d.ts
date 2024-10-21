import { Room } from './Room';
import { User } from './User';

export interface Booking {
  id: number;
  startDate: Date;
  endDate: Date;
  user: User;
  room: Room;
}
