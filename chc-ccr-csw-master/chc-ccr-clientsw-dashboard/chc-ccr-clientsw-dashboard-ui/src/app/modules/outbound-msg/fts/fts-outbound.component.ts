import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { ControllerPath } from '../../common-dto/common-dto';
import { ConfigPathInOut, interfaceTypeArray, MQlogicalStateArray, MQStatusArray } from '../../inout/common-inout-dto';
import { CommonInOutComponent } from '../../inout/common.inout-component';
import { FTSDBlogicalStateArray, ftsDBStatusArray, FTSSendDTO, FTSSendFilterDTO } from './dto/fts-outbound-dto';
import { FTSOutboundService } from './fts-outbound-service';

@Component({
  selector: 'fts-outbound-app',
  templateUrl: './fts-outbound.component.html',
  styleUrls: ['./fts-outbound.component.scss']
})
export class FTSOutboundComponent extends CommonInOutComponent<FTSSendFilterDTO, FTSSendDTO, FTSOutboundService> implements OnInit {

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

  dbDisplayedColumns = ['baInsertTimestamp', 'localBaId', 'remoteBaId', 'vfn', 'status','statusInfo', 'complete','fname','applCheck'];
  mqDisplayedColumns = ['cswInsertTimestamp', 'localBaId', 'remoteBaId', 'vfn', 'status','statusInfo', 'complete','fname'];
  displayedColumns = this.dbDisplayedColumns;

  constructor(private localservice: FTSOutboundService) {
    super(new FTSSendFilterDTO(), localservice);
  }

  statusArray = ftsDBStatusArray;
  FTSlogicalStateArray = FTSDBlogicalStateArray;

  ngOnInit(): void {

    let formControls = {
      fileName: new FormControl(this.filter.fileName),
      vfn: new FormControl(this.filter.vfn),
      status: new FormControl(this.filter.status),
      logicalStateList: new FormControl(this.filter.logicalStateList),
      fileSizeList: new FormControl(this.filter.fileSizeList),
      baInsertTimestamp: new FormControl(this.filter.baInsertTimestamp),
      startDate: new FormControl(this.filter.startDate),
      endDate: new FormControl(this.filter.endDate),
      cswInsertTimestamp: new FormControl(this.filter.cswInsertTimestamp)


    }
    super.buildCommonFilterForm(formControls);

    // additional filters specifit to FMSConfigFilterDTO
    // this.filterForm.controls["sndCodePage"] = new FormControl((this.filter as FMSConfigFilterDTO).sndCodePage, [Validators.maxLength(12)]);
    this.service.updateServiceUri(ControllerPath.FTSDBFS_PREFIX + ConfigPathInOut.OUTBOUND)
    super.loadData();
  }

  onInterfaceSelection(interfaceType: string,form: FormGroup) {
    form.controls['logicalStateList'].reset();
    form.controls['status'].reset();
    delete this.filter.logicalStateList;
    delete this.filter.status;
    this.filter.interfaceType = interfaceType;
    if (interfaceType == "MQ") {
      this.service.updateServiceUri(ControllerPath.FTSMQ_PREFIX + ConfigPathInOut.OUTBOUND) 
      this.statusArray = MQStatusArray;
      this.FTSlogicalStateArray = MQlogicalStateArray;
      this.displayedColumns = this.mqDisplayedColumns;
    }else {
      this.service.updateServiceUri(ControllerPath.FTSDBFS_PREFIX + ConfigPathInOut.OUTBOUND)
      this.statusArray = ftsDBStatusArray;
      this.FTSlogicalStateArray = FTSDBlogicalStateArray;
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
    this.filter.ftsInterface = this.filter.interfaceType;
    this.loadData();
  }

  getFtsStatusBaArray() {
    return this.statusArray;
  }

  override getInterfaceArray() {
    return interfaceTypeArray;
  }

  getLogicalStateArray() {
    return this.FTSlogicalStateArray;
  }

}