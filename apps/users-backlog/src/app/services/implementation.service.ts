import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Implementation } from '../models/implementation.model';
import { Recommendation } from '../models/recommendation.model';
import { Reply } from '../models/reply.model';

@Injectable({
  providedIn: 'root'
})
export class ImplementationService {

  public implementationForEditing: Implementation;

  private _serviceURL = "http://localhost:8080/implementation/";

  constructor(
    private readonly _http: HttpClient
  ) { }

  getImplementations(categoryName?: string): Observable<Implementation[]> {
    let urlParameters = '';
    if (categoryName) {
      urlParameters += `categoryName=${categoryName}`;
    }  
    return this._http.get<Implementation[]>(`${this._serviceURL}getImplementations?${urlParameters}`);
  }

  getImplementation(name: string): Observable<Implementation> {
    return this._http.get<Implementation>(`${this._serviceURL}getImplementation?name=${encodeURIComponent(name)}`);
  }

  postImplementation(implementation: Implementation): Observable<Implementation> {
    return this._http.post<Implementation>(`${this._serviceURL}postImplementation`, implementation);
  }

  postVote(implementationId: number, innovatorId: number, up: boolean): Observable<number> {
    const parameters = `implementationId=${implementationId}&innovatorId=${innovatorId}&up=${up}`;
    return this._http.post<number>(`${this._serviceURL}postVote?${parameters}`, null);
  }

  postRecommendation(recommendation: Recommendation): Observable<Recommendation> {
    return this._http.post<Recommendation>(`${this._serviceURL}postRecommendation`, recommendation);
  }

  postRecommendationVote(recommendationId: number, innovatorId: number, up: boolean): Observable<number> {
    const parameters = `recommendationId=${recommendationId}&innovatorId=${innovatorId}&up=${up}`;
    return this._http.post<number>(`${this._serviceURL}postRecommendationVote?${parameters}`, null);
  }

  postRecommendationReply(reply: Reply): Observable<Reply> {
    return this._http.post<Reply>(`${this._serviceURL}postRecommendationReply`, reply);
  }
}
