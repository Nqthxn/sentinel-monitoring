import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { ContainerService } from '../../services/container.service';
import { ContainerData } from '../../services/container.service';
import { FormatBytesPipe } from "../../pipes/format-bytes.pipe";
import { RouterLink } from '@angular/router';
import { Subscription } from 'rxjs';
import { WebsocketService } from '../../services/websocket.service';

@Component({
  selector: 'app-container-list',
  standalone: true, 
  imports: [CommonModule, FormatBytesPipe, RouterLink],
  templateUrl: './container-list.component.html',
  styleUrl: './container-list.component.scss'
})
export class ContainerListComponent implements OnInit, OnDestroy{
  containerData: ContainerData[] = [];
  private statsSubscription: Subscription | undefined; 

  constructor(private websocketService: WebsocketService){}

  ngOnInit(): void {
      this.statsSubscription = this.websocketService.stats$.subscribe(stats => {
        this.containerData = stats;
      });

      this.websocketService.activate();
  }

  ngOnDestroy(): void {
      if(this.statsSubscription){
        this.statsSubscription.unsubscribe();
      }
      this.websocketService.deactivate();
  }



}
