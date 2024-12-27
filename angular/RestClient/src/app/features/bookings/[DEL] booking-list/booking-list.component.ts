import { Component } from '@angular/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import {
  MatDatepickerInputEvent,
  MatDatepickerModule,
} from '@angular/material/datepicker';
import { FormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import {Hotel, Room, RoomType, roomTypeArray} from '@features/hotels'
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { LocalStorageService } from '../../../core/services/storage/local-storage.service';
import { HotelClientService } from '../../../core/services/api/hotels/hotel-client.service';

type SelectableRoomType = 'All' | RoomType;
const selectableRoomTypeArray: SelectableRoomType[] = ['All', ...roomTypeArray];

@Component({
  selector: 'app-booking-list',
  standalone: true,
  imports: [
    MatCardModule,
    MatChipsModule,
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
  searched: boolean = false;
  hotels!: Hotel[];
  start?: Date;
  end?: Date;
  hotelSelected?: Hotel;
  roomTypeSelected?: SelectableRoomType;
  roomTypes = selectableRoomTypeArray;
  rooms: Room[] = [];
  trateRooms: Room[] = [];

  constructor(
    private router: Router,
    private hotelClient: HotelClientService,
    private storage: LocalStorageService
  ) {}

  ngOnInit() {
    this.getHotels();
  }

  getHotels() {
    this.hotelClient.getAllHotels().subscribe({
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
    this.hotelClient
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
    this.searched = true;
    this.trateRooms =
      this.roomTypeSelected && this.roomTypeSelected !== 'All'
        ? this.rooms.filter((room) => room.type === this.roomTypeSelected)
        : this.rooms;
  }

  bookingRoom(roomId: number) {
    this.storage.save('booking-data', {
      roomId,
      startDate: this.start,
      endDate: this.end,
    });
    this.router.navigate(['/bookings', 'new'], { queryParams: { roomId } });
  }
}
