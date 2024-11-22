import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  standalone: true,
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
  imports: [ReactiveFormsModule, CommonModule]
})

export class RegisterComponent {
  registerForm: FormGroup;
  errorMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    });
  }

  onSubmit() {
    if (this.registerForm.valid) {
      const { name, email, password } = this.registerForm.value;

      // Enviar solicitud al backend
      this.http.post('http://localhost:8080/users', { name, email, password }).subscribe({
        next: () => {
          alert('Usuario registrado con éxito.');
          this.router.navigate(['/auth/login']); // Redirigir al login
        },
        error: (err) => {
          if (err.error instanceof ErrorEvent) {
            this.errorMessage = `Error: ${err.error.message}`;
          } else {
            // Si el backend devuelve un objeto de error
            this.errorMessage = err.error.message || 'Ocurrió un error al registrar el usuario.';
          }
        }
        
      });
    }
  }
}
