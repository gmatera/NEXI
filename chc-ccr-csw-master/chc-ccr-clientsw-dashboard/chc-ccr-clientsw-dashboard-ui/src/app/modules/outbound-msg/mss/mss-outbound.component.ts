import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { ControllerPath } from '../../common-dto/common-dto';
import { ConfigPathInOut} from '../../inout/common-inout-dto';
import { CommonInOutComponent } from '../../inout/common.inout-component';
import { mssDBStatusArray, MSSDBlogicalStateArray, MSSSendDTO, MSSSendFilterDTO, MSSMQStatusArray, MSSMQlogicalStateArray } from './dto/mss-outbound-dto';
import { MSSOutboundService } from './mss-outbound-service';

@Component({
  selector: 'mss-outbound-app',
  templateUrl: './mss-outbound.component.html',
  styleUrls: ['./mss-outbound.component.scss']
})
export class MSSOutboundComponent extends CommonInOutComponent<MSSSendFilterDTO, MSSSendDTO, MSSOutboundService> implements OnInit {

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
  dbDisplayedColumns = ['baInsertTimestamp', 'localBaId', 'remoteBaId', 'priority', 'msgId', 'status','statusInfo', 'complete', 'catAppl', 'remoteRef'];
  mqDisplayedColumns = ['cswInsertTimestamp', 'localBaId', 'remoteBaId', 'priority', 'id', 'status','statusInfo', 'complete', 'catAppl', 'udr'];
  displayedColumns = this.dbDisplayedColumns;
  statusArray = mssDBStatusArray;
  MSSlogicalStateArray = MSSDBlogicalStateArray;
  constructor(private localservice: MSSOutboundService) {
    super(new MSSSendFilterDTO(), localservice);
  }


  ngOnInit(): void {
  
  let formControls ={
      msgId:new FormControl(this.filter.msgId),
      id:new FormControl(this.filter.id),
      status:new FormControl(this.filter.status),
      logicalStateList:new FormControl(this.filter.logicalStateList),
      msgSizeList:new FormControl(this.filter.msgSizeList),   
      baInsertTimestamp:new FormControl(this.filter.baInsertTimestamp),
      tur:new FormControl(this.filter.tur),
      startDate:new FormControl(this.filter.startDate),
      endDate:new FormControl(this.filter.endDate),
      cswInsertTimestamp:new FormControl(this.filter.cswInsertTimestamp),
      udr: new FormControl(this.filter.udr),
      remoteRef: new FormControl(this.filter.remoteRef)

    }
    super.buildCommonFilterForm(formControls);

    // additional filters specifit to FMSConfigFilterDTO
   // this.filterForm.controls["sndCodePage"] = new FormControl((this.filter as FMSConfigFilterDTO).sndCodePage, [Validators.maxLength(12)]);
    this.service.updateServiceUri(ControllerPath.MSSDB_PREFIX + ConfigPathInOut.OUTBOUND);
    super.loadData();
  }

  onInterfaceSelection(interfaceType: string,form: FormGroup){
    form.controls['logicalStateList'].reset();
    form.controls['status'].reset();
    delete this.filter.logicalStateList;
    delete this.filter.status;
    this.filter.interfaceType=interfaceType;
    if (interfaceType == "MQ") { 
      this.service.updateServiceUri(ControllerPath.MSSMQ_PREFIX + ConfigPathInOut.OUTBOUND);
      this.statusArray = MSSMQStatusArray;
      this.MSSlogicalStateArray = MSSMQlogicalStateArray;
      form.controls['msgId'].reset();
      this.displayedColumns = this.mqDisplayedColumns;
      this.isInterfaceTypeMQ = true; 
      // const  i = this.displayedColumns.findIndex((f)=>f=='msgId');
      // this.displayedColumns.splice(i,1,'id')
    } else {
      this.service.updateServiceUri(ControllerPath.MSSDB_PREFIX + ConfigPathInOut.OUTBOUND);
      this.statusArray = mssDBStatusArray;
      this.MSSlogicalStateArray = MSSDBlogicalStateArray;
      form.controls['id'].reset();
      this.displayedColumns = this.dbDisplayedColumns;
      this.isInterfaceTypeMQ = false; 

      // const  i = this.displayedColumns.findIndex((f)=>f=='id');
      // this.displayedColumns.splice(i,1,'msgId')
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