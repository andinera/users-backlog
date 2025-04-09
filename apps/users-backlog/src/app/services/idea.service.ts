import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Idea } from '../models/idea.model';
import { AuthenticationService } from './authentication.service';
import { Innovator } from '../models/innovator.model';

@Injectable({
  providedIn: 'root'
})
export class IdeaService {

  private _serviceURL = "http://localhost:8080/idea/";

  constructor(
    private readonly _http: HttpClient,
    private readonly _authenticationService: AuthenticationService
  ) { }

  getIdeas(categoryName: string): Observable<Idea[]> {
    return this._http.get<Idea[]>(`${this._serviceURL}getIdeas?categoryName=${categoryName}`);
  }

  getIdea(summary: string): Observable<Idea> {
    return this._http.get<Idea>(`${this._serviceURL}getIdea?summary=${encodeURIComponent(summary)}`);
  }

  postIdea(idea: Idea): Observable<Idea> {
    return this._authenticationService.innovator.pipe(
      flatMap((innovator: Innovator) => {
        if (innovator) {
          idea.innovator = innovator;
          return this._http.post<Idea>(`${this._serviceURL}postIdea`, idea);
        } else {
          return of(null);
        }
      })
    );
  }

  deleteIdea(id: number) {
    return this._http.delete<string>(`${this._serviceURL}deleteIdea?id=${encodeURIComponent(id)}`);
  }
}
