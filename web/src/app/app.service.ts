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
  playlistsLoaded = new EventEmitter();
  playlistLoaded = new EventEmitter();
  error = null;

  constructor(private http: HttpClient) { }

  loadAlbums(page: number) {
    console.log('http://localhost:8180/api/albums?p=' + page + '&q=' + this.searchText);
    this.http.get('http://localhost:8180/api/albums?p=' + page + '&q=' + this.searchText).subscribe(
      (data: any) => this.albumsLoaded.emit({pageNum: page, content: data}),
      error => this.error = error);
  }

  loadAlbum(id: string) {
    console.log('http://localhost:8180/api/albums/' + id);
    this.http.get('http://localhost:8180/api/albums/' + id).subscribe(
      (data: any) => this.albumLoaded.emit(data[0]),
      error => this.error = error);
  }

  loadArtists(page: number) {
    console.log('http://localhost:8180/api/artists?p=' + page + '&q=' + this.searchText);
    this.http.get('http://localhost:8180/api/artists?p=' + page + '&q=' + this.searchText).subscribe(
      (data: any) => this.artistsLoaded.emit({pageNum: page, content: data}),
      error => this.error = error);
  }

  loadArtist(id: string) {
    console.log('http://localhost:8180/api/artists/' + id);
    this.http.get('http://localhost:8180/api/artists/' + id).subscribe(
      (data: any) => this.artistLoaded.emit(data[0]),
      error => this.error = error);
  }
}
