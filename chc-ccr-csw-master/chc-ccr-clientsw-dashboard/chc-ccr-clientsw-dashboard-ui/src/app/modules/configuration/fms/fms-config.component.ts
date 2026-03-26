import { AfterViewInit, Component, OnInit, ViewChild } from "@angular/core";
import { FormControl, Validators } from "@angular/forms";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { Router } from "@angular/router";
import { ControllerPath } from "../../common-dto/common-dto";
import { AuthService } from "../../_services/auth.service";
import { booleanArray, codePageArray, CommonConfigDTO, digestFileAlgArray, dnsCreationAlgoArray, formatArray, lineSeparatorArray, recordFormatArray, vfnCreationAlgoArray } from "../common-config-dto";
import { CommonConfigComponent } from "../common.config-component";
import { FmsConfigDTO, FMSConfigFilterDTO } from "./dto/fms-config-dto";
import { FMSConfigService } from "./fms-config-service";

@Component({
  selector: 'fms-config-component',
  templateUrl: './fms-config.component.html',
  styleUrls: ['./fms-config.component.css']
})
export class FMSConfigController extends CommonConfigComponent<FMSConfigFilterDTO, FmsConfigDTO, FMSConfigService> implements OnInit {

  @ViewChild(MatPaginator)
  paginator!: MatPaginator;

  @ViewChild(MatSort, { static: false }) set sort(s: MatSort) {
    if (this.fmsConfigurationService.dataSource) {
      this.fmsConfigurationService.dataSource.sort = s;
    }
  }
  sortData(sort: MatSort) {
    this.sort = sort;
  }

  constructor(private fmsConfigurationService: FMSConfigService, private r: Router, public a: AuthService) {
    super(new FMSConfigFilterDTO(), fmsConfigurationService, r, a);
  }


  ngOnInit(): void {

    this.service.updateServiceUri(ControllerPath.FMSDB_PREFIX);
    super.buildCommonFilterForm();
    // additional filters specifit to FMSConfigFilterDTO
    this.filterForm.controls["sndCodePage"] = new FormControl((this.filter as FMSConfigFilterDTO).sndCodePage, [Validators.maxLength(12)]);
    super.loadData();

  }


  editConfiguration(dto: FmsConfigDTO) {
    this.service.currentSelection = dto;
    this.router.navigateByUrl("configuration/fms/edit");
  }

  copyConfiguration(dto: FmsConfigDTO) {
    var dto2:FmsConfigDTO = dto;
    delete dto2["id"];
    this.editConfiguration(dto2);
  }


  onSelectionFilter(interfaceType: any) {

    if (interfaceType == "MQ") {
      this.service.updateServiceUri(ControllerPath.FMSMQ_PREFIX);
      this.displayedColumns = this.mqDisplayedColumns;
    } else {
      this.service.updateServiceUri(ControllerPath.FMSDB_PREFIX);
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
  
}


