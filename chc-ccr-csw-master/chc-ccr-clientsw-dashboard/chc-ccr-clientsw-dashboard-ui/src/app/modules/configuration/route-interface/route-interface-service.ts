import { Injectable } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { CommonService } from "src/app/service/common.service";
import { NotificationService } from "src/app/service/notification.service";
import { ConfiguratiorService } from "../common-config.service";
import { Location } from '@angular/common';
import { ControllerPath } from "../../common-dto/common-dto";
import { ConfigRouteInterfaceDTO, ConfigRouteInterfaceFilterDTO } from "./dto/route-interface-dto";
import { Router } from "@angular/router";

@Injectable({
    providedIn: 'root'
  })
export class RouteInterfaceService extends ConfiguratiorService<ConfigRouteInterfaceDTO, ConfigRouteInterfaceFilterDTO>{

    constructor(private httpService: CommonService,
        notify: NotificationService, 
        confirmationDialog: MatDialog, 
        locationHistory: Location, router:Router) {
        super(httpService, ControllerPath.ROUTE_INTERFACE_PREFIX, notify, confirmationDialog, locationHistory, router);
    }

    protected initDTO() {
        let dto = new ConfigRouteInterfaceDTO();

        dto.localBaId = "";
        dto.remoteBaId = "";
        dto.interFace="";
        dto.service="";

        super.currentSelection = dto;
    }


}