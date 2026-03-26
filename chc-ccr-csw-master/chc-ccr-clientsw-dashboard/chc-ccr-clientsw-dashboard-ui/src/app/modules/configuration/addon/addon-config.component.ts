import { Component, OnInit, ViewChild } from "@angular/core";
import { FormControl, Validators } from "@angular/forms";
import { MatPaginator } from "@angular/material/paginator";
import { Router } from "@angular/router";
import { CommonConfigComponent } from "../common.config-component";
import { AddonConfigDTO, AddonConfigFilterDTO } from "./dto/addon-config-dto";
import { AddonConfigService } from "./addon-config-service";
import { AuthService } from "../../_services/auth.service";
import { MatSort } from "@angular/material/sort";

@Component({
  selector: 'addon-config-component',
  templateUrl: './addon-config.component.html',
  styleUrls: ['./addon-config.component.css']
})
export class AddonConfigController extends CommonConfigComponent<AddonConfigFilterDTO, AddonConfigDTO, AddonConfigService> implements OnInit {

  @ViewChild(MatPaginator)
  paginator!: MatPaginator;

  @ViewChild(MatSort, { static: false }) set sort(s: MatSort) {
    if (this.addonConfigurationService.dataSource) {
      this.addonConfigurationService.dataSource.sort = s;
    }
  }
  sortData(sort: MatSort) {
    this.sort = sort;
  }

  constructor(private addonConfigurationService: AddonConfigService, private r: Router,public a: AuthService) {
    super(new AddonConfigFilterDTO(), addonConfigurationService, r,a);
  }

  ngOnInit(): void {

    super.buildCommonFilterForm();
    // additional filters specifit to AddonConfigFilterDTO
    //this.filterForm.controls["sndCodePage"] = new FormControl((this.filter as AddonConfigFilterDTO).sndCodePage, [Validators.maxLength(12)]);

    super.loadData();
  }

  editConfiguration(dto: AddonConfigDTO) {
    this.service.currentSelection = dto;
    this.router.navigateByUrl("configuration/addon/edit");
  }


  copyConfiguration(dto: AddonConfigDTO) {
    var dto2:AddonConfigDTO = dto;
    delete dto2["id"];
    dto.localBaId ="";
    dto.remoteBaId="";
    dto.rcvPath="";
    dto.sndPath="";
    this.editConfiguration(dto2);
  }

}


