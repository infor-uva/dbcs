import { Room } from './Room';
import { User } from './User';

export interface Booking {
  id: number;
  startDate: Date;
  endDate: Date;
  userId: User;
  roomId: Room;
}
