const developmentHost = 'localhost';
export const environment = {
  production: false,
  authAPI: `http://${developmentHost}:8101`,
  userAPI: `http://${developmentHost}:8080/users`,
  hotelAPI: `http://${developmentHost}:8080/hotels`,
  bookingAPI: `http://${developmentHost}:8080/bookings`,
};
