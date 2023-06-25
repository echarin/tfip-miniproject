// signup.component.ts
import { animate, state, style, transition, trigger } from '@angular/animations';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, Validators } from '@angular/forms';
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
export class SignupComponent implements OnInit {
  signupForm!: FormGroup;
  isLoading: boolean = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  private unsubscribe$ = new Subject<void>();
  @Output() showLoginEvent = new EventEmitter<void>();

  constructor(
    private fb: FormBuilder,
    private authSvc: AuthService,
    private errSvc: ErrorService,
  ) { }

  ngOnInit(): void {
    this.signupForm = this.fb.group({
      email: this.fb.control({value: null, disabled: this.isLoading}, [ Validators.required ]),
      password: this.fb.control({value: null, disabled: this.isLoading}, [ Validators.required ]),
      confirmPassword: this.fb.control({value: null, disabled: this.isLoading}, [ Validators.required ]),
    }, { validators: [this.passwordMatchValidator] });
  }

  private passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
    const password = control.get('password')?.value;
    const confirmPassword = control.get('confirmPassword')?.value;
    return password === confirmPassword ? null : { 'mismatch': true };
  }

  onSubmit(): void {
    if (this.signupForm.valid) {
      this.toggleForm(false);

      const signup: AuthRequest = this.signupForm.value;
      console.log(signup);

      this.authSvc.signup(signup).pipe(takeUntil(this.unsubscribe$)).subscribe({
        next: (data: AuthResponse) => this.handleSubmission(data, null),
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

  private handleSubmission(data: AuthResponse | null, error: HttpErrorResponse | null): void {
    console.log(data || error);
    this.toggleForm(true);

    if (data) {
      this.signupForm.reset();
      this.successMessage = 'successfully registered!';
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
