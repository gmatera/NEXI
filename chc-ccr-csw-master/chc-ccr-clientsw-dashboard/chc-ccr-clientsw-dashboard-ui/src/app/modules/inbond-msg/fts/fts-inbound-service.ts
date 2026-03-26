import { Injectable } from "@angular/core";
import { CommonService } from "src/app/service/common.service";
import { ControllerPath } from "../../common-dto/common-dto";
import { CommonInOutFilterDTO, ConfigPathInOut } from "../../inout/common-inout-dto";
import { CommonInOutMessagesService } from "../../inout/inout-messages.service";
import { FTSRecvDTO, FTSRecvFilterDTO } from "./dto/fts-inbound-dto";
import { MatDialog } from "@angular/material/dialog";
import { NotificationService } from "src/app/service/notification.service";

@Injectable({
    providedIn: 'root'
  })
export class FTSInboundService extends CommonInOutMessagesService<FTSRecvDTO, FTSRecvFilterDTO>{

    constructor(private httpService: CommonService, notify: NotificationService, confirmationDialog: MatDialog){
        super(httpService, ControllerPath.FTSDB_PREFIX + ConfigPathInOut.INBOUND,  confirmationDialog, notify);
    }
}