import { Component, OnInit, Input } from '@angular/core';
import {DomSanitizer} from '@angular/platform-browser';
import { AppService } from '../app.service';

@Component({
  selector: 'app-tracks',
  templateUrl: './tracks.component.html',
  styleUrls: ['./tracks.component.css']
})
export class TracksComponent implements OnInit {
  song = null;
  @Input() tracks = null;

  constructor(public service: AppService, public sanitizer: DomSanitizer) {}

  ngOnInit() {
  }

  loadTrack(track: any) {
    this.song = track.spotify ? track : null;
  }

  close(event: Event) {
    this.song = null;
  }

  spotifyURL() {
    return this.sanitizer.bypassSecurityTrustResourceUrl('https://open.spotify.com/embed/track/' + this.song.spotify);
  }
}