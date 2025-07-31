import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute} from '@angular/router';
import { ContainerData, ContainerService, ContainerStatHistory } from '../../services/container.service';
@Component({
  selector: 'app-container-details',
  imports: [CommonModule],
  templateUrl: './container-details.component.html',
  styleUrl: './container-details.component.scss'
})
export class ContainerDetailsComponent implements OnInit{
  historicalData: ContainerStatHistory[] | null = null;
  containerId: string | null = null;

  constructor(private route: ActivatedRoute, private containerService: ContainerService){}

  ngOnInit(): void {
      this.containerId = this.route.snapshot.paramMap.get('id');

      if(this.containerId){
        this.containerService.getContainerHistory(this.containerId).subscribe({
          next: (data) => {
            this.historicalData = data;
            console.log('Historical Data Received : ', data);
          },
          error: (err) => {
            console.error('Failed to fetch historical data:', err);
          }
        });
      }
  }
}
