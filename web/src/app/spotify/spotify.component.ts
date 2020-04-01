import { Component, OnInit, Input } from '@angular/core';
import {DomSanitizer} from '@angular/platform-browser';
import { DocComponent } from '../doc/doc.component';

@Component({
  selector: 'app-spotify',
  templateUrl: './spotify.component.html',
  styleUrls: ['./spotify.component.css']
})
export class SpotifyComponent implements OnInit {
  song = null;
  @Input() tracks = null;

  constructor(public doc: DocComponent, public sanitizer: DomSanitizer) {}

  ngOnInit() {
  }

  selectTrack(track: any){
    this.doc.track = track;
  }

  loadTrack(track: any){
    this.song = track.spotify ? track : null;
  }

  close(event: Event){
    this.song = null;
  }

  spotifyURL() {
    return this.sanitizer.bypassSecurityTrustResourceUrl('https://open.spotify.com/embed/track/' + this.song.spotify);
  }
}