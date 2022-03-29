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
  shows = [];
  showsPage:number = 0;

  constructor(public app: AppComponent, private http: HttpClient, private route: ActivatedRoute, private title: Title, private meta: Meta) {
    combineLatest(this.route.params, this.route.queryParams)
    .pipe(map(params => ({source: params[0].source, date: params[0].date})))
    .subscribe(params => {
      window.scrollTo(0, 0);
      this.loadStats(params.source, parseInt(params.date));
      this.loadPodcasts();
    });
  }

  ngOnInit() {
    // nothing to do
  }

  setStatus(status:string) {
    this.status = status;
  }

  loadStats(source:string=null, date:number=null) {
    if(typeof source == 'undefined' || source == null) {
      source = null;
    }
    if(typeof date == 'undefined' || date == null || isNaN(date)) {
      date = new Date().getFullYear() * 100 + new Date().getMonth() + 1;
    }

    this.status = 'album';
    this.data = null;
    this.albums = [];
    this.albumsPage = 0;
    this.videos = [];
    this.videosPage = 0;
    this.tracks = [];
    this.tracksPpage = 0;
    this.shows = [];
    this.showsPage = 0;
    this.app.loading = true;

    if(source == null) {
      this.http.get('/api/stats/' + date).subscribe(
        (data: any) => {
          this.data = data;
          this.loadPage();
        });
    }
    else {
      this.http.get('/api/stats/' + source + "/" + date).subscribe(
        (data: any) => {
          this.data = data;
          this.loadPage();
        });
    }
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
    if(this.shows.length == 0) {
      this.shows = [];
      this.showsPage = 0;
      this.app.loading = true;

      this.http.get('/api/podcasts').subscribe(
        (data: any) => {
          this.shows.push.apply(this.shows, data);
          this.app.loading = false;
        });
    }
  }
}
