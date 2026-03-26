import { Injectable } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { CommonService } from "src/app/service/common.service";
import { NotificationService } from "src/app/service/notification.service";
import { ConfiguratiorService } from "../common-config.service";
import { Location } from '@angular/common';
import { ControllerPath } from "../../common-dto/common-dto";
import { SystemConfigDTO, SystemConfigFilterDTO } from "./dto/system-config-dto";
import { Router } from "@angular/router";

@Injectable({
    providedIn: 'root'
  })
export class SystemConfigService extends ConfiguratiorService<SystemConfigDTO, SystemConfigFilterDTO>{

    constructor(private httpService: CommonService,
        notify: NotificationService, 
        confirmationDialog: MatDialog, 
        locationHistory: Location, router:Router) {
        super(httpService, ControllerPath.SYSTEM_PREFIX, notify, confirmationDialog, locationHistory, router);
    }

    protected initDTO() {
        let dto = new SystemConfigDTO();

        dto.paramKey = "";
        dto.paramValue = "";
        dto.module="";


        super.currentSelection = dto;
    }


}