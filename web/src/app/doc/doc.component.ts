import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import {DomSanitizer} from '@angular/platform-browser';
import { AppService } from '../app.service';

@Component({
  selector: 'app-doc',
  templateUrl: './doc.component.html',
  styleUrls: ['./doc.component.css']
})
export class DocComponent implements OnInit {
  error = null;
  type = null;
  id = null;
  doc = null;
  links = [];
  linksOther = [];
  spotify = null;
  video = null;

  constructor(private http: HttpClient, private route: ActivatedRoute, public service: AppService, public sanitizer: DomSanitizer) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      window.scrollTo(0, 0);
      this.service.resetEvents();
      this.type = params.get('type');
      this.id = params.get('id');
      this.loadDoc();
    });
  }

  loadDoc() {
    this.http.get('/api/' + this.type + '/' + this.id).subscribe(
      (data: any) => this.setDoc(data[0]),
      error => this.error = error);
  }

  setDoc(doc: any) {
    this.doc = doc;
    this.links = [];
    this.linksOther = [];
    this.spotify = null;
    this.video = null;
    if(this.service.audio){
      this.service.audio.pause();
      this.service.audio = null;
    }
    this.loadLinks();
  }

  loadLinks() {
    this.http.get('/api/' + this.type + '/' + this.id + '/links').subscribe(
      (data: any) => this.setLinks(data),
      error => this.error = error);
  }

  setLinks(links: any) {
    var doc = this.doc;
    this.links = links.filter(function(link: any){
		  return doc.spartistid != null && doc.spartistid == link.spartistid;
    });
    this.linksOther = links.filter(function(link: any){
		  return doc.spartistid == null || doc.spartistid != link.spartistid;
    });
  }

  spotifyURL() {
    return this.doc.spalbumid != null ? 
    this.sanitizer.bypassSecurityTrustResourceUrl('https://open.spotify.com/embed/album/' + this.spotify) :
    this.sanitizer.bypassSecurityTrustResourceUrl('https://open.spotify.com/embed/artist/' + this.spotify);
  }

  youtubeURL(){
    return this.sanitizer.bypassSecurityTrustResourceUrl('https://www.youtube.com/embed/' + this.video.youtube + '?autoplay=1');
  }

  loadAlbum() {
    if(this.doc.spartistid || this.doc.spalbumid) {
      this.spotify = this.doc.spalbumid != null ? this.doc.spalbumid : this.doc.spartistid;
    }
    if(this.doc.audio != null && this.service.audio == null) {
      this.service.audio = new Audio();
      this.service.audio.src = this.doc.audio;
      this.service.audio.load();
      this.service.audio.play();
    }
  }

  loadVideo(track: any){
    this.video = track.youtube ? track : null;
  }

  close(event: Event){
    this.spotify = null;
    this.video = null;
    if(this.service.audio) {
      this.service.audio.pause();
      this.service.audio = null;
    }
  }

  playPause(event: Event){
    if(this.service.audio.paused){
      this.service.audio.play();
    }
    else{
      this.service.audio.pause();
    }
  }

  forward(event: Event){
    if(!this.service.audio.paused){
      this.service.audio.currentTime += 60.0;
    }
  }

  backward(event: Event){
    if(!this.service.audio.paused){
      this.service.audio.currentTime -= 60.0;
    }
  }
}
