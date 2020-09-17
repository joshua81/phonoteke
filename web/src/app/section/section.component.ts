import {Component, OnInit, Input} from '@angular/core';
import { PodcastsComponent } from '../podcasts/podcasts.component';

@Component({
  selector: 'app-section',
  templateUrl: './section.component.html',
  styleUrls: ['./section.component.css']
})
export class SectionComponent implements OnInit {
  @Input() docs = [];
  scroll: number = 0;

  constructor(public component: PodcastsComponent) {}

  ngOnInit() {
  }

  scrollDocs() {
    this.component.scrollDocs();
  }
}
