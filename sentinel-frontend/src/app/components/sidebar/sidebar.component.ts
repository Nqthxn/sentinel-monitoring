import { NgClass } from '@angular/common';
import { Component, NgModule } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import {  faFontAwesome, faHouse, faBoxOpen } from '@fortawesome/free-solid-svg-icons'; 
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome'; // Import the module

@Component({
  selector: 'app-sidebar',
  imports: [RouterLink, NgClass, FontAwesomeModule],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss'
})
export class SidebarComponent {
  faHouse = faHouse;
  faBoxOpen = faBoxOpen;
  constructor(public router: Router){}
}
