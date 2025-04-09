import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs'

import { Service } from './service';
import { SearchResults } from '../models/search-results.model';


@Injectable({
  providedIn: 'root'
})
export class SearchService extends Service {

  private _serviceURL = `${this.endpointURL}/search/`;

  constructor(
    private readonly _http: HttpClient
  ) {
    super();
  }

  search(criteria: string): Observable<SearchResults[]> {
    const parameters = `criteria=${criteria}`
    return this._http.get<SearchResults[]>(`${this._serviceURL}search?${parameters}`);
  }
}
