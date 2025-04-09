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
    const headers = {'ID-TOKEN': this._innovatorService.innovator$.value.idToken};
    const innovator = this._innovatorService.innovator$.value;
    recommendation.innovator = innovator;
    return this._http.post<Recommendation>(`${this._serviceURL}postRecommendation`, recommendation, {headers});
  }
}
