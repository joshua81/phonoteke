import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common'
import { AppComponent } from 'src/app/app.component';
import { DocComponent } from '../doc.component';

@Component({
  selector: 'doc-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class DocMenuComponent implements OnInit {

  constructor(private location: Location, public app: AppComponent, public doc: DocComponent) { }

  ngOnInit() {}

  back(): void {
    this.location.back()
  }
}
