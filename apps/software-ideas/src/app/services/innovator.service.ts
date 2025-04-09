import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import{ tap } from 'rxjs/operators';

import { Innovator } from '../models/innovator';

@Injectable({
  providedIn: 'root'
})
export class InnovatorService {

  public innovator: Innovator;

  private serviceURL = "http://localhost:8080/innovator/";

  constructor(private readonly http: HttpClient) { }

  getInnovator(emailAddress: string): Observable<Innovator> {
    if (this.innovator) {
      return of(this.innovator);
    } else {
      return this.http.get<Innovator>(`${this.serviceURL}getInnovator?emailAddress=${encodeURIComponent(emailAddress)}`).pipe(
        tap((innovator: Innovator) => {
          this.innovator = innovator;
        })
      )
    }
  }

  postInnovator(innovator: Innovator) {
    return this.http.post<Innovator>(`${this.serviceURL}postInnovator`, innovator);
  }
}
