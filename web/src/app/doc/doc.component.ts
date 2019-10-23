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
  events = [];
  links = [];

  constructor(private http: HttpClient, private route: ActivatedRoute, private service: AppService) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.docId = params.get('docId');
      this.loadDoc(this.docId);
    });
  }

  loadDoc(id: string) {
    console.log(this.service.server + '/api/docs/' + id);
    this.http.get(this.service.server + '/api/docs/' + id).subscribe(
      (data: any) => this.setDoc(data[0]),
      error => this.error = error);
  }

  loadEvents(id: string) {
    console.log(this.service.server + '/api/docs/' + id + '/events');
    this.http.get(this.service.server + '/api/docs/' + id + '/events').subscribe(
      (data: any) => this.setEvents(data),
      error => this.error = error);
  }

  loadLinks(id: string) {
    console.log(this.service.server + '/api/docs/' + id + '/links');
    this.http.get(this.service.server + '/api/docs/' + id + '/links').subscribe(
      (data: any) => this.setLinks(data),
      error => this.error = error);
  }

  setDoc(doc: any) {
    this.doc = doc;

    if(this.service.audio)
    {
      this.service.audio.pause();
      this.service.audio = null;
    }
    if(this.doc.audio) {
      this.service.audio = new Audio();
      this.service.audio.src = this.doc.audio;
      this.service.audio.load();
    }
    if(this.doc.spalbumid && this.doc.spalbumid == 'UNKNOWN') {
      this.doc.spalbumid = null;
    }

    this.events.splice(0, this.events.length);
    this.links.splice(0, this.links.length);
    this.loadEvents(this.docId);
    this.loadLinks(this.docId);
  }

  setEvents(events: any) {
    if(typeof(events) != 'undefined' && events != null && events.length > 0){
      this.events.push.apply(this.events, events);
    }
  }

  setLinks(links: any) {
    if(typeof(links) != 'undefined' && links != null && links.length > 0){
      this.links.push.apply(this.links, links);
    }
  }
}
