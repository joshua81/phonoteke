import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute} from '@angular/router';
import { AppComponent } from '../app.component';

@Component({
  selector: 'app-podcasts',
  templateUrl: './podcasts.component.html',
  styleUrls: ['./podcasts.component.css']
})
export class PodcastsComponent implements OnInit {
  error = null;
  searchText = '';
  user = null;
  podcasts = [];
  podcastsPage: number = 0;

  constructor(public app: AppComponent, private http: HttpClient, private route: ActivatedRoute) {}

  ngOnInit() {
    //this.loadUser();
    this.route.paramMap.subscribe(params => {
      window.scrollTo(0, 0);
      this.app.close();
      this.searchText = '';
      this.loadDocs();
    });
  }

  scrollDocs(type: string) {
    var page: number = 0;
    this.podcastsPage++;
    page = this.podcastsPage;
  
    this.http.get('/api/docs?p=' + page + '&q=' + this.searchText).subscribe(
      (data: any) => this.docsLoaded('podcasts', data),
      error => this.error = error);
  }

  loadDocs() {
    var page: number = 0;
    this.podcastsPage = 0;
    this.podcasts = [];

    this.http.get('/api/docs?p=' + page + '&q=' + this.searchText).subscribe(
      (data: any) => this.docsLoaded('podcasts', data),
      error => this.error = error);
  }

  docsLoaded(type: string, data: any) {
    this.podcasts.push.apply(this.podcasts, data);
  }

  /*loadUser() {
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
  }*/
}
