import { Injectable } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { CommonService } from "src/app/service/common.service";
import { NotificationService } from "src/app/service/notification.service";
import { ConfiguratiorService } from "../common-config.service";
import { Location } from '@angular/common';
import { ControllerPath } from "../../common-dto/common-dto";
import { GlobalPropertiesDTO, GlobalPropertiesFilterDTO } from "./dto/global-properties-dto";
import { Router } from "@angular/router";

@Injectable({
    providedIn: 'root'
  })
export class GlobalPropertiesService extends ConfiguratiorService<GlobalPropertiesDTO, GlobalPropertiesFilterDTO>{

    constructor(private httpService: CommonService,
        notify: NotificationService, 
        confirmationDialog: MatDialog, 
        locationHistory: Location, router:Router) {
        super(httpService, ControllerPath.GLOBAL_PROPERTIES_PREFIX, notify, confirmationDialog, locationHistory, router);
    }

    protected initDTO() {
        let dto = new GlobalPropertiesDTO();

        dto.propertyName = "";
        dto.value = "";
        dto.type="";
        dto.mandatory=false;
        dto.displayName="";

        super.currentSelection = dto;
    }


}