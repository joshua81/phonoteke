import {EventEmitter, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class AppService {
  //server = 'http://localhost:8180';
  server = 'http://192.168.1.82:8180';
  searchText = '';
  albumsLoaded = new EventEmitter();
  albumLoaded = new EventEmitter();
  artistsLoaded = new EventEmitter();
  artistLoaded = new EventEmitter();
  linksLoaded = new EventEmitter();
  tracksLoaded = new EventEmitter();
  error = null;

  constructor(private http: HttpClient) {}

  loadAlbums(page: number) {
    console.log(this.server + '/api/albums?p=' + page + '&q=' + this.searchText);
    this.http.get(this.server + '/api/albums?p=' + page + '&q=' + this.searchText).subscribe(
      (data: any) => this.albumsLoaded.emit({pageNum: page, content: data}),
      error => this.error = error);
  }

  loadAlbum(id: string) {
    console.log(this.server + '/api/albums/' + id);
    this.http.get(this.server + '/api/albums/' + id).subscribe(
      (data: any) => this.albumLoaded.emit(data[0]),
      error => this.error = error);
  }

  loadArtists(page: number) {
    console.log(this.server + '/api/artists?p=' + page + '&q=' + this.searchText);
    this.http.get(this.server + '/api/artists?p=' + page + '&q=' + this.searchText).subscribe(
      (data: any) => this.artistsLoaded.emit({pageNum: page, content: data}),
      error => this.error = error);
  }

  loadArtist(id: string) {
    console.log(this.server + '/api/artists/' + id);
    this.http.get(this.server + '/api/artists/' + id).subscribe(
      (data: any) => this.artistLoaded.emit(data[0]),
      error => this.error = error);
  }

  loadTracks(id: string) {
    console.log(this.server + '/api/tracks/' + id);
    this.http.get(this.server + '/api/tracks/' + id).subscribe(
      (data: any) => this.tracksLoaded.emit(data[0]),
      error => this.error = error);
  }

  loadLinks(id: string) {
    console.log(this.server + '/api/links/' + id);
    this.http.get(this.server + '/api/links/' + id).subscribe(
      (data: any) => this.linksLoaded.emit(data[0]),
      error => this.error = error);
  }
}
