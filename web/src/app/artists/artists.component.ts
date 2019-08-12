import { Component, OnInit } from '@angular/core';
import {AppService} from '../app.service';

@Component({
  selector: 'app-artists',
  templateUrl: './artists.component.html',
  styleUrls: ['./artists.component.css']
})
export class ArtistsComponent implements OnInit {
  page = 0;
  artists = [];

  constructor(private service: AppService) { }

  ngOnInit() {
    this.service.artistsLoaded.subscribe((msg: any) => this.pageLoaded(msg));
    this.loadPage();
  }

  pageLoaded(msg: any) {
    if(msg.pageNum === 0) {
      this.page = 1;
      this.artists.splice(0, this.artists.length);
    }
    this.artists.push.apply(this.artists, msg.content);
  }

  loadPage() {
    this.service.loadArtists(this.page++);
  }

}
