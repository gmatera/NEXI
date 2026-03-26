import { Injectable } from "@angular/core";
import { CommonService } from "src/app/service/common.service";
import { ControllerPath } from "../../common-dto/common-dto";
import { ConfigPathInOut } from "../../inout/common-inout-dto";
import { CommonInOutMessagesService } from "../../inout/inout-messages.service";
import { FMSSendDTO, FMSSendFilterDTO } from "./dto/fms-outbound-dto";
import { MatDialog } from "@angular/material/dialog";
import { NotificationService } from "src/app/service/notification.service";

@Injectable({
    providedIn: 'root'
  })
export class FMSOutboundService extends CommonInOutMessagesService<FMSSendDTO, FMSSendFilterDTO>{

    constructor(private httpService: CommonService, notify: NotificationService, confirmationDialog: MatDialog){
        super(httpService, ControllerPath.FMSDB_PREFIX + ConfigPathInOut.OUTBOUND, confirmationDialog, notify);
    }
}