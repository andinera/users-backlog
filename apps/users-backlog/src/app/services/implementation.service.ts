import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';

import { Implementation } from '../models/implementation.model';
import { Recommendation } from '../models/recommendation.model';
import { Reply } from '../models/reply.model';
import { Service } from './service';
import { InnovatorService } from './innovator.service';
import { Router } from '@angular/router';
import { SessionStorageService } from './session-storage.service';
import { Product } from '../models/product.model';

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
    const params = new HttpParams();
    if (categoryName) {
      params.set('categoryName', categoryName);
    }
    const options = {params: params};
    return this._http.get<Implementation[]>(`${this._serviceURL}getImplementations`, options);
  }

  getImplementation(name: string): Observable<Implementation> {
    const params = new HttpParams().set('name', name);
    const options = {params: params};
    return this._http.get<Implementation>(`${this._serviceURL}getImplementation`, options);
  }

  postImplementation(implementation: Implementation): Observable<Implementation> {
    implementation.idToken = this._innovatorService.innovator$.value.idToken;
    return this._http.post<Implementation>(`${this._serviceURL}postImplementation`, implementation);
  }

  deleteImplementation(implementation: Implementation): Observable<boolean> {
    implementation.idToken = this._innovatorService.innovator$.value.idToken;
    return this._http.post<boolean>(`${this._serviceURL}deleteImplementation`, implementation);
  }

  postProduct(product: Product): Observable<Product> {
    product.idToken = this._innovatorService.innovator$.value.idToken;
    return this._http.post<Product>(`${this._serviceURL}postProduct`, product);
  }

  deleteProduct(product: Product): Observable<boolean> {
    product.idToken = this._innovatorService.innovator$.value.idToken;
    return this._http.post<boolean>(`${this._serviceURL}deleteProduct`, product);
  }

  postVote(implementation: Implementation, up: boolean): Observable<number> {
    const innovator = this._innovatorService.innovator$.value;
    if (!innovator) {
      this._sessionStorageService.storeData('postImplementationVote', {implementationId: implementation.id, up: up});
      this._router.navigate(['/log-in']);
      return throwError('Not logged in.');
    } else {
      implementation.idToken = innovator.idToken;
      const params = new HttpParams().set('up', String(up));
      const options = {params: params};
      return this._http.post<number>(`${this._serviceURL}postVote`, implementation, options);
    }
  }

  postRecommendation(recommendation: Recommendation<Implementation>): Observable<Recommendation<Implementation>> {
    const innovator = this._innovatorService.innovator$.value;
    if (!innovator) {
      this._sessionStorageService.storeData('postImplementationRecommendation', {recommendation: recommendation});
      this._router.navigate(['/log-in']);
      return throwError('Not logged in.');
    } else {
      recommendation.idToken = this._innovatorService.innovator$.value.idToken;
      return this._http.post<Recommendation<Implementation>>(`${this._serviceURL}postRecommendation`, recommendation);
    }
  }

  deleteRecommendation(recommendation: Recommendation<Implementation>): Observable<boolean> {
    recommendation.idToken = this._innovatorService.innovator$.value.idToken;
    return this._http.post<boolean>(`${this._serviceURL}deleteRecommendation`, recommendation);
  }

  postRecommendationVote(recommendation: Recommendation<Implementation>, up: boolean): Observable<number> {
    const innovator = this._innovatorService.innovator$.value;
    if (!innovator) {
      this._sessionStorageService.storeData('postImplementationRecommendationVote', {recommendationId: recommendation.id, up: up});
      this._router.navigate(['/log-in']);
      return throwError('Not logged in.');
    } else {
      recommendation.idToken = this._innovatorService.innovator$.value.idToken;
      const params = new HttpParams().set('up', String(up));
      const options = {params: params};
      return this._http.post<number>(`${this._serviceURL}postRecommendationVote`, recommendation, options);
    }
  }

  postRecommendationReply(reply: Reply<Implementation>): Observable<Reply<Implementation>> {
    const innovator = this._innovatorService.innovator$.value;
    if (!innovator) {
      this._sessionStorageService.storeData('postImplementationRecommendationReply', {reply: reply});
      this._router.navigate(['/log-in']);
      return throwError('Not logged in.');
    } else {
      reply.idToken = this._innovatorService.innovator$.value.idToken;
      return this._http.post<Reply<Implementation>>(`${this._serviceURL}postRecommendationReply`, reply);
    }
  }

  deleteRecommendationReply(reply: Reply<Implementation>): Observable<boolean> {
    reply.idToken = this._innovatorService.innovator$.value.idToken;
    return this._http.post<boolean>(`${this._serviceURL}deleteRecommendationReply`, reply);
  }
}
