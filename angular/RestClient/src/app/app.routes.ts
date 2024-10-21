import { Routes } from '@angular/router';
import { HotelListComponent } from './hotel-list/hotel-list.component';

export const routes: Routes = [
  {
    path: 'hotels',
    component: HotelListComponent,
  },
  {
    path: '**',
    redirectTo: 'hotels',
    pathMatch: 'full',
  },
];
