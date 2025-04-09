import { Injectable, EventEmitter } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, mergeMap, flatMap } from 'rxjs/operators';

import { Idea } from '../models/idea';
import { AuthenticationService } from './authentication.service';
import { Innovator } from '../models/innovator';

@Injectable({
  providedIn: 'root'
})
export class IdeaService {

  private serviceURL = "http://localhost:8080/idea/";

  constructor(
    private readonly http: HttpClient,
    private readonly authenticationService: AuthenticationService
  ) { }

  getAllIdeas(): Observable<Idea[]> {
    return this.http.get<Idea[]>(`${this.serviceURL}getAllIdeas`);
  }

  getIdea(summary: string): Observable<Idea> {
    return this.http.get<Idea>(`${this.serviceURL}getIdea?summary=${encodeURIComponent(summary)}`);
  }

  postIdea(idea: Idea): Observable<Idea> {
    return this.authenticationService.innovator.pipe(
      flatMap((innovator: Innovator) => {
        if (innovator) {
          idea.innovator = innovator;
          return this.http.post<Idea>(`${this.serviceURL}postIdea`, idea);
        } else {
          return of(null);
        }
      })
    );
  }

  deleteIdea(summary: string) {
    return this.http.delete<string>(`${this.serviceURL}deleteIdea?summary=${encodeURIComponent(summary)}`);
  }
}
