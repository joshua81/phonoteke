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
  links = null;


  constructor(public app: AppComponent, private http: HttpClient, private route: ActivatedRoute) {
    this.route.paramMap.subscribe(params => {
      window.scrollTo(0, 0);
      this.id = params.get('id');
      this.loadDoc();
    });
  }

  ngOnInit() {}

  loadDoc() {
    this.app.loading = true;
    this.http.get('/api/docs/' + this.id).subscribe(
      (data: any) => {
        this.app.loading = false;
        this.setDoc(data[0]);
      });
  }

  setDoc(doc: any) {
    this.doc = doc;
    this.links = null;
    this.loadLinks();
  }

  loadLinks() {
    //this.app.loading = true;
    this.http.get('/api/docs/' + this.id + '/links').subscribe(
      (data: any) => {
        //this.app.loading = false;
        this.links = data;
      });
  }

  formatDate(date: string) {
    return AppComponent.formatDate(date);
  }
}
