import {EventEmitter, Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AppService {
  server = 'http://localhost:8180';
  //server = 'http://192.168.1.82:8180';
  //server = 'http://10.103.2.31:8180';
  searchText = '';
  onSearch = new EventEmitter();
  audio = null;

  constructor() {}

  searchHandler(searchText: string) {
    this.searchText = searchText;
    this.onSearch.emit(searchText);
  }
}
