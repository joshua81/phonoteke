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

  constructor(private http: HttpClient, private route: ActivatedRoute, private service: AppService) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.docId = params.get('docId');
      this.loadDoc(this.docId);
      this.loadLinks(this.docId);
    });
  }

  loadDoc(id: string) {
    console.log(this.service.server + '/api/docs/' + id);
    this.showEvents = false;
    this.events = [];
    this.http.get(this.service.server + '/api/docs/' + id).subscribe(
      (data: any) => this.setDoc(data[0]),
      error => this.error = error);
  }

  loadEvents(id: string) {
    console.log(this.service.server + '/api/artists/' + id + '/events');
    this.showEvents = true;
    this.http.get(this.service.server + '/api/artists/' + id + '/events').subscribe(
      (data: any) => this.setEvents(data),
      error => this.error = error);
  }

  loadLinks(id: string) {
    console.log(this.service.server + '/api/docs/' + id + '/links');
    this.links = [];
    this.http.get(this.service.server + '/api/docs/' + id + '/links').subscribe(
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
}
