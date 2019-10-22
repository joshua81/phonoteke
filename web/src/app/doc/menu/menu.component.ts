import { Component, OnInit } from '@angular/core';
import {AppService} from '../../app.service';
import {DocComponent} from '../doc.component';

@Component({
  selector: 'doc-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class DocMenuComponent implements OnInit {

  constructor(private service: AppService, private component: DocComponent) { }

  ngOnInit() {}


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
