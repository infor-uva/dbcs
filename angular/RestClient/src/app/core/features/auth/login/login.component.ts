import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  standalone: true,
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  imports: [CommonModule, ReactiveFormsModule]
})


export class LoginComponent {
  loginForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
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

      // Realizar la solicitud al backend
      this.http.post('http://localhost:8101', { email, password }).subscribe({
        next: (response: any) => {
          // Guardar el token en localStorage
          localStorage.setItem('authToken', response.token);

          // Redirigir al dashboard
          this.router.navigate(['/']);
        },
        error: (err) => {
          console.error('Error en el login:', err);
        },
      });
    }
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem('authToken');
  }
}
