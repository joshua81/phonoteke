import { Component, Input } from '@angular/core';
import { AppService } from '../app.service';

@Component({
    selector: 'app-events',
    templateUrl: './events.component.html',
    styleUrls: ['./events.component.css']
})
export class EventsComponent {
    @Input() events = [];

    constructor(private service: AppService) { }

    ngOnInit() {}

    close() {
        this.service.showEvents = false;
    }
}