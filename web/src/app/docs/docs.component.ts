import {Component, OnInit} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {ActivatedRoute} from '@angular/router';
import { AppComponent } from '../app.component';

@Component({
  selector: 'app-podcasts',
  templateUrl: './docs.component.html',
  styleUrls: ['./docs.component.css']
})
export class DocsComponent implements OnInit {
  searchText: string = '';
  docs = [];
  sources = [];
  type: string = null;
  source: string = null;
  page: number = 0;

  constructor(public app: AppComponent, private http: HttpClient, private route: ActivatedRoute) {}

  ngOnInit() {
    this.loadSources();
    this.route.paramMap.subscribe(params => {
      window.scrollTo(0, 0);
      this.type = params.get('type') == '' ? null : params.get('type');
      this.source = params.get('source') == '' ? null : params.get('source');
      this.loadDocs();
    });
  }

  scrollDocs() {
    this.page++;
  
    if(this.type == null) {
      this.http.get('/api/docs?p=' + this.page + '&q=' + this.searchText).subscribe(
        (data: any) => this.docsLoaded(data),
        error => this.app.error = error);
    }
    else if(this.source == null) {
      this.http.get('/api/docs?p=' + this.page + '&q=' + this.searchText + '&t=' + this.type).subscribe(
        (data: any) => this.docsLoaded(data),
        error => this.app.error = error);
    }
    else {
      this.http.get('/api/docs?p=' + this.page + '&q=' + this.searchText + '&t=' + this.type + '&s=' + this.source).subscribe(
        (data: any) => this.docsLoaded(data),
        error => this.app.error = error);
    }
  }

  loadDocs() {
    this.page = 0;
    this.docs = [];
    if(this.type == null) {
      this.http.get('/api/docs?p=' + this.page + '&q=' + this.searchText).subscribe(
        (data: any) => this.docsLoaded(data),
        error => this.app.error = error);
    }
    else if(this.source == null) {
      this.http.get('/api/docs?p=' + this.page + '&q=' + this.searchText + '&t=' + this.type).subscribe(
        (data: any) => this.docsLoaded(data),
        error => this.app.error = error);
    }
    else {
      this.http.get('/api/docs?p=' + this.page + '&q=' + this.searchText + '&t=' + this.type + '&s=' + this.source).subscribe(
        (data: any) => this.docsLoaded(data),
        error => this.app.error = error);
    }
  }

  loadSources() {
    if(this.sources.length == 0) {
      this.http.get('/api/docs/sources').subscribe(
        (data: any) => this.sources.push.apply(this.sources, data),
        error => this.app.error = error);
    }
  }

  docsLoaded(data: any) {
    this.docs.push.apply(this.docs, data);
  }
}
