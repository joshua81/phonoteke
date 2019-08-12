import {EventEmitter, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class AppService {
  searchText = '';
  albumsLoaded = new EventEmitter();
  albumLoaded = new EventEmitter();
  artistsLoaded = new EventEmitter();
  artistLoaded = new EventEmitter();
  error = null;

  constructor(private http: HttpClient) { }

  loadAlbums(page: number) {
    console.log('http://localhost:8180/api/doc/review?p=' + page + '&q=' + this.searchText);
    this.http.get('http://localhost:8180/api/doc/review?p=' + page + '&q=' + this.searchText).subscribe(
      (data: any) => this.albumsLoaded.emit({pageNum: page, content: data}),
      error => this.error = error);
  }

  loadAlbum(id: string) {
    console.log('http://localhost:8180/api/doc/review/' + id);
    this.http.get('http://localhost:8180/api/doc/review/' + id).subscribe(
      (data: any) => this.albumLoaded.emit(data[0]),
      error => this.error = error);
  }

  loadArtists(page: number) {
    console.log('http://localhost:8180/api/doc/monograph?p=' + page + '&q=' + this.searchText);
    this.http.get('http://localhost:8180/api/doc/monograph?p=' + page + '&q=' + this.searchText).subscribe(
      (data: any) => this.artistsLoaded.emit({pageNum: page, content: data}),
      error => this.error = error);
  }

  loadArtist(id: string) {
    console.log('http://localhost:8180/api/doc/monograph/' + id);
    this.http.get('http://localhost:8180/api/doc/monograph/' + id).subscribe(
      (data: any) => this.artistLoaded.emit(data[0]),
      error => this.error = error);
  }
}
