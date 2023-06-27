// budget.component.ts
import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject, forkJoin } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { Budget, Category } from 'src/app/models/entities';
import { RequestService } from 'src/app/services/request.service';
import { TokenService } from 'src/app/services/token.service';

@Component({
  selector: 'app-budget',
  templateUrl: './budget.component.html',
  styleUrls: ['./budget.component.css']
})
export class BudgetComponent implements OnInit, OnDestroy {
  userId?: string | null;
  budget?: Budget;
  categories?: Category[];
  private unsubscribe$ = new Subject<void>();

  isLoading: boolean = true;
  displayMessage: string | null = null;
  errorMessage: string | null = null;
  displayedColumns: string[] = ['id', 'name', 'amount', 'delete']; // No 'update' column

  constructor(
    private route: ActivatedRoute,
    private reqSvc: RequestService,
    private tokenSvc: TokenService,
    private router: Router
  ) { }

  ngOnInit(): void {
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

    if (this.userId) {
      forkJoin({
        budget: this.reqSvc.getBudget(this.userId),
        categories: this.reqSvc.getCategories(this.userId)
      }).pipe(takeUntil(this.unsubscribe$)).subscribe({
        next: (data) => {
          this.budget = data.budget;
          this.categories = data.categories;
          this.isLoading = false;
        },
        error: (err) => {
          console.error(err);
          this.isLoading = false;
        },
      });
    }
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }

  getBudget(userId: string) {
    this.reqSvc.getBudget(userId).pipe(takeUntil(this.unsubscribe$)).subscribe({
      next: (budget) => this.budget = budget,
      error: (err) => {
        console.error(err);
      }
    });
  }

  getCategories(userId: string) {
    this.reqSvc.getCategories(userId).pipe(takeUntil(this.unsubscribe$)).subscribe({
      next: (categories) => {
        this.categories = categories;
      },
      error: (err) => console.error(err),
    });
  }

  onDeleteCategory(categoryId: string) {
    this.displayMessage = "deleting...";
    this.reqSvc.deleteCategory(this.userId!, categoryId).pipe(takeUntil(this.unsubscribe$)).subscribe({
      next: (response) => {
        this.displayMessage = response.message;
        this.getBudget(this.userId!);
        this.getCategories(this.userId!);
      },
      error: (err) => this.errorMessage = err.error.message,
    });
  }
}
