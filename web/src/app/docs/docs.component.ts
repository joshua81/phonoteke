import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Component({
  selector: 'app-docs',
  templateUrl: './docs.component.html',
  styleUrls: ['./docs.component.css']
})
export class DocsComponent implements OnInit {
  error = null;
  searchText = '';
  type = 'album';
  page = 0;
  docs = [];

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.searchText = '';
    this.loadDocs(0, 'album');
  }

  onSearch() {
      this.loadDocs(0, this.type);
  }

  scrollDocs() {
    this.loadDocs(this.page+1, this.type);
  }

  loadDocs(page: number, type: string) {
    this.page = page;
    this.type = type;
    if(this.page == 0) {
      this.docs.splice(0, this.docs.length);
    }

    this.http.get('/api/' + type + 's?p=' + this.page + '&q=' + this.searchText).subscribe(
      (data: any) => this.docsLoaded(data),
      error => this.error = error);
  }

  docsLoaded(data: any) {
    this.docs.push.apply(this.docs, data);
  }
}
