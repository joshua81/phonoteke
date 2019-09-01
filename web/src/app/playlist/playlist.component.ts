import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {AppService} from '../app.service';

@Component({
  selector: 'app-playlist',
  templateUrl: './playlist.component.html',
  styleUrls: ['./playlist.component.css']
})
export class PlaylistComponent implements OnInit {
  playlistId = '';
  playlist = null;

  constructor(private route: ActivatedRoute, private service: AppService) {
    service.playlistLoaded.subscribe((playlist: any) => this.playlist = playlist);
  }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.playlistId = params.get('playlistId');
    });
    this.service.loadAlbum(this.playlistId);
  }
}
