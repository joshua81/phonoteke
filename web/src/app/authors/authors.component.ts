import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute} from '@angular/router';
import { Title, Meta } from '@angular/platform-browser';
import { combineLatest } from 'rxjs';
import { map } from 'rxjs/operators';
import { AppComponent } from '../app.component';

@Component({
  selector: 'app-authors',
  templateUrl: './authors.component.html',
  styleUrls: ['./authors.component.css']
})
export class AuthorsComponent implements OnInit {
  searchText: string = '';
  docs = [];
  authors = [];
  source: string = null;
  page: number = 0;

  constructor(public app: AppComponent, private http: HttpClient, private route: ActivatedRoute, private title: Title, private meta: Meta) {
    combineLatest(this.route.params, this.route.queryParams)
    .pipe(map(params => ({source: params[0].source, searchText: params[1].q})))
    .subscribe(params => {
        window.scrollTo(0, 0);
        this.searchText = (typeof(params.searchText) == 'undefined' || params.searchText == null) ? '' : params.searchText;
        this.source = (typeof(params.source) == 'undefined' || params.source == '') ? null : params.source;
        this.loadSources();
    });
  }

  ngOnInit() {}

  loadSources() {
    if(this.authors.length == 0) {
      this.http.get('/api/sources').subscribe(
        (data: any) => this.authors.push.apply(this.authors, data));
    }
  }
}
