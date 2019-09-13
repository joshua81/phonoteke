import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';

import { AppComponent } from './app.component';
import { MenuComponent } from './menu/menu.component';
import { DocsComponent } from './docs/docs.component';
import { DocComponent } from './doc/doc.component';

@NgModule({
  declarations: [
    AppComponent,
    MenuComponent,
    DocsComponent,
    DocComponent
  ],
  imports: [
    BrowserModule,
    RouterModule.forRoot([
      {path: '', component: DocsComponent},
      {path: 'docs', component: DocsComponent},
      {path: 'docs/:docId', component: DocComponent}]),
    FormsModule,
    HttpClientModule,
    InfiniteScrollModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
