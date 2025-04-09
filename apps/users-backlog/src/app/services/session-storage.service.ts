import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class SessionStorageService {

  private readonly _key = 'userbacklog-dot-com';

  constructor() {}

  public storeData(keySuffix, data): void {
    sessionStorage.removeItem(this._key);
    sessionStorage.setItem(`${this._key}_${keySuffix}`, JSON.stringify(data));
  }

  public retrieveData(keySuffix): any {
    const data = JSON.parse(sessionStorage.getItem(`${this._key}_${keySuffix}`));
    sessionStorage.removeItem(`${this._key}_${keySuffix}`);
    return data;
  }
  
}
