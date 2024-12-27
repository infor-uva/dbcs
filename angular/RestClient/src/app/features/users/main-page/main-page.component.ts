import { Component, OnInit, ViewChild } from '@angular/core';
import { Client, User, UserStateFilter } from '@features/users';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { UserClientService } from '../../../core/services/api/users/user-client.service';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatCardModule } from '@angular/material/card';

@Component({
  standalone: true,
  imports: [
    FormsModule,
    CommonModule,
    RouterModule,
    MatTableModule,
    MatCardModule,
    MatPaginatorModule,
  ],
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.css'],
})
export class MainPageComponent implements OnInit {
  users: Client[] = [];
  filteredUsers: Client[] = [];
  selectedStatus: UserStateFilter = 'All';
  displayedColumns: string[] = ['id', 'name', 'email', 'rol'];
  dataSource = new MatTableDataSource<User>();

  constructor(private userClient: UserClientService, private router: Router) {}

  ngOnInit(): void {
    this.users = users;
    this.filteredUsers = [...this.users];

    // Sobrescribir con datos reales si están disponibles
    this.userClient.getAllUsers().subscribe({
      next: (data: Client[]) => {
        this.users = data;
        this.filterUsers();
      },
      error: (err) => console.error('Error al cargar usuarios:', err),
    });
  }

  @ViewChild(MatPaginator) paginator?: MatPaginator;

  filterUsers(): void {
    if (this.selectedStatus === 'All') {
      this.filteredUsers = [...this.users];
    } else {
      this.filteredUsers = this.users.filter(
        (user) => user.status === this.selectedStatus
      );
    }
    this.dataSource = new MatTableDataSource<User>(this.filteredUsers);
    this.dataSource.paginator = this.paginator!;
  }

  getState(user: Client): string {
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

  userDetails(id: number) {
    this.router.navigate(['/admin', 'users', id]);
  }
}
