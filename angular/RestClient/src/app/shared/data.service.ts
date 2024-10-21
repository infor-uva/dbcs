import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class DataService {
  private message = new BehaviorSubject('hotel list');
  currentMessage = this.message.asObservable();

  private showMessage = new BehaviorSubject<boolean>(false);
  showCurrentMessage = this.showMessage.asObservable();

  constructor() {}

  setMessage(message: string) {
    this.message.next(message);
  }

  setShowCurrentMessage(valor: boolean) {
    this.showMessage.next(valor);
  }
}
