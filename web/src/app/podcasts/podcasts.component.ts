import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-podcasts',
  templateUrl: './podcasts.component.html',
  styleUrls: ['./podcasts.component.css']
})
export class PodcastsComponent implements OnInit {
  error = null;
  searchText = '';
  user = null;
  isStarred: boolean = false;
  podcasts = [];
  podcastsPage: number = 0;

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
        this.loadDocs();
      }
    });
  }

  scrollDocs(type: string) {
    if(!this.isStarred) {
      var page: number = 0;
      this.podcastsPage++;
      page = this.podcastsPage;
  
      this.http.get('/api/docs/podcasts?p=' + page + '&q=' + this.searchText).subscribe(
        (data: any) => this.docsLoaded('podcasts', data),
        error => this.error = error);
    }
  }

  loadDocs() {
    var page: number = 0;
    this.podcastsPage = 0;
    this.podcasts = [];

    this.http.get('/api/docs/podcasts?p=' + page + '&q=' + this.searchText).subscribe(
      (data: any) => this.docsLoaded('podcasts', data),
      error => this.error = error);
  }

  loadStarred() {
    this.podcasts = [];

    this.http.get('/api/docs/starred').subscribe(
      (data: any) => this.docsLoaded('starred', data),
      error => this.error = error);
  }

  docsLoaded(type: string, data: any) {
    if(type == 'podcasts') {
      this.podcasts.push.apply(this.podcasts, data);
    }
    else if(type == 'starred') {
      var podcasts = [];
      data.forEach(function(doc) {
        if(doc.type  == 'podcast') {
          podcasts.push(doc);
        }
      });
      this.podcasts.push.apply(this.podcasts, podcasts);
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
