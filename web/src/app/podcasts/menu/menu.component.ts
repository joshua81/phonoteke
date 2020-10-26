import { Component, OnInit } from '@angular/core';
import {PodcastsComponent} from '../podcasts.component';
import { AppComponent } from '../../app.component';

@Component({
  selector: 'podcasts-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class PodcastsMenuComponent implements OnInit {

  constructor(public app: AppComponent, public docs: PodcastsComponent) { }

  ngOnInit() {}
}
