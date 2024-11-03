import { Component } from '@angular/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import {
  MatDatepickerInputEvent,
  MatDatepickerModule,
} from '@angular/material/datepicker';
import { FormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { Hotel, Room, RoomType, roomTypeArray } from '../../../../../types';

import { Router } from '@angular/router';
import { MatCard, MatCardModule } from '@angular/material/card';
import { ClienteApiRestService } from '../../../../shared/cliente-api-rest.service';

type SelectableRoomType = 'All' | RoomType;
const selectableRoomTypeArray: SelectableRoomType[] = ['All', ...roomTypeArray];

@Component({
  selector: 'app-booking-list',
  standalone: true,
  imports: [
    MatFormFieldModule,
    MatDatepickerModule,
    MatCardModule,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    FormsModule,
  ],
  templateUrl: './booking-list.component.html',
  styleUrl: './booking-list.component.css',
})
export class BookingListComponent {
  start?: Date;
  end?: Date;
  hotels!: Hotel[];
  hotelSelected?: Hotel;
  roomTypeSelected?: SelectableRoomType;
  roomTypes = selectableRoomTypeArray;
  rooms: Room[] = [];
  trateRooms: Room[] = [];

  constructor(private router: Router, private client: ClienteApiRestService) {}

  ngOnInit() {
    this.getHotels();
  }

  getHotels() {
    this.client.getAllHotels().subscribe({
      next: (resp) => {
        if (resp != null) this.hotels = [...resp];
      },
      error(err) {
        console.log('Error al traer la lista: ' + err.message);
        throw err;
      },
    });
  }

  updateStart(event: MatDatepickerInputEvent<Date>) {
    this.start = event.value!;
  }

  updateEnd(event: MatDatepickerInputEvent<Date>) {
    this.end = event.value!;
  }

  search() {
    this.client
      .getRoomsAvailableInDateRange(
        this.hotelSelected!.id,
        this.start!,
        this.end!
      )
      .subscribe({
        next: (resp) => {
          this.rooms = resp;
          this.updateRooms();
        },
      });
  }

  updateRooms() {
    this.trateRooms =
      this.roomTypeSelected && this.roomTypeSelected !== 'All'
        ? this.rooms.filter((room) => room.type === this.roomTypeSelected)
        : this.rooms;
  }

  bookingRoom(roomId: number) {
    // TODO plantear si se quiere mantener el query param o el rest param
    this.router.navigate(['/bookings', 'new'], { queryParams: { roomId } });
  }
}
