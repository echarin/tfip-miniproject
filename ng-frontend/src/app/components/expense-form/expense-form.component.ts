// expense-form.component.ts
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';
import { AuthResponse } from 'src/app/models/auth-dtos';
import { Expense } from 'src/app/models/entities';
import { ErrorService } from 'src/app/services/error.service';
import { RequestService } from 'src/app/services/request.service';
import { TokenService } from 'src/app/services/token.service';

@Component({
  selector: 'app-expense-form',
  templateUrl: './expense-form.component.html',
  styleUrls: ['./expense-form.component.css']
})
export class ExpenseFormComponent implements OnInit {
  authDetails!: AuthResponse;

  expenseForm!: FormGroup;
  categories: string[] = [];
  isLoading: boolean = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  private unsubscribe$ = new Subject<void>();

  constructor(
    private fb: FormBuilder, 
    private reqSvc: RequestService,
    private errSvc: ErrorService,
    private tokenSvc: TokenService,
  ) { }

  ngOnInit(): void {
    this.expenseForm = this.fb.group({
      category: this.fb.control({value: null, disabled: this.isLoading}, [ Validators.required ]),
      amount: this.fb.control({value: null, disabled: this.isLoading}, [ Validators.required, Validators.min(0.01) ]),
      date: this.fb.control({value: null, disabled: this.isLoading}, [ Validators.required ]),
      description: this.fb.control({value: null, disabled: this.isLoading})
    });

    this.tokenSvc.getAuth();
    this.fetchCategories();
  }

  fetchCategories(): void {
    // We hardcode this for now
    // If user is implemented, then we would fetch categories from user
    this.categories = ['Food', 'Transport', 'Rent', 'Entertainment', 'Other'];
  }
  

  onSubmit(): void {
    if (this.expenseForm.valid) {
      this.isLoading = true;
      this.toggleFormControlsState(false);

      // How do you get category ID from the category?

      const expense: Expense = this.expenseForm.value;
      console.log(expense);

      const userId: string = this.authDetails.userId;

      this.reqSvc.createExpense(userId, categoryId, expense).pipe(takeUntil(this.unsubscribe$)).subscribe({
        next: (data) => this.handleSubmission(data, null),
        error: (err: HttpErrorResponse) => this.handleSubmission(null, err),
      });
    } else {
      this.errorMessage = 'please fill out all required fields.';
    }
  }

  private handleSubmission(data: Expense | null, error: HttpErrorResponse | null): void {
    console.log(data || error);
    this.isLoading = false;
    this.toggleFormControlsState(true);

    if (data) {
      this.expenseForm.reset();
      this.successMessage = 'expense successfully submitted!';
    } else if (error) {
      this.errorMessage = this.errSvc.handleError(error);
    }
  }

  private toggleFormControlsState(isEnabled: boolean): void {
    const method = isEnabled ? 'enable' : 'disable';
    this.expenseForm.get('category')?.[method]();
    this.expenseForm.get('amount')?.[method]();
    this.expenseForm.get('date')?.[method]();
    this.expenseForm.get('description')?.[method]();
  }
}
