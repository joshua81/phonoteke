import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {AppService} from '../app.service';

@Component({
  selector: 'app-docs',
  templateUrl: './docs.component.html',
  styleUrls: ['./docs.component.css']
})
export class DocsComponent implements OnInit {
  server = 'http://localhost:8180';
  //server = 'http://192.168.1.82:8180';
  //server = 'http://10.103.2.31:8180';
  searchText = '';
  error = null;
  page = 0;
  docs = [];

  constructor(private http: HttpClient, private service: AppService) {}

  ngOnInit() {
    this.service.onSearch.subscribe((searchText: string) => this.loadPageSearch(searchText));
    this.loadPage(0);
  }

  loadPageSearch(searchText: string) {
    this.searchText = searchText;
    this.loadPage(0);
  }

  loadPageScroll() {
    this.page++;
    this.loadPage(this.page);
  }

  loadPage(page: number) {
    this.page = page;
    if(this.page == 0) {
      this.docs.splice(0, this.docs.length);
    }

    console.log(this.server + '/api/docs?p=' + this.page + '&q=' + this.searchText);
    this.http.get(this.server + '/api/docs?p=' + this.page + '&q=' + this.searchText).subscribe(
      (data: any) => this.pageLoaded(data),
      error => this.error = error);
  }

  pageLoaded(data: any) {
    this.docs.push.apply(this.docs, data);
  }
}
