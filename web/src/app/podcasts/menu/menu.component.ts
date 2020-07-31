import { Component, OnInit } from '@angular/core';
import {PodcastsComponent} from '../podcasts.component';

@Component({
  selector: 'podcasts-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class PodcastsMenuComponent implements OnInit {

  constructor(public docs: PodcastsComponent) { }

  ngOnInit() {}
}
