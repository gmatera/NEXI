import { Injectable } from "@angular/core";
import { CommonService } from "src/app/service/common.service";
import { ControllerPath } from "../../common-dto/common-dto";
import { ConfigPathInOut } from "../../inout/common-inout-dto";
import { CommonInOutMessagesService } from "../../inout/inout-messages.service";
import { FTSSendDTO, FTSSendFilterDTO } from "./dto/fts-outbound-dto";
import { MatDialog } from "@angular/material/dialog";
import { NotificationService } from "src/app/service/notification.service";

@Injectable({
    providedIn: 'root'
  })
export class FTSOutboundService extends CommonInOutMessagesService<FTSSendDTO, FTSSendFilterDTO>{

    constructor(private httpService: CommonService, notify: NotificationService, confirmationDialog: MatDialog){
        super(httpService, ControllerPath.FTSDB_PREFIX + ConfigPathInOut.OUTBOUND, confirmationDialog, notify);
    }
}