import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Category } from '../models/category.model';
import { Service } from './service';

@Injectable({
  providedIn: 'root'
})
export class CategoryService extends Service {

  private _serviceURL = `${this.endpointURL}/category/`;

  constructor(
      private readonly _http: HttpClient
  ) {
    super();
  }

  getAllCategories(): Observable<Category[]> {
      return this._http.get<Category[]>(`${this._serviceURL}getAllCategories`);
  }
}