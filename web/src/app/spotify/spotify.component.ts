import { Component, OnInit, Input } from '@angular/core';
import {DomSanitizer} from '@angular/platform-browser';
import { AppService } from '../app.service';

@Component({
  selector: 'app-spotify',
  templateUrl: './spotify.component.html',
  styleUrls: ['./spotify.component.css']
})
export class SpotifyComponent implements OnInit {
  song = null;
  @Input() tracks = null;

  constructor(public service: AppService, public sanitizer: DomSanitizer) {}

  ngOnInit() {
  }

  loadTrack(track: any){
    this.song = track.spotify ? track : null;
  }

  forward(event: Event){
    if(this.tracks.indexOf(this.song) < this.tracks.length-1)
    {
      var i: number;
      for (i = this.tracks.indexOf(this.song)+1; i < this.tracks.length; i++) {
        if(this.tracks[i].spotify)
        {
          this.song = this.tracks[i];
          break;
        }
      }
    }
  }

  backward(event: Event){
    if(this.tracks.indexOf(this.song) > 0)
    {
      var i: number;
      for (i = this.tracks.indexOf(this.song)-1; i >= 0; i--) {
        if(this.tracks[i].spotify)
        {
          this.song = this.tracks[i];
          break;
        }
      }
    }
  }

  close(event: Event){
    this.song = null;
  }

  spotifyURL() {
    console.log('https://open.spotify.com/embed/track/' + this.song.spotify);
    return this.sanitizer.bypassSecurityTrustResourceUrl('https://open.spotify.com/embed/track/' + this.song.spotify);
  }
}