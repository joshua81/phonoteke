import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';
import { CookieService } from 'ngx-cookie-service';
import { CommonModule } from '@angular/common';
import { YouTubePlayerModule } from '@angular/youtube-player';

import { AppComponent } from './app.component';
import { DocsComponent } from './docs/docs.component';
import { MenuComponent } from './menu/menu.component';
import { PodcastsComponent } from './podcasts/podcasts.component';
import { DocMenuComponent } from './doc/menu/menu.component';
import { DocComponent } from './doc/doc.component';
import { TracksComponent } from './tracks/tracks.component';
import { VideoComponent } from './video/video.component';
import { LinkComponent } from './link/link.component';
import { FooterComponent } from './footer/footer.component';

@NgModule({
  declarations: [
    AppComponent,
    MenuComponent,
    DocsComponent,
    PodcastsComponent,
    DocMenuComponent,
    DocComponent,
    TracksComponent,
    VideoComponent,
    LinkComponent,
    FooterComponent
  ],
  imports: [
    BrowserModule,
    RouterModule.forRoot([
      {path: '', component: PodcastsComponent},
      {path: 'docs/:id', component: DocComponent},
      {path: 'albums', component: DocsComponent},
      {path: 'podcasts', component: PodcastsComponent},
      {path: 'podcasts/:source', component: PodcastsComponent}]),
    FormsModule,
    HttpClientModule,
    InfiniteScrollModule,
    YouTubePlayerModule,
    CommonModule
  ],
  providers: [CookieService],
  bootstrap: [AppComponent]
})
export class AppModule { }
