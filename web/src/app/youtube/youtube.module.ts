import { NgModule } from '@angular/core';
import { YouTubePlayerModule } from '@angular/youtube-player';
import { YoutubeComponent } from './youtube.component';

@NgModule({
  imports: [YouTubePlayerModule],
  declarations: [YoutubeComponent],
  exports: [YoutubeComponent]
})
export class YoutubeModule {}