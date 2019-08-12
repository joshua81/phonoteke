import {Component, OnInit} from '@angular/core';
import {AppService} from '../app.service';

@Component({
  selector: 'app-albums',
  templateUrl: './albums.component.html',
  styleUrls: ['./albums.component.css']
})
export class AlbumsComponent implements OnInit {
  page = 0;
  albums = [];

  constructor(private service: AppService) {}

  ngOnInit() {
    this.service.albumsLoaded.subscribe((msg: any) => this.pageLoaded(msg));
    this.loadPage();
  }

  pageLoaded(msg: any) {
    if(msg.pageNum === 0) {
      this.page = 1;
      this.albums.splice(0, this.albums.length);
    }
    this.albums.push.apply(this.albums, msg.content);
  }

  loadPage() {
    this.service.loadAlbums(this.page++);
  }
}
