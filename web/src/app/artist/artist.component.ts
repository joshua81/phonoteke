import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {AppService} from '../app.service';

@Component({
  selector: 'app-artist',
  templateUrl: './artist.component.html',
  styleUrls: ['./artist.component.css']
})
export class ArtistComponent implements OnInit {
  artistId = '';
  artist = null;
  links = null;

  constructor(private route: ActivatedRoute, private service: AppService) {
    service.artistLoaded.subscribe((artist: any) => this.setArtist(artist));
    service.linksLoaded.subscribe((links: any) => this.links = links.links);
  }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.artistId = params.get('artistId');
      this.service.loadArtist(this.artistId);
    });
  }

  setArtist(artist: any) {
    this.artist = artist;
    this.links = null;
    this.service.loadLinks(this.artistId);
  }
}
