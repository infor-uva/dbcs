const developmentHost = 'localhost';
export const environment = {
  production: false,
  authAPI: `http://${developmentHost}:8101/auth`,
  userAPI: `http://${developmentHost}:8201/users`,
  hotelAPI: `http://${developmentHost}:8301/hotels`,
  bookingAPI: `http://${developmentHost}:8401/bookings`,
};
