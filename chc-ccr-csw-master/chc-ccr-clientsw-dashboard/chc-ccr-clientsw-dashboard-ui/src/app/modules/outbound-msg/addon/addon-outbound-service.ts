import { Injectable } from "@angular/core";
import { ControllerPath } from "src/app/modules/common-dto/common-dto";
import { ConfigPathInOut } from "src/app/modules/inout/common-inout-dto";
import { CommonInOutMessagesService } from "src/app/modules/inout/inout-messages.service";
import { CommonService } from "src/app/service/common.service";
import { ADDONOutDTO, ADDONOutFilterDTO } from "./dto/addon-outbound-dto";
import { MatDialog } from "@angular/material/dialog";
import { NotificationService } from "src/app/service/notification.service";


@Injectable({
    providedIn: 'root'
  })
export class ADDONOutboundService extends CommonInOutMessagesService<ADDONOutDTO, ADDONOutFilterDTO>{

    constructor(private httpService: CommonService, notify: NotificationService, confirmationDialog: MatDialog){
        super(httpService, ControllerPath.FTSDBFS_PREFIX + ConfigPathInOut.OUTBOUND, confirmationDialog, notify);
    }
}