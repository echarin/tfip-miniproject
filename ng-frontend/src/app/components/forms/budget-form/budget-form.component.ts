// budget-form.component.ts
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';
import { Budget } from 'src/app/models/entities';
import { ErrorService } from 'src/app/services/error.service';
import { RequestService } from 'src/app/services/request.service';
import { TokenService } from 'src/app/services/token.service';

@Component({
  selector: 'app-budget-form',
  templateUrl: './budget-form.component.html',
  styleUrls: ['./budget-form.component.css']
})
export class BudgetFormComponent implements OnInit, OnDestroy {
  userId?: string;

  budgetForm!: FormGroup;

  isLoading: boolean = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  private unsubscribe$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private reqSvc: RequestService,
    private errSvc: ErrorService,
    private tokenSvc: TokenService
  ) { }

  ngOnInit(): void {
    this.budgetForm = this.fb.group({
      name: this.fb.control({value: null, disabled: this.isLoading}, [ Validators.required ]),
      moneyPool: this.fb.control({value: null, disabled: this.isLoading}, [ Validators.required ]),
    });

    this.userId = this.tokenSvc.getAuth()?.userId;
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }

  onSubmit(): void {
    if (this.budgetForm.valid) {
      if (!this.userId) {
        this.errorMessage = 'user not identified.';
        return;
      }

      this.isLoading = true;
      this.toggleFormControlsState(false);
      const budget: Budget = this.budgetForm.value;
      this.reqSvc.createBudget(budget, this.userId).pipe(takeUntil(this.unsubscribe$)).subscribe({
        next: (data) => this.handleSubmission(data, null),
        error: (err: HttpErrorResponse) => this.handleSubmission(null, err),
      });
    } else {
      this.errorMessage = 'please fill out all required fields.';
    }
  }

  private handleSubmission(data: Budget | null, error: HttpErrorResponse | null): void {
    console.log(data || error);
    this.isLoading = false;
    this.toggleFormControlsState(true);

    if (data) {
      this.budgetForm.reset();
      this.successMessage = 'budget successfully submitted!';
    } else if (error) {
      this.errorMessage = this.errSvc.handleError(error);
    }
  }
  
  private toggleFormControlsState(isEnabled: boolean): void {
    const method = isEnabled ? 'enable' : 'disable';
    this.budgetForm.get('name')?.[method]();
    this.budgetForm.get('moneyPool')?.[method]();
  }
}
