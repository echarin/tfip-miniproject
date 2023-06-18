import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Expense } from '../models/models';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class BudgetService {
  private apiUrl: string = environment.sbServerUrl + environment.api;
  private jsonHeaders = new HttpHeaders().set('Content-Type', 'application/json');

  constructor(private httpClient: HttpClient) { }

  getExpenses(userId: string): Observable<Expense[]> {
    const url = `${this.apiUrl}/${userId}/expenses`;
    return this.httpClient.get<Expense[]>(url, { headers: this.jsonHeaders });
  }

  createExpense(expense: Expense, userId: string): Observable<Expense> {
    const url = `${this.apiUrl}/${userId}/expenses`;
    return this.httpClient.post<Expense>(url, expense, { headers: this.jsonHeaders });
  }
}
