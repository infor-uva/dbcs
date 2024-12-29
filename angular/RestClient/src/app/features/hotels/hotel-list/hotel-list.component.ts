import { Component, NgModule } from '@angular/core';
import { RouterModule, Router, ActivatedRoute, Data } from '@angular/router';
import { Hotel, Room, RoomType } from '@features/hotels';
import { roomTypeArray } from '@features/hotels/types/Room.interface';
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
import {
  NgbAccordionModule,
  NgbDatepickerModule,
} from '@ng-bootstrap/ng-bootstrap';
import { HotelClientService } from '../../../core/services/api/hotels/hotel-client.service';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatSelectModule } from '@angular/material/select';
import { LocalStorageService } from '../../../core/services/storage/local-storage.service';
import { SessionService } from '../../../core/services/session/session.service';
import { getBasePath } from '../../../utils/utils';

type SelectableRoomType = 'All' | RoomType;
const selectableRoomTypeArray: SelectableRoomType[] = ['All', ...roomTypeArray];

@Component({
  selector: 'app-hotel-list',
  standalone: true,
  imports: [
    NgbDatepickerModule,
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
    MatCardModule,
    MatIconModule,
    MatFormFieldModule,
    MatDatepickerModule,
    MatSelectModule,
    ReactiveFormsModule,
  ],
  templateUrl: './hotel-list.component.html',
  styleUrl: './hotel-list.component.css',
})
export class HotelListComponent {
  _hotels!: Hotel[];
  hotels!: Hotel[];
  isEditing = false;
  isManaging = false;
  dateRangeForm: FormGroup;
  hotelSelected?: Hotel = undefined;
  roomTypeSelected: SelectableRoomType = 'All';
  roomTypes = selectableRoomTypeArray;
  rooms: Room[] = [];
  trateRooms: Room[] = [];
  userId = 0;
  start:Date = new Date(new Date().getDate() - 3);
  end:Date = new Date(new Date().getDate() - 3);

  constructor(
    private fb: FormBuilder,
    private hotelClient: HotelClientService,
    private router: Router,
    private route: ActivatedRoute,
    private storage: LocalStorageService,
    private sessionService: SessionService
  ) {
    const url = this.router.url;
    const baseUrl = getBasePath(url).split('/')[1];
    const isHotelManger = url.split('/')[1] === 'me';
    const isAdmin = baseUrl === 'admin';
    this.isManaging = isHotelManger || isAdmin;
    const today = new Date();

    // Inicializa el formulario con las fechas predefinidas
    this.dateRangeForm = this.fb.group({
      dateRange: this.fb.group({
        start: [today], // Fecha de inicio
        end: [today], // Fecha de fin
      }),
    });

    this.sessionService.getSession().subscribe({
      next: (session) => {
        console.log({ session });
        if (session && session.rol !== 'CLIENT') {
          this.isEditing = true;
          this.userId = isHotelManger
            ? session.id
            : Number(this.route.snapshot.paramMap.get('id'));
        }
      },
    });
  }

  ngOnInit(): void {
    this.getHotels();
    this.dateRangeForm.get('dateRange')?.valueChanges.subscribe(() => {
      this.getHotels();
    });
  }

  update() {
    this.hotels = (
      !!this.hotelSelected
        ? [...this._hotels].filter((h) => h.id === this.hotelSelected!.id)
        : [...this._hotels]
    )
      .map((h) => {
        h = { ...h, rooms: [...h.rooms] };
        if (!this.isManaging) {
          h.rooms = h.rooms.filter(
            (r) =>
              r.available &&
              (this.roomTypeSelected === 'All' ||
                (r.type as SelectableRoomType) === this.roomTypeSelected)
          );
        }
        return h;
      })
      .filter((h) => h.rooms.length > 0);
  }

  showRequested(room: Room) {
    return (
      this.roomTypeSelected === 'All' ||
      (room.type as SelectableRoomType) === this.roomTypeSelected
    );
  }

  isAvailable(room: Room) {
    const value =
      !this.isEditing &&
      room.available &&
      (this.roomTypeSelected === 'All' ||
        (room.type as SelectableRoomType) === this.roomTypeSelected);

    return value;
  }

  getHotels() {
    const { start, end } = this.dateRangeForm.value.dateRange;
    console.log({start, end, s:this.start, e:this.end})
    if (end != null && this.end.getDate() === end.getDate()) return

    
    const observable = this.isManaging
    ? this.hotelClient.getAllHotelsByUser(this.userId)
    : this.hotelClient.getAllHotels(start, end);
    
    observable.subscribe({
      next: (resp) => {
        if (!!resp && (resp as never[]).length >= 0) {
          this.start = start
          this.end = end
          this._hotels = resp;
          this.update();
        }
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
      next: () => {
        this.getHotels();
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
          if (resp) {
            this.getHotels();
          }
        },
        error: (error) => {
          console.log('Error al cambiar disponibilidad: ' + error.message);
          throw error;
        },
      });
  }

  getHotelUri(hotelId: number) {
    var base;
    try {
      // TODO revisar
      base = getBasePath(this.router.url) + '/';
    } catch (error) {
      base = '';
    }
    return (this.isManaging ? base : '/') + 'hotels/' + hotelId;
  }

  bookingRoom(roomId: number, hotel: Hotel) {
    const { start, end } = this.dateRangeForm.value.dateRange as {
      start: Date;
      end: Date;
    };
    console.log({ hotel });

    const { managerId } = hotel;
    const hotelId = hotel.id;
    this.storage.save('booking-data', {
      roomId,
      start: start.toString(),
      end: end.toString(),
      hotelId,
      managerId,
    });
    this.router.navigate(['/me', 'bookings', 'new'], {
      queryParams: { roomId, startDate: start.toLocaleDateString() },
    });
  }
}
