import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { first } from 'rxjs/operators';

import { SearchService } from '../services/search.service';
import { SearchResults } from '../models/search-results.model';

@Injectable({
  providedIn: 'root'
})
export class SearchResolver implements Resolve<SearchResults[]> {

  constructor(
    private readonly _searchService: SearchService
  ) { }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<SearchResults[]> {
    return this._searchService.search(route.paramMap.get('criteria')).pipe(
      first()
    );
  }
}
