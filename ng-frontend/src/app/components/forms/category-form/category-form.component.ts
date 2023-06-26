// category-form.component.ts
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';
import { Category } from 'src/app/models/entities';
import { ErrorService } from 'src/app/services/error.service';
import { RequestService } from 'src/app/services/request.service';
import { TokenService } from 'src/app/services/token.service';

@Component({
  selector: 'app-category-form',
  templateUrl: './category-form.component.html',
  styleUrls: ['./category-form.component.css']
})
export class CategoryFormComponent implements OnInit, OnDestroy {
  userId?: string;
  budgetId?: string;

  categoryForm!: FormGroup;

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
    this.categoryForm = this.fb.group({
      name: this.fb.control({value: null, disabled: this.isLoading}, [ Validators.required ]),
      budgetedAmount: this.fb.control({value: null, disabled: this.isLoading}, [ Validators.required ]),
    });

    const auth = this.tokenSvc.getAuth();
    this.userId = auth?.userId;
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }

  onSubmit(): void {
    if (this.categoryForm.valid) {
      if (!this.userId || !this.budgetId) {
        this.errorMessage = 'user or budget not identified.';
        return;
      }

      this.isLoading = true;
      this.toggleFormControlsState(false);
      const category: Category = this.categoryForm.value;
      this.reqSvc.createCategory(category, this.userId, this.budgetId).pipe(takeUntil(this.unsubscribe$)).subscribe({
        next: (data) => this.handleSubmission(data, null),
        error: (err: HttpErrorResponse) => this.handleSubmission(null, err),
      });
    } else {
      this.errorMessage = 'please fill out all required fields.';
    }
  }

  private handleSubmission(data: Category | null, error: HttpErrorResponse | null): void {
    console.log(data || error);
    this.isLoading = false;
    this.toggleFormControlsState(true);

    if (data) {
      this.categoryForm.reset();
      this.successMessage = 'category successfully submitted!';
    } else if (error) {
      this.errorMessage = this.errSvc.handleError(error);
    }
  }
  
  private toggleFormControlsState(isEnabled: boolean): void {
    const method = isEnabled ? 'enable' : 'disable';
    this.categoryForm.get('name')?.[method]();
    this.categoryForm.get('budgetedAmount')?.[method]();
  }
}