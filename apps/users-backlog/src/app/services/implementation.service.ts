import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Implementation } from '../models/implementation.model';
import { Recommendation } from '../models/recommendation.model';
import { Reply } from '../models/reply.model';
import { Service } from './service';
import { AuthenticationService } from './authentication.service';
import { Innovator } from '../models/innovator.model';

@Injectable({
  providedIn: 'root'
})
export class ImplementationService extends Service {

  public implementationForEditing: Implementation;

  private _serviceURL = `${this.endpointURL}/implementation/`;

  constructor(
    private readonly _http: HttpClient,
    private readonly _authenticationService: AuthenticationService
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
    return this._http.post<Implementation>(`${this._serviceURL}postImplementation`, implementation);
  }

  postVote(implementationId: number, innovatorId: number, up: boolean): Observable<number> {
    const parameters = `implementationId=${implementationId}&innovatorId=${innovatorId}&up=${up}`;
    return this._authenticationService.innovator.pipe(
      flatMap((innovator: Innovator) => {
        if (innovator) {
          const headers = {
            'ID-TOKEN': innovator.idToken
          };
          return this._http.post<number>(`${this._serviceURL}postVote?${parameters}`, null, {headers});
        } else {
          return of(null);
        }
      })
    );
  }

  postRecommendation(recommendation: Recommendation): Observable<Recommendation> {
    return this._http.post<Recommendation>(`${this._serviceURL}postRecommendation`, recommendation);
  }

  postRecommendationVote(recommendationId: number, innovatorId: number, up: boolean): Observable<number> {
    const parameters = `recommendationId=${recommendationId}&innovatorId=${innovatorId}&up=${up}`;
    return this._authenticationService.innovator.pipe(
      flatMap((innovator: Innovator) => {
        if (innovator) {
          const headers = new HttpHeaders();
          headers.set('ID-TOKEN', innovator.idToken);
          return this._http.post<number>(`${this._serviceURL}postRecommendationVote?${parameters}`, headers);
        } else {
          return of(null);
        }
      })
    );
  }

  postRecommendationReply(reply: Reply): Observable<Reply> {
    return this._http.post<Reply>(`${this._serviceURL}postRecommendationReply`, reply);
  }
}
