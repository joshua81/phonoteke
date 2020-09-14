import {Component, OnInit, Input} from '@angular/core';
import { AppComponent } from '../app.component';

@Component({
  selector: 'app-video',
  templateUrl: './video.component.html',
  styleUrls: ['./video.component.css']
})
export class VideoComponent implements OnInit {
  @Input() type: string;
  @Input() label: string;
  @Input() tracks = [];
  scroll: number = 0;

  constructor(public app: AppComponent) {}

  ngOnInit() {}

  next() {
    this.scroll++;
    var slider = document.querySelector('#videos');
    slider.scrollTo((126 * Math.floor(slider.clientWidth / 126) * this.scroll), 0);
  }

  previous() {
    if(this.scroll > 0) {
      this.scroll--;
      var slider = document.querySelector('#videos');
      slider.scrollTo((126 * Math.floor(slider.clientWidth / 126) * this.scroll), 0);
    }
  }
}
