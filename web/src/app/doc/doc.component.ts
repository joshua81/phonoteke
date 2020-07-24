import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import {DomSanitizer} from '@angular/platform-browser';

@Component({
  selector: 'app-doc',
  templateUrl: './doc.component.html',
  styleUrls: ['./doc.component.css']
})
export class DocComponent implements OnInit {
  error = null;
  id: string = null;
  doc = null;
  links = [];
  podcasts = [];
  spotify: string = null;
  songkick: string = null;
  audio = null;
  audioCurrentTime: string = null;
  audioDuration: string = null;


  constructor(private http: HttpClient, private route: ActivatedRoute, public sanitizer: DomSanitizer) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      window.scrollTo(0, 0);
      this.id = params.get('id');
      this.loadDoc();
    });
  }

  loadDoc() {
    this.http.get('/api/docs/' + this.id).subscribe(
      (data: any) => this.setDoc(data[0]),
      error => this.error = error);
  }

  setDoc(doc: any) {
    this.doc = doc;
    this.links = [];
    this.podcasts = [];
    this.spotify = null;
    this.songkick = null;
    if(this.audio){
      this.audio.pause();
      this.audio = null;
      this.audioCurrentTime = null;
      this.audioDuration = null;
    }
    this.loadLinks();
  }

  loadLinks() {
    this.http.get('/api/docs/' + this.id + '/links').subscribe(
      (data: any) => this.setLinks(data),
      error => this.error = error);
  }

  setLinks(links: any) {
    this.links = links.filter(function(link: any){
		  return link.type != 'podcast';
    });
    this.podcasts = links.filter(function(link: any){
		  return link.type == 'podcast';
    });
  }

  spotifyURL() {
    return this.doc.type == 'podcast' ?
    this.sanitizer.bypassSecurityTrustResourceUrl('https://open.spotify.com/embed/track/' + this.spotify) :
    this.doc.spalbumid != null ?
    this.sanitizer.bypassSecurityTrustResourceUrl('https://open.spotify.com/embed/album/' + this.spotify) :
    this.sanitizer.bypassSecurityTrustResourceUrl('https://open.spotify.com/embed/artist/' + this.spotify);
  }

  loadAlbum() {
    if(this.doc.spartistid || this.doc.spalbumid) {
      this.spotify = this.doc.spalbumid != null ? this.doc.spalbumid : this.doc.spartistid;
    }
    if(this.doc.audio != null && this.audio == null) {
      this.audio = new Audio();
      this.audio.src = this.doc.audio;
      this.audio.ontimeupdate = () => {
        this.audioDuration = this.formatTime(this.audio.duration);
        this.audioCurrentTime = this.formatTime(this.audio.currentTime);
      }
      this.audio.load();
      this.audio.play();
    }
  }

  loadTrack(track: any) {
    this.spotify = track.spotify;
  }

  close(event: Event){
    this.spotify = null;
    if(this.audio) {
      this.audio.pause();
      this.audio = null;
      this.audioCurrentTime = null;
      this.audioDuration = null;
    }
  }

  playPause(event: Event){
    if(this.audio.paused){
      this.audio.play();
    }
    else{
      this.audio.pause();
    }
  }

  forward(event: Event){
    if(!this.audio.paused){
      this.audio.currentTime += 60.0;
    }
  }

  backward(event: Event){
    if(!this.audio.paused){
      this.audio.currentTime -= 60.0;
    }
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

  loadEvents(artistid: string) {
    this.songkick = artistid;
  }

  formatTime(seconds: number) {
    if(!isNaN(seconds)) {
      var minutes: number = Math.floor(seconds / 60);
      var mins = (minutes >= 10) ? minutes : "0" + minutes;
      seconds = Math.floor(seconds % 60);
      var secs = (seconds >= 10) ? seconds : "0" + seconds;
      return mins + ":" + secs;
    }
  }
}
