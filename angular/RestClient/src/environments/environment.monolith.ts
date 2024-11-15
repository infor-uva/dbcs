// Simple -> Un servicio fachada / monolito
const hostname = 'http://localhost:8080';

export const environment = {
  production: false,
  authAPI: `${hostname}/auth`,
  userAPI: `${hostname}/users`,
  hotelAPI: `${hostname}/hotels`,
  bookingAPI: `${hostname}/bookings`,
};
