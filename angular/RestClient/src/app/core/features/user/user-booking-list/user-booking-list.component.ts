import { Component } from '@angular/core';
import { ClienteApiRestService } from '../../../../shared/cliente-api-rest.service';
import { Booking, User } from '../../../../../types';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { MatSelectModule } from '@angular/material/select';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BookingService } from '../../../../shared/booking.service';

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
  search = false;
  bookings: Booking[] = [];
  userId: number = 0;
  user?: User;

  constructor(
    private client: ClienteApiRestService,
    private bookingClient: BookingService,
    private route: ActivatedRoute
  ) {
    this.route.paramMap.subscribe({
      next: (params) => {
        this.userId = Number(params.get('id'));
        this.updateBookings();
      },
    });
    this.client
      .getUser(this.userId)
      .subscribe({ next: (user) => (this.user = user) });
  }

  ngOnInit() {
    this.updateBookings();
  }

  updateBookings() {
    this.client.getUserBookings(this.userId).subscribe({
      next: (bookings) => {
        this.search = true;
        switch (this.selectedState) {
          case 'all':
            this.bookings = bookings;
            break;
          case 'active':
            this.bookings = bookings.filter(
              (booking) => this.genBookingState(booking) === 'Reserva activa'
            );
            break;
          case 'inactive':
            this.bookings = bookings.filter(
              (booking) => this.genBookingState(booking) === 'Reserva inactiva'
            );
            break;
        }
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

  deleteBooking(bookingId: number) {
    this.bookingClient.deleteBooking(bookingId).subscribe({
      next: () => {
        console.log('Actualizadas');
        this.updateBookings();
      },
      error: (err) => {
        console.error('Error al eliminar una reserva', err);
      },
    });
  }
}
