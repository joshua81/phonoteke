import {Component, OnInit, Input} from '@angular/core';
import { AppComponent } from '../app.component';

@Component({
  selector: 'app-link',
  templateUrl: './link.component.html',
  styleUrls: ['./link.component.css']
})
export class LinkComponent implements OnInit {
  @Input() type: string;
  @Input() links = [];
  scrollLinks: number = 0;

  constructor(public app: AppComponent) {}

  ngOnInit() {}

  next() {
    var slider = document.querySelector('#' + this.type);
    if(slider.scrollLeft + slider.clientWidth < slider.scrollWidth) {
      slider.scrollTo(slider.scrollLeft + slider.clientWidth, 0);
    }
  }

  previous() {
    var slider = document.querySelector('#' + this.type);
    if(slider.scrollLeft > 0) {
      slider.scrollTo(slider.scrollLeft - slider.clientWidth, 0);
    }
  }

  formatDate(date: string) {
    return AppComponent.formatDate(date);
  }

  formatYear(date: string) {
    return AppComponent.formatYear(date);
  }
}
