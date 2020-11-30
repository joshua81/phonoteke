import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AppComponent } from '../app.component';

@Component({
  selector: 'app-doc',
  templateUrl: './doc.component.html',
  styleUrls: ['./doc.component.css']
})
export class DocComponent implements OnInit {
  id: string = null;
  doc = null;
  links = [];
  podcasts = [];


  constructor(public app: AppComponent, private http: HttpClient, private route: ActivatedRoute) {
    this.route.paramMap.subscribe(params => {
      window.scrollTo(0, 0);
      this.id = params.get('id');
      this.loadDoc();
    });
  }

  ngOnInit() {}

  loadDoc() {
    this.http.get('/api/docs/' + this.id).subscribe(
      (data: any) => this.setDoc(data[0]),
      error => this.app.error = error);
  }

  setDoc(doc: any) {
    this.doc = doc;
    this.links = [];
    this.podcasts = [];
    this.loadLinks();
  }

  loadLinks() {
    this.http.get('/api/docs/' + this.id + '/links').subscribe(
      (data: any) => this.setLinks(data),
      error => this.app.error = error);
  }

  setLinks(links: any) {
    this.links = links.filter(function(link: any){
		  return link.type != 'podcast';
    });
    this.podcasts = links.filter(function(link: any){
		  return link.type == 'podcast';
    });
  }
}
