export interface User {
  id: number;
  name: string;
  email: String;
  // status: "noBookings" | "withActiveBookings" | "withInactiveBookings";
  status: UserState;
}

export type UserState =
  | 'NO_BOOKINGS'
  | 'WITH_ACTIVE_BOOKINGS'
  | 'WITH_INACTIVE_BOOKINGS';
