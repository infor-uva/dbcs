import { Routes } from '@angular/router';
import { HotelListComponent } from './hotel-list/hotel-list.component';
import { BookingComponent } from './booking/booking.component'; 
import { BookingListComponent } from './booking-list/booking-list.component';
import { HotelRegisterComponent } from './hotel-register/hotel-register.component';
import { MainPageComponent } from './main-page/main-page.component';

export const routes: Routes = [
  {
    path: '', // Ruta principal
    component: MainPageComponent, // Componente de la página principal
  },
  {
    path: 'booking/search',
    component: BookingListComponent,
  },
  {
    path: 'hotels',
    component: HotelListComponent,
  },
  {
    path: 'hotels/new',
    component: HotelRegisterComponent,
  },
  {
    path: 'hotels/:id',
    component: HotelRegisterComponent,
  },
  {
    path: '**', 
    redirectTo: '', 
    pathMatch: 'full',
  },
  {
    path: 'booking', // Añade la ruta para el componente de reservas
    component: BookingComponent,
  }
]