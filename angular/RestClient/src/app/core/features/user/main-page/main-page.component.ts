// main-page.component.ts
import { Component, OnInit } from '@angular/core';
import { ClienteApiRestService } from '../../../../shared/cliente-api-rest.service';
import { User, UserStateFilter } from '../../../../../types';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import users from '../../../../../mocks/users.json';
import { RouterModule } from '@angular/router';
@Component({
  standalone: true,
  imports: [FormsModule, CommonModule, RouterModule],
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.css'],
})
export class MainPageComponent implements OnInit {
  users: User[] = [];
  filteredUsers: User[] = [];
  selectedStatus: UserStateFilter = 'All';

  constructor(private ClienteApiRestService: ClienteApiRestService) {}

  ngOnInit(): void {
    this.users = users as unknown as User[];
    this.ClienteApiRestService.getAllUsers().subscribe((data: User[]) => {
      this.users = data;
      this.filteredUsers = data; // Inicialmente, muestra todos los usuarios
    });
  }

  filterUsers(): void {
    if (this.selectedStatus === 'All') {
      this.filteredUsers = this.users;
    } else {
      this.filteredUsers = this.users.filter(
        (user) => user.status === this.selectedStatus
      );
    }
  }

  getState(user: User) {
    switch (user.status) {
      case 'NO_BOOKINGS':
        return 'SIN RESERVAS';
      case 'WITH_ACTIVE_BOOKINGS':
        return 'CON RESERVAS ACTIVAS';
      case 'WITH_INACTIVE_BOOKINGS':
        return 'CON RESERVAS INACTIVAS';
    }
  }
}
