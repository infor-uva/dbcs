const monolith = 'localhost:8080'; // kong

export const environment = {
  production: false,
  authAPI: `http://localhost:8101/auth`,
  userAPI: `http://${monolith}/users`,
  hotelAPI: `http://${monolith}/hotels`,
  bookingAPI: `http://${monolith}/bookings`,
};
