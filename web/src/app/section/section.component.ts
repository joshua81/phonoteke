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
  scroll: number = 0;

  constructor(public component: DocsComponent) {}

  ngOnInit() {
  }

  scrollDocs() {
    this.component.scrollDocs(this.type);
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
}
