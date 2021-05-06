import { Component, OnInit } from '@angular/core';
import { AppComponent } from 'src/app/app.component';
import { PodcastComponent } from '../podcast.component';

@Component({
  selector: 'podcast-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class PodcastMenuComponent implements OnInit {
  
  constructor(public app: AppComponent, public doc: PodcastComponent) {}

  ngOnInit() {}
}
