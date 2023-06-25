// request.service.ts
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Budget, Category, Expense } from '../models/entities';
import { environment } from 'src/environments/environment';
import { ResponseDTO } from '../models/response-dto';
import { Page } from '../models/page';

@Injectable({
  providedIn: 'root'
})
export class RequestService {
  private apiUrl: string = environment.sbServerUrl + environment.api;
  private jsonHeaders = new HttpHeaders().set('Content-Type', 'application/json');

  constructor(private httpClient: HttpClient) { }

  // Budget
  getBudget(userId: string): Observable<Budget> {
    const url = `${this.apiUrl}/${userId}/budget`;
    return this.httpClient.get<Budget>(url, { headers: this.jsonHeaders });
  }

  createBudget(budget: Budget, userId: string): Observable<Budget> {
    const url = `${this.apiUrl}/${userId}/budget`;
    return this.httpClient.post<Budget>(url, budget, { headers: this.jsonHeaders });
  }

  deleteBudget(userId: string, budgetId: string): Observable<ResponseDTO> {
    const url = `${this.apiUrl}/${userId}/budget/${budgetId}`;
    return this.httpClient.delete<ResponseDTO>(url, { headers: this.jsonHeaders });
  }

  // Category
  getCategories(userId: string): Observable<Category[]> {
    const url = `${this.apiUrl}/${userId}/categories`;
    return this.httpClient.get<Category[]>(url, { headers: this.jsonHeaders });
  }

  createCategory(category: Category, userId: string, budgetId: string): Observable<Category> {
    const url = `${this.apiUrl}/${userId}/${budgetId}/categories`;
    return this.httpClient.post<Category>(url, category, { headers: this.jsonHeaders });
  }

  deleteCategory(userId: string, categoryId: string): Observable<ResponseDTO> {
    const url = `${this.apiUrl}/${userId}/categories/${categoryId}`;
    return this.httpClient.delete<ResponseDTO>(url, { headers: this.jsonHeaders });
  }

  // Expense
  getExpenses(userId: string, page: number = 0, size: number = 10, from?: Date, to?: Date): Observable<Page<Expense>> {
    const params = new HttpParams()
      .set('page', String(page))
      .set('size', String(size));
    
    if (from) {
      params.set('from', from?.toISOString());
    }

    if (to) {
      params.set('to', to?.toISOString());
    }

    const url = `${this.apiUrl}/${userId}/expenses`;
    return this.httpClient.get<Page<Expense>>(url, { params, headers: this.jsonHeaders });
  }

  getAllExpensesByCategory(userId: string, categoryId: string): Observable<Expense[]> {
    const url = `${this.apiUrl}/${userId}/${categoryId}/expenses`;
    return this.httpClient.get<Expense[]>(url, { headers: this.jsonHeaders });
  }

  createExpense(userId: string, categoryId: string, expense: Expense): Observable<Expense> {
    const url = `${this.apiUrl}/${userId}/${categoryId}/expenses`;
    return this.httpClient.post<Expense>(url, expense, { headers: this.jsonHeaders });
  }

  deleteExpense(userId: string, expenseId: string): Observable<ResponseDTO> {
    const url = `${this.apiUrl}/${userId}/expenses/${expenseId}`;
    return this.httpClient.delete<ResponseDTO>(url, { headers: this.jsonHeaders });
  }

  // Comment
  getComments(userId: string, expenseId: string, page: number = 0, size: number = 10): Observable<Comment[]> {
    const params = new HttpParams()
      .set('page', String(page))
      .set('size', String(size));
    const url = `${this.apiUrl}/${userId}/${expenseId}/comments`;
    return this.httpClient.get<Comment[]>(url, { params, headers: this.jsonHeaders });
  }

  createComment(comment: Comment, userId: string, expenseId: string): Observable<Comment> {
    const url = `${this.apiUrl}/${userId}/${expenseId}/comments`;
    return this.httpClient.post<Comment>(url, comment, { headers: this.jsonHeaders });
  }

  deleteComment(userId: string, commentId: string): Observable<ResponseDTO> {
    const url = `${this.apiUrl}/${userId}/comments/${commentId}`;
    return this.httpClient.delete<ResponseDTO>(url, { headers: this.jsonHeaders });
  }
}
