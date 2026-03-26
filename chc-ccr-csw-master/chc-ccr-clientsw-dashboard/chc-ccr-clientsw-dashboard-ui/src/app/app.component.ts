
import { Component } from '@angular/core';
import {MatMenuModule} from '@angular/material/menu';
import { ActivatedRoute, Router, NavigationEnd } from '@angular/router';
import { catchError, map, Subscription, throwError } from 'rxjs';
import { AuthService } from './modules/_services/auth.service';
import { TokenStorageService } from './modules/_services/token-storage.service';
import { EventBusService } from './modules/_shared/event-bus.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'LMI - Local Management Interface Client-Software';
  panelOpenState = false;
  username = this.tokenStorageService.getUserName();
  userrole = this.tokenStorageService.getUserRole();
  eventBusSub?: Subscription;

  constructor(private route:Router, public auth: AuthService,private eventBusService: EventBusService,private tokenStorageService: TokenStorageService) {
  }

  ngOnInit(): void {

    if (this.auth.isAuthenticated) {
      this.route.navigateByUrl("home");
    }else{
        this.auth.logout();  
    }
    this.eventBusSub = this.eventBusService.on('logout', () => {
      this.auth.logout();
    });
  }

  ngOnDestroy(): void { 
    if (this.eventBusSub)
    this.eventBusSub.unsubscribe();
  }
  




}
