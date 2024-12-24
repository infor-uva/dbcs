import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpResponse } from '@angular/common/http';
import { UserFormComponent } from './user-form.component';
import { UserClientService } from '../../../core/services/api/users/user-client.service';
import { of } from 'rxjs';

describe('UserFormComponent', () => {
  let component: UserFormComponent;
  let fixture: ComponentFixture<UserFormComponent>;
  let userService: UserClientService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, FormsModule],
      declarations: [UserFormComponent],
      providers: [UserClientService],
    }).compileComponents();

    fixture = TestBed.createComponent(UserFormComponent);
    component = fixture.componentInstance;
    userService = TestBed.inject(UserClientService);

    spyOn(userService, 'getCurrentUser').and.returnValue(
      of({
        id: 1,
        name: 'John Doe',
        email: 'johndoe@example.com',
        rol: 'CONSUMER',
        status: 'WITH_ACTIVE_BOOKINGS',
      })
    );

    spyOn(userService, 'updateUser').and.returnValue(
      of(new HttpResponse({ body: 'User updated successfully' }))
    );
    spyOn(userService, 'updatePassword').and.returnValue(
      of(new HttpResponse({ body: 'Password updated successfully' }))
    );

    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should load user data on initialization', () => {
    expect(component.userForm.value).toEqual({
      name: 'John Doe',
      email: 'johndoe@example.com',
      password: '',
      confirmPassword: '',
    });
  });

  it('should call updateUser when saving valid user data', () => {
    component.userForm.patchValue({
      name: 'Jane Doe',
      email: 'janedoe@example.com',
    });

    component.saveChanges();

    expect(userService.updateUser).toHaveBeenCalledWith({
      name: 'Jane Doe',
      email: 'janedoe@example.com',
    });
  });

  it('should call updatePassword when password is updated and matches confirmPassword', () => {
    component.userForm.patchValue({
      password: 'newpassword123',
      confirmPassword: 'newpassword123',
    });

    component.saveChanges();

    expect(userService.updatePassword).toHaveBeenCalledWith(
      '',
      'newpassword123'
    );
  });

  it('should not call updatePassword if password and confirmPassword do not match', () => {
    component.userForm.patchValue({
      password: 'newpassword123',
      confirmPassword: 'differentpassword',
    });

    component.saveChanges();

    expect(userService.updatePassword).not.toHaveBeenCalled();
  });
});
