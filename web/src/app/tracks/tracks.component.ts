import { Component, OnInit, Input } from '@angular/core';
import { AppComponent } from '../app.component';

@Component({
  selector: 'app-tracks',
  templateUrl: './tracks.component.html',
  styleUrls: ['./tracks.component.css']
})
export class TracksComponent implements OnInit {
  @Input() spalbumid = null;
  @Input() tracks = null;
  @Input() doc = null;

  constructor(public app: AppComponent) {
    // nothing to do
  }

  ngOnInit() {
    // nothing to do
  }

  playPauseSpotify(track:string) {
    if(this.spalbumid != null && track != null) {
      var pos:number = 0;
      for(var j:number=0; j < this.tracks.length; j++) {
        if(this.tracks[j].spotify == track) {
          break;
        }
        if(this.tracks[j].spotify != null) {
          pos++
        }
      }
      if(pos >= 0) {
        this.app.playPauseSpotify(this.spalbumid, 'playlist', pos, track);
      }
    }
  }
}