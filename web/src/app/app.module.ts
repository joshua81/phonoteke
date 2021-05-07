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
import { TracksComponent } from './tracks/tracks.component';
import { VideoComponent } from './video/video.component';
import { LinkComponent } from './link/link.component';
import { FooterComponent } from './footer/footer.component';
import { AuthorsComponent } from './authors/authors.component';
import { AuthorsMenuComponent } from './authors/menu/menu.component';

@NgModule({
  declarations: [
    AppComponent,
    AuthorsComponent,
    AuthorsMenuComponent,
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
      {path: '', component: AuthorsComponent},
      {path: ':source', component: PodcastComponent},
      {path: 'docs/:id', component: DocComponent}]),
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
