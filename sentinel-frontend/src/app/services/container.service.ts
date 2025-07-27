import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface ContainerData{
  id: string,
  name: string,
  status: string,
  image: string, 
  cpu: CpuStats,
  memory: MemoryStats,
  network: NetworkStats
}
export interface CpuStats{
  usagePercent: number
}

export interface MemoryStats{
  usageBytes: number,
  limitBytes: number 
}

export interface NetworkStats{
  rxBytes: number,
  txBytes: number,
}

@Injectable({
  providedIn: 'root'
})
export class ContainerService {
  private baseUrl = '/api/v1';
  constructor(private http: HttpClient) { }

  getRunningContainers(): Observable<ContainerData[]>{
    return this.http.get<ContainerData[]>(`${this.baseUrl}/containers`);
  }
}
