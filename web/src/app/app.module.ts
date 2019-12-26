import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';

import { AppComponent } from './app.component';
import { DocsMenuComponent } from './docs/menu/menu.component';
import { DocsComponent } from './docs/docs.component';
import { DocMenuComponent } from './doc/menu/menu.component';
import { DocComponent } from './doc/doc.component';
import { EventsComponent } from './doc/events/events.component';
import { YoutubeModule } from './youtube/youtube.module';

@NgModule({
  declarations: [
    AppComponent,
    DocsMenuComponent,
    DocsComponent,
    DocMenuComponent,
    DocComponent,
    EventsComponent
  ],
  imports: [
    BrowserModule,
    RouterModule.forRoot([
      {path: '', component: DocsComponent},
      {path: 'docs', component: DocsComponent},
      {path: 'docs/:docId', component: DocComponent}]),
    FormsModule,
    HttpClientModule,
    InfiniteScrollModule,
    YoutubeModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
