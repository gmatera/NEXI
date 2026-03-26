import { Component, OnInit, ViewChild } from "@angular/core";
import { FormControl, FormGroup, Validators } from "@angular/forms";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { Router } from "@angular/router";
import { AuthService } from "../../_services/auth.service";
import { CommonConfigComponent } from "../common.config-component";
import { ConfigFemwSDTO, FemwsConfigFilterDTO } from "./dto/femws-config-dto";
import { FemwsConfigService } from "./femws-config-service";

@Component({
  selector: 'config-component',
  templateUrl: './femws-config.component.html',
  styleUrls: ['./femws-config.component.scss']
})
export class FemwsConfigController extends CommonConfigComponent<FemwsConfigFilterDTO, ConfigFemwSDTO, FemwsConfigService>  implements OnInit {

  @ViewChild(MatPaginator)
  paginator!: MatPaginator;

  @ViewChild(MatSort, { static: false }) set sort(s: MatSort) {
    if (this.femwsConfigService.dataSource) {
      this.femwsConfigService.dataSource.sort = s;
    }
  }
  sortData(sort: MatSort) {
    this.sort = sort;
  }


  constructor(private femwsConfigService: FemwsConfigService, private r: Router,public a: AuthService) {
    super(new FemwsConfigFilterDTO(), femwsConfigService, r,a);
  }

  femwsDisplayedColumns: string[] = ['id','baId', 'wsSoapAction', 'webServerUrl'];

  ngOnInit(): void {
    this.filterForm = new FormGroup({
      baId: new FormControl(this.filter.baId),
  
  });
  this.femwsDisplayedColumns =this.auth.isAdmin ?  this.femwsDisplayedColumns : this.femwsDisplayedColumns.filter(f=>f!='id');

    super.loadData();

  }

  editConfiguration(dto: ConfigFemwSDTO) {
    this.service.currentSelection = dto;
    this.router.navigateByUrl("configuration/femws/edit");
  }


}
