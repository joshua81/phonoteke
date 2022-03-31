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

  constructor(public app: AppComponent) {
    // nothing to do
  }

  ngOnInit() {
    // nothing to do
  }
}