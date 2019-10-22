import { Component, OnInit } from '@angular/core';
import {AppService} from '../../app.service';

@Component({
  selector: 'docs-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class DocsMenuComponent implements OnInit {
  searchText = '';

  constructor(private service: AppService) { }

  ngOnInit() {}

  onSearch() {
    this.service.searchHandler(this.searchText);
  }
}
