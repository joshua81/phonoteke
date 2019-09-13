import {Component, OnInit} from '@angular/core';
import {AppService} from '../app.service';

@Component({
  selector: 'app-docs',
  templateUrl: './docs.component.html',
  styleUrls: ['./docs.component.css']
})
export class DocsComponent implements OnInit {
  page = 0;
  docs = [];

  constructor(private service: AppService) {}

  ngOnInit() {
    this.service.docsLoaded.subscribe((msg: any) => this.pageLoaded(msg));
    this.loadPage();
  }

  pageLoaded(msg: any) {
    if(msg.pageNum === 0) {
      this.page = 1;
      this.docs.splice(0, this.docs.length);
    }
    this.docs.push.apply(this.docs, msg.content);
  }

  loadPage() {
    this.service.loadDocs(this.page++);
  }
}
