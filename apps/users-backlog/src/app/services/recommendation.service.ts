import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Recommendation } from '../models/recommendation.model';
import { Service } from './service';
import { InnovatorService } from './innovator.service';

@Injectable({
  providedIn: 'root'
})
export class RecommendationService extends Service {

  private _serviceURL = `${this.endpointURL}/recommendation/`;

  constructor(
    private readonly _http: HttpClient,
    private readonly _innovatorService: InnovatorService
  ) {
    super();
  }

  postRecommendation(recommendation: Recommendation): Observable<Recommendation> {
    recommendation.idToken = this._innovatorService.innovator$.value.idToken;
    recommendation.innovator = this._innovatorService.innovator$.value;
    return this._http.post<Recommendation>(`${this._serviceURL}postRecommendation`, recommendation);
  }
}
