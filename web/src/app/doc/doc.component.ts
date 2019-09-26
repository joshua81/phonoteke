import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Injectable } from '@angular/core';
import {AppService} from '../app.service';

@Component({
  selector: 'app-doc',
  templateUrl: './doc.component.html',
  styleUrls: ['./doc.component.css']
})
export class DocComponent implements OnInit {
  docId = null;
  doc = null;
  tracks = null;
  events = null;
  links = null;

  constructor(private route: ActivatedRoute, private service: AppService) {
    service.docLoaded.subscribe((doc: any) => this.setDoc(doc));
    service.tracksLoaded.subscribe((tracks: any) => this.tracks = tracks);
    service.eventsLoaded.subscribe((events: any) => this.events = events);
    service.linksLoaded.subscribe((links: any) => this.links = links);
  }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.docId = params.get('docId');
      this.service.loadDoc(this.docId);
    });
  }

  setDoc(doc: any) {
    this.doc = doc;
    this.links = null;
    this.service.loadTracks(this.docId);
    this.service.loadEvents(this.docId);
    this.service.loadLinks(this.docId);
  }
}
