import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';

import { AppComponent } from './app.component';
import { MenuComponent } from './menu/menu.component';
import { AlbumsComponent } from './albums/albums.component';
import { AlbumComponent } from './album/album.component';
import { ArtistsComponent } from './artists/artists.component';
import { ArtistComponent } from './artist/artist.component';

@NgModule({
  declarations: [
    AppComponent,
    MenuComponent,
    AlbumsComponent,
    AlbumComponent,
    ArtistsComponent,
    ArtistComponent
  ],
  imports: [
    BrowserModule,
    RouterModule.forRoot([
      {path: '', component: AlbumsComponent},
      {path: 'albums', component: AlbumsComponent},
      {path: 'albums/:albumId', component: AlbumComponent},
      {path: 'artists', component: ArtistsComponent},
      {path: 'artists/:artistId', component: ArtistComponent}]),
    FormsModule,
    HttpClientModule,
    InfiniteScrollModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
