import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs'

import { Service } from './service';
import { SearchResults } from '../models/search-results.model';
import { Idea } from '../models/idea.model';


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
    const params = new HttpParams().set('criteria', criteria);
    const options = {params: params};
    return this._http.get<SearchResults[]>(`${this._serviceURL}search`, options);
  }

  searchForIdeas(partialSummary: string): Observable<Idea[]> {
    const params = new HttpParams().set('partialSummary', partialSummary);
    const options = {params: params};
    return this._http.get<Idea[]>(`${this._serviceURL}searchForIdeas`, options);
  }
}
