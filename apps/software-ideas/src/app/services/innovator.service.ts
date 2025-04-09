import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Innovator } from '../models/innovator';

@Injectable({
  providedIn: 'root'
})
export class InnovatorService {

  private _serviceURL = "http://localhost:8080/innovator/";

  constructor(private readonly _http: HttpClient) { }

  getInnovator(emailAddress: string): Observable<Innovator> {
    return this._http.get<Innovator>(`${this._serviceURL}getInnovator?emailAddress=${encodeURIComponent(emailAddress)}`);
  }

  postInnovator(innovator: Innovator) {
    return this._http.post<Innovator>(`${this._serviceURL}postInnovator`, innovator);
  }
}
