import { Component } from '@angular/core';

import { Booking } from '@features/bookings';
import { User } from '@features/users';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserClientService } from '../../../core/services/api/users/user-client.service';
import { BookingClientService } from '../../../core/services/api/bookings/booking-client.service';
import { SessionService } from '../../../core/services/session/session.service';
import { Observable } from 'rxjs';

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
  user: User = { id: 0, name: '', email: '', rol: 'CLIENT' };

  constructor(
    private userClient: UserClientService,
    private bookingClient: BookingClientService,
    private sessionService: SessionService,
    private route: ActivatedRoute
  ) {
    this.loadUser();
  }

  resolve(): Observable<any> {
    const id = this.route.snapshot.paramMap.get('id');
    return id
      ? this.userClient.getUser(Number(id))
      : this.sessionService.getSession();
  }

  loadUser() {
    this.resolve().subscribe({
      next: (user) => {
        this.user = user;
        this.updateBookings();
      },
    });
  }

  updateBookings() {
    this.bookingClient.getBookingsByUser(this.user.id).subscribe({
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
    return new Date().setHours(0, 0, 0, 0) <= new Date(booking.end).getTime()
      ? 'Reserva activa'
      : 'Reserva inactiva';
  }

  deleteBooking(bookingId: number) {
    this.bookingClient.deleteBooking(bookingId).subscribe({
      next: () => {
        this.updateBookings();
      },
      error: (err) => {
        console.error('Error al eliminar una reserva', err);
      },
    });
  }
}
