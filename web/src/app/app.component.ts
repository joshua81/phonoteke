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
  player = null;
  album = null;
  track = null;
  timer = null;

  audio = null;
  
  events = null;
  error = null;

  constructor(private http: HttpClient, private sanitizer: DomSanitizer, private cookieService: CookieService) {}

  login() {
    const token = this.cookieService.get('spotify-token');
    if(token != null && token != '' && this.user == null) {
      const options = {
        headers: new HttpHeaders({'Authorization': 'Bearer ' + token}),
      };
      this.http.get('https://api.spotify.com/v1/me', options).subscribe(
        (data: any) => { 
          if(data) {
            this.user = data;
            this.loadDevices();
          }},
        error => this.error = error);
    }
  }

  loadDevices() {
    const token = this.cookieService.get('spotify-token');
    if(token != null && token != '') {
      const options = {
        headers: new HttpHeaders({'Authorization': 'Bearer ' + token}),
      };
      this.http.get('https://api.spotify.com/v1/me/player/devices', options).subscribe(
        (data: any) => {
          if(data && data.devices.length > 0) {
            this.setPlayer(data.devices[0]);
          }},
        error => this.error = error);
    }
  }

  setPlayer(device: any) {
    const token = this.cookieService.get('spotify-token');
    if(token != null && token != '') {
      var body = {
        'device_ids': [device.id]
      };
      const options = {
        headers: new HttpHeaders({'Authorization': 'Bearer ' + token}),
      };
      this.http.put('https://api.spotify.com/v1/me/player', body, options).subscribe(
        (data: any) => this.player = device,
        error => this.error = error);
    }
  }

  playSpotify(type: string, id: string, pos: number=0) {
    this.close();
    type = type == 'podcast' ? 'playlist' : type;
    const token = this.cookieService.get('spotify-token');
    if(token != null && token != '') {
      var body = type != 'track' ? {
        'context_uri': 'spotify:' + type + ':' + id,
        'uris': null,
        'offset': {'position': pos}
      } : {
        'context_uri': null,
        'uris': ['spotify:track:' + id],
        'offset': {'position': pos}
      };
      const options = {
        headers: new HttpHeaders({'Authorization': 'Bearer ' + token}),
      };
      this.http.put('https://api.spotify.com/v1/me/player/play?device_id=' + this.player.id, body, options).subscribe(
        (data: any) => {
          this.album = id;
          this.timer = timer(0, 3000).subscribe(() => this.statusSpotify());
        },
        error => this.error = error);
    }
  }

  pauseSpotify() {
    const token = this.cookieService.get('spotify-token');
    if(token != null && token != '') {
      const options = {
        headers: new HttpHeaders({'Authorization': 'Bearer ' + token}),
      };
      this.http.put('https://api.spotify.com/v1/me/player/pause?device_id=' + this.player.id, null, options).subscribe(
        error => this.error = error);
    }
  }

  playPauseSpotify() {
    const token = this.cookieService.get('spotify-token');
    if(token != null && token != '') {
      if(this.track.is_playing) {
        this.pauseSpotify();
      }
      else {
        const options = {
          headers: new HttpHeaders({'Authorization': 'Bearer ' + token}),
        };
        this.http.put('https://api.spotify.com/v1/me/player/play?device_id=' + this.player.id, null, options).subscribe(
          error => this.error = error);
      }
    }
  }

  previousSpotify() {
    const token = this.cookieService.get('spotify-token');
    if(token != null && token != '') {
      const options = {
        headers: new HttpHeaders({'Authorization': 'Bearer ' + token}),
      };
      this.http.post('https://api.spotify.com/v1/me/player/previous', null, options).subscribe(
        error => this.error = error);
    }
  }

  nextSpotify() {
    const token = this.cookieService.get('spotify-token');
    if(token != null && token != '') {
      const options = {
        headers: new HttpHeaders({'Authorization': 'Bearer ' + token}),
      };
      this.http.post('https://api.spotify.com/v1/me/player/next', null, options).subscribe(
        error => this.error = error);
    }
  }

  statusSpotify() {
    const token = this.cookieService.get('spotify-token');
    if(token != null && token != '') {
      const options = {
        headers: new HttpHeaders({'Authorization': 'Bearer ' + token}),
      };
      this.http.get('https://api.spotify.com/v1/me/player/currently-playing', options).subscribe(
        (data: any) => {
          if(data) {
            this.track = data;
          }},
        error => this.error = error);
    }
  }

  playPauseAudio(audio: any=null, title: string=null, artist: string=null, cover: string=null){
    if(this.audio == null) {
      this.close();
      this.audio = new Audio();
      this.audio.src = audio;
      this.audio.title = title;
      this.audio.artist = artist;
      this.audio.cover = cover;
      /*this.audio.ontimeupdate = () => {
        this.audioDuration = this.formatTime(this.audio.duration);
        this.audioCurrentTime = this.formatTime(this.audio.currentTime);
      }*/
      this.audio.load();
    }

    if(this.audio.paused){
      this.audio.play();
    }
    else{
      this.audio.pause();
    }
  }

  forwardAudio(){
    if(!this.audio.paused){
      this.audio.currentTime += 60.0;
    }
  }

  backwardAudio(){
    if(!this.audio.paused){
      this.audio.currentTime -= 60.0;
    }
  }

  /*formatTime(seconds: number) {
    if(!isNaN(seconds)) {
      var minutes: number = Math.floor(seconds / 60);
      var mins = (minutes >= 10) ? minutes : "0" + minutes;
      seconds = Math.floor(seconds % 60);
      var secs = (seconds >= 10) ? seconds : "0" + seconds;
      return mins + ":" + secs;
    }
  }*/

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
    this.closeEvents();
    this.http.get('/api/events/' + artist).subscribe(
      (data: any) => {
        if(data) {
          this.events = data;
        }},
      error => this.error = error);
  }

  close() {
    this.album = null;
    this.track = null;
    this.timer = null;

    if(this.audio) {
      this.audio.pause();
      this.audio = null;
    }

    this.youtube = null;
    this.events = null;
    this.error = null;
  }

  closeEvents(){
    this.events = null;
    this.error = null;
  }

  closeAlert(){
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
