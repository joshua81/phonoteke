import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute} from '@angular/router';
import { Title, Meta } from '@angular/platform-browser';
import { combineLatest } from 'rxjs';
import { map } from 'rxjs/operators';
import { AppComponent } from '../app.component';

@Component({
  selector: 'app-doc',
  templateUrl: './doc.component.html',
  styleUrls: ['./doc.component.css']
})
export class DocComponent implements OnInit {
  doc = null;
  links = [];

  constructor(public app: AppComponent, private http: HttpClient, private route: ActivatedRoute, private title: Title, private meta: Meta) {
    combineLatest(this.route.params, this.route.queryParams)
    .pipe(map(params => ({id: params[0].id})))
    .subscribe(params => {
      window.scrollTo(0, 0);
      var id:string = null;
      if(params.id != undefined && params.id != null) {
        id = params.id;
      }
      this.reset();
      this.loadDoc(id);
    });
  }

  ngOnInit() {
    this.reset();
  }

  reset() {
    this.doc = null;
    this.links = [];
  }

  loadDoc(id:string) {
    if(id != null) {
      this.app.loading = true;
      this.http.get('/api/' + id).subscribe(
        (data: any) => {
          this.app.loading = false;
          this.setDoc(data[0]);
        });
    }
  }

  setDoc(doc: any) {
    this.doc = doc;

    this.setMeta();
    this.loadLinks();
    this.loadTracks();
  }

  loadTracks() {
    if(this.doc.tracks && this.doc.tracks.length > 0) {
      this.app.checkTracks(this.doc.tracks);
    }
  }

  loadLinks() {
    this.links = null;
    this.http.get('/api/' + this.doc.id + '/links').subscribe(
      (data: any) => {
        this.links = data;
      });
  }

  setMeta() {
    this.title.setTitle(this.doc.artist + ' - ' + this.doc.title);
    this.meta.updateTag({ name: 'og:title', content: this.doc.artist + ' - ' + this.doc.title });
    this.meta.updateTag({ name: 'og:type', content: 'music:' + this.doc.type });
    this.meta.updateTag({ name: 'og:url', content: 'https://humanbeats.appspot.com/' + this.doc.id });
    this.meta.updateTag({ name: 'og:image', content: this.doc.cover });
    this.meta.updateTag({ name: 'og:description', content: this.doc.description });
  }

  formatDate(date: string) {
    return AppComponent.formatDate(date);
  }
}
