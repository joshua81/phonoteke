import {Component} from '@angular/core';
import { DomSanitizer, SafeResourceUrl} from '@angular/platform-browser';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { CookieService } from 'ngx-cookie-service';
import { timer } from 'rxjs';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  youtube: SafeResourceUrl = null;
  user = null;
  device = null;
  track = null;
  events = null;
  error = null;
  timer = null;

  constructor(private http: HttpClient, private sanitizer: DomSanitizer, private cookieService: CookieService) {}

  login() {
    const token = this.cookieService.get('spotify-token');
    if(token != null && token != '' && this.user == null) {
      const options = {
        headers: new HttpHeaders({'Authorization': 'Bearer ' + token}),
      };
      this.http.get('https://api.spotify.com/v1/me', options).subscribe(
        (data: any) => this.userLoaded(data),
        error => this.error = error);
    }
  }

  userLoaded(data: any) {
    if(data) {
      this.user = data;
      this.timer = timer(0, 3000).subscribe(() => this.playerStatus());

      const token = this.cookieService.get('spotify-token');
      const options = {
        headers: new HttpHeaders({'Authorization': 'Bearer ' + token}),
      };
      this.http.get('https://api.spotify.com/v1/me/player/devices', options).subscribe(
        (data: any) => this.deviceLoaded(data),
        error => this.error = error);
    }
  }

  deviceLoaded(data: any) {
    if(data && data.devices.length > 0) {
      this.device = data.devices[0];
    }
  }

  play(type: string, id: string) {
    this.close();
    const token = this.cookieService.get('spotify-token');
    if(token != null && token != '' && this.device != null) {
      var body = type != 'track' ? {
        'context_uri': 'spotify:' + type + ':' + id,
        'uris': null,
        'offset': {'position': 0}
      } : {
          'context_uri': null,
          'uris': ['spotify:track:' + id],
          'offset': {'position': 0}
      };
      const options = {
        headers: new HttpHeaders({'Authorization': 'Bearer ' + token}),
      };
      this.http.put('https://api.spotify.com/v1/me/player/play?device_id=' + this.device.id, body, options).subscribe(
        (data: any) => this.playerStatus(),
        error => this.error = error);
    }
  }

  pause() {
    const token = this.cookieService.get('spotify-token');
    if(token != null && token != '' && this.device != null) {
      const options = {
        headers: new HttpHeaders({'Authorization': 'Bearer ' + token}),
      };
      this.http.put('https://api.spotify.com/v1/me/player/pause', null, options).subscribe(
        (data: any) => this.playerStatus(),
        error => this.error = error);
    }
  }

  playPause() {
    const token = this.cookieService.get('spotify-token');
    if(token != null && token != '' && this.device != null) {
      if(this.track.is_playing) {
        this.pause();
      }
      else {
        const options = {
          headers: new HttpHeaders({'Authorization': 'Bearer ' + token}),
        };
        this.http.put('https://api.spotify.com/v1/me/player/play?device_id=' + this.device.id, null, options).subscribe(
          (data: any) => this.playerStatus(),
          error => this.error = error);
      }
    }
  }

  previous() {
    const token = this.cookieService.get('spotify-token');
    if(token != null && token != '' && this.device != null) {
      const options = {
        headers: new HttpHeaders({'Authorization': 'Bearer ' + token}),
      };
      this.http.post('https://api.spotify.com/v1/me/player/previous', null, options).subscribe(
        (data: any) => this.playerStatus(),
        error => this.error = error);
    }
  }

  next() {
    const token = this.cookieService.get('spotify-token');
    if(token != null && token != '' && this.device != null) {
      const options = {
        headers: new HttpHeaders({'Authorization': 'Bearer ' + token}),
      };
      this.http.post('https://api.spotify.com/v1/me/player/next', null, options).subscribe(
        (data: any) => this.playerStatus(),
        error => this.error = error);
    }
  }

  playerStatus() {
    const token = this.cookieService.get('spotify-token');
    if(token != null && token != '' && this.device != null) {
      const options = {
        headers: new HttpHeaders({'Authorization': 'Bearer ' + token}),
      };
      this.http.get('https://api.spotify.com/v1/me/player/currently-playing', options).subscribe(
        (data: any) => this.statusLoaded(data),
        error => this.error = error);
    }
  }

  statusLoaded(data: any) {
    if(data){
      this.track = data;
    }
  }

  playYoutube(track: string){
    if(this.youtube == null) {
      this.close();
      this.youtube = this.sanitizer.bypassSecurityTrustResourceUrl('https://www.youtube.com/embed/' + track + '?autoplay=1');;
    }
    else {
      this.youtube = null;
    }
  }

  loadEvents(artist: string) {
    this.close();
    this.http.get('/api/events/' + artist).subscribe(
      (data: any) => this.eventsLoaded(data),
      error => this.error = error);
  }

  eventsLoaded(data: any) {
    if(data){
      this.events = data;
    }
  }

  close() {
    this.track = null;
    this.youtube = null;
    this.events = null;
    this.error = null;
  }

  closeEvents(){
    this.events = null;
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
