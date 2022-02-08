import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Title, Meta } from '@angular/platform-browser';
import { AppComponent } from '../app.component';

@Component({
  selector: 'app-doc',
  templateUrl: './doc.component.html',
  styleUrls: ['./doc.component.css']
})
export class DocComponent implements OnInit {
  id: string = null;
  doc = null;
  links = null;


  constructor(public app: AppComponent, private http: HttpClient, private route: ActivatedRoute, private title: Title, private meta: Meta) {
    this.route.paramMap.subscribe(params => {
      window.scrollTo(0, 0);
      this.id = params.get('id');
      this.loadDoc();
    });
  }

  ngOnInit() {}

  loadDoc() {
    this.app.loading = true;
    this.http.get('/api/' + this.id).subscribe(
      (data: any) => {
        this.app.loading = false;
        this.setDoc(data[0]);
      });
  }

  setDoc(doc: any) {
    this.doc = doc;

    this.title.setTitle(this.doc.artist + ' - ' + this.doc.title);
    this.meta.updateTag({ name: 'og:title', content: this.doc.artist + ' - ' + this.doc.title });
    this.meta.updateTag({ name: 'og:type', content: 'music:' + this.doc.type });
    this.meta.updateTag({ name: 'og:url', content: 'https://humanbeats.appspot.com/' + this.doc.id });
    this.meta.updateTag({ name: 'og:image', content: this.doc.cover });
    this.meta.updateTag({ name: 'og:description', content: this.doc.description });

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
    this.http.get('/api/' + this.id + '/links').subscribe(
      (data: any) => {
        this.links = data;
      });
  }

  formatDate(date: string) {
    return AppComponent.formatDate(date);
  }
}
