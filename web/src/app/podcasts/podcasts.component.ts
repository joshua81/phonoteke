import {Component, OnInit} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {ActivatedRoute} from '@angular/router';
import { AppComponent } from '../app.component';
import { CookieService } from 'ngx-cookie-service';

@Component({
  selector: 'app-podcasts',
  templateUrl: './podcasts.component.html',
  styleUrls: ['./podcasts.component.css']
})
export class PodcastsComponent implements OnInit {
  public type: string = null;
  error = null;
  searchText: string = '';
  user: string = null;
  podcasts = [];
  podcastsPage: number = 0;

  constructor(public app: AppComponent, private http: HttpClient, private route: ActivatedRoute, private cookieService: CookieService) {}

  ngOnInit() {
    this.loadUser();
    this.route.paramMap.subscribe(params => {
      window.scrollTo(0, 0);
      this.app.close();
      //this.searchText = '';
      this.type = params.get('type') == '' ? null : params.get('type');
      this.loadDocs();
    });
  }

  scrollDocs() {
    var page: number = 0;
    this.podcastsPage++;
    page = this.podcastsPage;
  
    if(this.type == null) {
      this.http.get('/api/docs?p=' + page + '&q=' + this.searchText).subscribe(
        (data: any) => this.docsLoaded(data),
        error => this.error = error);
    }
    else {
      this.http.get('/api/docs?p=' + page + '&q=' + this.searchText + '&t=' + this.type).subscribe(
        (data: any) => this.docsLoaded(data),
        error => this.error = error);
    }
  }

  loadDocs() {
    var page: number = 0;
    this.podcastsPage = 0;
    this.podcasts = [];

    if(this.type == null) {
      this.http.get('/api/docs?p=' + page + '&q=' + this.searchText).subscribe(
        (data: any) => this.docsLoaded(data),
        error => this.error = error);
    }
    else {
      this.http.get('/api/docs?p=' + page + '&q=' + this.searchText + '&t=' + this.type).subscribe(
        (data: any) => this.docsLoaded(data),
        error => this.error = error);
    }
  }

  docsLoaded(data: any) {
    this.podcasts.push.apply(this.podcasts, data);
  }

  loadUser() {
    if(this.user == null) {
      const options = {
        headers: new HttpHeaders({
          'Authorization': 'Bearer ' + this.cookieService.get('spotify-token'),
        }),
      };
      this.http.get('https://api.spotify.com/v1/me', options).subscribe(
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
