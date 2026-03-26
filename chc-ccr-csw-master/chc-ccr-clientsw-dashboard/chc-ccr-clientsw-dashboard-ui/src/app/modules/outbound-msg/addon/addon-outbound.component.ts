import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { fsInterfaceTypeArray} from 'src/app/modules/inout/common-inout-dto';
import { CommonInOutComponent } from 'src/app/modules/inout/common.inout-component';
import { ADDONOutboundService } from './addon-outbound-service';
import { ADDONOutDTO, ADDONOutFilterDTO, FTSFSlogicalStateArray, ftsFSStatusArray } from './dto/addon-outbound-dto';

@Component({
  selector: 'addon-outbound-app',
  templateUrl: './addon-outbound.component.html',
  styleUrls: ['./addon-outbound.component.scss']
})
export class AddonOutboundComponent extends CommonInOutComponent<ADDONOutFilterDTO, ADDONOutDTO, ADDONOutboundService> implements OnInit {

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

  displayedColumns = ['baInsertTimestamp', 'localBaId', 'remoteBaId', 'vfn', 'status','logicalState', 'complete','fname'];

  constructor(private localservice: ADDONOutboundService) {
    super(new ADDONOutFilterDTO(), localservice);
  }

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
    return ftsFSStatusArray;
  }

  override getInterfaceArray() {
    return fsInterfaceTypeArray;
  }

  getLogicalStateArray() {
    return FTSFSlogicalStateArray;
  }

  getLogicalStatus(status: string){
    return FTSFSlogicalStateArray.find(f=>f.value?.map(m=>m.toLocaleLowerCase())?.includes(status?.toLocaleLowerCase()))?.code
  }

}