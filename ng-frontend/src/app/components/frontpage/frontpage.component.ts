// frontpage.component.ts
import { Component } from '@angular/core';

@Component({
  selector: 'app-frontpage',
  templateUrl: './frontpage.component.html',
  styleUrls: ['./frontpage.component.css']
})
export class FrontpageComponent {
  showLogin = true;
  showSignup = false;

  showLoginForm() {
    this.showLogin = true;
    this.showSignup = false;
  }

  showSignupForm() {
    this.showLogin = false;
    this.showSignup = true;
  }
}
