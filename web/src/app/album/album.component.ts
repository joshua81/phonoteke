import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Injectable } from '@angular/core';
import {AppService} from '../app.service';

@Component({
  selector: 'app-album',
  templateUrl: './album.component.html',
  styleUrls: ['./album.component.css']
})
@Injectable()
export class AlbumComponent implements OnInit {
  albumId = null;
  album = null;
  tracks = null;
  links = null;

  constructor(private route: ActivatedRoute, private service: AppService) {
    service.albumLoaded.subscribe((album: any) => this.setAlbum(album));
    service.tracksLoaded.subscribe((tracks: any) => this.tracks = tracks.tracks);
    service.linksLoaded.subscribe((links: any) => this.links = links.links);
  }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.albumId = params.get('albumId');
      this.service.loadAlbum(this.albumId);
    });
  }

  setAlbum(album: any) {
    this.album = album;
    this.tracks = null;
    this.links = null;
    this.service.loadTracks(this.albumId);
    this.service.loadLinks(this.albumId);
  }
}
