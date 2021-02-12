import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AppComponent } from '../app.component';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class MenuComponent implements OnInit {
  type: string = null;
  source: string = null;
  searchText: string = '';
  
  constructor(public app: AppComponent, private router: Router, private route: ActivatedRoute) {
    this.route.url.subscribe(params => {
      this.type = params.length == 0 ? 'podcasts' : params[0].path;
      this.source = params.length == 2 ? params[1].path : null;
    });
  }

  ngOnInit() {}

  search() {
    if(this.source == null) {
      this.router.navigate([this.type], {
        queryParams: { q: this.searchText }
      });
    }
    else {
      this.router.navigate([this.type + '/' + this.source], {
        queryParams: { q: this.searchText }
      });
    }
  }
}
