import { Component, OnInit } from '@angular/core';
import {DocsComponent} from '../docs.component';

@Component({
  selector: 'docs-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class DocsMenuComponent implements OnInit {
  searchText = '';

  constructor(public component: DocsComponent) { }

  ngOnInit() {}

  onSearch() {
    this.component.onSearch(this.searchText);
  }

  resetSearch() {
    this.searchText = '';
    this.component.onSearch(this.searchText);
  }

  login() {
    this.component.login();
  }
}
