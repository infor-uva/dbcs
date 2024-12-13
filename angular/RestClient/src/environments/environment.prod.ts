const gateWayUrl = 'localhost:8000'; // kong

export const environment = {
  production: true,
  authAPI: `http://${gateWayUrl}/auth`,
  userAPI: `http://${gateWayUrl}/users`,
  hotelAPI: `http://${gateWayUrl}/hotels`,
  bookingAPI: `http://${gateWayUrl}/bookings`,
};
