import { Component, OnInit } from '@angular/core';
import {Location} from '@angular/common';
import {DocComponent} from '../doc.component';

@Component({
  selector: 'doc-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class DocMenuComponent implements OnInit {

  constructor(public component: DocComponent, public location: Location) { }

  ngOnInit() {}

  back() {
    this.location.back();
  }
}
