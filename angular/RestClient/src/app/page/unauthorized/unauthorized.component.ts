import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SessionService } from '../../core/services/session/session.service';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-unauthorized',
  standalone: true,
  imports: [RouterModule, MatIconModule, MatButtonModule],
  templateUrl: './unauthorized.component.html',
  styleUrl: './unauthorized.component.css',
})
export class UnauthorizedComponent {
  mainPage: string = '';

  constructor(private sessionService: SessionService) {
    this.sessionService.getSession().subscribe({
      next: (session) => {
        this.mainPage = session
          ? sessionService.getMainPage(session.rol)
          : '/login';
      },
    });
  }
}
