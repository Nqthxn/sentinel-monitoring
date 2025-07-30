import { Component } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faHouse, faSearch, faBell, faCog, faSun, faMoon, faCircleUser } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-header',
  imports: [FontAwesomeModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {
  faHouse = faHouse;
  faSearch = faSearch;
  faBell = faBell;
  faCog = faCog;
  faSun = faSun;
  faMoon = faMoon;
  faCircleUser = faCircleUser;
}
