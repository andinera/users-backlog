import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Implementation } from '../models/implementation';

@Injectable({
  providedIn: 'root'
})
export class ImplementationService {

  private serviceURL = "http://localhost:8080/implementation/";

  constructor(
    private readonly http: HttpClient
  ) { }

  getImplementation(name: string): Observable<Implementation> {
    return this.http.get<Implementation>(`${this.serviceURL}getImplementation?name=${encodeURIComponent(name)}`);
  }

  postImplementation(implementation: Implementation): Observable<Implementation> {
    return this.http.post<Implementation>(`${this.serviceURL}postImplementation`, implementation);
  }
}
