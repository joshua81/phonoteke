import {Component, OnInit, Input} from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { DocComponent } from '../doc/doc.component';

@Component({
  selector: 'app-link',
  templateUrl: './link.component.html',
  styleUrls: ['./link.component.css']
})
export class LinkComponent implements OnInit {
  @Input() type: string;
  @Input() label: string;
  @Input() links = [];
  scrollLinks: number = 0;

  constructor(public component: DocComponent, public sanitizer: DomSanitizer) {}

  ngOnInit() {}

  next() {
    this.scrollLinks++;
    var slider = document.querySelector('#' + this.type);
    slider.scrollTo((126 * Math.floor(slider.clientWidth / 126) * this.scrollLinks), 0);
  }

  previous() {
    if(this.scrollLinks > 0) {
      this.scrollLinks--;
      var slider = document.querySelector('#' + this.type);
      slider.scrollTo((126 * Math.floor(slider.clientWidth / 126) * this.scrollLinks), 0);
    }
  }
}
