import { Component, Input } from '@angular/core';
import {DocComponent} from '../doc.component';

@Component({
    selector: 'app-events',
    templateUrl: './events.component.html',
    styleUrls: ['./events.component.css']
})
export class EventsComponent {
    @Input() events: any;

    constructor(private component: DocComponent) { }

    ngOnInit() {}

    close() {
        this.component.showEvents = false;
    }
}