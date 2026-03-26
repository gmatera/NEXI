import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../_services/auth.service';
import { TokenStorageService } from '../_services/token-storage.service';

@Component({
  selector: 'app-sidenav',
  templateUrl: './sidenav.component.html',
  styleUrls: ['./sidenav.component.css']
})
export class SidenavComponent {
  username = this.tokenStorageService.getUserName();
  userrole = this.tokenStorageService.getUserRole();

  constructor(private route:Router, public auth: AuthService, private tokenStorageService: TokenStorageService) {
  }

}
