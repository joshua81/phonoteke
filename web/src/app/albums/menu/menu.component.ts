import { ElementRef, ViewChild, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AppComponent } from '../../app.component';

@Component({
  selector: 'albums-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class AlbumsMenuComponent implements OnInit {
  source: string = null;
  searchText: string = '';
  showSearch: boolean = false;
  
  @ViewChild('input', { static: false }) 
  set input(element: ElementRef<HTMLInputElement>) {
    if(element) {
      element.nativeElement.focus();
    }
  }

  constructor(public app: AppComponent, private router: Router, private route: ActivatedRoute) {
    this.route.url.subscribe(params => {
      this.source = params.length == 0 ? 'podcasts' : params[0].path;
    });
  }

  ngOnInit() {}

  search() {
    if(this.searchText != null && this.searchText != '') {
      this.router.navigate([this.router.url.split("?")[0]], {queryParams: { q: this.searchText }});
    }
    this.toggleSearch();
  }

  toggleSearch() {
    this.showSearch = !this.showSearch;
    this.searchText = '';
  }
}
