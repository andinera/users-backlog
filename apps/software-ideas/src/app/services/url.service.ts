import { Injectable } from '@angular/core';
import { Router, RoutesRecognized } from '@angular/router';
import { filter, pairwise } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class URLService {

  public previousURL = '';
  public currentURL = '';

  constructor(
    private readonly router: Router
  ) {
    this.router.events.pipe(
      filter((evt: any) => evt instanceof RoutesRecognized),
      pairwise())
    .subscribe((events: RoutesRecognized[]) => {
      this.previousURL = decodeURIComponent(events[0].urlAfterRedirects);
      this.currentURL = decodeURIComponent(events[0].urlAfterRedirects);
    })
  }
  
}
