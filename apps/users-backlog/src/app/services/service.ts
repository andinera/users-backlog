import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class Service {

  private _endpointURL = environment.serviceEndpoint;

  constructor() { }

  public get endpointURL(): string {
    return this._endpointURL;
  }
}
