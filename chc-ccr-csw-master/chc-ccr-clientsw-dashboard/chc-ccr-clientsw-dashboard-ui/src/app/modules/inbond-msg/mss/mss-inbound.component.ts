import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { ControllerPath } from '../../common-dto/common-dto';
import { ConfigPathInOut, InboundMQlogicalStateArray, InboundMQStatusArray, MQlogicalStateArray, MQStatusArray } from '../../inout/common-inout-dto';
import { CommonInOutComponent } from '../../inout/common.inout-component';
import { MSSDBlogicalStateArray, mssDBStatusArray, mssMQStatusArray, MSSRecvDTO, MSSRecvFilterDTO } from './dto/mss-inbound-dto';
import { MSSInboundService } from './mss-inbound-service';

@Component({
  selector: 'mss-inbound-app',
  templateUrl: './mss-inbound.component.html',
  styleUrls: ['./mss-inbound.component.scss']
})
export class MSSInboundComponent extends CommonInOutComponent<MSSRecvFilterDTO, MSSRecvDTO, MSSInboundService> implements OnInit {

  isInterfaceTypeMQ! : boolean;

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


  dbDisplayedColumns = ['receiveTimestamp', 'localBaId', 'remoteBaId', 'priority', 'msgId', 'status','statusInfo', 'complete', 'catAppl', 'remoteRef'];
  mqDisplayedColumns = ['cswInsertTimestamp', 'localBaId', 'remoteBaId', 'priority', 'msgId', 'status','statusInfo', 'complete', 'catAppl', 'udr'];
  displayedColumns = this.dbDisplayedColumns;
  
  statusArray = mssDBStatusArray;
  MSSlogicalStateArray = MSSDBlogicalStateArray
  constructor(private localservice: MSSInboundService) {
    super(new MSSRecvFilterDTO(), localservice);
  }

  ngOnInit(): void {

    let formControls = {
      msgId: new FormControl(this.filter.msgId),
      status: new FormControl(this.filter.status),
      logicalStateList: new FormControl(this.filter.logicalStateList),
      msgSizeList: new FormControl(this.filter.msgSizeList),
      receiveTimestamp: new FormControl(this.filter.receiveTimestamp),
      tur: new FormControl(this.filter.tur),
      startDate: new FormControl(this.filter.startDate),
      endDate: new FormControl(this.filter.endDate),
      cswInsertTimestamp: new FormControl(this.filter.cswInsertTimestamp),
      udr: new FormControl(this.filter.udr),
      remoteRef: new FormControl(this.filter.remoteRef)


    }
    super.buildCommonFilterForm(formControls);

    // additional filters specifit to FMSConfigFilterDTO
    // this.filterForm.controls["sndCodePage"] = new FormControl((this.filter as FMSConfigFilterDTO).sndCodePage, [Validators.maxLength(12)]);
    this.service.updateServiceUri(ControllerPath.MSSDB_PREFIX + ConfigPathInOut.INBOUND);
    super.loadData();
  }

  onInterfaceSelection(interfaceType: string,form: FormGroup) {
    form.controls['logicalStateList'].reset();
    form.controls['status'].reset();
    delete this.filter.logicalStateList;
    delete this.filter.status;
    if (interfaceType == "MQ") { this.service.updateServiceUri(ControllerPath.MSSMQ_PREFIX + ConfigPathInOut.INBOUND);
      this.statusArray = mssMQStatusArray;
      this.MSSlogicalStateArray = InboundMQlogicalStateArray;
      this.displayedColumns = this.mqDisplayedColumns;
      this.isInterfaceTypeMQ = true; 
    }
    else {
      this.service.updateServiceUri(ControllerPath.MSSDB_PREFIX + ConfigPathInOut.INBOUND);
      this.statusArray = mssDBStatusArray;
      this.MSSlogicalStateArray = MSSDBlogicalStateArray;
      this.displayedColumns = this.dbDisplayedColumns;
      this.isInterfaceTypeMQ = false; 

    }
    super.loadData();
  }


  getMssStatusBaArray(){
    return this.statusArray;
}

 getLogicalStateArray(){
  return this.MSSlogicalStateArray;
}
}