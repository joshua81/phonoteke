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
import { EpisodeComponent } from './episode/episode.component';
import { TracksComponent } from './tracks/tracks.component';
import { VideoComponent } from './video/video.component';
import { LinkComponent } from './link/link.component';
import { FooterComponent } from './footer/footer.component';
import { HomeComponent } from './home/home.component';
import { AlbumComponent } from './album/album.component';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    PodcastComponent,
    EpisodeComponent,
    AlbumComponent,
    TracksComponent,
    VideoComponent,
    LinkComponent,
    FooterComponent
  ],
  imports: [
    BrowserModule,
    RouterModule.forRoot([
      {path: '', component: HomeComponent},
      {path: 'shows/:source', component: HomeComponent},
      {path: 'shows/:source/episodes/:id', component: EpisodeComponent},
      {path: 'shows/:source/:section', component: HomeComponent},
      {path: 'albums/:id', component: AlbumComponent},
      {path: ':section', component: HomeComponent}]),
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
