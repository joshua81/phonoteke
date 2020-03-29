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
  spotify = null;

  constructor(private http: HttpClient, private route: ActivatedRoute, public service: AppService, public sanitizer: DomSanitizer) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.type = params.get('type');
      this.id = params.get('id');
      this.loadDoc();
      this.loadLinks();
    });
  }

  loadDoc() {
    this.service.resetEvents();
    this.http.get('/api/' + this.type + '/' + this.id).subscribe(
      (data: any) => this.setDoc(data[0]),
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
  }

  loadLinks() {
    this.links = [];
    this.http.get('/api/' + this.type + '/' + this.id + '/links').subscribe(
      (data: any) => this.setLinks(data),
      error => this.error = error);
  }

  setLinks(links: any) {
    this.links.splice(0, this.links.length);
    this.links.push.apply(this.links, links);
    var id = this.doc.id;
    this.links = this.links.filter(function(value, index, arr){
		  return value.id != id;
		});
  }

  spotifyURL() {
    return this.sanitizer.bypassSecurityTrustResourceUrl('https://open.spotify.com/embed/album/' + this.spotify);
  }

  loadAlbum() {
    this.spotify = this.doc.spalbumid;
  }

  close(event: Event){
    this.spotify = null;
  }
}
