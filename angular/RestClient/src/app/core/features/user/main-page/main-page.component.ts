import { Component, OnInit } from '@angular/core';
import { User, UserStateFilter } from '../../../../../types';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { UserClientService } from '../../../../shared/user-client.service';
import mockUsers from '../../../../../mocks/users.json'; // Renombrado para claridad

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

  constructor(private userClient: UserClientService) {}

  ngOnInit(): void {
    // Validar que el mock sea del tipo correcto
    const isValidMock = Array.isArray(mockUsers) && mockUsers.every(user => 'id' in user && 'name' in user && 'status' in user);
    this.users = isValidMock ? (mockUsers as User[]) : [];
    this.filteredUsers = [...this.users];

    // Sobrescribir con datos reales si están disponibles
    this.userClient.getAllUsers().subscribe({
      next: (data: User[]) => {
        this.users = data;
        this.filteredUsers = [...data];
      },
      error: (err) => console.error('Error al cargar usuarios:', err),
    });
  }

  filterUsers(): void {
    if (this.selectedStatus === 'All') {
      this.filteredUsers = [...this.users];
    } else {
      this.filteredUsers = this.users.filter(
        (user) => user.status === this.selectedStatus
      );
    }
  }

  getState(user: User): string {
    switch (user.status) {
      case 'NO_BOOKINGS':
        return 'SIN RESERVAS';
      case 'WITH_ACTIVE_BOOKINGS':
        return 'CON RESERVAS ACTIVAS';
      case 'WITH_INACTIVE_BOOKINGS':
        return 'CON RESERVAS INACTIVAS';
      default:
        return 'ESTADO DESCONOCIDO';
    }
  }
}
