import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Expense } from '../models/models';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class BudgetService {
  private apiUrl: string = environment.sbApiUrl;
  private testUserUUID: string = environment.testUserUUID;
  private jsonHeaders = new HttpHeaders().set('Content-Type', 'application/json');

  constructor(private httpClient: HttpClient) { }

  postExpense(expense: any): Observable<Expense> {
    const url = `${this.apiUrl}/expenses`;
    return this.httpClient.post<Expense>(url, expense, { headers: this.jsonHeaders });
  }
}
