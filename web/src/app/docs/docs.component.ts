import {Component, OnInit} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {DomSanitizer} from '@angular/platform-browser';

@Component({
  selector: 'app-docs',
  templateUrl: './docs.component.html',
  styleUrls: ['./docs.component.css']
})
export class DocsComponent implements OnInit {
  error = null;
  searchText = '';
  type = 'album';
  page = 0;
  docs = [];
  user = null;

  constructor(private http: HttpClient, public sanitizer: DomSanitizer) {}

  ngOnInit() {
    this.searchText = '';
    this.loadUser();
    this.loadDocs(0, 'album');
  }

  onSearch() {
      this.loadDocs(0, this.type);
  }

  scrollDocs() {
    this.loadDocs(this.page+1, this.type);
  }

  loadDocs(page: number, type: string) {
    this.page = page;
    this.type = type;
    if(this.page == 0) {
      this.docs.splice(0, this.docs.length);
    }

    this.http.get('/api/' + type + 's?p=' + this.page + '&q=' + this.searchText).subscribe(
      (data: any) => this.docsLoaded(data),
      error => this.error = error);
  }

  loadStarred() {
    this.page = 0;
    this.type = 'starred';
    this.docs.splice(0, this.docs.length);

    this.http.get('/api/user/starred').subscribe(
      (data: any) => this.docsLoaded(data),
      error => this.error = error);
  }

  docsLoaded(data: any) {
    this.docs.push.apply(this.docs, data);
  }

  login() {
    this.http.get('/api/login').subscribe(
      (data: any) => console.log(data),
      error => this.error = error);
  }

  loadUser() {
    this.user = null;

    this.http.get('/api/user').subscribe(
      (data: any) => this.userLoaded(data),
      error => this.error = error);
  }

  userLoaded(data: any) {
    this.user = data.images[0].url;
  }
}
