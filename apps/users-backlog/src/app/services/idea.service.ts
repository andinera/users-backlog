import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';

import { Idea } from '../models/idea.model';
import { Service } from './service';
import { InnovatorService } from './innovator.service';
import { SessionStorageService } from './session-storage.service';
import { Router } from '@angular/router';
import { Reply } from '../models/reply.model';
import { Recommendation } from '../models/recommendation.model';

@Injectable({
  providedIn: 'root'
})
export class IdeaService extends Service {

  public ideaForEditing: Idea;

  private _serviceURL = `${this.endpointURL}/idea/`;

  constructor(
    private readonly _http: HttpClient,
    private readonly _innovatorService: InnovatorService,
    private readonly _router: Router,
    private readonly _sessionStorageService: SessionStorageService
  ) {
    super();
  }

  getIdeas(categoryName?: string): Observable<Idea[]> {
    const params = new HttpParams();
    if (categoryName) {
      params.set('categoryName', categoryName);
    }
    const options = {params: params};
    return this._http.get<Idea[]>(`${this._serviceURL}getIdeas`, options);
  }

  getIdea(summary: string): Observable<Idea> {
    const params = new HttpParams().set('summary', summary);
    const options = {params: params};
    return this._http.get<Idea>(`${this._serviceURL}getIdea`, options);
  }

  postIdea(idea: Idea): Observable<Idea> {
    idea.idToken = this._innovatorService.innovator$.value.idToken;
    idea.innovator = this._innovatorService.innovator$.value;
    return this._http.post<Idea>(`${this._serviceURL}postIdea`, idea);
  }

  deleteIdea(idea: Idea) {
    idea.idToken = this._innovatorService.innovator$.value.idToken;
    return this._http.post<boolean>(`${this._serviceURL}deleteIdea`, idea);
  }

  postVote(idea: Idea, up: boolean): Observable<number> {
    const innovator = this._innovatorService.innovator$.value;
    if (!innovator) {
      this._sessionStorageService.storeData('postIdeaVote', {ideaId: idea.id, up: up});
      this._router.navigate(['/log-in']);
      return throwError('Not logged in.');
    } else {
      idea.idToken = innovator.idToken;
      const params = new HttpParams().set('up', String(up));
      const options = {params: params};
      return this._http.post<number>(`${this._serviceURL}postIdeaVote`, idea, options);
    }
  }

  postRecommendation(recommendation: Recommendation<Idea>): Observable<Recommendation<Idea>> {
    const innovator = this._innovatorService.innovator$.value;
    if (!innovator) {
      this._sessionStorageService.storeData('postIdeaRecommendation', {recommendation: recommendation});
      this._router.navigate(['/log-in']);
      return throwError('Not logged in.');
    } else {
      recommendation.idToken = this._innovatorService.innovator$.value.idToken;
      return this._http.post<Recommendation<Idea>>(`${this._serviceURL}postRecommendation`, recommendation);
    }
  }

  deleteRecommendation(recommendation: Recommendation<Idea>): Observable<boolean> {
    recommendation.idToken = this._innovatorService.innovator$.value.idToken;
    return this._http.post<boolean>(`${this._serviceURL}deleteRecommendation`, recommendation);
  }

  postRecommendationVote(recommendation: Recommendation<Idea>, up: boolean): Observable<number> {
    const innovator = this._innovatorService.innovator$.value;
    if (!innovator) {
      this._sessionStorageService.storeData('postIdeaRecommendationVote', {recommendationId: recommendation.id, up: up});
      this._router.navigate(['/log-in']);
      return throwError('Not logged in.');
    } else {
      recommendation.idToken = this._innovatorService.innovator$.value.idToken;
      const params = new HttpParams().set('up', String(up));
      const options = {params: params};
      return this._http.post<number>(`${this._serviceURL}postRecommendationVote`, recommendation, options);
    }
  }

  postRecommendationReply(reply: Reply<Idea>): Observable<Reply<Idea>> {
    const innovator = this._innovatorService.innovator$.value;
    if (!innovator) {
      this._sessionStorageService.storeData('postIdeaRecommendationReply', {reply: reply});
      this._router.navigate(['/log-in']);
      return throwError('Not logged in.');
    } else {
      reply.idToken = this._innovatorService.innovator$.value.idToken;
      return this._http.post<Reply<Idea>>(`${this._serviceURL}postRecommendationReply`, reply);
    }
  }

  deleteRecommendationReply(reply: Reply<Idea>): Observable<boolean> {
    reply.idToken = this._innovatorService.innovator$.value.idToken;
    return this._http.post<boolean>(`${this._serviceURL}deleteRecommendationReply`, reply);
  }
}
