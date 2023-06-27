// expenses.component.ts
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { Expense } from 'src/app/models/entities';
import { RequestService } from 'src/app/services/request.service';
import { TokenService } from 'src/app/services/token.service';

@Component({
  selector: 'app-expenses',
  templateUrl: './expenses.component.html',
  styleUrls: ['./expenses.component.css']
})
export class ExpensesComponent implements OnInit, OnDestroy {
  expensePaginationForm!: FormGroup;

  userId?: string | null;
  expenses?: Expense[];
  private unsubscribe$ = new Subject<void>();

  isLoading: boolean = true;
  hasExpenses: boolean = false;
  errorMessage: string | null = null;
  displayMessage: string | null = null;
  displayedColumns: string[] = ['id', 'category name', 'amount', 'date', 'delete'];

  // For pagination
  from?: Date;
  to?: Date;
  size: number = 10;
  start: number = 0;
  end: number = this.size;
  total: number = 0;

  constructor(
    private fb: FormBuilder,
    private reqSvc: RequestService,
    private tokenSvc: TokenService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.isLoading = false; 
    this.expensePaginationForm = this.fb.group({
      size: this.fb.control({value: 10, disabled: this.isLoading}),
      from: this.fb.control({value: null, disabled: this.isLoading}),
      to: this.fb.control({value: null, disabled: this.isLoading}),
    });

    const parentParam = this.route.parent?.snapshot.paramMap.get('userId');
    if (parentParam !== null) {
      this.userId = parentParam;
    }

    const authId = this.tokenSvc.getAuth()?.userId;
    if (authId !== this.userId) {
      this.errorMessage = 'you do not have access to this resource.'
      this.router.navigate([''], { queryParams: 
        { message: this.errorMessage } 
      });
    }

    this.getExpenses();
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }

  getExpenses() {
    this.isLoading = true;

    this.size = this.expensePaginationForm.get('size')?.value;
    this.from = this.expensePaginationForm.get('from')?.value;
    this.to = this.expensePaginationForm.get('to')?.value;

    this.reqSvc.getExpenses(
      this.userId!, this.start, this.size, this.from, this.to
    ).pipe(takeUntil(this.unsubscribe$)).subscribe({
      next: (page) => {
        this.expenses = page.content;
        this.total = page.totalElements;
        this.end = this.start + this.expenses.length;

        this.isLoading = false;
      },
      error: (err) => this.errorMessage = err.message,
    });
  }

  onDeleteExpense(expenseId: string) {
    this.displayMessage = "deleting...";
    this.reqSvc.deleteExpense(this.userId!, expenseId).pipe(takeUntil(this.unsubscribe$)).subscribe({
      next: (response) => {
        this.displayMessage = response.message;
        this.getExpenses();
      },
      error: (err) => this.errorMessage = err.error.message,
    });
  }

  onRowClicked(row: Expense) {
    console.log(row);
    this.router.navigate(['/', this.userId, 'expenses', row.id]);
  }

  // Pagination
  prevPage() {
    if (this.start > 0) {
      this.start -= this.size;
      this.getExpenses();
    }
  }
  
  nextPage() {
    if (this.end < this.total) {
      this.start += this.size;
      this.getExpenses();
    }
  }
  
  canPrevPage(): boolean {
    return this.start > 0;
  }
  
  canNextPage(): boolean {
    return this.end < this.total;
  }  
}