import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';
import { CommonModule } from '@angular/common';
import { YouTubePlayerModule } from '@angular/youtube-player';

import { AppComponent } from './app.component';
import { DocsMenuComponent } from './docs/menu/menu.component';
import { DocsComponent } from './docs/docs.component';
import { DocMenuComponent } from './doc/menu/menu.component';
import { DocComponent } from './doc/doc.component';
import { SpotifyComponent } from './spotify/spotify.component';
import { EventsComponent } from './doc/events/events.component';
import { SectionComponent } from './section/section.component';

@NgModule({
  declarations: [
    AppComponent,
    DocsMenuComponent,
    DocsComponent,
    DocMenuComponent,
    DocComponent,
    SpotifyComponent,
    SectionComponent,
    EventsComponent
  ],
  imports: [
    BrowserModule,
    RouterModule.forRoot([
      {path: '', component: DocsComponent},
      {path: ':type', component: DocsComponent},
      {path: ':type/:id', component: DocComponent}]),
    FormsModule,
    HttpClientModule,
    InfiniteScrollModule,
    YouTubePlayerModule,
    CommonModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
