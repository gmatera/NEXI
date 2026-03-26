import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { Router } from '@angular/router';
import { AuthService } from '../../_services/auth.service';
import { CommonConfigComponent } from '../common.config-component';
import { GlobalPropertiesDTO, GlobalPropertiesFilterDTO } from './dto/global-properties-dto';
import { GlobalPropertiesService } from './global-properties-service';


@Component({
  selector: 'global-properties-component',
  templateUrl: './global-properties.component.html',
  styleUrls: ['./global-properties.component.css']
})
export class GlobalPropertiesController extends CommonConfigComponent<GlobalPropertiesFilterDTO, GlobalPropertiesDTO, GlobalPropertiesService>  implements OnInit {

  @ViewChild(MatPaginator)
  paginator!: MatPaginator;

  @ViewChild(MatSort, { static: false }) set sort(s: MatSort) {
    if (this.globalPropertiesService.dataSource) {
      this.globalPropertiesService.dataSource.sort = s;
    }
  }
  sortData(sort: MatSort) {
    this.sort = sort;
  }


  constructor(private globalPropertiesService: GlobalPropertiesService, private r: Router,public a: AuthService) {
    super(new GlobalPropertiesFilterDTO(), globalPropertiesService, r,a);
  }

  globalPropDisplayedColumns: string[] = ['id','propertyName', 'value', 'requiredMsg'];

  ngOnInit(): void {
    this.filterForm = new FormGroup({
      propertyName: new FormControl(this.filter.propertyName),

  });
  this.globalPropDisplayedColumns =this.auth.isAdmin ?  this.globalPropDisplayedColumns : this.globalPropDisplayedColumns.filter(f=>f!='id');

    super.loadData();

  }

  editConfiguration(dto: GlobalPropertiesDTO) {
    this.service.currentSelection = dto;
    this.router.navigateByUrl("configuration/global-properties/edit");
  }
  

}
