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
  video = null;
  @Input() tracks = null;

  constructor(public service: AppService, public sanitizer: DomSanitizer) {}

  ngOnInit() {
  }

  loadTrack(track: any) {
    this.song = track.spotify ? track : null;
  }

  loadVideo(track: any){
    this.video = track.youtube ? track : null;
  }

  close(event: Event) {
    this.song = null;
    this.video = null;
  }

  spotifyURL() {
    return this.sanitizer.bypassSecurityTrustResourceUrl('https://open.spotify.com/embed/track/' + this.song.spotify);
  }

  youtubeURL(){
    return this.sanitizer.bypassSecurityTrustResourceUrl('https://www.youtube.com/embed/' + this.video.youtube + '?autoplay=1');
  }
}