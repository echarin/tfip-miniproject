// budget.resolver.ts
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { RequestService } from './request.service';
import { TokenService } from './token.service';

export function resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
  const budgetService = inject(RequestService);
  const tokenService = inject(TokenService);
  const router = inject(Router);
  const userId = route.params['userId'];

  const auth = tokenService.getAuth();
  if (!auth || auth.userId !== userId) {
    return of(router.createUrlTree([''])); // Redirect to frontpage
  }

  return budgetService.getBudget(userId).pipe(
    map(budget => {
      if (budget) {
        return router.createUrlTree([`${userId}/${budget.id}`]);
      } else {
        return router.createUrlTree([`${userId}/createBudget`]);
      }
    }),
    // Will return 404 budget not found if no budget
    catchError((err) => {
      return of(router.createUrlTree([`${userId}/createBudget`]));
    })
  );
}
