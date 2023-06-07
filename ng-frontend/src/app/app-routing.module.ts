import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BudgetComponent } from './components/budget/budget.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { ExpensesComponent } from './components/expenses/expenses.component';
import { FrontpageComponent } from './components/frontpage/frontpage.component';
import { SettingsComponent } from './components/settings/settings.component';

const routes: Routes = [
  { path: '', component: FrontpageComponent },
  {
    path: 'dashboard',
    component: DashboardComponent,
    children: [
      { path: 'settings', component: SettingsComponent },
      { path: 'budget', component: BudgetComponent },
      { path: 'expenses', component: ExpensesComponent },
      { path: '', redirectTo: 'budget', pathMatch: 'full' }
    ]
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
