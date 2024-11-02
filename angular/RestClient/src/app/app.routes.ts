import { Routes } from '@angular/router';
import { MainPageComponent } from './main-page/main-page.component'; // Asegúrate de que la ruta al componente principal sea correcta

export const routes: Routes = [
  {
    path: '', // Ruta principal
    component: MainPageComponent, // Componente de la página principal
  },
  {
    path: '**', // Redirección para rutas no encontradas
    redirectTo: '', // Redirige a la página principal
    pathMatch: 'full',
  },
];
