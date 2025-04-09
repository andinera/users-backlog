import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Innovator } from '../models/innovator';

@Injectable({
  providedIn: 'root'
})
export class InnovatorService {

  private serviceURL = "http://localhost:8080/innovator/";

  constructor(private readonly http: HttpClient) { }

  getInnovator(emailAddress: string): Observable<Innovator> {
    return this.http.get<Innovator>(`${this.serviceURL}getInnovator?emailAddress=${encodeURIComponent(emailAddress)}`);
  }

  postInnovator(innovator: Innovator) {
    return this.http.post<Innovator>(`${this.serviceURL}postInnovator`, innovator);
  }
}
