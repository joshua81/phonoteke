import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute} from '@angular/router';
import { Title, Meta } from '@angular/platform-browser';
import { combineLatest } from 'rxjs';
import { map } from 'rxjs/operators';
import { AppComponent } from '../app.component';

@Component({
  selector: 'app-podcasts',
  templateUrl: './podcasts.component.html',
  styleUrls: ['./podcasts.component.css']
})
export class PodcastsComponent implements OnInit {
  searchText: string = '';
  docs = [];
  sources = [];
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
        this.loadDocs();
    });
  }

  ngOnInit() {}

  scrollDocs() {
    this.page++;
  
    if(this.source == null) {
      this.http.get('/api/docs?t=podcast&p=' + this.page + '&q=' + this.searchText).subscribe(
        (data: any) => this.docsLoaded(data));
    }
    else {
      this.http.get('/api/docs?t=podcast&p=' + this.page + '&q=' + this.searchText + '&s=' + this.source).subscribe(
        (data: any) => this.docsLoaded(data));
    }
  }

  loadDocs() {
    this.page = 0;
    this.docs = [];
    this.app.loading = true;

    if(this.source == null) {
      this.http.get('/api/docs?t=podcast&p=' + this.page + '&q=' + this.searchText).subscribe(
        (data: any) => {
          this.app.loading = false;
          this.docsLoaded(data);
        });
    }
    else {
      this.http.get('/api/docs?t=podcast&p=' + this.page + '&q=' + this.searchText + '&s=' + this.source).subscribe(
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

    this.title.setTitle('Human Beats - Podcast');
    this.meta.updateTag({ name: 'og:title', content: 'Human Beats - Podcast' });
    this.meta.updateTag({ name: 'og:type', content: 'music' });
    this.meta.updateTag({ name: 'og:url', content: 'https://humanbeats.appspot.com/podcasts' });
    this.meta.updateTag({ name: 'og:image', content: 'https://humanbeats.appspot.com/images/logo.png' });
    this.meta.updateTag({ name: 'og:description', content: 'Human Beats - Podcast' });
  }

  formatDate(date: string) {
    return AppComponent.formatDate(date);
  }

  next() {
    var slider = document.querySelector('#sources');
    if(slider.scrollLeft + slider.clientWidth < slider.scrollWidth) {
      slider.scrollTo(slider.scrollLeft + slider.clientWidth, 0);
    }
  }

  previous() {
    var slider = document.querySelector('#sources');
    if(slider.scrollLeft > 0) {
      slider.scrollTo(slider.scrollLeft - slider.clientWidth, 0);
    }
  }
}
