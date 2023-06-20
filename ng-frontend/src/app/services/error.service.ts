// error.service.ts
import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';

// Define constants for error messages
export const CLIENT_ERROR_MESSAGE = 'an error occurred: ';
export const BAD_REQUEST_MESSAGE = 'there was a problem with your request. please try again.';
export const UNAUTHORISED_REQUEST_MESSAGE = 'incorrect email/password. please try again.';
export const CONFLICT_MESSAGE = 'a user by that email already exists. please try again.';
export const SERVER_ERROR_MESSAGE = 'there was a problem with the server. please try again later.';
export const UNEXPECTED_ERROR_MESSAGE = 'unexpected error occurred: ';

@Injectable({
  providedIn: 'root'
})
export class ErrorService {

  constructor() { }

  handleError(error: HttpErrorResponse): string {
    if (error.error instanceof ErrorEvent) {
      // A client-side or network error occured
      return CLIENT_ERROR_MESSAGE + error.error.message;
    } else {
      // The backend returned an unsuccessful response code
      switch (error.status) {
        case 400:
          return BAD_REQUEST_MESSAGE;
        case 401:
          return UNAUTHORISED_REQUEST_MESSAGE;
        case 409:
          return CONFLICT_MESSAGE;
        case 500:
          return SERVER_ERROR_MESSAGE;
        default:
          return UNEXPECTED_ERROR_MESSAGE + error.message;
      }
    }
  }
}