import { TestBed } from '@angular/core/testing';

import { BudgetResolverService } from './budget-resolver.service';

describe('BudgetResolverService', () => {
  let service: BudgetResolverService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BudgetResolverService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
