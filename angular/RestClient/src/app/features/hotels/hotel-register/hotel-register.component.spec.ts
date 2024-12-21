import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HotelRegisterComponent } from './hotel-register.component';

describe('HotelRegisterComponent', () => {
  let component: HotelRegisterComponent;
  let fixture: ComponentFixture<HotelRegisterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HotelRegisterComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HotelRegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
