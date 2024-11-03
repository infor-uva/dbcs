import { Component } from '@angular/core';
import { ClienteApiRestService } from '../../../../shared/cliente-api-rest.service';
import { Booking } from '../../../../../types';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { MatSelectModule } from '@angular/material/select';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

type state = 'all' | 'active' | 'inactive';

@Component({
  selector: 'app-user-booking-list',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterModule],
  templateUrl: './user-booking-list.component.html',
  styleUrl: './user-booking-list.component.css',
})
export class UserBookingListComponent {
  selectedState: state = 'all';
  bookings: Booking[] = [];
  userId: number = 0;

  constructor(
    private client: ClienteApiRestService,
    private route: ActivatedRoute
  ) {
    this.route.paramMap.subscribe({
      next: (params) => {
        this.userId = Number(params.get('id'));
        this.updateBookings();
      },
    });
  }

  ngOnInit() {
    this.updateBookings();
  }

  updateBookings() {
    this.client.getUserBookings(this.userId).subscribe({
      next: (bookings) => {
        this.bookings = bookings;
        console.log({ bookings });
      },
      error: (error) => {
        console.error(error);
      },
    });
  }

  genBookingState(booking: Booking) {
    return new Date(booking.endDate).getTime() < Date.now()
      ? 'Reserva inactiva'
      : 'Reserva activa';
  }
}
