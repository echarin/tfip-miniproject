// signup.component.ts
import { animate, state, style, transition, trigger } from '@angular/animations';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, OnDestroy, OnInit, Output } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { AuthRequest, AuthResponse } from 'src/app/models/auth-dtos';
import { AuthService } from 'src/app/services/auth.service';
import { ErrorService } from 'src/app/services/error.service';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css'],
  animations: [
    trigger('fadeInOut', [
      state('void', style({
        opacity: 0
      })),
      transition('void <=> *', animate(1000)),
    ]),
  ]
})
export class SignupComponent implements OnInit, OnDestroy {
  @Output() showLoginEvent = new EventEmitter<void>();

  signupForm!: FormGroup;

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
    this.signupForm = this.fb.group({
      email: this.fb.control({value: null, disabled: this.isLoading}, [ Validators.required ]),
      password: this.fb.control({value: null, disabled: this.isLoading}, [ Validators.required ]),
      confirmPassword: this.fb.control({value: null, disabled: this.isLoading}, [ Validators.required ]),
    }, { validators: [this.passwordMatchValidator] });
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }

  private passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
    const password = control.get('password')?.value;
    const confirmPassword = control.get('confirmPassword')?.value;
    return password === confirmPassword ? null : { 'mismatch': true };
  }

  onSubmit(): void {
    if (this.signupForm.valid) {
      this.errorMessage = null;
      this.successMessage = null;
      this.toggleForm(false);

      const signup: AuthRequest = {
        email: this.signupForm.get('email')?.value,
        password: this.signupForm.get('password')?.value,
      };
      console.log(signup);
      this.subscribeToUserLoggedIn();

      this.authSvc.signup(signup).pipe(takeUntil(this.unsubscribe$)).subscribe({
        next: (data: AuthResponse) => {
          this.handleSubmission(data, null);
        },
        error: (err: HttpErrorResponse) => this.handleSubmission(null, err),
      });
    } else if (this.signupForm.hasError('mismatch')) {
      this.errorMessage = 'passwords do not match';
      this.toggleForm(true);
      return;
    } else {
      this.errorMessage = 'please fill out all required fields.';
      this.toggleForm(true);
      return;
    }
  }

  private subscribeToUserLoggedIn() {
    this.authSvc.userLoggedIn.pipe(takeUntil(this.unsubscribe$)).subscribe({
      next: (userId: string) => {
        this.successMessage = 'successfully registered! logging you in...';
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
      this.signupForm.reset();
      this.successMessage = 'registering...';
    } else if (error) {
      this.errorMessage = this.errSvc.handleError(error);
    }
  }

  private toggleFormControlsState(isEnabled: boolean): void {
    const method = isEnabled ? 'enable' : 'disable';
    this.signupForm.get('email')?.[method]();
    this.signupForm.get('password')?.[method]();
  }

  private toggleForm(isEnabled: boolean): void {
    const method = isEnabled? true : false;
    this.isLoading = !method;
    this.toggleFormControlsState(method);
  }

  switchToLogin() {
    this.showLoginEvent.emit();
  }
}
