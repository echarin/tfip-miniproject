import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { NgModule, isDevMode } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ServiceWorkerModule } from '@angular/service-worker';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AuthInterceptor } from './auth/auth.interceptor';
import { BudgetComponent } from './components/entities/budget/budget.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { ExpenseFormComponent } from './components/forms/expense-form/expense-form.component';
import { ExpensesComponent } from './components/entities/expenses/expenses.component';
import { FrontpageComponent } from './components/frontpage/frontpage.component';
import { LoginComponent } from './components/forms/login/login.component';
import { SignupComponent } from './components/forms/signup/signup.component';
import { MaterialModule } from './material.module';
import { BudgetFormComponent } from './components/forms/budget-form/budget-form.component';
import { CategoryFormComponent } from './components/forms/category-form/category-form.component';
import { CommentFormComponent } from './components/forms/comment-form/comment-form.component';
import { resolve } from './services/budget-resolver.service';
import { TokenService } from './services/token.service';

@NgModule({
  declarations: [
    AppComponent,
    ExpenseFormComponent,
    DashboardComponent,
    BudgetComponent,
    ExpensesComponent,
    FrontpageComponent,
    LoginComponent,
    SignupComponent,
    BudgetFormComponent,
    CategoryFormComponent,
    CommentFormComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    MaterialModule,
    ServiceWorkerModule.register('ngsw-worker.js', {
      enabled: !isDevMode(),
      // Register the ServiceWorker as soon as the application is stable
      // or after 30 seconds (whichever comes first).
      registrationStrategy: 'registerWhenStable:30000'
    }),
    BrowserAnimationsModule
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
