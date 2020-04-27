import {Component, OnInit, Input} from '@angular/core';
import { DocsComponent } from '../docs/docs.component';

@Component({
  selector: 'app-section',
  templateUrl: './section.component.html',
  styleUrls: ['./section.component.css']
})
export class SectionComponent implements OnInit {
  @Input() type: string = null;
  @Input() label: string = null;
  @Input() docs = [];
  error = null;
  page: number = 0;
  scroll: number = 0;

  constructor(public component: DocsComponent) {}

  ngOnInit() {
  }

  scrollDocs() {
    this.page++;
    this.component.loadDocs(this.type, this.page);
  }

  next() {
    this.scroll++;
    var slider = document.querySelector('#'+this.type);
    slider.scrollTo((206 * Math.floor(slider.clientWidth / 206) * this.scroll), 0);
  }

  previous() {
    if(this.scroll > 0) {
      this.scroll--;
      var slider = document.querySelector('#'+this.type);
      slider.scrollTo((206 * Math.floor(slider.clientWidth / 206) * this.scroll), 0);
    }
  }

  showScroll() {
    // s: 540px, m: 720px, l: 960px, xl: 1140px
    return window.innerWidth >= 960;
  }
}
