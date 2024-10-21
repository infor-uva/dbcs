export interface Room {
  id: number;
  roomNumber: String;
  // type: "single" | "double" | "suite";
  type: 'SINGLE' | 'DOUBLE' | 'SUITE';
  available: boolean;
}
