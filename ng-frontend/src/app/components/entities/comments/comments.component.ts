import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { Comment } from 'src/app/models/entities';
import { RequestService } from 'src/app/services/request.service';
import { TokenService } from 'src/app/services/token.service';

@Component({
  selector: 'app-comments',
  templateUrl: './comments.component.html',
  styleUrls: ['./comments.component.css'] // Assuming you are using the same styles
})
export class CommentsComponent implements OnInit, OnDestroy {
  userId?: string | null;
  expenseId?: string | null;
  comments?: Comment[];
  private unsubscribe$ = new Subject<void>();

  isLoading: boolean = true;
  hasComments: boolean = false;
  errorMessage: string | null = null;
  displayMessage: string | null = null;
  displayedColumns: string[] = ['id', 'comment', 'delete'];

  constructor(
    private reqSvc: RequestService,
    private tokenSvc: TokenService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.isLoading = true;
    
    const parentParam = this.route.parent?.snapshot.paramMap.get('userId');
    if (parentParam !== null) {
      this.userId = parentParam;
    }

    const expenseParam = this.route.snapshot.paramMap.get('expenseId');
    if (expenseParam !== null) {
      this.expenseId = expenseParam;
    }

    const authId = this.tokenSvc.getAuth()?.userId;
    if (authId !== this.userId) {
      this.errorMessage = 'you do not have access to this resource.'
      this.router.navigate([''], { queryParams: 
        { message: this.errorMessage } 
      });
    }

    this.getComments();
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }

  getComments() {
    this.reqSvc.getComments(
      this.userId!, this.expenseId!
    ).pipe(takeUntil(this.unsubscribe$)).subscribe({
      next: (response) => {
        this.comments = response;
        this.isLoading = false;
      },
      error: (err) => this.errorMessage = err.message,
    });
  }

  onDeleteComment(commentId: string) {
    this.displayMessage = "deleting...";
    this.reqSvc.deleteComment(this.userId!, commentId).pipe(takeUntil(this.unsubscribe$)).subscribe({
      next: (response) => {
        this.displayMessage = response.message;
        this.getComments();
      },
      error: (err) => this.errorMessage = err.error.message,
    });
  }
}
