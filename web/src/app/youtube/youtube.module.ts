import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { YouTubePlayerModule } from '@angular/youtube-player';
import { YoutubeComponent } from './youtube.component';

@NgModule({
  imports: [YouTubePlayerModule, CommonModule],
  declarations: [YoutubeComponent],
  exports: [YoutubeComponent]
})
export class YoutubeModule {}