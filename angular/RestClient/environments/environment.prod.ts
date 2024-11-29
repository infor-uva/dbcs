const monolithUrl = 'http://room-booking:8080';

export const environment = {
  production: true,
  authAPI: 'http://auth-api:8101',
  userAPI: `http://${monolithUrl}`,
  hotelAPI: `http://${monolithUrl}`,
  bookingAPI: `http://${monolithUrl}`,
};
