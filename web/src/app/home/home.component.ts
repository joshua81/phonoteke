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
  PAGE_SIZE: number = 20;
  spalbumid:string = null;
  status:string = 'album';
  data:any = null;
  albums = [];
  albumsPage:number = 0;
  videos = [];
  videosPage:number = 0;
  tracks = [];
  tracksPpage:number = 0;
  podcasts = [];
  podcastsPage:number = 0;

  constructor(public app: AppComponent, private http: HttpClient, private route: ActivatedRoute, private title: Title, private meta: Meta) {
    combineLatest(this.route.params, this.route.queryParams)
    .pipe(map(params => ({source: params[0].source})))
    .subscribe(params => {
      window.scrollTo(0, 0);
      this.loadStats(params.source);
    });
  }

  ngOnInit() {
    // nothing to do
  }

  setStatus(status:string) {
    this.status = status;
    if(status == 'podcast') {
      this.loadPodcasts();
    }
  }

  loadStats(source:string=null) {
    this.data = null;
    this.albums = [];
    this.albumsPage = 0;
    this.videos = [];
    this.videosPage = 0;
    this.tracks = [];
    this.tracksPpage = 0;
    this.podcasts = [];
    this.podcastsPage = 0;
    this.app.loading = true;

    this.http.get('/api/stats/202203').subscribe(
      (data: any) => {
        this.data = data;
        this.loadPage();
      });
  }

  loadPage() {
    /*var start:number = this.page*this.PAGE_SIZE;
    var end:number = (this.page+1)*this.PAGE_SIZE;
    this.albums.push.apply(this.albums, data.albums.slice(start,end));
    this.videos.push.apply(this.videos, data.videos.slice(start,end));
    this.tracks.push.apply(this.tracks, data.tracks.slice(start,end));*/
    this.albums.push.apply(this.albums, this.data.albums);
    this.videos.push.apply(this.videos, this.data.videos);
    this.tracks.push.apply(this.tracks, this.data.tracks);
    this.app.loading = false;
  }
  
  loadPodcasts() {
    if(this.podcasts.length == 0) {
      this.podcasts = [];
      this.podcastsPage = 0;
      this.app.loading = true;

      this.http.get('/api/podcasts').subscribe(
        (data: any) => {
          this.podcasts.push.apply(this.podcasts, data);
          this.app.loading = false;
        });
    }
  }

  /*scollPodcasts() {
    this.podcastsPage++;
    this.app.loading = true;

    this.http.get('/api/podcasts?p=' + this.podcastsPage).subscribe(
      (data: any) => {
        this.podcasts.push.apply(this.podcasts, data);
        this.app.loading = false;
      });
  }*/

  /*loadAlbums() {
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
  }*/
}
