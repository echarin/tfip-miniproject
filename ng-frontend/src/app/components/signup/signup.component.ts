// signup.component.ts
import { animate, state, style, transition, trigger } from '@angular/animations';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';
import { SignupDTO, SignupResponse } from 'src/app/models/dtos';
import { ErrorService } from 'src/app/services/error.service';
import { SignupService } from 'src/app/services/signup.service';

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
    private suSvc: SignupService,
    private errSvc: ErrorService,
  ) { }

  ngOnInit(): void {
    this.signupForm = this.fb.group({
      email: this.fb.control({value: null, disabled: this.isLoading}, [ Validators.required ]),
      password: this.fb.control({value: null, disabled: this.isLoading}, [ Validators.required ]),
      confirmPassword: this.fb.control({value: null, disabled: this.isLoading}, [ Validators.required ]),
    })
  }

  onSubmit(): void {
    if (this.signupForm.valid) {
      this.isLoading = true;
      this.toggleFormControlsState(false);

      if (!this.isPasswordsMatch()) {
        this.errorMessage = 'passwords do not match';
        this.isLoading = false;
        this.toggleFormControlsState(true);
        return;
      }

      const signup: SignupDTO = this.signupForm.value;
      console.log(signup);

      this.suSvc.signup(signup).pipe(takeUntil(this.unsubscribe$)).subscribe({
        next: (data: SignupResponse) => this.handleSubmission(data, null),
        error: (err: HttpErrorResponse) => this.handleSubmission(null, err),
      });
    } else {
      this.errorMessage = 'please fill out all required fields.';
    }
  }

  private handleSubmission(data: SignupResponse | null, error: HttpErrorResponse | null): void {
    console.log(data || error);
    this.isLoading = false;
    this.toggleFormControlsState(true);

    if (data) {
      this.signupForm.reset();
      this.successMessage = 'successfully registered!';
    } else if (error) {
      this.errorMessage = this.errSvc.handleError(error);
    }
  }

  isPasswordsMatch(): boolean {
    return this.signupForm.get('password')?.value === this.signupForm.get('confirmPassword')?.value;
  }

  switchToLogin() {
    this.showLoginEvent.emit();
  }

  private toggleFormControlsState(isEnabled: boolean): void {
    const method = isEnabled ? 'enable' : 'disable';
    this.signupForm.get('email')?.[method]();
    this.signupForm.get('password')?.[method]();
  }
}
