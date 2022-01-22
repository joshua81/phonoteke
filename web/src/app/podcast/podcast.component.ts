import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute} from '@angular/router';
import { Title, Meta } from '@angular/platform-browser';
import { combineLatest } from 'rxjs';
import { map } from 'rxjs/operators';
import { AppComponent } from '../app.component';

@Component({
  selector: 'app-podcasts',
  templateUrl: './podcast.component.html',
  styleUrls: ['./podcast.component.css']
})
export class PodcastComponent implements OnInit {
  searchText: string = '';
  docs = [];
  source = null;
  page: number = 0;

  constructor(public app: AppComponent, private http: HttpClient, private route: ActivatedRoute, private title: Title, private meta: Meta) {
    combineLatest(this.route.params, this.route.queryParams)
    .pipe(map(params => ({source: params[0].source, searchText: params[1].q})))
    .subscribe(params => {
        window.scrollTo(0, 0);
        this.searchText = (typeof(params.searchText) == 'undefined' || params.searchText == null) ? '' : params.searchText;
        var source = params.source;
        this.loadSource(source);
    });
  }

  ngOnInit() {}

  loadSource(source: string) {
    this.source = null;
    this.app.loading = true;
    
    this.http.get('/api/sources/' + source).subscribe(
      (data: any) => {
        this.app.loading = false;
        this.sourceLoaded(data);
      }); 
  }

  sourceLoaded(data: any) {
    if(data.length != 0) {
      this.source = data[0];
      this.title.setTitle('Human Beats - ' + this.source.name);
      this.meta.updateTag({ name: 'og:title', content: 'Human Beats - ' + this.source.name });
      this.meta.updateTag({ name: 'og:type', content: 'music' });
      this.meta.updateTag({ name: 'og:url', content: 'https://humanbeats.appspot.com/podcasts/' + this.source.source });
      this.meta.updateTag({ name: 'og:image', content: this.source.cover });
      this.meta.updateTag({ name: 'og:description', content: 'Human Beats - ' + this.source.name });

      this.loadDocs();
    }
  }

  loadDocs() {
    this.page = 0;
    this.docs = [];
    this.app.loading = true;

    this.http.get('/api/podcasts?p=' + this.page + '&q=' + this.searchText + '&s=' + this.source.source).subscribe(
      (data: any) => {
        this.app.loading = false;
        this.docsLoaded(data);
      });     
  }

  docsLoaded(data: any) {
    this.docs.push.apply(this.docs, data);
  }

  scrollDocs() {
    this.page++;
    this.http.get('/api/podcasts?p=' + this.page + '&q=' + this.searchText + '&s=' + this.source.source).subscribe(
      (data: any) => this.docsLoaded(data));
  }

  formatDate(date: string) {
    return AppComponent.formatDate(date);
  }
}
