import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { ControllerPath } from '../../common-dto/common-dto';
import { ConfigPathInOut, InboundMQlogicalStateArray, InboundMQStatusArray } from '../../inout/common-inout-dto';
import { CommonInOutComponent } from '../../inout/common.inout-component';
import { FMSDBlogicalStateArray, fmsDBStatusBaArray, FMSRecvDTO, FMSRecvFilterDTO } from './dto/fms-inbound-dto';
import { FMSInboundService } from './fms-inbound-service';

@Component({
  selector: 'fms-inbound-app',
  templateUrl: './fms-inbound.component.html',
  styleUrls: ['./fms-inbound.component.scss']
})
export class FMSInboundComponent extends CommonInOutComponent<FMSRecvFilterDTO, FMSRecvDTO, FMSInboundService> implements OnInit {

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

  mqDisplayedColumns = ['cswInsertTimestamp','localBaId', 'remoteBaId', 'vfn', 'udr', 'status','statusInfo', 'complete', 'fname'];
  dbDisplayedColumns = ['receiveTimestamp', 'localBaId', 'remoteBaId', 'vfn', 'udr', 'status','statusInfo', 'complete', 'fname'];
  displayedColumns = this.dbDisplayedColumns;
  statusBaArray = fmsDBStatusBaArray;
  FMSlogicalStateArray = FMSDBlogicalStateArray;
  constructor(private localservice: FMSInboundService) {
    super(new FMSRecvFilterDTO(), localservice);
  }

  ngOnInit(): void {

    let formControls = {
      fileName: new FormControl(this.filter.fileName),
      vfn: new FormControl(this.filter.vfn),
      status: new FormControl(this.filter.status),
      //status: new FormControl(this.filter.status),
      logicalStateList: new FormControl(this.filter.logicalStateList),
      fileSizeList: new FormControl(this.filter.fileSizeList),
      receiveTimestamp: new FormControl(this.filter.receiveTimestamp),
      udr: new FormControl(this.filter.udr),
      tur: new FormControl(this.filter.tur),
      startDate: new FormControl(this.filter.startDate),
      endDate: new FormControl(this.filter.endDate),
      cswInsertTimestamp: new FormControl(this.filter.cswInsertTimestamp)

    }
    this.service.updateServiceUri(ControllerPath.FMSDB_PREFIX + ConfigPathInOut.INBOUND);
    super.buildCommonFilterForm(formControls);

    // additional filters specifit to FMSConfigFilterDTO
    // this.filterForm.controls["sndCodePage"] = new FormControl((this.filter as FMSConfigFilterDTO).sndCodePage, [Validators.maxLength(12)]);

    super.loadData();
  }

  onInterfaceSelection(interfaceType: string, form: FormGroup) {
    form.controls['logicalStateList'].reset();
    //form.controls['statusBA'].reset();
    form.controls['status'].reset();
    delete this.filter.logicalStateList;
    //delete this.filter.statusBA;
    delete this.filter.status;

    if (interfaceType == "MQ") {
      this.service.updateServiceUri(ControllerPath.FMSMQ_PREFIX + ConfigPathInOut.INBOUND);
      this.statusBaArray = InboundMQStatusArray;
      this.FMSlogicalStateArray = InboundMQlogicalStateArray;
      this.displayedColumns = this.mqDisplayedColumns;
    } else { this.service.updateServiceUri(ControllerPath.FMSDB_PREFIX + ConfigPathInOut.INBOUND);
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

  getFmsStatusArray() {
    return this.statusBaArray;
  }

  getLogicalStateArray() {
    return this.FMSlogicalStateArray;
  }


}
