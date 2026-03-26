
import {delay, Observable, of} from 'rxjs';
import { CommonService } from 'src/app/service/common.service';
import { MatTableDataSource } from '@angular/material/table';
import { NotificationDTO, NotificationService, NotificationStatus } from 'src/app/service/notification.service';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialog, DialogData } from '../confirmation-dialog/confirm-dialog';
import { Location } from '@angular/common';
import { CommonConfigDTO, CommonConfigFilterDTO, ConfigPath } from './common-config-dto';
import { PagedResultDTO } from '../common-dto/common-dto';
import { Router } from '@angular/router';

export abstract class ConfiguratiorService<D extends CommonConfigDTO, F extends CommonConfigFilterDTO> {

  dataSource!: MatTableDataSource<D>;

  currentSelection!: D;

  length = 0;
  pageSize = 25;
  pageIndex = 0;
  pageSizeOptions: number[] = [25, 50, 100, 150];
  isLoading = false;

  protected abstract initDTO(): void;

  constructor(private http: CommonService, private serviceUri: string, private notifyService: NotificationService,
    public dialog: MatDialog,
    private location: Location,  private router:Router) {
    this.initDTO();
  }

  loadData(filter: F) {

    this.http.post<PagedResultDTO>(this.serviceUri + ConfigPath.CONFIG_LIST, filter).subscribe(res => {
      this.dataSource = new MatTableDataSource(res.list)
      this.length = res.totalItems;
      this.initDTO();
    });
  }

  updateServiceUri(serviceUri : string){
    this.serviceUri = serviceUri;
  }

  postConfiguration(navigateTo: string){
    console.log("URI is ",this.serviceUri);
         
    this.http.post<any>(this.serviceUri + ConfigPath.CONFIG_SAVE, this.currentSelection).subscribe( () => {
      var notificationDTO = new NotificationDTO();
      notificationDTO.message = "success";
      notificationDTO.notificationsStatus = NotificationStatus.SUCCESS;
      //this.notifyService.openNotification(notificationDTO);
      this.router.navigateByUrl(navigateTo);
    });
  }

  deleteConfigurations(){
    
    let dataDialog: DialogData = {
      elementId: this.currentSelection.id,
      message: "Are you sure you want to delete this configuration? the operation is irreversible",
      confirmed: false
    };
    const dialogRef = this.dialog.open(ConfirmDialog, {
      width: '400px',
      data: dataDialog
    });
    

    dialogRef.afterClosed().subscribe((result: DialogData) => {
      if (dataDialog.confirmed) {
        this.http.post<any>(this.serviceUri + ConfigPath.CONFIG_DELETE + this.currentSelection.id).subscribe( () => {
          var notificationDTO = new NotificationDTO();
          notificationDTO.message = "configuration deleted";
          notificationDTO.notificationsStatus = NotificationStatus.DANGER;
          this.notifyService.openNotification(notificationDTO);
          delay(2000);
          this.location.back();
        });
      }
    });
    
  }
}
