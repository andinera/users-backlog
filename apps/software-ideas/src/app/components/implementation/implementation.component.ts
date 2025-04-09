import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Implementation } from 'src/app/models/implementation';

@Component({
  selector: 'app-implementation',
  templateUrl: './implementation.component.html'
})
export class ImplementationComponent implements OnInit {

  implementation: Implementation;

  constructor(
    private readonly route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.route.data.subscribe((data: {implementation: Implementation}) => {
      this.implementation = data.implementation;
    });
  }

}
