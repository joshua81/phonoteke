import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute} from '@angular/router';
import { AppComponent } from '../app.component';

@Component({
  selector: 'app-docs',
  templateUrl: './docs.component.html',
  styleUrls: ['./docs.component.css']
})
export class DocsComponent implements OnInit {
  searchText: string = '';
  docs = [];
  page: number = 0;

  constructor(public app: AppComponent, private http: HttpClient, private route: ActivatedRoute) {
    this.route.paramMap.subscribe(params => {
      window.scrollTo(0, 0);
      this.loadDocs();
    });
  }

  ngOnInit() {}

  scrollDocs() {
    this.page++;
    this.http.get('/api/docs?t=album&p=' + this.page + '&q=' + this.searchText).subscribe(
      (data: any) => this.docsLoaded(data));
  }

  loadDocs() {
    this.page = 0;
    this.docs = [];
    this.app.loading = true;
    this.http.get('/api/docs?t=album&p=' + this.page + '&q=' + this.searchText).subscribe(
      (data: any) => {
        this.app.loading = false;
        this.docsLoaded(data);
      });
  }

  docsLoaded(data: any) {
    this.docs.push.apply(this.docs, data);
  }

  formatDate(date: string) {
    return AppComponent.formatDate(date);
  }
}
