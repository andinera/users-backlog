import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { Implementation } from '../models/implementation.model';
import { Recommendation } from '../models/recommendation.model';
import { Reply } from '../models/reply.model';
import { Service } from './service';
import { InnovatorService } from './innovator.service';
import { Router } from '@angular/router';
import { SessionStorageService } from './session-storage.service';
import { Product } from '../models/product.model';
import { Idea } from '../models/idea.model';

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

  getImplementation(id: number): Observable<Implementation> {
    const params = new HttpParams().set('id', String(id));
    const options = {params: params};
    return this._http.get<Implementation>(`${this._serviceURL}getImplementation`, options);
  }

  postImplementation(implementation: Implementation): Observable<Implementation> {
    return this._innovatorService.getIdToken().pipe(
      mergeMap((idToken: string) => {
        implementation.idToken = idToken;
        return this._http.post<Implementation>(`${this._serviceURL}postImplementation`, implementation);
      })
    );
  }

  deleteImplementation(implementation: Implementation): Observable<boolean> {
    return this._innovatorService.getIdToken().pipe(
      mergeMap((idToken: string) => {
        implementation.idToken = idToken;
        return this._http.post<boolean>(`${this._serviceURL}deleteImplementation`, implementation);
      })
    );
  }

  postProduct(product: Product): Observable<Product> {
    return this._innovatorService.getIdToken().pipe(
      mergeMap((idToken: string) => {
        product.idToken = idToken;
        return this._http.post<Product>(`${this._serviceURL}postProduct`, product);
      })
    );
  }

  deleteProduct(product: Product): Observable<boolean> {
    return this._innovatorService.getIdToken().pipe(
      mergeMap((idToken: string) => {
        product.idToken = idToken;
        return this._http.post<boolean>(`${this._serviceURL}deleteProduct`, product);
      })
    );
  }

  associateWithIdea(implementation: Implementation, idea: Idea): Observable<boolean> {
    return this._innovatorService.getIdToken().pipe(
      mergeMap((idToken: string) => {
        implementation.idToken = idToken;
        const params = new HttpParams().set('ideaId', String(idea.id));
        const options = {params: params};
        return this._http.post<boolean>(`${this._serviceURL}associateWithIdea`, implementation, options);
      })
    );
  }

  disassociateWithIdea(implementation: Implementation, idea: Idea): Observable<boolean> {
    return this._innovatorService.getIdToken().pipe(
      mergeMap((idToken: string) => {
        implementation.idToken = idToken;
        const params = new HttpParams().set('ideaId', String(idea.id));
        const options = {params: params};
        return this._http.post<boolean>(`${this._serviceURL}disassociateWithIdea`, implementation, options);
      })
    );
  }

  getInnovatorVotes(implementationId: number): Observable<any> {
    const params = new HttpParams()
      .set('implementationId', String(implementationId))
      .set('innovatorId', String(this._innovatorService.innovator$.value.id));
    const options = {params: params};
    return this._http.get<any>(`${this._serviceURL}getInnovatorVotes`, options);
  }

  postVote(implementation: Implementation, up: boolean): Observable<number> {
    if (!this._innovatorService.innovator$.value) {
      this._sessionStorageService.storeData('postImplementationVote', {implementationId: implementation.id, up: up});
      this._router.navigate(['/log-in']);
      return throwError('Not logged in.');
    } else {
      return this._innovatorService.getIdToken().pipe(
        mergeMap((idToken: string) => {
          implementation.idToken = idToken;
          const params = new HttpParams().set('up', String(up));
          const options = {params: params};
          return this._http.post<number>(`${this._serviceURL}postVote`, implementation, options);
        })
      );
    }
  }

  postRecommendation(recommendation: Recommendation<Implementation>): Observable<Recommendation<Implementation>> {
    if (!this._innovatorService.innovator$.value) {
      this._sessionStorageService.storeData('postImplementationRecommendation', {recommendation: recommendation});
      this._router.navigate(['/log-in']);
      return throwError('Not logged in.');
    } else {
      return this._innovatorService.getIdToken().pipe(
        mergeMap((idToken: string) => {
          recommendation.idToken = idToken;
          return this._http.post<Recommendation<Implementation>>(`${this._serviceURL}postRecommendation`, recommendation);
        })
      );
    }
  }

  deleteRecommendation(recommendation: Recommendation<Implementation>): Observable<boolean> {
    return this._innovatorService.getIdToken().pipe(
      mergeMap((idToken: string) => {
        recommendation.idToken = idToken;
        return this._http.post<boolean>(`${this._serviceURL}deleteRecommendation`, recommendation);
      })
    );
  }

  postRecommendationVote(recommendation: Recommendation<Implementation>, up: boolean): Observable<number> {
    if (!this._innovatorService.innovator$.value) {
      this._sessionStorageService.storeData('postImplementationRecommendationVote', {recommendationId: recommendation.id, up: up});
      this._router.navigate(['/log-in']);
      return throwError('Not logged in.');
    } else {
      return this._innovatorService.getIdToken().pipe(
        mergeMap((idToken: string) => {
          recommendation.idToken = idToken;
          const params = new HttpParams().set('up', String(up));
          const options = {params: params};
          return this._http.post<number>(`${this._serviceURL}postRecommendationVote`, recommendation, options);
        })
      );
    }
  }

  postRecommendationReply(reply: Reply<Implementation>): Observable<Reply<Implementation>> {
    if (!this._innovatorService.innovator$.value) {
      this._sessionStorageService.storeData('postImplementationRecommendationReply', {reply: reply});
      this._router.navigate(['/log-in']);
      return throwError('Not logged in.');
    } else {
      return this._innovatorService.getIdToken().pipe(
        mergeMap((idToken: string) => {
          reply.idToken = idToken;
          return this._http.post<Reply<Implementation>>(`${this._serviceURL}postRecommendationReply`, reply);
        })
      );
    }
  }

  deleteRecommendationReply(reply: Reply<Implementation>): Observable<boolean> {
    return this._innovatorService.getIdToken().pipe(
      mergeMap((idToken: string) => {
        reply.idToken = idToken;
        return this._http.post<boolean>(`${this._serviceURL}deleteRecommendationReply`, reply);
      })
    );
  }
}
