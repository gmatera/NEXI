import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { Router } from '@angular/router';
import { ControllerPath } from '../../common-dto/common-dto';
import { AuthService } from '../../_services/auth.service';
import { booleanArray, formatArray } from '../common-config-dto';
import { CommonConfigComponent } from '../common.config-component';
import { MSSConfigDTO, MSSConfigFilterDTO } from './dto/mss-config-dto';
import { MSSConfigService } from './mss-config-service';

@Component({
  selector: 'mss-config-component',
  templateUrl: './mss-config.component.html',
  styleUrls: ['./mss-config.component.css']
})

export class MSSConfigController extends CommonConfigComponent<MSSConfigFilterDTO, MSSConfigDTO, MSSConfigService> implements OnInit {

  @ViewChild(MatPaginator)
  paginator!: MatPaginator;

  @ViewChild(MatSort, { static: false }) set sort(s: MatSort) {
    if (this.mssConfigurationService.dataSource) {
      this.mssConfigurationService.dataSource.sort = s;
    }
  }
  sortData(sort: MatSort) {
    this.sort = sort;
  }

  constructor(private mssConfigurationService: MSSConfigService, private r: Router,public a: AuthService) {
    super(new MSSConfigFilterDTO(), mssConfigurationService, r,a);
  }

  ngOnInit(): void {

    this.service.updateServiceUri(ControllerPath.MSSDB_PREFIX);
    super.buildCommonFilterForm();
    // additional filters specifit to MSSConfigFilterDTO
   // this.filterForm.controls["sndCodePage"] = new FormControl((this.filter as MSSConfigFilterDTO).sndCodePage, [Validators.maxLength(12)]);

    super.loadData();
  }

  editConfiguration(dto: MSSConfigDTO) {
    this.service.currentSelection = dto;
    this.router.navigateByUrl("configuration/mss/edit");
  }

  copyConfiguration(dto: MSSConfigDTO) {
    var dto2:MSSConfigDTO = dto;
    delete dto2["id"];
    this.editConfiguration(dto2);
  }

  onSelectionFilter(interfaceType: any) {

    (interfaceType == "MQ") ? this.service.updateServiceUri(ControllerPath.MSSMQ_PREFIX) : this.service.updateServiceUri(ControllerPath.MSSDB_PREFIX)
    this.onSubmitFilter();
  }


  getBooleanArray() {
    return booleanArray;
  }
  
  getFormatArray() {
    return formatArray;
  }

}



