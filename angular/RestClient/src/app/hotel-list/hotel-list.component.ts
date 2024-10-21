import { Component } from '@angular/core';
import { RouterModule, Router } from '@angular/router';
import { Hotel, Address } from '../../types';
import {
  MatAccordion,
  MatExpansionPanel,
  MatExpansionPanelDescription,
  MatExpansionPanelHeader,
  MatExpansionPanelTitle,
} from '@angular/material/expansion';
import { MatSlideToggle } from '@angular/material/slide-toggle';
import { MatTable, MatTableModule } from '@angular/material/table';
import { MatButton } from '@angular/material/button';

import hotels from '../../mocks/hotels.json';
import { ClienteApiRestService } from '../shared/cliente-api-rest.service';

@Component({
  selector: 'app-hotel-list',
  standalone: true,
  imports: [
    RouterModule,
    MatAccordion,
    MatSlideToggle,
    MatButton,
    MatTable,
    MatTableModule,
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle,
    MatExpansionPanelDescription,
  ],
  templateUrl: './hotel-list.component.html',
  styleUrl: './hotel-list.component.css',
})
export class HotelListComponent {
  hotels!: Hotel[];
  mostrarMensaje!: boolean;
  mensaje!: string;

  constructor(private router: Router, private client: ClienteApiRestService) {}

  ngOnInit() {
    this.getHotelsResponse();
  }

  getHotelsResponse() {
    this.hotels = hotels as Hotel[];
    return;
    this.client.getAllHotels().subscribe({
      next: (resp) => {
        if (resp.body != null) this.hotels = resp.body;
      },
      error(err) {
        console.log('Error al traer la lista: ' + err.message);
        throw err;
      },
    });
  }

  deleteHotel(id: number) {
    if (!confirm(`Borrar hotel con id ${id}. Continuar?`)) return;
    this.hotels = this.hotels.filter((h) => h.id !== id);
    return;

    this.client.deleteHotel(id).subscribe({
      next: (resp) => {
        if (resp.status < 400) {
          this.mostrarMensaje = true;
          this.mensaje = resp.body as string;
          this.getHotelsResponse();
        } else {
          this.mostrarMensaje = true;
          this.mensaje = 'Error al eliminar registro';
        }
      },
      error: (err) => {
        console.log('Error al borrar: ' + err.message);
        throw err;
      },
    });
  }

  toggleRoomAvailability(hotelId: number, roomId: number) {
    const target = hotels
      .find((hotel) => hotel.id === hotelId)!
      .rooms.find((room) => room.id === roomId);
    if (!target) {
      alert('Error');
      return;
    }
    const availability = !target.available;
    target.available = availability;
    alert(`Change availability from room ${roomId} to ${availability}`);
  }

  goToEdit(hotelId: number): void {
    this.router.navigate(['/hotels', hotelId, 'edit']);
  }
}
