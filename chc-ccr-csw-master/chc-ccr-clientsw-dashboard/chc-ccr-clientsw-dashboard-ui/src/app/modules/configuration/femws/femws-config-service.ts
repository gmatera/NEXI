import { Injectable } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { CommonService } from "src/app/service/common.service";
import { NotificationService } from "src/app/service/notification.service";
import { ConfiguratiorService } from "../common-config.service";
import { Location } from '@angular/common';
import { ControllerPath } from "../../common-dto/common-dto";
import { ConfigFemwSDTO, FemwsConfigFilterDTO } from "./dto/femws-config-dto";
import { Router } from "@angular/router";

@Injectable({
    providedIn: 'root'
  })
export class FemwsConfigService extends ConfiguratiorService<ConfigFemwSDTO, FemwsConfigFilterDTO>{

    constructor(private httpService: CommonService,
        notify: NotificationService, 
        confirmationDialog: MatDialog, 
        locationHistory: Location, router:Router) {
        super(httpService, ControllerPath.FEMWS_PREFIX, notify, confirmationDialog, locationHistory, router);
    }

    protected initDTO() {
        let dto = new ConfigFemwSDTO();

        dto.baId = "";
        dto.wsSoapAction = "";
        dto.webServerUrl = "";


        super.currentSelection = dto;
    }


}