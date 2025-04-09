import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Idea } from '../models/idea.model';
import { Service } from './service';
import { InnovatorService } from './innovator.service';

@Injectable({
  providedIn: 'root'
})
export class IdeaService extends Service {

  private _serviceURL = `${this.endpointURL}/idea/`;

  constructor(
    private readonly _http: HttpClient,
    private readonly _innovatorService: InnovatorService
  ) {
    super();
  }

  getIdeas(categoryName: string): Observable<Idea[]> {
    return this._http.get<Idea[]>(`${this._serviceURL}getIdeas?categoryName=${categoryName}`);
  }

  getIdea(summary: string): Observable<Idea> {
    return this._http.get<Idea>(`${this._serviceURL}getIdea?summary=${encodeURIComponent(summary)}`);
  }

  postIdea(idea: Idea): Observable<Idea> {
    const headers = {'ID-TOKEN': this._innovatorService.innovator$.value.idToken};
    const innovator = this._innovatorService.innovator$.value;
    idea.innovator = innovator;
    return this._http.post<Idea>(`${this._serviceURL}postIdea`, idea, {headers});
  }

  deleteIdea(id: number) {
    const headers = {'ID-TOKEN': this._innovatorService.innovator$.value.idToken};
    return this._http.delete<string>(`${this._serviceURL}deleteIdea?id=${encodeURIComponent(id)}`, {headers});
  }
}
