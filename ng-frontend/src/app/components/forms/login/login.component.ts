// login.component.ts
import { animate, state, style, transition, trigger } from '@angular/animations';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { AuthRequest, AuthResponse } from 'src/app/models/auth-dtos';
import { AuthService } from 'src/app/services/auth.service';
import { ErrorService } from 'src/app/services/error.service';

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

  loginForm!: FormGroup;

  isLoading: boolean = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  private unsubscribe$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private authSvc: AuthService,
    private errSvc: ErrorService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      email: this.fb.control({value: null, disabled: this.isLoading}, [ Validators.required ]),
      password: this.fb.control({value: null, disabled: this.isLoading}, [ Validators.required ]),
    });
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      this.errorMessage = null;
      this.successMessage = null;
      this.toggleForm(false);

      const login: AuthRequest = this.loginForm.value;
      this.subscribeToUserLoggedIn();

      this.authSvc.login(login).pipe(takeUntil(this.unsubscribe$)).subscribe({
        next: (data: AuthResponse) => {
          this.handleSubmission(data, null);
        },
        error: (err: HttpErrorResponse) => this.handleSubmission(null, err),
      });
    } else {
      this.errorMessage = 'please fill out all required fields.';
      this.toggleForm(true);
      return;
    }
  }

  private subscribeToUserLoggedIn() {
    this.authSvc.userLoggedIn.pipe(takeUntil(this.unsubscribe$)).subscribe({
      next: (userId: string) => {
        this.successMessage = 'successfully authenticated! logging you in...';
        setTimeout(() => {
          this.router.navigate([`/${userId}`]);
        }, 2000);
      },
      error: (err: HttpErrorResponse) => { 
        this.errorMessage = this.errSvc.handleError(err);
        this.toggleForm(true);
      }
    });
  }

  private handleSubmission(data: AuthResponse | null, error: HttpErrorResponse | null): void {
    console.log(data || error);
    this.toggleForm(true);

    if (data) {
      this.loginForm.reset();
      this.successMessage = 'authenticating...';
    } else if (error) {
      this.errorMessage = this.errSvc.handleError(error);
    }
  }

  private toggleFormControlsState(isEnabled: boolean): void {
    const method = isEnabled ? 'enable' : 'disable';
    this.loginForm.get('email')?.[method]();
    this.loginForm.get('password')?.[method]();
  }

  private toggleForm(isEnabled: boolean): void {
    const method = isEnabled? true : false;
    this.isLoading = !method;
    this.toggleFormControlsState(method);
  }
  
  switchToSignup() {
    this.showSignupEvent.emit();
  }
}

