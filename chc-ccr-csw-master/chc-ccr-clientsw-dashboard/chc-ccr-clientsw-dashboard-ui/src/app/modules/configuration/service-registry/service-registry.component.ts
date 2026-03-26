import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { Router } from '@angular/router';
import { AuthService } from '../../_services/auth.service';
import { CommonConfigComponent } from '../common.config-component';
import { ServiceRegistryDTO, ServiceRegistryFilterDTO } from './dto/service-registry-dto';
import { ServiceRegistryService } from './service-registry-service';
import { PagedResultDTO } from '../../common-dto/common-dto';
import { CommonConfigDTO, CommonConfigFilterDTO, ConfigPath } from '../common-config-dto';
import { CommonService } from 'src/app/service/common.service';
import { MatTableDataSource } from '@angular/material/table';
import { HttpClient } from '@angular/common/http';


@Component({
  selector: 'service-registry-component',
  templateUrl: './service-registry.component.html',
  styleUrls: ['./service-registry.component.css']
})

//export class ServiceRegistryController extends CommonConfigComponent<ServiceRegistryFilterDTO, ServiceRegistryDTO, ServiceRegistryService>  implements OnInit {
    export class ServiceRegistryController implements OnInit {

      @ViewChild(MatPaginator)
      paginator!: MatPaginator;
    
      @ViewChild(MatSort, { static: false }) set sort(s: MatSort) {
        if (this.service.dataSource) {
          this.service.dataSource.sort = s;
        }
      }
      sortData(sort: MatSort) {
        this.sort = sort;
      }
    
    
 protected editConfiguration(dto: ServiceRegistryDTO): void {
   
 }
  constructor(public service: ServiceRegistryService, private r: Router,public a: AuthService) {
   // super(new ServiceRegistryFilterDTO(), ServiceRegistryService, r,a); 
  }

  serviceRegDisplayedColumns: string[] = ['id','groupId','hostName', 'lastUpdate','port','roles','serviceStatus'];

  ngOnInit(): void {
    this.service.loadFromServer();
    console.log(this.service);
  }

/*   editConfiguration(dto: ServiceRegistryDTO) {
    this.service.currentSelection = dto;
    this.router.navigateByUrl("configuration/service-registry/edit");
  } */



}
