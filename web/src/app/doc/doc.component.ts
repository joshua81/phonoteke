import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {AppService} from '../app.service';

@Component({
  selector: 'app-doc',
  templateUrl: './doc.component.html',
  styleUrls: ['./doc.component.css']
})
export class DocComponent implements OnInit {
  error = null;
  docId = null;
  doc = null;
  showEvents = false;
  events = [];
  links = [];
  videos = [];
  video = null;

  constructor(private http: HttpClient, private route: ActivatedRoute, public service: AppService) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.docId = params.get('docId');
      this.loadDoc(this.docId);
      this.loadLinks(this.docId);
    });
  }

  loadDoc(id: string) {
    this.showEvents = false;
    this.events = [];
    this.http.get('/api/docs/' + id).subscribe(
      (data: any) => this.setDoc(data[0]),
      error => this.error = error);
  }

  loadEvents(id: string) {
    this.showEvents = true;
    this.http.get('/api/artists/' + id + '/events').subscribe(
      (data: any) => this.setEvents(data),
      error => this.error = error);
  }

  loadVideo(track: any) {
    this.video = track;
  }

  loadLinks(id: string) {
    this.links = [];
    this.http.get('/api/docs/' + id + '/links').subscribe(
      (data: any) => this.setLinks(data),
      error => this.error = error);
  }

  setDoc(doc: any) {
    this.doc = doc;

    if(this.service.audio){
      this.service.audio.pause();
      this.service.audio = null;
    }
    if(this.doc.audio) {
      this.service.audio = new Audio();
      this.service.audio.src = this.doc.audio;
      this.service.audio.load();
    }

    this.videos = [];
    if(this.doc.tracks) {
      this.doc.tracks.forEach(function(track: any) 
			{
        if(track.youtube)
        {
          this.videos.push(track);
        }
      }, this);
    }

    this.showEvents = false;
    this.events.splice(0, this.events.length);
    this.links.splice(0, this.links.length);

    if(this.doc.artistid) {
      this.loadLinks(this.doc.artistid);
    }
  }

  setEvents(events: any) {
    this.events.splice(0, this.events.length);
    if(typeof(events) != 'undefined' && events != null){
      this.events.push.apply(this.events, events);
    }
  }

  setLinks(links: any) {
    this.links.splice(0, this.links.length);
    this.links.push.apply(this.links, links);
    var id = this.doc.id;
    this.links = this.links.filter(function(value, index, arr){
		  return value.id != id;
		});
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
