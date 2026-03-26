import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { Router } from '@angular/router';
import { ControllerPath } from '../../common-dto/common-dto';
import { AuthService } from '../../_services/auth.service';
import { booleanArray, codePageArray, digestFileAlgArray, dnsCreationAlgoArray, formatArray, lineSeparatorArray, recordFormatArray, vfnCreationAlgoArray } from '../common-config-dto';
import { CommonConfigComponent } from '../common.config-component';
import { FtsConfigDTO, FTSConfigFilterDTO } from './dto/fts-config-dto';
import { FTSConfigService } from './fts-config-service';

@Component({
  selector: 'fts-config-component',
  templateUrl: './fts-config.component.html',
  styleUrls: ['./fts-config.component.css']
})

export class FTSConfigController extends CommonConfigComponent<FTSConfigFilterDTO, FtsConfigDTO, FTSConfigService> implements OnInit {

  @ViewChild(MatPaginator)
  paginator!: MatPaginator;

  @ViewChild(MatSort, { static: false }) set sort(s: MatSort) {
    if (this.ftsConfigurationService.dataSource) {
      this.ftsConfigurationService.dataSource.sort = s;
    }
  }
  sortData(sort: MatSort) {
    this.sort = sort;
  }

  value: any = 'DB';

  constructor(private ftsConfigurationService: FTSConfigService, private r: Router, public a: AuthService) {
    super(new FTSConfigFilterDTO(), ftsConfigurationService, r, a);
  }

  ngOnInit(): void {

    this.service.updateServiceUri(ControllerPath.FTSDB_PREFIX);
    super.buildCommonFilterForm();
    // additional filters specifit to FMSConfigFilterDTO
    super.loadData();
  }

  editConfiguration(dto: FtsConfigDTO) {
    this.service.currentSelection = dto;
    this.router.navigateByUrl("configuration/fts/edit");
  }

  onSelectionFilter(interfaceType: any) {

    if (interfaceType == "MQ") {
      this.service.updateServiceUri(ControllerPath.FTSMQ_PREFIX);
      this.displayedColumns = this.mqDisplayedColumns;
    }
    else {
      this.service.updateServiceUri(ControllerPath.FTSDB_PREFIX);
      this.displayedColumns = this.dbDisplayedColumns;

    }
    this.onSubmitFilter();
  }

  getCodePageArray() {
    return codePageArray;
  }

  getLineSeparatorArray() {
    return lineSeparatorArray;
  }

  getRecordFormatArray() {
    return recordFormatArray;
  }

  getVfnCreationAlgoArray() {
    return vfnCreationAlgoArray;
  }

  getDnsCreationAlgoArray() {
    return dnsCreationAlgoArray;
  }

  getBooleanArray() {
    return booleanArray;
  }

  getDigestFileAlgArray() {
    return digestFileAlgArray;
  }

  getFormatArray() {
    return formatArray;
  }

  copyConfiguration(dto: FtsConfigDTO) {
    var dto2:FtsConfigDTO = dto;
    delete dto2["id"];
    this.editConfiguration(dto2);
  }
}


