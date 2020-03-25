import {EventEmitter, Injectable} from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class AppService {
  error = null;
  audio = null;
  showEvents = false;
  events = [];

  constructor(private http: HttpClient) {}

  resetEvents() {
    this.showEvents = false;
    this.events.splice(0, this.events.length);
  }

  loadEvents(id: string) {
    this.showEvents = true;
    this.http.get('/api/artists/' + id + '/events').subscribe(
      (data: any) => this.setEvents(data),
      error => this.error = error);
  }

  setEvents(events: any) {
    this.events.splice(0, this.events.length);
    if(typeof(events) != 'undefined' && events != null){
      this.events.push.apply(this.events, events);
    }
  }
}
