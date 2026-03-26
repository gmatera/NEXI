import { Component, OnInit, ViewChild } from "@angular/core";
import { FormControl, FormGroup } from "@angular/forms";
import { MatPaginator } from "@angular/material/paginator";
import { Router } from "@angular/router";
import { AuthService } from "../../_services/auth.service";
import { CommonConfigComponent } from "../common.config-component";
import { SystemConfigDTO, SystemConfigFilterDTO } from "./dto/system-config-dto";
import { SystemConfigService } from "./system-config-service";

@Component({
  selector: 'system-config-component',
  templateUrl: './system-config.component.html',
  styleUrls: ['./system-config.component.css']
})
export class SystemConfigController extends CommonConfigComponent<SystemConfigFilterDTO, SystemConfigDTO, SystemConfigService> implements OnInit {

  @ViewChild(MatPaginator)
  paginator!: MatPaginator;

  constructor(private systemConfigurationService: SystemConfigService, private r: Router,public a: AuthService) {
    super(new SystemConfigFilterDTO(), systemConfigurationService, r,a);
  }


  ngOnInit(): void {
    this.filterForm = new FormGroup({
      paramKey: new FormControl(this.filter.paramKey),
  });

    super.loadData();

  }

  editConfiguration(dto: SystemConfigDTO) {
    this.service.currentSelection = dto;
    this.router.navigateByUrl("configuration/system/edit");
  }


}


