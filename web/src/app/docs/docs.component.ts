import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-docs',
  templateUrl: './docs.component.html',
  styleUrls: ['./docs.component.css']
})
export class DocsComponent implements OnInit {
  error = null;
  searchText = '';
  user = null;
  isStarred: boolean = false;
  albums = [];
  albumsPage: number = 0;
  interviews = [];
  interviewsPage: number = 0;
  podcasts = [];
  podcastsPage: number = 0;
  artists = [];
  artistsPage: number = 0;
  concerts = [];
  concertsPage: number = 0;

  constructor(private http: HttpClient, private route: ActivatedRoute) {}

  ngOnInit() {
    this.loadUser();
    this.route.paramMap.subscribe(params => {
      window.scrollTo(0, 0);
      this.searchText = '';
      if(params.get('type') == 'starred') {
        this.isStarred = true;
        this.loadStarred();
      }
      else {
        this.isStarred = false;
        this.loadDocsAll();
      }
    });
  }

  loadDocsAll() {
    this.loadDocs('albums');
    this.loadDocs('interviews');
    this.loadDocs('podcasts');
    this.loadDocs('artists');
    this.loadDocs('concerts');
  }

  scrollDocs(type: string) {
    if(!this.isStarred) {
      var page: number = 0;
      if(type == 'albums') {
        this.albumsPage++;
        page = this.albumsPage;
      }
      else if(type == 'interviews') {
        this.interviewsPage++;
        page = this.interviewsPage;
      }
      else if(type == 'podcasts') {
        this.podcastsPage++;
        page = this.podcastsPage;
      }
      else if(type == 'artists') {
        this.artistsPage++;
        page = this.artistsPage;
      }
      else if(type == 'concerts') {
        this.concertsPage++;
        page = this.concertsPage;
      }
  
      this.http.get('/api/docs/' + type + '?p=' + page + '&q=' + this.searchText).subscribe(
        (data: any) => this.docsLoaded(type, data),
        error => this.error = error);
    }
  }

  loadDocs(type: string) {
    var page: number = 0;
    if(type == 'albums') {
      this.albumsPage = 0;
      this.albums = [];
    }
    else if(type == 'interviews') {
      this.interviewsPage = 0;
      this.interviews = [];
    }
    else if(type == 'podcasts') {
      this.podcastsPage = 0;
      this.podcasts = [];
    }
    else if(type == 'artists') {
      this.artistsPage = 0;
      this.artists = [];
    }
    else if(type == 'concerts') {
      this.concertsPage = 0;
      this.concerts = [];
    }

    this.http.get('/api/docs/' + type + '?p=' + page + '&q=' + this.searchText).subscribe(
      (data: any) => this.docsLoaded(type, data),
      error => this.error = error);
  }

  loadStarred() {
    this.albums = [];
    this.interviews = [];
    this.podcasts = [];
    this.artists = [];
    this.concerts = [];

    this.http.get('/api/docs/starred').subscribe(
      (data: any) => this.docsLoaded('starred', data),
      error => this.error = error);
  }

  docsLoaded(type: string, data: any) {
    if(type == 'albums') {
      this.albums.push.apply(this.albums, data);
    }
    else if(type == 'interviews') {
      this.interviews.push.apply(this.interviews, data);
    }
    else if(type == 'podcasts') {
      this.podcasts.push.apply(this.podcasts, data);
    }
    else if(type == 'artists') {
      this.artists.push.apply(this.artists, data);
    }
    else if(type == 'concerts') {
      this.concerts.push.apply(this.concerts, data);
    }
    else if(type == 'starred') {
      var albums = [];
      var interviews = [];
      var podcasts = [];
      var artists = [];
      var concerts = [];
      data.forEach(function(doc) {
				if(doc.type  == 'album') {
          albums.push(doc);
        }
        else if(doc.type  == 'interview') {
          interviews.push(doc);
        }
        else if(doc.type  == 'podcast') {
          podcasts.push(doc);
        }
        else if(doc.type  == 'artist') {
          artists.push(doc);
        }
        else if(doc.type  == 'concert') {
          concerts.push(doc);
				}
      });
      this.albums.push.apply(this.albums, albums);
      this.interviews.push.apply(this.interviews, interviews);
      this.podcasts.push.apply(this.podcasts, podcasts);
      this.artists.push.apply(this.artists, artists);
      this.concerts.push.apply(this.concerts, concerts);
    }
  }

  loadUser() {
    if(this.user == null) {
      this.http.get('/api/user').subscribe(
        (data: any) => this.userLoaded(data),
        error => this.error = error);
    }
  }

  userLoaded(data: any) {
    if(data) {
      this.user = data.images[0].url;
    }
  }

  isDesktop() {
    var hasTouchScreen = false;
    if (window.navigator.maxTouchPoints > 0) { 
      hasTouchScreen = true;
    } 
    else if (window.navigator.msMaxTouchPoints > 0) {
      hasTouchScreen = true;
    } 
    else {
      var mQ = window.matchMedia && matchMedia("(pointer:coarse)");
      if (mQ && mQ.media === "(pointer:coarse)") {
        hasTouchScreen = !!mQ.matches;
      }
      else {
        // Only as a last resort, fall back to user agent sniffing
        var ua = window.navigator.userAgent;
        hasTouchScreen = (
          /\b(BlackBerry|webOS|iPhone|IEMobile)\b/i.test(ua) ||
          /\b(Android|Windows Phone|iPad|iPod)\b/i.test(ua));
      }
    }
    return !hasTouchScreen;
  }
}
