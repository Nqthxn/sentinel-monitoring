import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ContainerService } from '../../services/container.service';
import { ContainerData } from '../../services/container.service';

@Component({
  selector: 'app-container-list',
  standalone: true, 
  imports: [CommonModule],
  templateUrl: './container-list.component.html',
  styleUrl: './container-list.component.scss'
})
export class ContainerListComponent implements OnInit{
  containerData: ContainerData[] | null = null; 
  
  constructor(private containerService: ContainerService){}

  ngOnInit(): void {
      this.getRunningContainers();
  }

  getRunningContainers(){
    this.containerService.getRunningContainers().subscribe({
      next: (res: ContainerData[]) => {
        this.containerData = res; 
        console.log('Data : ', res);
      },
      error: (err) => {
        console.error(err);
      }
    })
  }
}
