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
    if(this.source == null) {
      this.app.loading = true;
      this.http.get('/api/stats').subscribe(
        (data: any) => {
          this.loadPage(data);
          this.loadPodcasts();
        });
    }
    else {
      this.app.loading = true;
      this.http.get('/api/stats/' + this.source).subscribe(
        (data: any) => {
          this.loadPage(data);
          this.loadEpisodes();
        });
    }
  }

  loadPage(data:any) {
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
          this.shows.push.apply(this.shows, data);
          this.app.loading = false;
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
          this.episodes.push.apply(this.episodes, data);
          this.app.loading = false;
        });    
    }
  }

  scrollEpisodes() {
    if(this.source != null) {
      this.episodesPage++;
      this.app.loading = true;
      this.http.get('/api/podcasts/' + this.source + '/episodes?p=' + this.episodesPage).subscribe(
        (data: any) => {
          this.episodes.push.apply(this.episodes, data);
          this.app.loading = false;
        });
    }
  }

  setMeta(data:any) {
    this.title.setTitle('Human Beats - ' + data.name);
    this.meta.updateTag({ name: 'og:title', content: 'Human Beats - ' + data.name });
    this.meta.updateTag({ name: 'og:type', content: 'music' });
    this.meta.updateTag({ name: 'og:url', content: 'https://humanbeats.appspot.com/podcasts/' + data.source });
    this.meta.updateTag({ name: 'og:image', content: data.cover });
    this.meta.updateTag({ name: 'og:description', content: 'Human Beats - ' + data.name });
  }
}
