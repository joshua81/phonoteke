import { Component, OnInit } from '@angular/core';
import {AppService} from '../app.service';

@Component({
  selector: 'app-playlists',
  templateUrl: './playlists.component.html',
  styleUrls: ['./playlists.component.css']
})
export class PlaylistsComponent implements OnInit {
  page = 0;
  playlists = [];

  constructor(private service: AppService) {}

  ngOnInit() {
    this.service.playlistsLoaded.subscribe((msg: any) => this.pageLoaded(msg));
    this.loadPage();
  }

  pageLoaded(msg: any) {
    if(msg.pageNum === 0) {
      this.page = 1;
      this.playlists.splice(0, this.playlists.length);
    }
    this.playlists.push.apply(this.playlists, msg.content);
  }

  loadPage() {
    this.service.loadAlbums(this.page++);
  }
}
