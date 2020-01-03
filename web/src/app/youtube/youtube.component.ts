import { Component, OnInit, Input } from '@angular/core';
import {DocComponent} from '../doc/doc.component';

@Component({
  selector: 'app-youtube',
  templateUrl: './youtube.component.html',
  styleUrls: ['./youtube.component.css']
})
export class YoutubeComponent implements OnInit {
  player = null;
  status = null;
  video = null;
  videos = [];
  private _tracks = [];

  get tracks(): any {
    return this._tracks;
  }
  
  @Input()
  set tracks(val: any) {
    this._tracks = val;
    this.videos = [];
    if(this.tracks) {
      this.tracks.forEach(function(track: any) 
			{
        if(track.youtube)
        {
          this.videos.push(track);
        }
      }, this);
    }
    this.video = this.videos.length > 0 ? this.videos[0] : null;
  }

  constructor(public component: DocComponent) {}

  ngOnInit() {
    const tag = document.createElement('script');
    tag.src = 'https://www.youtube.com/iframe_api';
    document.body.appendChild(tag);
  }

  onReady(event: any){
    this.player = event.target;
    //this.player.playVideo();
  }

  onStateChange(event: any){
    this.status = event.data;
    switch(this.status) {
      case 0:
        console.log('video ended');
        this.next(null);
        break;
      case 1:
        console.log('video playing');
        break;
      case 2:
        console.log('video paused');
        break;
      case 5:
        console.log('video cued');
        this.play(null);
        break;
    }
  }

  loadVideo(video: any){
    this.video = video;
  }

  play(event: Event){
    this.player.playVideo();
  }

  pause(event: Event){
    this.player.pauseVideo();
  }

  next(event: Event){
    if(this.videos.indexOf(this.video) < this.videos.length-1)
    {
      this.video = this.videos[this.videos.indexOf(this.video)+1];
    }
  }

  previous(event: Event){
    if(this.videos.indexOf(this.video) > 0)
    {
      this.video = this.videos[this.videos.indexOf(this.video)-1];
    }
  }
}