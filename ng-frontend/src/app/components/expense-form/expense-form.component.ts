import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-expense-form',
  templateUrl: './expense-form.component.html',
  styleUrls: ['./expense-form.component.css']
})
export class ExpenseFormComponent implements OnInit {
  expenseForm!: FormGroup;

  constructor(private fb: FormBuilder) { }

  ngOnInit(): void {
    this.expenseForm = this.fb.group({
      category: this.fb.control(null, [ Validators.required ]),
      amount: this.fb.control(null, [ Validators.required, Validators.min(0.01) ]),
      date: this.fb.control(null, [ Validators.required ]),
    });
  }

  onSubmit(): void {
    console.log(this.expenseForm.value);
    // Call the POST method
    this.expenseForm.reset();
  }
}
