import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class Service {

  private _endpointURL = "https://service-dot-users-backlog.uc.r.appspot.com/";

  constructor() { }

  public get endpointURL(): string {
    return this._endpointURL;
  }
}
