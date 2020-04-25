import {Component, OnInit, Input} from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'app-link',
  templateUrl: './link.component.html',
  styleUrls: ['./link.component.css']
})
export class LinkComponent implements OnInit {
  @Input() name: string;
  @Input() links = [];
  scrollLinks: number = 0;

  constructor(public sanitizer: DomSanitizer) {}

  ngOnInit() {}

  next() {
    this.scrollLinks++;
    var slider = document.querySelector('#' + this.name);
    slider.scrollTo((126 * Math.floor(slider.clientWidth / 126) * this.scrollLinks), 0);
  }

  previous() {
    if(this.scrollLinks > 0) {
      this.scrollLinks--;
      var slider = document.querySelector('#' + this.name);
      slider.scrollTo((126 * Math.floor(slider.clientWidth / 126) * this.scrollLinks), 0);
    }
  }

  showScroll() {
    // s: 540px, m: 720px, l: 960px, xl: 1140px
    return window.innerWidth >= 960;
  }
}
