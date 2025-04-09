import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { Innovator } from '../models/innovator.model';

@Injectable({
  providedIn: 'root'
})
export class InnovatorService {

  private _serviceURL = "http://localhost:8080/innovator/";

  constructor(private readonly _http: HttpClient) { }

  getInnovator(id?: string, emailAddress?: string): Observable<Innovator> {
    let parameters: string;
    if (id) {
      parameters = `getInnovator?id=${encodeURIComponent(id)}`;
    } else if (emailAddress) {
      parameters = `getInnovator?emailAddress=${encodeURIComponent(emailAddress)}`;
    } else {
      return of(null);
    }
    return this._http.get<Innovator>(`${this._serviceURL}${parameters}`);
  }

  postInnovator(innovator: Innovator) {
    return this._http.post<Innovator>(`${this._serviceURL}postInnovator`, innovator);
  }
}
