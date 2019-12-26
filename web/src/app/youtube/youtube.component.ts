import { Component, OnInit } from '@angular/core';
import {AppService} from '../app.service';

@Component({
  selector: 'app-youtube',
  templateUrl: './youtube.component.html',
  styleUrls: ['./youtube.component.css']
})
export class YoutubeComponent implements OnInit {
  player = null;

  constructor(public service: AppService) {}

  ngOnInit() {
    const tag = document.createElement('script');
    tag.src = 'https://www.youtube.com/iframe_api';
    document.body.appendChild(tag);
  }

  onReady(event){
    console.log(event);
    this.player = event.target;
    this.player.playVideo();
  }

  onStateChange(event){
    console.log(event);
    switch(event.data) {
      case 0:
        console.log('video ended');
        break;
      case 1:
        console.log('video playing');
        break;
      case 2:
        console.log('video paused');
    }
  }

  play(event: Event){
    this.player.playVideo();
  }

  pause(event: Event){
    this.player.pauseVideo();
  }

  close(event: Event){
    this.player.pauseVideo();
  }

  next(event: Event){
    this.service.video = null;
  }

  previous(event: Event){

  }
}