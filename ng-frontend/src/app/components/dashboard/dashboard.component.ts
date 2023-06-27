// dashboard.component.ts
import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TokenService } from 'src/app/services/token.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  userId?: string;

  errorMessage: string | null = null;
  today: number = Date.now();
  exchangeRate?: number;

  constructor(
    private tokenSvc: TokenService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.userId = this.tokenSvc.getAuth()?.userId;
    if (!this.userId) {
      this.errorMessage = 'user not identified.';
      this.router.navigate([''], { queryParams: 
        { message: this.errorMessage } 
      });
    }
  }

  logout(): void {
    this.tokenSvc.removeAuth();
    this.router.navigate([''], { queryParams: 
      { message: 'successfully logged out.' } 
    });
  }
}
