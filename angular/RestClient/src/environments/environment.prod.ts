const monolithUrl = 'http://rooms-booking-api:8080';

export const environment = {
  production: true,
  authAPI: 'http://auth-api:8101',
  userAPI: `http://${monolithUrl}/users`,
  hotelAPI: `http://${monolithUrl}/hotels`,
  bookingAPI: `http://${monolithUrl}/bookings`,
};
