import { Component, OnInit } from '@angular/core';
import { DocsComponent } from '../docs.component';
import { AppComponent } from '../../app.component';

@Component({
  selector: 'podcasts-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class DocsMenuComponent implements OnInit {

  constructor(public app: AppComponent, public docs: DocsComponent) { }

  ngOnInit() {}

  toggleSource(source: string) {
    this.docs.source = this.docs.source == source ? null : source;
  }
}
