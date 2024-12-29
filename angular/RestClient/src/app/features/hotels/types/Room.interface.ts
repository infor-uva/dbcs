export const roomTypeArray = ['SINGLE', 'DOUBLE', 'SUITE'] as const;
export type RoomType = 'SINGLE' | 'DOUBLE' | 'SUITE';

export interface Room {
  id: number;
  roomNumber: String;
  type: RoomType;
  available: boolean;
}
