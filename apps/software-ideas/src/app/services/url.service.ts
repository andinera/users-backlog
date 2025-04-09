import { Injectable } from '@angular/core';
import { Router, RoutesRecognized } from '@angular/router';
import { filter, pairwise } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class URLService {

  previousURL = '';
  currentURL = '';

  constructor(
    private readonly _router: Router
  ) {
    this._router.events.pipe(
      filter((event: any) => event instanceof RoutesRecognized),
      pairwise()
    ).subscribe((events: RoutesRecognized[]) => {
      this.previousURL = decodeURIComponent(events[0].urlAfterRedirects);
      this.currentURL = decodeURIComponent(events[1].urlAfterRedirects);
    })
  }
  
}
