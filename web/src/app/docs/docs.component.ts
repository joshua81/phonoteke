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

  constructor(public app: AppComponent, private http: HttpClient, private route: ActivatedRoute) {
    this.loadSources();
    this.route.paramMap.subscribe(params => {
      window.scrollTo(0, 0);
      this.type = params.get('type') == '' ? null : params.get('type');
      this.source = params.get('source') == '' ? null : params.get('source');
      this.loadDocs();
    });
  }

  ngOnInit() {}

  scrollDocs() {
    this.page++;
  
    if(this.type == null) {
      this.http.get('/api/docs?p=' + this.page + '&q=' + this.searchText).subscribe(
        (data: any) => this.docsLoaded(data));
    }
    else if(this.source == null) {
      this.http.get('/api/docs?p=' + this.page + '&q=' + this.searchText + '&t=' + this.type).subscribe(
        (data: any) => this.docsLoaded(data));
    }
    else {
      this.http.get('/api/docs?p=' + this.page + '&q=' + this.searchText + '&t=' + this.type + '&s=' + this.source).subscribe(
        (data: any) => this.docsLoaded(data));
    }
  }

  loadDocs() {
    this.page = 0;
    this.docs = [];
    this.app.loading = true;
    if(this.type == null) {
      this.http.get('/api/docs?p=' + this.page + '&q=' + this.searchText).subscribe(
        (data: any) => {
          this.app.loading = false;
          this.docsLoaded(data);
        });
    }
    else if(this.source == null) {
      this.http.get('/api/docs?p=' + this.page + '&q=' + this.searchText + '&t=' + this.type).subscribe(
        (data: any) => {
          this.app.loading = false;
          this.docsLoaded(data);
        });
    }
    else {
      this.http.get('/api/docs?p=' + this.page + '&q=' + this.searchText + '&t=' + this.type + '&s=' + this.source).subscribe(
        (data: any) => {
          this.app.loading = false;
          this.docsLoaded(data);
        });
    }
  }

  loadSources() {
    if(this.sources.length == 0) {
      this.http.get('/api/docs/sources').subscribe(
        (data: any) => this.sources.push.apply(this.sources, data));
    }
  }

  docsLoaded(data: any) {
    this.docs.push.apply(this.docs, data);
  }

  formatDate(date: string) {
    return AppComponent.formatDate(date);
  }
}
