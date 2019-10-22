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
}
