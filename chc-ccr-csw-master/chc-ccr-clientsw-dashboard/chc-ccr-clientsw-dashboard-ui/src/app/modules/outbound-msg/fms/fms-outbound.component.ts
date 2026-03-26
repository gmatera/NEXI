import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { ControllerPath } from '../../common-dto/common-dto';
import { ConfigPathInOut, MQlogicalStateArray, MQStatusArray } from '../../inout/common-inout-dto';
import { CommonInOutComponent } from '../../inout/common.inout-component';
import { FMSDBlogicalStateArray, fmsDBStatusBaArray, FMSSendDTO, FMSSendFilterDTO } from './dto/fms-outbound-dto';
import { FMSOutboundService } from './fms-outbound-service';


@Component({
  selector: 'fms-outbound-app',
  templateUrl: './fms-outbound.component.html',
  styleUrls: ['./fms-outbound.component.scss']
})
export class FMSOutboundComponent extends CommonInOutComponent<FMSSendFilterDTO, FMSSendDTO, FMSOutboundService> implements OnInit {

  @ViewChild(MatPaginator)
  paginator!: MatPaginator;

  @ViewChild(MatSort, { static: false }) set sort(s: MatSort) {
    if (this.localservice.dataSource) {
      this.localservice.dataSource.sort = s;
    }
  }
  sortData(sort: MatSort) {
    this.sort = sort;
  }

  mqDisplayedColumns = ['cswInsertTimestamp', 'localBaId', 'remoteBaId', 'vfn', 'udr', 'status', 'statusInfo', 'complete', 'fname'];
  dbDisplayedColumns = ['baInsertTimestamp', 'localBaId', 'remoteBaId', 'vfn', 'udr', 'status', 'statusInfo', 'complete', 'fname'];
  displayedColumns = this.dbDisplayedColumns;
  statusBaArray = fmsDBStatusBaArray;
  FMSlogicalStateArray = FMSDBlogicalStateArray;
  constructor(private localservice: FMSOutboundService) {
    super(new FMSSendFilterDTO(), localservice);
  }

  ngOnInit(): void {
        
    let formControls = {
      fileName: new FormControl(this.filter.fileName),
      vfn: new FormControl(this.filter.vfn),
      status: new FormControl(this.filter.status),
      //status: new FormControl(this.filter.status),
      logicalStateList: new FormControl(this.filter.logicalStateList),
      fileSizeList: new FormControl(this.filter.fileSizeList),
      baInsertTimestamp: new FormControl(this.filter.baInsertTimestamp),
      udr: new FormControl(this.filter.udr),
      tur: new FormControl(this.filter.tur),
      startDate: new FormControl(this.filter.startDate),
      endDate: new FormControl(this.filter.endDate),
      cswInsertTimestamp: new FormControl(this.filter.cswInsertTimestamp)

    }
    
    super.buildCommonFilterForm(formControls);

    // additional filters specifit to FMSConfigFilterDTO
    // this.filterForm.controls["sndCodePage"] = new FormControl((this.filter as FMSConfigFilterDTO).sndCodePage, [Validators.maxLength(12)]);
    this.service.updateServiceUri(ControllerPath.FMSDB_PREFIX + ConfigPathInOut.OUTBOUND);

    super.loadData();
  }

  onInterfaceSelection(interfaceType: string, form: FormGroup) {
    form.controls['logicalStateList'].reset();
    form.controls['status'].reset();
    form.controls['status'].reset();
    delete this.filter.logicalStateList;
    delete this.filter.status;
    this.filter.interfaceType=interfaceType;
    //delete this.filter.status;

    if (interfaceType == "MQ") {
      this.service.updateServiceUri(ControllerPath.FMSMQ_PREFIX + ConfigPathInOut.OUTBOUND);
      this.statusBaArray = MQStatusArray;
      this.FMSlogicalStateArray = MQlogicalStateArray;
      this.displayedColumns = this.mqDisplayedColumns;
    } else {
      this.service.updateServiceUri(ControllerPath.FMSDB_PREFIX + ConfigPathInOut.OUTBOUND);
      this.statusBaArray = fmsDBStatusBaArray;
      this.FMSlogicalStateArray = FMSDBlogicalStateArray;
      this.displayedColumns = this.dbDisplayedColumns;

    }
    super.loadData();
  }

  override onSubmitFilter() {
    let filterFormValue = this.filterForm.value;

    for (let key in this.filterForm.value) {
      filterFormValue[key] = this.filterForm.value[key] || this.filterForm.value[key] === false ? this.filterForm.value[key] : undefined;
    }

    this.filter = filterFormValue;
    // if (this.filter.interfaceType == 'DB') {
    //   this.filter.status = this.filter.status?.slice();
    //   delete this.filter.status;
    // }
    this.loadData();
  }


  getFmsStatusBaArray() {
    return this.statusBaArray;
  }

  getLogicalStateArray() {
    return this.FMSlogicalStateArray;
  }
}