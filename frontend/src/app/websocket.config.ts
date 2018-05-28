import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { Subscription } from 'rxjs/Subscription';
import { Subject } from 'rxjs/Subject';
import { Observer } from 'rxjs/Observer';
import { SubscriptionPayload } from './subscriptionPayload';
import { EventEmitter } from '@angular/core';
import { sleep } from './utils';

const SERVER_URL = 'ws://localhost:8080/subscriptions/websocket';

@Injectable()
export class WebSocketConfig {

    private socket: WebSocket;
    private listener: EventEmitter<any> = new EventEmitter();

    public constructor() {
        this.initConnection();
    }

    initConnection() {
        this.socket = new WebSocket(SERVER_URL);
        this.socket.onopen = event => {
            console.log('Socket connection opened');
        };
        this.socket.onclose = event => {
        };
            console.log('Socket connection closed');
        this.socket.onmessage = event => {
            this.listener.emit(JSON.parse(event.data));
        };
        this.socket.onerror = event => {
            console.log('error: ' + event);
            sleep(1000).then(() => this.initConnection());
        };
    }

    public send(data: string) {
        this.socket.send(data);
    }

    public close() {
        this.socket.close();
    }

    public getEventListener() {
        return this.listener;
    }
}
