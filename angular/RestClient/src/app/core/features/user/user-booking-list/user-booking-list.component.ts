import { Component } from '@angular/core';

import { Booking, User } from '../../../../types';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserClientService } from '../../../../shared/user-client.service';
import { BookingClientService } from '../../../../shared/booking-client.service';
import { SessionService } from '../../../../shared/session.service';
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
    return new Date().setHours(0, 0, 0, 0) <=
      new Date(booking.endDate).getTime()
      ? 'Reserva activa'
      : 'Reserva inactiva';
  }

  deleteBooking(bookingId: number) {
    this.bookingClient.deleteBooking(bookingId).subscribe({
      next: () => {
        this.updateBookings();
        this.updateUserStatus();
      },
      error: (err) => {
        console.error('Error al eliminar una reserva', err);
      },
    });
  }

  updateUserStatus() {
    this.bookingClient.getBookingsByUser(this.user.id).subscribe({
      next: (bookings) => {
        const withActive = bookings.find(
          (booking) => this.genBookingState(booking) === 'Reserva activa'
        );
        const withInactive = bookings.find(
          (booking) => this.genBookingState(booking) === 'Reserva inactiva'
        );
        if (withActive) {
          this.userClient
            .alterUserStatus(this.user.id, 'WITH_ACTIVE_BOOKINGS')
            .subscribe({
              next: (response) => {
                console.log('Cambio de estado en el usuario a activo correcto');
              },
              error: (err) => {
                console.error('Error al cambiar de estado al usuario a activo');
              },
            });
        } else if (withInactive) {
          this.userClient
            .alterUserStatus(this.user.id, 'WITH_INACTIVE_BOOKINGS')
            .subscribe({
              next: (response) => {
                console.log(
                  'Cambio de estado en el usuario a inactivo correcto'
                );
              },
              error: (err) => {
                console.error(
                  'Error al cambiar de estado al usuario a inactivo'
                );
              },
            });
        } else {
          this.userClient
            .alterUserStatus(this.user.id, 'NO_BOOKINGS')
            .subscribe({
              next: (response) => {
                console.log(
                  'Cambio de estado en el usuario a sin reservas correcto'
                );
              },
              error: (err) => {
                console.error(
                  'Error al cambiar de estado al usuario sin reservas'
                );
              },
            });
        }
      },
    });
  }
}
