import { ElementRef, ViewChild, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AppComponent } from '../../app.component';

@Component({
  selector: 'authors-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class AuthorsMenuComponent implements OnInit {
  searchText: string = '';
  showSearch: boolean = false;
  
  @ViewChild('input', { static: false }) 
  set input(element: ElementRef<HTMLInputElement>) {
    if(element) {
      element.nativeElement.focus();
    }
  }

  constructor(public app: AppComponent, private router: Router) {
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
