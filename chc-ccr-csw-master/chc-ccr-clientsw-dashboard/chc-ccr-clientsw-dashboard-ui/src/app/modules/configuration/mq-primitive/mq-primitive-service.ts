import { Injectable } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { CommonService } from "src/app/service/common.service";
import { NotificationService } from "src/app/service/notification.service";
import { ConfiguratiorService } from "../common-config.service";
import { Location } from '@angular/common';
import { ControllerPath } from "../../common-dto/common-dto";
import { MQPrimitiveDTO, MQPrimitiveFilterDTO } from "./dto/mq-primitive-dto";
import { Router } from "@angular/router";

@Injectable({
    providedIn: 'root'
  })
export class MqPrimitiveService extends ConfiguratiorService<MQPrimitiveDTO, MQPrimitiveFilterDTO>{

    constructor(private httpService: CommonService,
        notify: NotificationService, 
        confirmationDialog: MatDialog, 
        locationHistory: Location, router:Router) {
        super(httpService, ControllerPath.MQ_PRIMITIVE_PREFIX, notify, confirmationDialog, locationHistory, router);
    }

    protected initDTO() {
        let dto = new MQPrimitiveDTO();

        dto.mqChannel = "";
        dto.primitive = "";
        dto.queueName="";

        super.currentSelection = dto;
    }


}