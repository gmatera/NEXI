import { Injectable } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { CommonService } from "src/app/service/common.service";
import { NotificationService } from "src/app/service/notification.service";
import { ConfiguratiorService } from "../common-config.service";
import { Location } from '@angular/common';
import { ControllerPath } from "../../common-dto/common-dto";
import { AddonConfigDTO, AddonConfigFilterDTO } from "./dto/addon-config-dto";
import { Router } from "@angular/router";

@Injectable({
    providedIn: 'root'
  })
export class AddonConfigService extends ConfiguratiorService<AddonConfigDTO, AddonConfigFilterDTO>{

    constructor(private httpService: CommonService,
        notify: NotificationService, 
        confirmationDialog: MatDialog, 
        locationHistory: Location, router:Router) {
        super(httpService, ControllerPath.ADDON_PREFIX, notify, confirmationDialog, locationHistory, router);
    }

    protected initDTO() {
        let dto = new AddonConfigDTO();

        dto.localBaId = "";
        dto.remoteBaId = "";

        dto.sndPath = "";
        dto.rcvPath = "";
        dto.sendingPrefix = "";
        dto.errorPrefix = "";
        dto.sentPrefix = "";
        dto.errorDeliverPrefix = "";
        dto.csc = "";
    

        super.currentSelection = dto;
    }


}