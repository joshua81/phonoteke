import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-docs',
  templateUrl: './docs.component.html',
  styleUrls: ['./docs.component.css']
})
export class DocsComponent implements OnInit {
  error = null;
  searchText = '';
  user = null;

  constructor(private http: HttpClient, private route: ActivatedRoute) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      window.scrollTo(0, 0);
      this.searchText = '';
      this.loadUser();
    });
  }

  onSearch(searchText: string) {
    this.searchText = searchText;
  }

  login() {
    if(this.user == null) {
      this.http.get('/api/login').subscribe(
        (data: any) => console.log(data),
        error => this.error = error);
    }
  }

  loadUser() {
    if(this.user == null) {
      this.http.get('/api/user').subscribe(
        (data: any) => this.userLoaded(data),
        error => this.error = error);
    }
  }

  userLoaded(data: any) {
    if(data) {
      this.user = data.images[0].url;
    }
  }
}
