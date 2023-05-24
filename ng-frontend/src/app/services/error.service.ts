import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ErrorService {

  constructor() { }

  handleError(error: HttpErrorResponse): string {
    if (error.error instanceof ErrorEvent) {
      // A client-side or network error occured
      return `An error occurred: ${error.error.message}`;
    } else {
      // The backend returned an unsuccessful response code
      switch (error.status) {
        case 400:
          return 'There was a problem with your request. Please check your input and try again.';
        case 401:
          return 'Unauthorised request. Please login again.';
        case 500:
          return 'There was a problem with the server. Please try again later.';
        default:
          return `Unexpected error occurred: ${error.message}`
      }
    }
  }
}
