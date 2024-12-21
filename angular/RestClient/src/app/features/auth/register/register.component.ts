import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { Router } from '@angular/router';
import { AuthClientService } from '../../../core/services/api/auth/auth-client.service';
import { SessionService } from '../../../core/services/session/session.service';

// TODO agregar selector de roles

@Component({
  standalone: true,
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
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
export class RegisterComponent {
  registerForm: FormGroup;
  errorMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private authClient: AuthClientService,
    private sessionManager: SessionService,
    private router: Router
  ) {
    if (this.sessionManager.isValid()) {
      const s = this.sessionManager.getSession();
      console.log({ s });
    }
    this.registerForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    });
  }

  onSubmit() {
    if (this.registerForm.valid) {
      const { name, email, password } = this.registerForm.value;

      this.authClient.register(name, email, password).subscribe({
        next: (res: any) => {
          console.log({ res });
          this.sessionManager.login(res, '');
          alert('Usuario registrado con éxito.');
          this.router.navigate(['/']); // Redirigir al login
        },
        error: (err) => {
          if (err.error instanceof ErrorEvent) {
            this.errorMessage = `Error: ${err.error.message}`;
          } else {
            // Si el backend devuelve un objeto de error
            this.errorMessage =
              err.error.message || 'Ocurrió un error al registrar el usuario.';
          }
        },
      });
    }
  }
}
