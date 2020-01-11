import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {AppService} from '../app.service';

@Component({
  selector: 'app-docs',
  templateUrl: './docs.component.html',
  styleUrls: ['./docs.component.css']
})
export class DocsComponent implements OnInit {
  error = null;
  page = 0;
  docs = [];
  tracks = [];

  constructor(private http: HttpClient, private service: AppService) {}

  ngOnInit() {
    this.service.onSearch.subscribe((searchText: string) => this.onSearch(searchText));
    this.loadAlbums(0);
    this.loadTracks(0);
  }

  onSearch(searchText: string) {
    this.loadAlbums(0);
  }

  loadAlbumsScroll() {
    this.page++;
    this.loadAlbums(this.page);
    this.loadTracks(this.page);
  }

  loadAlbums(page: number) {
    this.page = page;
    if(this.page == 0) {
      this.docs.splice(0, this.docs.length);
    }

    this.http.get('/api/docs?p=' + this.page + '&q=' + this.service.searchText).subscribe(
      (data: any) => this.albumsLoaded(data),
      error => this.error = error);
  }

  albumsLoaded(data: any) {
    this.docs.push.apply(this.docs, data);
  }

  loadTracks(page: number) {
    this.page = page;
    if(this.page == 0) {
      this.tracks.splice(0, this.tracks.length);
    }

    this.http.get('/api/tracks?p=' + this.page).subscribe(
      (data: any) => this.tracksLoaded(data),
      error => this.error = error);
  }

  tracksLoaded(data: any) {
    this.tracks.push.apply(this.tracks, data);
  }
}
