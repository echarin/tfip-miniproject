import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExpenseFormComponent } from './expense-form.component';
import { ReactiveFormsModule } from '@angular/forms';

describe('ExpenseFormComponent', () => {
  let component: ExpenseFormComponent;
  let fixture: ComponentFixture<ExpenseFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ExpenseFormComponent ],
      imports: [ ReactiveFormsModule ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExpenseFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create form with 3 controls', () => {
    expect(component.expenseForm.contains('category')).toBeTruthy();
    expect(component.expenseForm.contains('amount')).toBeTruthy();
    expect(component.expenseForm.contains('date')).toBeTruthy();
  });

  it('should make the category control required', () => {
    let control = component.expenseForm.get('category');
    control?.setValue('');
    expect(control?.valid).toBeFalsy();
  });

  it('should make the amount control required', () => {
    let control = component.expenseForm.get('amount');
    control?.setValue('');
    expect(control?.valid).toBeFalsy();
  });

  it('should make the date control required', () => {
    let control = component.expenseForm.get('date');
    control?.setValue('');
    expect(control?.valid).toBeFalsy();
  });
});
