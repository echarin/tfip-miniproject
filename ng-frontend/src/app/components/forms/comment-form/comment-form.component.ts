// comment-form.component.ts
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';
import { Comment } from 'src/app/models/entities';
import { ErrorService } from 'src/app/services/error.service';
import { RequestService } from 'src/app/services/request.service';
import { TokenService } from 'src/app/services/token.service';

@Component({
  selector: 'app-comment-form',
  templateUrl: './comment-form.component.html',
  styleUrls: ['./comment-form.component.css']
})
export class CommentFormComponent implements OnInit, OnDestroy {
  userId?: string;
  expenseId?: string;

  commentForm!: FormGroup;

  isLoading: boolean = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  private unsubscribe$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private reqSvc: RequestService,
    private errSvc: ErrorService,
    private tokenSvc: TokenService
  ) { }

  ngOnInit(): void {
    this.commentForm = this.fb.group({
      text: this.fb.control({value: null, disabled: this.isLoading}, [ Validators.required ]),
    });

    const auth = this.tokenSvc.getAuth();
    this.userId = auth?.userId;
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }

  onSubmit(): void {
    if (this.commentForm.valid) {
      if (!this.userId || !this.expenseId) {
        this.errorMessage = 'user or expense not identified.';
        return;
      }

      this.isLoading = true;
      this.toggleFormControlsState(false);
      const comment: Comment = this.commentForm.value;
      this.reqSvc.createComment(comment, this.userId, this.expenseId).pipe(takeUntil(this.unsubscribe$)).subscribe({
        next: (data) => this.handleSubmission(data, null),
        error: (err: HttpErrorResponse) => this.handleSubmission(null, err),
      });
    } else {
      this.errorMessage = 'please fill out all required fields.';
    }
  }

  private handleSubmission(data: Comment | null, error: HttpErrorResponse | null): void {
    console.log(data || error);
    this.isLoading = false;
    this.toggleFormControlsState(true);

    if (data) {
      this.commentForm.reset();
      this.successMessage = 'comment successfully submitted!';
    } else if (error) {
      this.errorMessage = this.errSvc.handleError(error);
    }
  }
  
  private toggleFormControlsState(isEnabled: boolean): void {
    const method = isEnabled ? 'enable' : 'disable';
    this.commentForm.get('text')?.[method]();
  }
}