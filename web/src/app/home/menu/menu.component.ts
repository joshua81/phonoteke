import { ElementRef, Component, ViewChild, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'home-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class HomeMenuComponent implements OnInit {
  searchText: string = '';
  showSearch: boolean = false;
  
  @ViewChild('input', { static: false }) 
  set input(element: ElementRef<HTMLInputElement>) {
    if(element) {
      element.nativeElement.focus();
    }
  }
  constructor(private router: Router) {}

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
