import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { Idea } from '../models/idea';
import { InnovatorService } from './innovator.service';

@Injectable({
  providedIn: 'root'
})
export class IdeaService {

  private ideaServiceURL = "http://localhost:8080/idea/";

  constructor(
    private readonly http: HttpClient,
    private readonly innovatorService: InnovatorService
  ) { }

  getAllIdeas(): Observable<Idea[]> {
    return this.http.get<Idea[]>(`${this.ideaServiceURL}getAllIdeas`);
  }

  getIdea(summary: string): Observable<Idea> {
    return this.http.get<Idea>(`${this.ideaServiceURL}getIdea?summary=${summary}`);
  }

  postIdea(idea: Idea): Observable<Idea> {
    const innovator = this.innovatorService.innovator;
    if (innovator) {
      idea.innovator = innovator;
    } else {
      return of(null);
    }
    return this.http.post<Idea>(`${this.ideaServiceURL}postIdea`, idea);
  }

  deleteIdea(summary: string) {
    return this.http.delete<string>(`${this.ideaServiceURL}deleteIdea?summary=${summary}`);
  }
}
