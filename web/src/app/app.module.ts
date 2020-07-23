import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';
import { CommonModule } from '@angular/common';
import { YouTubePlayerModule } from '@angular/youtube-player';

import { AppComponent } from './app.component';
import { PodcastsMenuComponent } from './podcasts/menu/menu.component';
import { PodcastsComponent } from './podcasts/podcasts.component';
import { DocMenuComponent } from './doc/menu/menu.component';
import { DocComponent } from './doc/doc.component';
import { TracksComponent } from './tracks/tracks.component';
import { EventsComponent } from './events/events.component';
import { SectionComponent } from './section/section.component';
import { VideoComponent } from './video/video.component';
import { LinkComponent } from './link/link.component';
import { FooterComponent } from './footer/footer.component';

@NgModule({
  declarations: [
    AppComponent,
    PodcastsMenuComponent,
    PodcastsComponent,
    DocMenuComponent,
    DocComponent,
    TracksComponent,
    SectionComponent,
    VideoComponent,
    LinkComponent,
    FooterComponent,
    EventsComponent
  ],
  imports: [
    BrowserModule,
    RouterModule.forRoot([
      {path: '', component: PodcastsComponent},
      {path: 'docs/:id', component: DocComponent},]),
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
