import {Component} from '@angular/core';
import { DomSanitizer, SafeResourceUrl} from '@angular/platform-browser';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  spotifyUrl: SafeResourceUrl = null;
  youtubeUrl: SafeResourceUrl = null;
  artist = null;
  events = [];
  error = null;

  constructor(private http: HttpClient, private sanitizer: DomSanitizer) {}

  toggleSpotify(type: string, id: string) {
    if(this.spotifyUrl == null) {
      this.close();
      if(type == 'podcast') {
        type = 'playlist';
      }
      this.spotifyUrl = this.sanitizer.bypassSecurityTrustResourceUrl('https://open.spotify.com/embed/' + type + '/' + id);
    }
    else {
      this.spotifyUrl = null;
    }
  }

  toggleYoutube(track: string){
    if(this.youtubeUrl == null) {
      this.close();
      this.youtubeUrl = this.sanitizer.bypassSecurityTrustResourceUrl('https://www.youtube.com/embed/' + track + '?autoplay=1');;
    }
    else {
      this.youtubeUrl = null;
    }
  }

  loadEvents(artist: string) {
    if(this.artist != artist) {
      this.close();
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

  close() {
    this.spotifyUrl = null;
    this.youtubeUrl = null;
    this.artist = null;
    this.events = [];
    this.error = null;
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
