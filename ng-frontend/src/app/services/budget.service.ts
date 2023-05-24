import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Expense } from '../models/expense';

@Injectable({
  providedIn: 'root'
})
export class BudgetService {
  private apiUrl = environment.sbApiUrl;
  private jsonHeaders = new HttpHeaders().set('Content-Type', 'application/json');

  constructor(private httpClient: HttpClient) { }

  postExpense(expense: any): Observable<Expense> {
    const url = `${this.apiUrl}/expenses`;
    return this.httpClient.post<Expense>(url, expense, { headers: this.jsonHeaders });
  }
}
