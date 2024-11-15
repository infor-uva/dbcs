import { Component } from '@angular/core';
import { RouterModule, Router } from '@angular/router';
import { Hotel } from '../../../../../types';
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
import { NgbAccordionModule } from '@ng-bootstrap/ng-bootstrap';
import { HotelClientService } from '../../../../shared/hotel-client.service';

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
    NgbAccordionModule,
  ],
  templateUrl: './hotel-list.component.html',
  styleUrl: './hotel-list.component.css',
})
export class HotelListComponent {
  hotels!: Hotel[];
  mostrarMensaje!: boolean;
  mensaje!: string;

  constructor(
    private router: Router,
    private hotelClient: HotelClientService
  ) {}

  ngOnInit() {
    this.getHotels();
  }

  getHotels() {
    this.hotelClient.getAllHotels().subscribe({
      next: (resp) => {
        if (!!resp || (resp as never[]).length != 0) this.hotels = [...resp];
      },
      error(err) {
        console.log('Error al traer la lista: ' + err.message);
        throw err;
      },
    });
  }

  deleteHotel(id: number) {
    if (!confirm(`Borrar hotel con id ${id}. Continuar?`)) return;

    this.hotelClient.deleteHotel(id).subscribe({
      next: (resp) => {
        if (resp.status < 400) {
          this.mostrarMensaje = true;
          this.mensaje = resp.body as string;
          this.getHotels();
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

  toggleRoomAvailability(
    hotelId: number,
    roomId: number,
    availability: boolean
  ) {
    this.hotelClient
      .alterRoomAvailability(hotelId, roomId, availability)
      .subscribe({
        next: (resp) => {
          if (resp.status < 400) {
            this.mostrarMensaje = true;
            this.mensaje = resp.body as string;
            this.getHotels();
          } else {
            this.mostrarMensaje = true;
            this.mensaje = 'Error al cambiar disponibilidad';
            console.error(this.mensaje);
          }
        },
        error: (error) => {
          console.log('Error al cambiar disponibilidad: ' + error.message);
          throw error;
        },
      });
  }

  goToHotelDetails(hotelId: number): void {
    this.router.navigate(['/hotels', hotelId]);
  }
}
