import {Component, OnInit, Input} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-section',
  templateUrl: './section.component.html',
  styleUrls: ['./section.component.css']
})
export class SectionComponent implements OnInit {
  private _searchText: string = null;
  @Input() section: string = null;
  error = null;
  page: number = 0;
  scroll: number = 0;
  docs = [];

  constructor(private http: HttpClient, private route: ActivatedRoute) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.searchText = '';
      if(params.get('type') == 'starred') {
        //this.loadStarred();
        this.loadDocs(0);
      }
      else {
        this.loadDocs(0);
      }
    });
  }

  @Input()
  set searchText(val: string) {
    this._searchText = val;
    this.onSearch();
  }
  
  get searchText() {
    return this._searchText;
  }

  onSearch() {
    if(this.searchText && this.searchText != '') {
      this.loadDocs(0);
    }
  }

  scrollDocs() {
    this.loadDocs(this.page+1);
  }

  loadDocs(page: number) {
    if(this.section) {
      this.page = page;
      if(this.page == 0) {
        this.docs.splice(0, this.docs.length);
      }

      this.http.get('/api/' + this.section + '?p=' + this.page + '&q=' + this.searchText).subscribe(
        (data: any) => this.docsLoaded(data),
        error => this.error = error);
    }
  }

  loadStarred() {
    this.page = 0;
    this.scroll = 0;
    this.docs.splice(0, this.docs.length);

    this.http.get('/api/user/starred').subscribe(
      (data: any) => this.docsLoaded(data),
      error => this.error = error);
  }

  docsLoaded(data: any) {
    this.docs.push.apply(this.docs, data);
  }

  next() {
    this.scroll++;
    var slider = document.querySelector('#'+this.section);
    slider.scrollTo((206 * Math.floor(slider.clientWidth / 206) * this.scroll), 0);
  }

  previous() {
    if(this.scroll > 0) {
      this.scroll--;
      var slider = document.querySelector('#'+this.section);
      slider.scrollTo((206 * Math.floor(slider.clientWidth / 206) * this.scroll), 0);
    }
  }

  showNextPrev() {
    // s: 540px, m: 720px, l: 960px, xl: 1140px
    // var ua = navigator.userAgent;
    var slider = document.querySelector('#'+this.section);
    return slider ? (window.innerWidth >= 960 && slider.scrollWidth > window.innerWidth) : window.innerWidth >= 960;
  }
}
