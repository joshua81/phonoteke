import {Component} from '@angular/core';
import {AppService} from './app.service';
import {MenuComponent} from './menu/menu.component';
import {DocsComponent} from './docs/docs.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  providers: [AppService]
})
export class AppComponent {

  constructor(private service: AppService) {}

}
