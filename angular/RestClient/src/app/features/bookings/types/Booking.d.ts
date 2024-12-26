export interface Booking {
  id: number;
  start: Date;
  end: Date;
  userId: number;
  managerId: number;
  hotelId: number;
  roomId: number;
}
