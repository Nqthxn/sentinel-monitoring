import { Injectable } from '@angular/core';
import { Client, IMessage } from '@stomp/stompjs';
import { BehaviorSubject } from 'rxjs';
import { ContainerData } from './container.service';
import SockJS from 'sockjs-client';

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {
  private client: Client;
  public stats$: BehaviorSubject<ContainerData[]> = new BehaviorSubject<ContainerData[]>([]);


  constructor() {
    this.client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws-sentinel'),
      reconnectDelay: 5000,
      debug: (str) => { console.log(new Date(), str); }
    });

    this.client.onConnect = (frame) => {
      console.log('Connected to WebSockets:', frame);
      this.client.subscribe('/topic/stats', (message: IMessage) => {
        const stats = JSON.parse(message.body) as ContainerData[];
        this.stats$.next(stats);
      });
    };

    this.client.onStompError = (frame) => {
      console.error('Broker reported error: ' + frame.headers['message']);
      console.error('Additional Details: ', frame.body);
    };
  
  }

  activate(): void{
    this.client.activate();
  }

  deactivate(): void{
    this.client.deactivate();
  }
}
