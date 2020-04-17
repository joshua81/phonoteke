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

  login() {
    this.component.login();
  }
}
