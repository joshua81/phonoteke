import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute} from '@angular/router';
import {DomSanitizer} from '@angular/platform-browser';

@Component({
  selector: 'app-docs',
  templateUrl: './docs.component.html',
  styleUrls: ['./docs.component.css']
})
export class DocsComponent implements OnInit {
  error = null;
  searchText = '';
  type = 'albums';
  page = 0;
  docs = [];
  user = null;

  constructor(private http: HttpClient, private route: ActivatedRoute, public sanitizer: DomSanitizer) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      window.scrollTo(0, 0);
      this.type = params.get('type');
      if(this.type == null || this.type == '')
      {
        this.type = 'albums';
      }
      this.searchText = '';
      this.loadUser();
      if(this.type == 'starred')
      {
        this.loadStarred();
      }
      else
      {
        this.loadDocs(0, this.type);
      }
    });
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

    this.http.get('/api/' + type + '?p=' + this.page + '&q=' + this.searchText).subscribe(
      (data: any) => this.docsLoaded(data),
      error => this.error = error);
  }

  loadStarred() {
    this.page = 0;
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
    if(data) {
      this.user = data.images[0].url;
    }
  }
}
