import {EventEmitter, Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AppService {
  searchText = '';
  onSearch = new EventEmitter();
  audio = null;

  constructor() {}

  searchHandler(searchText: string) {
    this.searchText = searchText;
    this.onSearch.emit(searchText);
  }
}
