import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { Innovator } from 'src/app/models/innovator';

@Component({
  selector: 'app-innovator',
  templateUrl: './innovator.component.html'
})
export class InnovatorComponent implements OnInit {

  innovator: Innovator;

  constructor(
    private readonly router: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.router.data.subscribe((data: {innovator: Innovator}) => {
      this.innovator = data.innovator;
    })
  }

}
