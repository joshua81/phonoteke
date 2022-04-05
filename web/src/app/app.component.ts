import {Component} from '@angular/core';
import { Location } from '@angular/common';
import { Router, NavigationEnd } from '@angular/router';
import { DomSanitizer, SafeResourceUrl} from '@angular/platform-browser';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { CookieService } from 'ngx-cookie-service';
import { Subscription, timer } from 'rxjs';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  private history:string[] = [];

  isDesktop:boolean = false;
  youtube:SafeResourceUrl = null;

  player:any = null;
  spalbumid:string = null;
  track:any = null;
  timer:Subscription = null;

  audio:any = null;
  duration:string = "";
  currentTime:string = "";
  
  events = null;
  error = null;
  loading:boolean = false;

  constructor(private router: Router, private location: Location, private http: HttpClient, private sanitizer: DomSanitizer, private cookieService: CookieService) {
    this.isDesktop = !AppComponent.hasTouchScreen();
    this.loadDevices();

    this.router.events.subscribe((event) => {
      //console.log(event);
      if (event instanceof NavigationEnd) {
        this.history.push(event.urlAfterRedirects);
      }
    });
  }
  
  ngOnInit() {}

  back(): void {
    this.history.pop();
    if (this.history.length > 0) {
      this.location.back();
    } else {
      this.router.navigateByUrl('/');
    }
  }

  refreshToken() {
    const token = this.cookieService.get('spotify-token');
    console.log('Old token: ' + token);
    if(token != null && token != '') {
      this.http.get('/api/login/refresh').subscribe(
        (data: any) => {
          console.log('New token: ' + this.cookieService.get('spotify-token'));
        });
    }
  }

  checkTracks(tracks: any[]) {
    if(this.isDesktop && tracks && tracks.length > 0) {
      const token = this.cookieService.get('spotify-token');
      if(token != null && token != '') {
        const options = {
          headers: new HttpHeaders({'Authorization': 'Bearer ' + token}),
        };
        var ids = tracks.filter(track => track.spotify != null).
          map(track => track['spotify']);
        this.http.get('https://api.spotify.com/v1/me/tracks/contains?ids=' + ids.join(), options).subscribe(
          (data: any) => {
            if(data && data.length > 0) {
              for (var i = 0; i < tracks.length; i++) {
                if(tracks[i].spotify) {
                  tracks[i].saved = data.shift();
                } 
              }
            }
          },
          error => {
            this.refreshToken();
          });
      }
    }
  }

  saveTrack(track: any, save: boolean) {
    if(track != null && track.spotify != null) {
      const token = this.cookieService.get('spotify-token');
      if(token != null && token != '') {
        const options = {
          headers: new HttpHeaders({'Authorization': 'Bearer ' + token}),
        };
        if(save) {
          this.http.put('https://api.spotify.com/v1/me/tracks?ids=' + track.spotify, null, options).subscribe(
            (data: any) => {track.saved = true},
            error => {
              this.refreshToken();
          });
        }
        else {
          this.http.delete('https://api.spotify.com/v1/me/tracks?ids=' + track.spotify, options).subscribe(
            (data: any) => {track.saved = false},
            error => {
              this.refreshToken();
          });
        }
      }
    }
  }

  loadDevices() {
    if(this.isDesktop) {
      const token = this.cookieService.get('spotify-token');
      if(token != null && token != '' && this.player == null) {
        const options = {
          headers: new HttpHeaders({'Authorization': 'Bearer ' + token}),
        };
        this.http.get('https://api.spotify.com/v1/me/player/devices', options).subscribe(
          (data: any) => {
            if(data && data.devices.length > 0) {
              this.setPlayer(data.devices[0]);
            }
            else {
              this.error = 'Nessun device trovato. Apri Spotify.';
            }
          },
          error => {
            this.refreshToken();
          });
      }
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
        (data: any) => {this.player = device},
        error => {
          this.refreshToken();
      });
    }
  }

  playPauseSpotify(spalbumid:string, type:string=null, position:number=0, trackid:string=null) {
    const token = this.cookieService.get('spotify-token');
    if(token != null && token != '' && this.player != null) {      
      if(this.spalbumid == spalbumid && this.track != null && (type == null || this.track.item.id == trackid)) {
        // pause
        if(this.track.is_playing) {
          const options = {
            headers: new HttpHeaders({'Authorization': 'Bearer ' + token}),
          };
          this.http.put('https://api.spotify.com/v1/me/player/pause?device_id=' + this.player.id, null, options).subscribe(
            (data: any) => {this.statusSpotify()},
            error => {
              this.refreshToken();
            });
        }
        // play
        else if(!this.track.is_playing) {
          const options = {
            headers: new HttpHeaders({'Authorization': 'Bearer ' + token}),
          };
          this.http.put('https://api.spotify.com/v1/me/player/play?device_id=' + this.player.id, null, options).subscribe(
            (data: any) => {this.statusSpotify()},
            error => {
              this.refreshToken();
            });
        }
      }
      // play an other album or playlist
      else if(spalbumid != null){
        if(this.timer) {
          this.timer.unsubscribe();
          this.timer = null;
        }
        if(type == 'podcast') {
          type = 'playlist';
        }

        this.spalbumid = spalbumid;
        var body = type != 'track' ? {
          'context_uri': 'spotify:' + type + ':' + spalbumid,
          'uris': null,
          'offset': {'position': position}
        } : {
          'context_uri': null,
          'uris': ['spotify:track:' + spalbumid],
          'offset': {'position': position}
        };
        const options = {
          headers: new HttpHeaders({'Authorization': 'Bearer ' + token}),
        };
        this.http.put('https://api.spotify.com/v1/me/player/play?device_id=' + this.player.id, body, options).subscribe(
          (data: any) => {
            this.statusSpotify();
            this.timer = timer(0, 5000).subscribe(() => this.statusSpotify());
          },
          error => {
            this.refreshToken();
          });
      }
    }
  }

  closeSpotify() {
    this.track = null;
    this.duration = "";
    this.currentTime = "";
    if(this.timer) {
      this.timer.unsubscribe();
      this.timer = null;
    }

    const token = this.cookieService.get('spotify-token');
    if(token != null && token != '') {
      if(this.player) {
        const options = {
          headers: new HttpHeaders({'Authorization': 'Bearer ' + token}),
        };
        this.http.put('https://api.spotify.com/v1/me/player/pause?device_id=' + this.player.id, null, options).subscribe(
          (data: any) => {},
          error => {
            this.refreshToken();
          });
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
        (data: any) => {this.statusSpotify()},
        error => {
          this.refreshToken();
        });
    }
  }

  nextSpotify() {
    const token = this.cookieService.get('spotify-token');
    if(token != null && token != '') {
      const options = {
        headers: new HttpHeaders({'Authorization': 'Bearer ' + token}),
      };
      this.http.post('https://api.spotify.com/v1/me/player/next', null, options).subscribe(
        (data: any) => {this.statusSpotify()},
        error => {
          this.refreshToken();
        });
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
            this.duration = AppComponent.formatTime(this.track.item.duration_ms/1000);
            this.currentTime = AppComponent.formatTime(this.track.progress_ms/1000);
          }},
        error => {
          this.refreshToken();
        });
    }
  }

  playPauseAudio(doc:any=null){
    if(doc != null && (this.audio == null || this.audio.src != doc.audio)) {
      this.close();
      this.audio = new Audio();
      this.audio.src = doc.audio;
      this.audio.title = doc.title;
      this.audio.artist = doc.artist;
      this.audio.cover = doc.cover;
      this.audio.ontimeupdate = () => {
        this.duration = AppComponent.formatTime(this.audio.duration);
        this.currentTime = AppComponent.formatTime(this.audio.currentTime);
      }
      this.audio.load();
    }

    if(this.audio.paused){
      this.audio.play();
    }
    else{
      this.audio.pause();
    }
  }

  setCurrentTimeSpotify(e: any){
    var obj = e.target;
    var left = 0;
    if (obj.offsetParent) {
      do {
        left += obj.offsetLeft;
      } while (obj = obj.offsetParent);
    }
    var perc = (e.clientX-left)/(e.target.childElementCount == 1 ? e.target.clientWidth : e.target.parentElement.clientWidth);
    var currentTime = Math.floor(this.track.item.duration_ms * perc);

    const token = this.cookieService.get('spotify-token');
    if(token != null && token != '') {
      const options = {
        headers: new HttpHeaders({'Authorization': 'Bearer ' + token}),
      };
      this.http.put('https://api.spotify.com/v1/me/player/seek?position_ms=' + currentTime, null, options).subscribe(
        (data: any) => {this.statusSpotify()},
        error => {
          this.refreshToken();
        });
    }
  }

  setCurrentTimeAudio(e: any){
    var obj = e.target;
    var left = 0;
    if (obj.offsetParent) {
      do {
        left += obj.offsetLeft;
      } while (obj = obj.offsetParent);
    }
    var perc = (e.clientX-left)/(e.target.childElementCount == 1 ? e.target.clientWidth : e.target.parentElement.clientWidth);
    this.audio.currentTime = Math.floor(this.audio.duration * perc);
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
    this.closeEvents();
    this.loading = true;
    this.http.get('/api/events/' + artist).subscribe(
      (data: any) => {
        this.loading = false;
        if(data) {
          this.events = data;
        }});
  }

  close() {
    // podcast player
    if(this.audio) {
      this.audio.pause();
      this.audio = null;
      this.duration = "";
      this.currentTime = "";
    }

    // spotify + events + alerts
    this.closeSpotify();
    this.closeEvents();
    this.closeAlert();

    this.youtube = null;
  }

  closeEvents(){
    this.events = null;
  }

  closeAlert(){
    this.error = null;
  }

  gtYesterday(date: string){
    var yesterday = new Date();
    yesterday.setDate(yesterday.getDate()-1);
    yesterday.setHours(0, 0, 0, 0);  
    return Date.parse(date) >= yesterday.getTime();
  }

  static formatTime(seconds: number) {
    if(!isNaN(seconds)) {
      var minutes: number = Math.floor(seconds / 60);
      var mins = (minutes >= 10) ? minutes : "0" + minutes;
      seconds = Math.floor(seconds % 60);
      var secs = (seconds >= 10) ? seconds : "0" + seconds;
      return mins + ":" + secs;
    }
    return "";
  }

  static formatDate(str: string) {
    var date: Date = new Date(str);
    return AppComponent.lpad(date.getFullYear()) + "-" + AppComponent.lpad(date.getMonth() + 1) + "-" + AppComponent.lpad(date.getDate());
  }

  static formatYear(str: string) {
    var date: Date = new Date(str);
    return AppComponent.lpad(date.getFullYear());
  }

  static lpad(n: number){
    return n <= 9 ? ("0" + n) : n;
  }

  static hasTouchScreen() {
    var hasTouchScreen = false;
    if (window.navigator.maxTouchPoints > 0) { 
      hasTouchScreen = true;
    } 
    else if (window.navigator.maxTouchPoints > 0) {
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
    return hasTouchScreen;
  }
}
