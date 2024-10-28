import { Routes } from '@angular/router';
import { HotelListComponent } from './hotel-list/hotel-list.component';
import { BookingComponent } from './booking/booking.component'; // Asegúrate de ajustar la ruta

export const routes: Routes = [
  {
    path: 'hotels',
    component: HotelListComponent,
  },
  {
    path: 'booking', // Añade la ruta para el componente de reservas
    component: BookingComponent,
  },
  {
    path: '**', // Mantiene la redirección para cualquier otra ruta no definida
    redirectTo: 'hotels',
    pathMatch: 'full',
  },
];
