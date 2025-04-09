import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class Service {

  // private _endpointURL = "https://service-dot-use rs-backlog.uc.r.appspot.com";
  private _endpointURL = "http://localhost:8080";

  constructor() { }

  public get endpointURL(): string {
    return this._endpointURL;
  }
}
