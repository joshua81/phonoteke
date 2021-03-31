import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute} from '@angular/router';
import { Title, Meta } from '@angular/platform-browser';
import { combineLatest } from 'rxjs';
import { map } from 'rxjs/operators';
import { AppComponent } from '../app.component';

@Component({
  selector: 'app-albums',
  templateUrl: './albums.component.html',
  styleUrls: ['./albums.component.css']
})
export class AlbumsComponent implements OnInit {
  searchText: string = '';
  docs = [];
  page: number = 0;

  constructor(public app: AppComponent, private http: HttpClient, private route: ActivatedRoute, private title: Title, private meta: Meta) {
    combineLatest(this.route.queryParams)
    .pipe(map(params => ({searchText: params[0].q})))
    .subscribe(params => {
        window.scrollTo(0, 0);
        this.searchText = (typeof(params.searchText) == 'undefined' || params.searchText == null) ? '' : params.searchText;
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

    this.title.setTitle('Human Beats - Album');
    this.meta.updateTag({ name: 'og:title', content: 'Human Beats - Album' });
    this.meta.updateTag({ name: 'og:type', content: 'music' });
    this.meta.updateTag({ name: 'og:url', content: 'https://humanbeats.appspot.com/albums' });
    this.meta.updateTag({ name: 'og:image', content: 'https://humanbeats.appspot.com/images/logo.png' });
    this.meta.updateTag({ name: 'og:description', content: 'Human Beats - Album' });
  }

  formatDate(date: string) {
    return AppComponent.formatDate(date);
  }
}
