import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { fsInterfaceTypeArray } from '../../inout/common-inout-dto';
import { CommonInOutComponent } from '../../inout/common.inout-component';
import { ADDONInboundService } from './addon-inbound-service';
import { ADDONOutDTO, ADDONOutFilterDTO, FTSFSlogicalStateArray, ftsFSStatusArray } from './dto/addon-inbound-dto';


@Component({
  selector: 'app-addon-inbound',
  templateUrl: './addon-inbound.component.html',
  styleUrls: ['./addon-inbound.component.scss']
})

export class AddonInboundComponent extends CommonInOutComponent<ADDONOutFilterDTO, ADDONOutDTO, ADDONInboundService> implements OnInit {

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


  displayedColumns = ['createDate', 'localBaId', 'remoteBaId', 'vfn', 'status','logicalState', 'complete', 'fname'];

  constructor(private localservice: ADDONInboundService) {
    super(new ADDONOutFilterDTO(), localservice);
  }

  

  ngOnInit(): void {

    let formControls = {
      fileName: new FormControl(this.filter.fileName),
      vfn: new FormControl(this.filter.vfn),
      status: new FormControl(this.filter.status),
      logicalStateList: new FormControl(this.filter.logicalStateList),
      fileSizeList: new FormControl(this.filter.fileSizeList),
      createDate: new FormControl(this.filter.createDate),
      startDate: new FormControl(this.filter.startDate),
      endDate: new FormControl(this.filter.endDate),
      cswInsertTimestamp: new FormControl(this.filter.cswInsertTimestamp)


    }
    super.buildCommonFilterForm(formControls);

    // additional filters specifit to FMSConfigFilterDTO
    // this.filterForm.controls["sndCodePage"] = new FormControl((this.filter as FMSConfigFilterDTO).sndCodePage, [Validators.maxLength(12)]);

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

  override getInterfaceArray() {
    return fsInterfaceTypeArray;
  }

  getFtsStatusBaArray() {
    return ftsFSStatusArray;
  }

  getLogicalStateArray() {
    return FTSFSlogicalStateArray;
  }


  getLogicalStatus(status: string){
    return FTSFSlogicalStateArray.find(f=>f.value?.map(m=>m.toLocaleLowerCase())?.includes(status?.toLocaleLowerCase()))?.code
  }

}
