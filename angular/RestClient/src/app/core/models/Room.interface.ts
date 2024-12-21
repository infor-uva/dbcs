export type RoomType = 'SINGLE' | 'DOUBLE' | 'SUITE';

export const roomTypeArray: RoomType[] = ['SINGLE', 'DOUBLE', 'SUITE'];

export interface Room {
  id: number;
  roomNumber: String;
  type: RoomType;
  available: boolean;
}
