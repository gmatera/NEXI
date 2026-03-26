import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { Router } from '@angular/router';
import { AuthService } from '../../_services/auth.service';
import { CommonConfigComponent } from '../common.config-component';
import { ConfigRouteInterfaceDTO, ConfigRouteInterfaceFilterDTO } from './dto/route-interface-dto';
import { RouteInterfaceService } from './route-interface-service';


@Component({
  selector: 'route-interface-component',
  templateUrl: './route-interface.component.html',
  styleUrls: ['./route-interface.component.css']
})
export class RouteInterfaceComponent extends CommonConfigComponent<ConfigRouteInterfaceFilterDTO, ConfigRouteInterfaceDTO, RouteInterfaceService>  implements OnInit {

  @ViewChild(MatPaginator)
  paginator!: MatPaginator;

  @ViewChild(MatSort, { static: false }) set sort(s: MatSort) {
    if (this.routeInterfaceService.dataSource) {
      this.routeInterfaceService.dataSource.sort = s;
    }
  }
  sortData(sort: MatSort) {
    this.sort = sort;
  }


  constructor(private routeInterfaceService: RouteInterfaceService, private r: Router,public a: AuthService) {
    super(new ConfigRouteInterfaceFilterDTO(), routeInterfaceService, r,a);
  }

  routeInterfaceDisplayedColumns: string[] = ['id','localBaId', 'remoteBaId', 'interFace', 'service'];

  ngOnInit(): void {
    this.filterForm = new FormGroup({
      localBaId: new FormControl(this.filter.localBaId),
      remoteBaId: new FormControl(this.filter.remoteBaId),

  });
  this.routeInterfaceDisplayedColumns =this.auth.isAdmin ?  this.routeInterfaceDisplayedColumns : this.routeInterfaceDisplayedColumns.filter(f=>f!='id');

    super.loadData();

  }

  editConfiguration(dto: ConfigRouteInterfaceDTO) {
    this.service.currentSelection = dto;
    this.router.navigateByUrl("configuration/route-interface/edit");
  }



}
