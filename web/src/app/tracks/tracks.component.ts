import { Component, OnInit, Input } from '@angular/core';
import { DocComponent } from '../doc/doc.component';
import { DomSanitizer} from '@angular/platform-browser';

@Component({
  selector: 'app-tracks',
  templateUrl: './tracks.component.html',
  styleUrls: ['./tracks.component.css']
})
export class TracksComponent implements OnInit {
  @Input() tracks = null;
  spotify = null;

  constructor(public component: DocComponent, private sanitizer: DomSanitizer) {}

  ngOnInit() {
  }

  toggleSpotify(id: string) {
    if(this.spotify == null) {
      this.spotify = 'https://open.spotify.com/embed/track/' + id;
    }
    else {
      this.spotify = null;
    }
  }

  spotifyUrl() {
    return this.sanitizer.bypassSecurityTrustResourceUrl(this.spotify);
  }
}