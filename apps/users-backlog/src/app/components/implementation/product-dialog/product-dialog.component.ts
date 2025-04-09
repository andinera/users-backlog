import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-product-dialog',
  templateUrl: './product-dialog.component.html',
  styleUrls: ['./product-dialog.component.css']
})
export class ProductDialogComponent {

  public url: string;

  constructor(@Inject(MAT_DIALOG_DATA) public data: any) {
    this.url = data.url;
  }

  public onContinue() {
    try {
      const parsedURL = new URL(this.url);
      window.open(parsedURL.href, '_blank');
    } catch (error) {
      console.error(error);
    }
  }

}
