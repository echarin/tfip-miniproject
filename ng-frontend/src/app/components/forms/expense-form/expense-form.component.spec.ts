import { HttpErrorResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { Expense } from 'src/app/models/entities';
import { BudgetService } from 'src/app/services/budget.service';
import { ErrorService, SERVER_ERROR_MESSAGE } from 'src/app/services/error.service';
import { ExpenseFormComponent } from './expense-form.component';

describe('ExpenseFormComponent', () => {
  let component: ExpenseFormComponent;
  let fixture: ComponentFixture<ExpenseFormComponent>;
  let bSvcMock: jasmine.SpyObj<BudgetService>;
  let errSvcMock: jasmine.SpyObj<ErrorService>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ExpenseFormComponent ],
      imports: [
        ReactiveFormsModule,
        HttpClientTestingModule
      ],
      providers: [
        { provide: BudgetService, useValue: jasmine.createSpyObj('BudgetService', ['postExpense']) },
        { provide: ErrorService, useValue: jasmine.createSpyObj('ErrorService', ['handleError']) }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExpenseFormComponent);
    component = fixture.componentInstance;
    bSvcMock = TestBed.inject(BudgetService) as jasmine.SpyObj<BudgetService>;
    errSvcMock = TestBed.inject(ErrorService) as jasmine.SpyObj<ErrorService>;
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

  it('should call BudgetService postExpense on form submission', () => {
    let control = component.expenseForm.get('category');
    control?.setValue('Food');
    control = component.expenseForm.get('amount');
    control?.setValue(10);
    control = component.expenseForm.get('date');
    control?.setValue('2023-05-23');
    control = component.expenseForm.get('description');
    control?.setValue('Lunch');
  
    const mockExpense: Expense = { 
      id: 1, // Set this to any number, it doesn't matter for this test
      category: 'Food',
      amount: 10,
      date: '2023-05-23',
      description: 'Lunch'
    };
    bSvcMock.postExpense.and.returnValue(of(mockExpense));
    
    component.onSubmit();
  
    expect(bSvcMock.postExpense).toHaveBeenCalled();
  });

  it('should set successMessage on successful form submission', () => {
    let control = component.expenseForm.get('category');
    control?.setValue('Food');
    control = component.expenseForm.get('amount');
    control?.setValue(10);
    control = component.expenseForm.get('date');
    control?.setValue('2023-05-23');
    control = component.expenseForm.get('description');
    control?.setValue('Lunch');
  
    // We pretend that an Expense object comes back from observable
    const mockExpense: Expense = { 
      id: 1, // Set this to any number, it doesn't matter for this test
      category: 'Food',
      amount: 10,
      date: '2023-05-23',
      description: 'Lunch'
    };
    bSvcMock.postExpense.and.returnValue(of(mockExpense));
    
    component.onSubmit();

    expect(component.successMessage).toBe('Expense successfully submitted!');
  });

  it('should call ErrorService handleError on form submission error', () => {
    let control = component.expenseForm.get('category');
    control?.setValue('Food');
    control = component.expenseForm.get('amount');
    control?.setValue(10);
    control = component.expenseForm.get('date');
    control?.setValue('2023-05-23');

    const errorResponse = new HttpErrorResponse({
      error: 'test',
      status: 500,
      statusText: 'Internal Server Error'
    });
    bSvcMock.postExpense.and.returnValue(throwError(() => errorResponse));

    component.onSubmit();

    expect(errSvcMock.handleError).toHaveBeenCalledWith(errorResponse);
  });

  it('should set errorMessage on form submission error', () => {
    let control = component.expenseForm.get('category');
    control?.setValue('Food');
    control = component.expenseForm.get('amount');
    control?.setValue(10);
    control = component.expenseForm.get('date');
    control?.setValue('2023-05-23');

    // Setting up the conditions for your test
    // When postExpense and handleError are called, I want them to return such values
    const errorResponse = new HttpErrorResponse({
      error: 'test',
      status: 500,
      statusText: 'Internal Server Error'
    });
    bSvcMock.postExpense.and.returnValue(throwError(() => errorResponse));
    errSvcMock.handleError.and.returnValue(SERVER_ERROR_MESSAGE);

    // Therefore, when submitting, should result in an error being thrown and handled
    component.onSubmit();

    expect(component.errorMessage).toBe(SERVER_ERROR_MESSAGE);
  });
});