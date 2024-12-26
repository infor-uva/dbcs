export const roomTypeArray = ['SINGLE', 'DOUBLE', 'SUITE'] as const;
export type RoomType = typeof roomTypeArray;

export interface Room {
  id: number;
  roomNumber: String;
  type: RoomType;
  available: boolean;
}
