import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Implementation } from '../models/implementation.model';
import { Recommendation } from '../models/recommendation.model';
import { Reply } from '../models/reply.model';
import { Service } from './service';
import { InnovatorService } from './innovator.service';

@Injectable({
  providedIn: 'root'
})
export class ImplementationService extends Service {

  public implementationForEditing: Implementation;

  private _serviceURL = `${this.endpointURL}/implementation/`;

  constructor(
    private readonly _http: HttpClient,
    private readonly _innovatorService: InnovatorService
  ) {
    super();
  }

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
    implementation.idToken = this._innovatorService.innovator$.value.idToken;
    return this._http.post<Implementation>(`${this._serviceURL}postImplementation`, implementation);
  }

  postVote(implementationId: number, innovatorId: number, up: boolean): Observable<number> {
    const model = {idToken: this._innovatorService.innovator$.value.idToken};
    const parameters = `implementationId=${implementationId}&innovatorId=${innovatorId}&up=${up}`;
    return this._http.post<number>(`${this._serviceURL}postVote?${parameters}`, model);
  }

  postRecommendation(recommendation: Recommendation): Observable<Recommendation> {
    recommendation.idToken = this._innovatorService.innovator$.value.idToken;
    return this._http.post<Recommendation>(`${this._serviceURL}postRecommendation`, recommendation);
  }

  postRecommendationVote(recommendationId: number, innovatorId: number, up: boolean): Observable<number> {
    const model = {idToken: this._innovatorService.innovator$.value.idToken};
    const parameters = `recommendationId=${recommendationId}&innovatorId=${innovatorId}&up=${up}`;
    return this._http.post<number>(`${this._serviceURL}postRecommendationVote?${parameters}`, model);
  }

  postRecommendationReply(reply: Reply): Observable<Reply> {
    reply.idToken = this._innovatorService.innovator$.value.idToken;
    return this._http.post<Reply>(`${this._serviceURL}postRecommendationReply`, reply);
  }
}
