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
  docType = 'album';
  page = 0;
  docs = [];

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.searchText = '';
    this.loadDocs(0, 'album');
  }

  onSearch() {
      this.loadDocs(0, this.docType);
  }

  scrollDocs() {
    this.loadDocs(this.page+1, this.docType);
  }

  loadDocs(page: number, docType: string) {
    this.page = page;
    this.docType = docType;
    if(this.page == 0) {
      this.docs.splice(0, this.docs.length);
    }

    this.http.get('/api/docs?p=' + this.page + '&q=' + this.searchText + '&t=' + this.docType).subscribe(
      (data: any) => this.docsLoaded(data),
      error => this.error = error);
  }

  docsLoaded(data: any) {
    this.docs.push.apply(this.docs, data);
  }
}
