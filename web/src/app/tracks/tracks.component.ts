import { Component, OnInit, Input } from '@angular/core';
import { DocComponent } from '../doc/doc.component';

@Component({
  selector: 'app-tracks',
  templateUrl: './tracks.component.html',
  styleUrls: ['./tracks.component.css']
})
export class TracksComponent implements OnInit {
  @Input() tracks = null;

  constructor(public component: DocComponent) {}

  ngOnInit() {
  }
}