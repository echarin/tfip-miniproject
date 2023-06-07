// login.component.ts
import { animate, state, style, transition, trigger } from '@angular/animations';
import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  animations: [
    trigger('fadeInOut', [
      state('void', style({
        opacity: 0
      })),
      transition('void <=> *', animate(1000)),
    ]),
  ]
})
export class LoginComponent {
  @Output() showSignupEvent = new EventEmitter<void>();
  
  switchToSignup() {
    this.showSignupEvent.emit();
  }
}
