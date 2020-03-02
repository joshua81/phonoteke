import { Component, OnInit, Input } from '@angular/core';
import { AppService } from '../app.service';

@Component({
  selector: 'app-youtube',
  templateUrl: './youtube.component.html',
  styleUrls: ['./youtube.component.css']
})
export class YoutubeComponent implements OnInit {
  player = null;
  status = null;
  video = null;
  @Input() tracks = null;

  constructor(public service: AppService) {}

  ngOnInit() {
    const tag = document.createElement('script');
    tag.src = 'https://www.youtube.com/iframe_api';
    document.body.appendChild(tag);
  }

  onReady(event: any){
    this.player = event.target;
    this.player.playVideo();
  }

  onStateChange(event: any){
    this.status = event.data;
    switch(this.status) {
      case 0:
        console.log('video ended');
        this.forward(null);
        break;
      case 1:
        console.log('video playing');
        break;
      case 2:
        console.log('video paused');
        break;
      case 5:
        console.log('video cued');
        this.player.playVideo();
        break;
    }
  }

  loadVideo(track: any){
    this.video = track;
  }

  play(event: Event){
    this.player.playVideo();
  }

  pause(event: Event){
    this.player.pauseVideo();
  }

  playPause(event: Event){
    if(this.status == 1)
    {
      this.player.pauseVideo();
    }
    else
    {
      this.player.playVideo();
    }
  }

  forward(event: Event){
    if(this.tracks.indexOf(this.video) < this.tracks.length-1)
    {
      this.video = this.tracks[this.tracks.indexOf(this.video)+1];
    }
  }

  backward(event: Event){
    if(this.tracks.indexOf(this.video) > 0)
    {
      this.video = this.tracks[this.tracks.indexOf(this.video)-1];
    }
  }

  close(event: Event){
    this.video = null;
  }
}