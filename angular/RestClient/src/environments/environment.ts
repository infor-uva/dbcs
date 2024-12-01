const monolithUrl = 'localhost:8080';

export const environment = {
  production: false,
  authAPI: 'http://localhost:8101',
  userAPI: `http://${monolithUrl}/users`,
  hotelAPI: `http://${monolithUrl}/hotels`,
  bookingAPI: `http://${monolithUrl}/bookings`,
};
