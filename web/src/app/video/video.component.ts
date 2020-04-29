import {Component, OnInit, Input} from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { DocComponent } from '../doc/doc.component';

@Component({
  selector: 'app-video',
  templateUrl: './video.component.html',
  styleUrls: ['./video.component.css']
})
export class VideoComponent implements OnInit {
  @Input() type: string;
  @Input() label: string;
  @Input() tracks = [];
  video = null;
  scroll: number = 0;

  constructor(public component: DocComponent, public sanitizer: DomSanitizer) {}

  ngOnInit() {}

  youtubeURL(){
    return this.sanitizer.bypassSecurityTrustResourceUrl('https://www.youtube.com/embed/' + this.video.youtube + '?autoplay=1');
  }

  loadVideo(track: any){
    this.video = track.youtube ? track : null;
  }

  close(event: Event){
    this.video = null;
  }

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
