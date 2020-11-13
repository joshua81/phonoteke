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
  error = null;
  searchText: string = '';
  docs = [];
  sources = [];
  type: string = null;
  source: string = null;
  page: number = 0;

  constructor(public app: AppComponent, private http: HttpClient, private route: ActivatedRoute) {}

  ngOnInit() {
    this.app.loadDevices();
    this.route.paramMap.subscribe(params => {
      window.scrollTo(0, 0);
      this.app.close();
      this.type = params.get('type') == '' ? null : params.get('type');
      this.loadSources();
      this.loadDocs();
    });
  }

  scrollDocs() {
    this.page++;
  
    if(this.type == null) {
      this.http.get('/api/docs?p=' + this.page + '&q=' + this.searchText).subscribe(
        (data: any) => this.docsLoaded(data),
        error => this.error = error);
    }
    else {
      this.http.get('/api/docs?p=' + this.page + '&q=' + this.searchText + '&t=' + this.type).subscribe(
        (data: any) => this.docsLoaded(data),
        error => this.error = error);
    }
  }

  loadDocs() {
    this.page = 0;
    this.docs = [];
    if(this.type == null) {
      this.http.get('/api/docs?p=' + this.page + '&q=' + this.searchText).subscribe(
        (data: any) => this.docsLoaded(data),
        error => this.error = error);
    }
    else {
      this.http.get('/api/docs?p=' + this.page + '&q=' + this.searchText + '&t=' + this.type).subscribe(
        (data: any) => this.docsLoaded(data),
        error => this.error = error);
    }
  }

  loadSources() {
    this.sources = [];
    this.http.get('/api/docs/sources').subscribe(
      (data: any) => this.sources.push.apply(this.sources, data),
      error => this.error = error);
  }

  docsLoaded(data: any) {
    this.docs.push.apply(this.docs, data);
  }
}
