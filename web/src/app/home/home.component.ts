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
  status:string = "albums";
  source:string = null;
  name:string = null;
  albums = [];
  albumsPage:number = 0;
  videos = [];
  videosPage:number = 0;
  tracks = [];
  tracksPpage:number = 0;
  shows = [];
  showsPage:number = 0;
  episodes = [];
  episodesPage:number = 0;

  constructor(public app: AppComponent, private http: HttpClient, private route: ActivatedRoute, private title: Title, private meta: Meta) {
    combineLatest(this.route.params, this.route.queryParams)
    .pipe(map(params => ({source: params[0].source})))
    .subscribe(params => {
      window.scrollTo(0, 0);
      if(params.source != undefined && params.source != null) {
        this.source = params.source;
      }
      this.reset();
      this.loadStats();
    });
  }

  ngOnInit() {
    this.reset();
  }

  reset() {
    this.setStatus(this.source == null ? "shows" : "episodes");
    this.name = null;
    this.spalbumid = null;
    this.albums = [];
    this.albumsPage = 0;
    this.videos = [];
    this.videosPage = 0;
    this.tracks = [];
    this.tracksPpage = 0;
    this.shows = [];
    this.showsPage = 0;
    this.episodes = [];
    this.episodesPage = 0;
  }

  setStatus(status:string) {
    if(status != null) {
      this.status = status;
    }
  }

  loadStats() {
    this.app.loading = true;
    //var date = new Date().getFullYear() * 100 + new Date().getMonth() + 1;
    if(this.source == null) {
      this.http.get('/api/stats').subscribe(
        (data: any) => {
          this.loadPage(data);
          this.loadPodcasts();
        });
    }
    else {
      this.http.get('/api/stats/' + this.source).subscribe(
        (data: any) => {
          this.loadPage(data);
          this.loadEpisodes();
        });
    }
  }

  loadPage(data:any) {
    /*var start:number = this.page*this.PAGE_SIZE;
    var end:number = (this.page+1)*this.PAGE_SIZE;
    this.albums.push.apply(this.albums, data.albums.slice(start,end));
    this.videos.push.apply(this.videos, data.videos.slice(start,end));
    this.tracks.push.apply(this.tracks, data.tracks.slice(start,end));*/
    this.setMeta(data);
    this.name = data.name;
    this.spalbumid = data.spalbumid;
    this.albums.push.apply(this.albums, data.albums);
    this.videos.push.apply(this.videos, data.videos);
    this.tracks.push.apply(this.tracks, data.tracks);
    this.app.loading = false;
  }
  
  loadPodcasts() {
    if(this.source == null && this.shows.length == 0) {
      this.shows = [];
      this.showsPage = 0;
      this.app.loading = true;
      this.http.get('/api/podcasts').subscribe(
        (data: any) => {
          this.app.loading = false;
          this.shows.push.apply(this.shows, data);
        });
    }
  }

  loadEpisodes() {
    if(this.source != null && this.episodes.length == 0) {
      this.episodes = [];
      this.episodesPage = 0;
      this.app.loading = true;
      this.http.get('/api/podcasts/' + this.source + '/episodes?p=' + this.episodesPage).subscribe(
        (data: any) => {
          this.app.loading = false;
          this.episodes.push.apply(this.episodes, data);
        });    
    }
  }

  /*scrollDocs() {
    this.page++;
    this.http.get('/api/podcasts/' + this.source + '/episodes?p=' + this.page).subscribe(
      (data: any) => this.docsLoaded(data));
  }*/

  setMeta(data:any) {
    this.title.setTitle('Human Beats - ' + data.name);
    this.meta.updateTag({ name: 'og:title', content: 'Human Beats - ' + data.name });
    this.meta.updateTag({ name: 'og:type', content: 'music' });
    this.meta.updateTag({ name: 'og:url', content: 'https://humanbeats.appspot.com/podcasts/' + data.source });
    this.meta.updateTag({ name: 'og:image', content: data.cover });
    this.meta.updateTag({ name: 'og:description', content: 'Human Beats - ' + data.name });
  }
}
