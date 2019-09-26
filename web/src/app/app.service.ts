import {EventEmitter, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class AppService {
  server = 'http://localhost:8180';
  //server = 'http://192.168.1.82:8180';
  //server = 'http://10.103.2.31:8180';
  searchText = '';
  docsLoaded = new EventEmitter();
  docLoaded = new EventEmitter();
  tracksLoaded = new EventEmitter();
  eventsLoaded = new EventEmitter();
  linksLoaded = new EventEmitter();
  error = null;

  constructor(private http: HttpClient) {}

  loadDocs(page: number) {
    console.log(this.server + '/api/docs?p=' + page + '&q=' + this.searchText);
    this.http.get(this.server + '/api/docs?p=' + page + '&q=' + this.searchText).subscribe(
      (data: any) => this.docsLoaded.emit({pageNum: page, content: data}),
      error => this.error = error);
  }

  loadDoc(id: string) {
    console.log(this.server + '/api/docs/' + id);
    this.http.get(this.server + '/api/docs/' + id).subscribe(
      (data: any) => this.docLoaded.emit(data[0]),
      error => this.error = error);
  }

  loadTracks(id: string) {
    console.log(this.server + '/api/docs/' + id + '/tracks');
    this.http.get(this.server + '/api/docs/' + id + '/tracks').subscribe(
      (data: any) => this.tracksLoaded.emit(data),
      error => this.error = error);
  }

  loadEvents(id: string) {
    console.log(this.server + '/api/docs/' + id + '/events');
    this.http.get(this.server + '/api/docs/' + id + '/events').subscribe(
      (data: any) => this.eventsLoaded.emit(data),
      error => this.error = error);
  }

  loadLinks(id: string) {
    console.log(this.server + '/api/docs/' + id + '/links');
    this.http.get(this.server + '/api/docs/' + id + '/links').subscribe(
      (data: any) => this.linksLoaded.emit(data),
      error => this.error = error);
  }
}
