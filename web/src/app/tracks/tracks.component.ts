import { Component, OnInit, Input } from '@angular/core';
import { AppComponent } from '../app.component';
import {DocComponent} from '../doc/doc.component';

@Component({
  selector: 'app-tracks',
  templateUrl: './tracks.component.html',
  styleUrls: ['./tracks.component.css']
})
export class TracksComponent implements OnInit {
  @Input() tracks = null;

  constructor(public app: AppComponent, public doc: DocComponent) {}

  ngOnInit() {
  }

  playPauseSpotify(type: string, albumid: string, track: string) {
    if(track != null) {
      var pos: number = this.tracks.
        map(track => track.spotify).
        filter(spotify => spotify != null).
        indexOf(track);
      this.app.playPauseSpotify(type, albumid, pos, this.tracks);
    }
  }
}