import { Component, OnInit, Input } from '@angular/core';
import { AppComponent } from '../app.component';

@Component({
  selector: 'app-playlist',
  templateUrl: './playlist.component.html',
  styleUrls: ['./playlist.component.css']
})
export class PlaylistComponent implements OnInit {
  @Input() spalbumid = null;
  @Input() tracks = null;

  constructor(public app: AppComponent) {
    // nothing to do
  }

  ngOnInit() {
    // nothing to do
  }
}