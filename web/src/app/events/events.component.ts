import { Component, Input } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
    selector: 'app-events',
    templateUrl: './events.component.html',
    styleUrls: ['./events.component.css']
})
export class EventsComponent {
    private _artist: string = null;
    events = [];
    error = null;

    constructor(private http: HttpClient) { }

    ngOnInit() {}

    get artist(): string {
        return this._artist;
    }

    @Input()
    set artist(value: string) {
        this._artist = value;
        this.loadEvents();
    }

    close() {
        this.artist = null;
        this.events = [];
    }
    
    loadEvents() {
        if(this.artist != null) {
            this.http.get('/api/events/' + this.artist).subscribe(
                (data: any) => this.setEvents(data),
                error => this.error = error);
        }
    }
    
    setEvents(events: any) {
        if(typeof(events) != 'undefined' && events != null){
            this.events.push.apply(this.events, events);
        }
    }
}