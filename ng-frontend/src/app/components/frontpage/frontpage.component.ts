// frontpage.component.ts
import { Component, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { TokenService } from 'src/app/services/token.service';

@Component({
  selector: 'app-frontpage',
  templateUrl: './frontpage.component.html',
  styleUrls: ['./frontpage.component.css']
})
export class FrontpageComponent implements OnDestroy {
  message!: string;
  showLogin = true;
  showSignup = false;
  private unsubscribe$ = new Subject<void>();

  constructor(
    private route: ActivatedRoute,
    private tokenSvc: TokenService // For dev
  ) {
    this.route.queryParams.pipe(takeUntil(this.unsubscribe$))
      .subscribe(params => {
        this.message = params['message'];
    });
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }

  showLoginForm() {
    this.showLogin = true;
    this.showSignup = false;
  }

  showSignupForm() {
    this.showLogin = false;
    this.showSignup = true;
  }

  // For development purposes only
  clearLocalStorage() {
    this.tokenSvc.removeAuth();
  }
}
