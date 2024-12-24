import { Room } from './Room.interface';
import { User } from './User.interface';

export interface Booking {
  id: number;
  startDate: Date;
  endDate: Date;
  userId: User;
  roomId: Room;
}
