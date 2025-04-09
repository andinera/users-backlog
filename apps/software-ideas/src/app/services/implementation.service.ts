import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Implementation } from '../models/implementation.model';

@Injectable({
  providedIn: 'root'
})
export class ImplementationService {

  private _serviceURL = "http://localhost:8080/implementation/";

  constructor(
    private readonly _http: HttpClient
  ) { }

  getImplementations(categoryName: string): Observable<Implementation[]> {
    return this._http.get<Implementation[]>(`${this._serviceURL}getImplementations?categoryName=${categoryName}`);
  }

  getImplementation(name: string): Observable<Implementation> {
    return this._http.get<Implementation>(`${this._serviceURL}getImplementation?name=${encodeURIComponent(name)}`);
  }

  postImplementation(implementation: Implementation): Observable<Implementation> {
    return this._http.post<Implementation>(`${this._serviceURL}postImplementation`, implementation);
  }
}
