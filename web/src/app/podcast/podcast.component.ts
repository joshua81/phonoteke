import {Component, OnInit, Input} from '@angular/core';
import { AppComponent } from '../app.component';
import { HomeComponent } from '../home/home.component';

@Component({
  selector: 'app-podcasts',
  templateUrl: './podcast.component.html',
  styleUrls: ['./podcast.component.css']
})
export class PodcastComponent implements OnInit {
  @Input() source:string = null;
  @Input() docs = [];

  constructor(public app: AppComponent, private home: HomeComponent) {
    // nothing to do
  }

  ngOnInit() {
    // nothing to do
  }

  formatDate(date: string) {
    return AppComponent.formatDate(date);
  }

  scrollDocs() {
    this.home.scrollEpisodes();
  }
}
