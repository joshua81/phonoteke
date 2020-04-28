import { Component, OnInit, Input } from '@angular/core';
import { DocComponent } from '../doc/doc.component';
import { AppService } from '../app.service';

@Component({
  selector: 'app-tracks',
  templateUrl: './tracks.component.html',
  styleUrls: ['./tracks.component.css']
})
export class TracksComponent implements OnInit {
  @Input() tracks = null;

  constructor(public service: AppService, public component: DocComponent) {}

  ngOnInit() {
  }
}