import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Meta } from '@angular/platform-browser'; 
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

  constructor(private http: HttpClient, private route: ActivatedRoute, private meta: Meta, public service: AppService) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.type = params.get('type');
      this.id = params.get('id');
      this.loadDoc(this.id);
      this.loadLinks(this.id);
    });
  }

  loadDoc(id: string) {
    this.service.resetEvents();
    this.http.get('/api/' + this.type + '/' + id).subscribe(
      (data: any) => this.setDoc(data[0]),
      error => this.error = error);
  }

  loadLinks(id: string) {
    this.links = [];
    this.http.get('/api/' + this.type + '/' + id + '/links').subscribe(
      (data: any) => this.setLinks(data),
      error => this.error = error);
  }

  setDoc(doc: any) {
    this.doc = doc;
    this.meta.updateTag({ name: 'og:site_name', content: 'Human Beats' });
    this.meta.updateTag({ name: 'og:title', content: this.doc.artist + ' - ' + this.doc.title + ' :: Human Beats' });
    this.meta.updateTag({ name: 'og:type', content: 'music.album' });
    //this.meta.updateTag({ name: 'og:type', content: 'music.playlist' });
    this.meta.updateTag({ name: 'og:image', content: this.doc.cover });
    this.meta.updateTag({ name: 'og:url', content: 'https://humanbeats.appspot.com/docs/' + this.doc.id });
    this.meta.updateTag({ name: 'og:locale', content: 'it_IT' });
    this.meta.updateTag({ name: 'og:description', content: this.doc.description });
    this.meta.updateTag({ name: 'music:musician', content: this.doc.artist });
    this.meta.updateTag({ name: 'music:release_date', content: this.doc.year + '-01-01' });
    this.meta.updateTag({ name: 'music:album', content: this.doc.title });
    //this.meta.updateTag({ name: 'music:creator', content: this.doc.title });

    if(this.service.audio){
      this.service.audio.pause();
      this.service.audio = null;
    }
    if(this.doc.audio) {
      this.service.audio = new Audio();
      this.service.audio.src = this.doc.audio;
      this.service.audio.load();
    }

    this.service.resetEvents();
    this.links.splice(0, this.links.length);

    if(this.doc.artistid) {
      this.loadLinks(this.doc.artistid);
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
