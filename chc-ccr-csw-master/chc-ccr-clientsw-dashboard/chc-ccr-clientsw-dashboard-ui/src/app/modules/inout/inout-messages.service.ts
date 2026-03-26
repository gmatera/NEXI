
import { CommonService } from 'src/app/service/common.service';
import { MatTableDataSource } from '@angular/material/table';
import { PageableDTO, PagedResultDTO } from '../common-dto/common-dto';
import { ConfigPathInOut } from './common-inout-dto';
import { ConfirmDialog, DialogData } from '../confirmation-dialog/confirm-dialog';
import { NotificationDTO, NotificationService, NotificationStatus } from 'src/app/service/notification.service';
import { MatDialog } from '@angular/material/dialog';
import {delay, Observable, of} from 'rxjs';



export abstract class CommonInOutMessagesService<D, F extends PageableDTO> {

  dataSource!: MatTableDataSource<D>;
  currentSelection!: D;

  public length = 0;
  public pageSize = 50;
  public pageIndex = 0;
  public pageSizeOptions: number[] = [50, 100, 150];

  constructor(private http: CommonService, private serviceUri: string, public dialog: MatDialog,
    private notifyService: NotificationService) {
  }

  updateServiceUri(serviceUri : string){
    this.serviceUri = serviceUri;
  }

  loadData(filter: F) {

    this.http.post<PagedResultDTO>(this.serviceUri + ConfigPathInOut.LIST, filter).subscribe(res => {
      this.dataSource = new MatTableDataSource(res.list)
      this.length = res.totalItems;
    });
  }

  retryInvalidBa() {
    
    let dataDialog: DialogData = {
      message: "Are you sure you want to retry invalid ba and invalid interface requests? The operation is irreversible",
      confirmed: false
    };

    const dialogRef = this.dialog.open(ConfirmDialog, {
      width: '400px',
      data: dataDialog
    });

    dialogRef.afterClosed().subscribe((result: DialogData) => {
      if (dataDialog.confirmed) {
        this.http.post<any>(this.serviceUri + ConfigPathInOut.RETRY_INVALID_BA_AND_INVALID_INTERFACE).subscribe(res => {
          var notificationDTO = new NotificationDTO();
          notificationDTO.message = "Retry submitted for invalid ba and invalid interface";
          notificationDTO.notificationsStatus = NotificationStatus.SUCCESS;
          this.notifyService.openNotification(notificationDTO);
          delay(2000);
        });
      }
    });
  }

}
