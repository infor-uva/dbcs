import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { Router } from '@angular/router';
import { SessionService } from '../../../../shared/session.service';
import { UserRol, UserRolesArray } from '../../../../types';

@Component({
  standalone: true,
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  imports: [
    ReactiveFormsModule,
    CommonModule,
    MatCardModule,
    MatInputModule,
    MatFormFieldModule,
    MatSelectModule,
    MatSlideToggleModule,
  ],
})
export class LoginComponent {
  loginForm: FormGroup;
  selectedRol?: UserRol;
  rolOptions: UserRol[] = UserRolesArray;
  errorMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private sessionManager: SessionService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]],
    });
  }

  onSubmit() {
    if (this.loginForm.valid) {
      const { email, password } = this.loginForm.value;
      this.sessionManager.login(email, password).subscribe({
        next: (response) => {
          this.router.navigateByUrl(response.mainPage);
        },
      });
    }
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem('authToken');
  }
}
