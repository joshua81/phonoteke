import { Component, OnInit } from '@angular/core';
import {DocsComponent} from '../docs.component';

@Component({
  selector: 'docs-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class DocsMenuComponent implements OnInit {

  constructor(public component: DocsComponent) { }

  ngOnInit() {}

  onSearch() {
    this.component.onSearch();
  }

  resetSearch() {
    this.component.ngOnInit();
  }

  loadAlbums() {
    this.component.loadDocs(0, 'album');
  }

  loadArtists() {
    this.component.loadDocs(0, 'artist');
  }

  loadPodcasts() {
    this.component.loadDocs(0, 'podcast');
  }

  loadConcerts() {
    this.component.loadDocs(0, 'concert');
  }

  loadInterviews() {
    this.component.loadDocs(0, 'interview');
  }
}
