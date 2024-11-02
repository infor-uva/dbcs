import { Routes } from '@angular/router';
import { HotelListComponent } from './hotel-list/hotel-list.component';
import { HotelRegisterComponent } from './hotel-register/hotel-register.component';

export const routes: Routes = [
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
    redirectTo: 'hotels',
    pathMatch: 'full',
  },
];
