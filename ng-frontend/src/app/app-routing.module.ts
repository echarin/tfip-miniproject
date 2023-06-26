// app-routing.module.ts
import { NgModule, inject } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BudgetComponent } from './components/entities/budget/budget.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { ExpensesComponent } from './components/entities/expenses/expenses.component';
import { FrontpageComponent } from './components/frontpage/frontpage.component';
import { canActivate } from './auth/auth-guard.service';
import { BudgetFormComponent } from './components/forms/budget-form/budget-form.component';
import { CategoryFormComponent } from './components/forms/category-form/category-form.component';
import { ExpenseFormComponent } from './components/forms/expense-form/expense-form.component';
import { CommentFormComponent } from './components/forms/comment-form/comment-form.component';
import { resolve } from './services/budget-resolver.service';

const routes: Routes = [
  { path: '', component: FrontpageComponent },
  {
    path: ':userId',
    component: DashboardComponent,
    canActivate: [canActivate], // protected endpoints
    resolve: { hasBudget: () => inject(resolve) },
    children: [
      { path: '', redirectTo: 'budget', pathMatch: 'full' },
      { path: 'createBudget', component: BudgetFormComponent },
      { path: ':budgetId', component: BudgetComponent },
      { path: ':budgetId/createCategory', component: CategoryFormComponent },
      { path: ':budgetId/update', component: BudgetFormComponent },
      { path: ':budgetId/:categoryId/update', component: CategoryFormComponent },
      { path: 'expenses', component: ExpensesComponent },
      { path: 'expenses/createExpense', component: ExpenseFormComponent },
      { path: ':expenseId/createComment', component: CommentFormComponent },
      { path: ':expenseId', component: CommentFormComponent },
      { path: ':expenseId/update', component: ExpenseFormComponent }
    ],
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
