// category-form.component.ts
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
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
  categoryId?: string;

  categoryForm!: FormGroup;
  isCreate?: boolean;
  isEdit?: boolean;
  isLoading: boolean = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  
  private unsubscribe$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private reqSvc: RequestService,
    private errSvc: ErrorService,
    private tokenSvc: TokenService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.categoryForm = this.fb.group({
      name: this.fb.control({value: null, disabled: this.isLoading}, [ Validators.required ]),
      budgetedAmount: this.fb.control({value: null, disabled: this.isLoading}, [ Validators.required ]),
    });

    const parentParam = this.route.parent?.snapshot.paramMap.get('userId');
    if (parentParam !== null) {
      this.userId = parentParam;
    }

    this.route.params.pipe(takeUntil(this.unsubscribe$)).subscribe({
      next: (params) => {
        this.budgetId = params['budgetId'];
        this.categoryId = params['categoryId'];
      }
    });

    if (!this.userId) {
      this.errorMessage = 'user not identified.';
      this.router.navigate(['/', this.userId, 'budget'], { queryParams: 
        { message: this.errorMessage } 
      });
    } 
    
    const authId = this.tokenSvc.getAuth()?.userId;
    if (authId !== this.userId) {
      this.errorMessage = 'you do not have access to this resource.'
      this.router.navigate(['/', this.userId, 'budget'], { queryParams: 
        { message: this.errorMessage } 
      });
    } 

    if (!this.budgetId) {
      this.errorMessage = 'budget not identified.';
      this.router.navigate(['/', this.userId, 'budget'], { queryParams: 
        { message: this.errorMessage } 
      });
    }
    
    if (this.categoryId) {
      this.isEdit = true;
      this.reqSvc.getCategory(this.userId!, this.categoryId).pipe(takeUntil(this.unsubscribe$)).subscribe({
        next: (category) => {
          this.categoryForm.setValue({
            name: category.name,
            budgetedAmount: category.budgetedAmount
          });
        },
        error: (err) => console.error(err),
      });
    } else {
      this.isCreate = true;
    }
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

      if (this.isCreate) {
        this.reqSvc.createCategory(category, this.userId, this.budgetId).pipe(takeUntil(this.unsubscribe$)).subscribe({
          next: (data) => this.handleSubmission(data, null),
          error: (err: HttpErrorResponse) => this.handleSubmission(null, err),
        });
      } else if (this.isEdit && this.categoryId) {
        this.reqSvc.updateCategory(category, this.userId, this.categoryId).pipe(takeUntil(this.unsubscribe$)).subscribe({
          next: (data) => this.handleSubmission(data, null),
          error: (err: HttpErrorResponse) => this.handleSubmission(null, err),
        });
      }
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