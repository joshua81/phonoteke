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
import { PodcastComponent } from './podcast/podcast.component';
import { PodcastMenuComponent } from './podcast/menu/menu.component';
import { DocComponent } from './doc/doc.component';
import { DocMenuComponent } from './doc/menu/menu.component';
import { TracksComponent } from './doc/tracks/tracks.component';
import { VideoComponent } from './doc/video/video.component';
import { LinkComponent } from './doc/link/link.component';
import { FooterComponent } from './footer/footer.component';
import { HomeComponent } from './home/home.component';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    PodcastComponent,
    PodcastMenuComponent,
    DocComponent,
    DocMenuComponent,
    TracksComponent,
    VideoComponent,
    LinkComponent,
    FooterComponent
  ],
  imports: [
    BrowserModule,
    RouterModule.forRoot([
      {path: '', component: HomeComponent},
      {path: 'podcasts/:source', component: PodcastComponent},
      {path: ':id', component: DocComponent}]),
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
