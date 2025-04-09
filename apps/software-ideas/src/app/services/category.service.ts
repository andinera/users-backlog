import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Category } from '../models/category.model';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {

  private _serviceURL = "http://localhost:8080/category/"

  constructor(
      private readonly _http: HttpClient
  ) { }

  getAllCategories(): Observable<Category[]> {
      return this._http.get<Category[]>(`${this._serviceURL}getAllCategories`);
  }
}