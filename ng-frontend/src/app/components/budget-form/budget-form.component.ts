import { Component } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-budget-form',
  templateUrl: './budget-form.component.html',
  styleUrls: ['./budget-form.component.css']
})
export class BudgetFormComponent {
  budgetForm!: FormGroup;

  isLoading: boolean = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  private unsubscribe$ = new Subject<void>();

  constructor(
    
  ) {

  }

  onSubmit(): void {

  }
}
