import { Client } from '@features/users/types/User.interface'; // Asegúrate de importar el tipo Client desde donde lo hayas definido

export const users: Client[] = [
  {
    id: 1,  
    name: "John Doe",
    email: "john.doe@example.com",
    status: "NO_BOOKINGS",
    bookings: [], 
    rol: "CLIENT"
  },
  {
    id: 2,  // Otro ID único
    name: "Pepe",
    email: "pepe@example.com",
    status: "WITH_ACTIVE_BOOKINGS",
    bookings: [], 
    rol: "CLIENT" 
  }
];
