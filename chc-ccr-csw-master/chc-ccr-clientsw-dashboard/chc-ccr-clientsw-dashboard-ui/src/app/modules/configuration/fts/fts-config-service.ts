import { Injectable } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { CommonService } from "src/app/service/common.service";
import { NotificationService } from "src/app/service/notification.service";
import { ConfiguratiorService } from "../common-config.service";
import { Location } from '@angular/common';
import { ControllerPath } from "../../common-dto/common-dto";
import { FtsConfigDTO, FTSConfigFilterDTO } from "./dto/fts-config-dto";
import { Router } from "@angular/router";

@Injectable({
    providedIn: 'root'
  })
export class FTSConfigService extends ConfiguratiorService<FtsConfigDTO, FTSConfigFilterDTO>{

    constructor(private httpService: CommonService,
        notify: NotificationService, 
        confirmationDialog: MatDialog, 
        locationHistory: Location, router:Router) {
        super(httpService, ControllerPath.FTSDB_PREFIX, notify, confirmationDialog, locationHistory, router);
    }

    protected initDTO() {
        let dto = new FtsConfigDTO();

        dto.interfaceType = "";
        dto.localBaId = "";
        dto.remoteBaId = "";

        dto.sndCodePage = "";
        dto.sndLineSeparator = "";
        dto.sndMaxRecLength = 0;
        dto.sndRecordFormat = "";

        dto.rcvCodePage = "";
        dto.rcvLineSeparator = "";

        super.currentSelection = dto;
    }


}