import { Component, OnInit } from '@angular/core';
import {AppService} from '../app.service';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class MenuComponent implements OnInit {
  searchText = '';

  constructor(private service: AppService) { }

  ngOnInit() {}

  onSearch() {
    this.service.searchText = this.searchText;
    this.service.loadDocs(0);
  }
}
