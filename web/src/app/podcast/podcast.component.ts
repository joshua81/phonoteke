import {Component, OnInit, Input} from '@angular/core';
import { AppComponent } from '../app.component';

@Component({
  selector: 'app-podcasts',
  templateUrl: './podcast.component.html',
  styleUrls: ['./podcast.component.css']
})
export class PodcastComponent implements OnInit {
  @Input() source:string = null;
  @Input() docs = [];

  constructor(public app: AppComponent) {
    // nothing to do
  }

  ngOnInit() {
    // nothing to do
  }

  formatDate(date: string) {
    return AppComponent.formatDate(date);
  }
}
