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
  albumId = '';
  album = null;

  constructor(private route: ActivatedRoute, private service: AppService) {
    service.albumLoaded.subscribe((album: any) => this.album = album);
  }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.albumId = params.get('albumId');
    });
    this.service.loadAlbum(this.albumId);
  }
}
