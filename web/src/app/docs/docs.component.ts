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
  albums = [];
  interviews = [];
  podcasts = [];
  artists = [];
  concerts = [];

  constructor(private http: HttpClient, private route: ActivatedRoute) {}

  ngOnInit() {
    this.loadUser();
    this.route.paramMap.subscribe(params => {
      window.scrollTo(0, 0);
      this.searchText = '';
      if(params.get('type') == 'starred') {
        this.loadStarred();
      }
      else {
        this.loadDocsAll();
      }
    });
  }

  onSearch() {
    this.loadDocsAll();
  }

  resetSearch() {
    this.searchText = '';
    this.loadDocsAll();
  }

  loadDocsAll() {
    this.loadDocs('albums', 0);
    this.loadDocs('interviews', 0);
    this.loadDocs('podcasts', 0);
    this.loadDocs('artists', 0);
    this.loadDocs('concerts', 0);
  }


  loadDocs(type: string, page: number) {
    if(page == 0) {
      if(type == 'albums') {
        this.albums = [];
      }
      else if(type == 'interviews') {
        this.interviews = [];
      }
      else if(type == 'podcasts') {
        this.podcasts = [];
      }
      else if(type == 'artists') {
        this.artists = [];
      }
      else if(type == 'concerts') {
        this.concerts = [];
      }
    }

    this.http.get('/api/' + type + '?p=' + page + '&q=' + this.searchText).subscribe(
      (data: any) => this.docsLoaded(type, data),
      error => this.error = error);
  }

  loadStarred() {
    this.albums = [];
    this.interviews = [];
    this.podcasts = [];
    this.artists = [];
    this.concerts = [];

    this.http.get('/api/user/starred').subscribe(
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
}
