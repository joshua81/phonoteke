import { Component, OnInit, Input } from '@angular/core';
import {DomSanitizer} from '@angular/platform-browser';
import { AppService } from '../app.service';

@Component({
  selector: 'app-youtube',
  templateUrl: './youtube.component.html',
  styleUrls: ['./youtube.component.css']
})
export class YoutubeComponent implements OnInit {
  video = null;
  @Input() doc = null;

  constructor(public service: AppService, public sanitizer: DomSanitizer) {}

  ngOnInit() {
  }

  loadVideo(track: any){
    this.video = track.youtube ? track : null;
  }

  close(event: Event){
    this.video = null;
  }

  youtubeURL(){
    return this.sanitizer.bypassSecurityTrustResourceUrl('https://www.youtube.com/embed/' + this.video.youtube + '?autoplay=1');
  }
}