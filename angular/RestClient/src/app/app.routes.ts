import { Routes } from '@angular/router';
import { HotelListComponent } from './hotel-list/hotel-list.component';
import { HotelRegisterComponent } from './hotel-register/hotel-register.component';
import { MainPageComponent } from './main-page/main-page.component';

export const routes: Routes = [
  {
    path: '', // Ruta principal
    component: MainPageComponent, // Componente de la página principal
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
];
