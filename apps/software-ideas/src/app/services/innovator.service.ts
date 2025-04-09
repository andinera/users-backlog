import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import{ tap } from 'rxjs/operators';

import { Innovator } from '../models/innovator';

@Injectable({
  providedIn: 'root'
})
export class InnovatorService {

  public redirectUrl = '';
  public innovator: Innovator;

  private ideaServiceURL = "http://localhost:8080/innovator/";

  constructor(private readonly http: HttpClient) { }

  getInnovator(innovator: Innovator): Observable<Innovator> {
    if (this.innovator) {
      return of(this.innovator);
    } else {
      return this.http.get<Innovator>(`${this.ideaServiceURL}getInnovator?emailAddress=${encodeURIComponent(innovator.emailAddress)}`).pipe(
        tap((innovator: Innovator) => {
          this.innovator = innovator;
        })
      )
    }
  }

  postInnovator(innovator: Innovator) {
    return this.http.post<Innovator>(`${this.ideaServiceURL}postInnovator`, innovator);
  }
}
