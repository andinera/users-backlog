import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';

import { Implementation } from '../models/implementation.model';
import { Recommendation } from '../models/recommendation.model';
import { Reply } from '../models/reply.model';
import { Service } from './service';
import { InnovatorService } from './innovator.service';
import { Router } from '@angular/router';
import { SessionStorageService } from './session-storage.service';

@Injectable({
  providedIn: 'root'
})
export class ImplementationService extends Service {

  public implementationForEditing: Implementation;

  private _serviceURL = `${this.endpointURL}/implementation/`;

  constructor(
    private readonly _http: HttpClient,
    private readonly _innovatorService: InnovatorService,
    private readonly _router: Router,
    private readonly _sessionStorageService: SessionStorageService
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

  postVote(implementationId: number, up: boolean): Observable<number> {
    const innovator = this._innovatorService.innovator$.value;
    if (!innovator) {
      this._sessionStorageService.storeData('postVote', {implementationId: implementationId, up: up});
      this._router.navigate(['/log-in']);
      return throwError('Not logged in.');
    } else {
      const model = {idToken: innovator.idToken};
      const parameters = `implementationId=${implementationId}&up=${up}`;
      return this._http.post<number>(`${this._serviceURL}postVote?${parameters}`, model);
    }
  }

  postRecommendation(recommendation: Recommendation): Observable<Recommendation> {
    const innovator = this._innovatorService.innovator$.value;
    if (!innovator) {
      this._sessionStorageService.storeData('postRecommendation', {recommendation: recommendation});
      this._router.navigate(['/log-in']);
      return throwError('Not logged in.');
    } else {
      recommendation.idToken = this._innovatorService.innovator$.value.idToken;
      return this._http.post<Recommendation>(`${this._serviceURL}postRecommendation`, recommendation);
    }
  }

  postRecommendationVote(recommendationId: number, up: boolean): Observable<number> {
    const innovator = this._innovatorService.innovator$.value;
    if (!innovator) {
      this._sessionStorageService.storeData('postRecommendationVote', {recommendationId: recommendationId, up: up});
      this._router.navigate(['/log-in']);
      return throwError('Not logged in.');
    } else {
      const model = {idToken: this._innovatorService.innovator$.value.idToken};
      const parameters = `recommendationId=${recommendationId}&up=${up}`;
      return this._http.post<number>(`${this._serviceURL}postRecommendationVote?${parameters}`, model);
    }
  }

  postRecommendationReply(reply: Reply): Observable<Reply> {
    const innovator = this._innovatorService.innovator$.value;
    if (!innovator) {
      this._sessionStorageService.storeData('postRecommendationReply', {reply: reply});
      this._router.navigate(['/log-in']);
      return throwError('Not logged in.');
    } else {
      reply.idToken = this._innovatorService.innovator$.value.idToken;
      return this._http.post<Reply>(`${this._serviceURL}postRecommendationReply`, reply);
    }
  }
}
