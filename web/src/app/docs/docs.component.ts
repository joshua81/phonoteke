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
  tracks = [];

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.searchText = '';
    this.loadDocs(0, 'album');
  }

  onSearch() {
    if(this.docType == 'video')
    {
      this.loadTracks(0);
    }
    else
    {
      this.loadDocs(0, this.docType);
    }
  }

  scrollDocs() {
    this.loadDocs(this.page+1, this.docType);
  }

  scrollTracks() {
    this.loadTracks(this.page+1);
  }

  loadDocs(page: number, docType: string) {
    this.page = page;
    this.docType = docType;
    if(this.page == 0) {
      this.docs.splice(0, this.docs.length);
      this.tracks.splice(0, this.tracks.length);
    }

    this.http.get('/api/docs?p=' + this.page + '&q=' + this.searchText + '&t=' + this.docType).subscribe(
      (data: any) => this.docsLoaded(data),
      error => this.error = error);
  }

  docsLoaded(data: any) {
    this.docs.push.apply(this.docs, data);
  }

  loadTracks(page: number) {
    this.page = page;
    this.docType = 'video';
    if(this.page == 0) {
      this.docs.splice(0, this.docs.length);
      this.tracks.splice(0, this.tracks.length);
    }

    this.http.get('/api/tracks?p=' + this.page + '&q=' + this.searchText).subscribe(
      (data: any) => this.tracksLoaded(data),
      error => this.error = error);
  }

  tracksLoaded(data: any) {
    this.tracks.push.apply(this.tracks, data);
  }
}
