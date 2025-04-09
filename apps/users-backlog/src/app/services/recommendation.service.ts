import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { AuthenticationService } from './authentication.service';
import { Innovator } from '../models/innovator.model';
import { Recommendation } from '../models/recommendation.model';
import { Service } from './service';

@Injectable({
  providedIn: 'root'
})
export class RecommendationService extends Service {

  private _serviceURL = `${this.endpointURL}/recommendation/`;

  constructor(
    private readonly _http: HttpClient,
    private readonly _authenticationService: AuthenticationService
  ) {
    super();
  }

  postRecommendation(recommendation: Recommendation): Observable<Recommendation> {
    return this._authenticationService.innovator.pipe(
      flatMap((innovator: Innovator) => {
        if (innovator) {
          recommendation.innovator = innovator;
          return this._http.post<Recommendation>(`${this._serviceURL}postRecommendation`, recommendation);
        } else {
          return of(null);
        }
      })
    );
  }
}
