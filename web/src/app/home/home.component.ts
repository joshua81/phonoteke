import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute} from '@angular/router';
import { Title, Meta } from '@angular/platform-browser';
import { combineLatest } from 'rxjs';
import { map } from 'rxjs/operators';
import { AppComponent } from '../app.component';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  PAGE_SIZE: number = 12;
  searchText: string = '';
  albums = [];
  podcasts = [];
  albumsPage: number = 0;
  podcastsPage: number = 0;

  constructor(public app: AppComponent, private http: HttpClient, private route: ActivatedRoute, private title: Title, private meta: Meta) {
    combineLatest(this.route.params, this.route.queryParams)
    .pipe(map(params => ({source: params[0].source, searchText: params[1].q})))
    .subscribe(params => {
      var searchText = (typeof(params.searchText) == 'undefined' || params.searchText == null) ? '' : params.searchText;
      if(this.searchText != searchText) {
        this.searchText = searchText;
        window.scrollTo(0, 0);
        this.loadPodcasts();
        this.loadAlbums();
      }
    });
  }

  ngOnInit() {
    this.loadPodcasts();
    this.loadAlbums();
  }

  loadPodcasts() {
    this.podcasts = [];
    this.podcastsPage = 0;
    this.app.loading = true;

    this.http.get('/api/podcasts').subscribe(
      (data: any) => {
        this.podcasts.push.apply(this.podcasts, data);
        this.app.loading = false;
      });
  }

  scollPodcasts() {
    this.podcastsPage++;
    this.app.loading = true;

    this.http.get('/api/podcasts?p=' + this.podcastsPage).subscribe(
      (data: any) => {
        this.podcasts.push.apply(this.podcasts, data);
        this.app.loading = false;
      });
  }

  loadAlbums() {
    this.albums = [];
    this.albumsPage = 0;
    this.app.loading = true;

    this.http.get('/api/albums?q=' + this.searchText).subscribe(
      (data: any) => {
        this.albums.push.apply(this.albums, data);
        this.app.loading = false;
      });
  }

  scrollAlbums() {
    this.albumsPage++;
    this.app.loading = true;

    this.http.get('/api/albums?p=' + this.albumsPage + '&q=' + this.searchText).subscribe(
      (data: any) => {
        this.albums.push.apply(this.albums, data);
        this.app.loading = false;
      });
  }
}
