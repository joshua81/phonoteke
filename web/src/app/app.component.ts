import {Component} from '@angular/core';
import {AppService} from './app.service';
import { DomSanitizer} from '@angular/platform-browser';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  providers: [AppService]
})
export class AppComponent {
  spotify = null;
  youtube = null;
  artist = null;
  events = [];
  error = null;

  constructor(private service: AppService, private http: HttpClient, private sanitizer: DomSanitizer) {}

  toggleSpotify(type: string, id: string) {
    if(this.spotify == null) {
      this.youtube = null;
      if(type == 'podcast') {
        type = 'playlist';
      }
      this.spotify = 'https://open.spotify.com/embed/' + type + '/' + id;
    }
    else {
      this.spotify = null;
    }
  }

  toggleYoutube(track: string){
    if(this.youtube == null) {
      this.spotify = null;
      this.youtube = track;
    }
    else {
      this.youtube = null;
    }
  }

  spotifyUrl() {
    return this.sanitizer.bypassSecurityTrustResourceUrl(this.spotify);
  }

  youtubeUrl(){
    return this.sanitizer.bypassSecurityTrustResourceUrl('https://www.youtube.com/embed/' + this.youtube + '?autoplay=1');
  }

  loadEvents(artist: string) {
    if(this.artist != artist) {
      this.artist = artist;
      this.http.get('/api/events/' + artist).subscribe(
        (data: any) => this.setEvents(data),
        error => this.error = error);
    }
  }

  setEvents(events: any) {
    if(typeof(events) != 'undefined' && events != null){
      this.events.push.apply(this.events, events);
    }
  }

  closeEvents(){
    this.artist = null;
    this.events = [];
    this.error = null;
  }

  isDesktop() {
    var hasTouchScreen = false;
    if (window.navigator.maxTouchPoints > 0) { 
      hasTouchScreen = true;
    } 
    else if (window.navigator.msMaxTouchPoints > 0) {
      hasTouchScreen = true;
    } 
    else {
      var mQ = window.matchMedia && matchMedia("(pointer:coarse)");
      if (mQ && mQ.media === "(pointer:coarse)") {
        hasTouchScreen = !!mQ.matches;
      }
      else {
        // Only as a last resort, fall back to user agent sniffing
        var ua: string = window.navigator.userAgent;
        hasTouchScreen = (
          /\b(BlackBerry|webOS|iPhone|IEMobile)\b/i.test(ua) ||
          /\b(Android|Windows Phone|iPad|iPod)\b/i.test(ua));
      }
    }
    return !hasTouchScreen;
  }
}
