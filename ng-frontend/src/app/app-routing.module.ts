// app-routing.module.ts
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { canActivate } from './auth/auth-guard.service';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { BudgetComponent } from './components/entities/budget/budget.component';
import { CommentsComponent } from './components/entities/comments/comments.component';
import { ExpensesComponent } from './components/entities/expenses/expenses.component';
import { BudgetFormComponent } from './components/forms/budget-form/budget-form.component';
import { CategoryFormComponent } from './components/forms/category-form/category-form.component';
import { CommentFormComponent } from './components/forms/comment-form/comment-form.component';
import { ExpenseFormComponent } from './components/forms/expense-form/expense-form.component';
import { FrontpageComponent } from './components/frontpage/frontpage.component';

const routes: Routes = [
  { path: '', component: FrontpageComponent },
  {
    path: ':userId',
    component: DashboardComponent,
    canActivate: [canActivate], // protected endpoints
    children: [
      { path: '', redirectTo: 'budget', pathMatch: 'full' },
      { path: 'budget', component: BudgetComponent },
      { path: 'expenses', component: ExpensesComponent },
      { path: 'expenses/createExpense', component: ExpenseFormComponent },
      { path: 'createBudget', component: BudgetFormComponent },
      { path: ':budgetId', component: BudgetComponent },
      { path: ':budgetId/update', component: BudgetFormComponent },
      { path: ':budgetId/createCategory', component: CategoryFormComponent },
      { path: ':budgetId/:categoryId/update', component: CategoryFormComponent },
      { path: 'expenses/:expenseId', component: CommentsComponent },
      { path: 'expenses/:expenseId/createComment', component: CommentFormComponent }, 
      { path: 'expenses/:expenseId/update', component: ExpenseFormComponent }
    ],
  },
  { path: '**', redirectTo: '', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
