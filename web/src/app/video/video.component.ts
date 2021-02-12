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

  constructor(public app: AppComponent) {}

  ngOnInit() {}

  next() {
    var slider = document.querySelector('#videos');
    if(slider.scrollLeft + slider.clientWidth < slider.scrollWidth) {
      slider.scrollTo(slider.scrollLeft + slider.clientWidth, 0);
    }
  }

  previous() {
    var slider = document.querySelector('#videos');
    if(slider.scrollLeft > 0) {
      slider.scrollTo(slider.scrollLeft - slider.clientWidth, 0);
    }
  }
}
