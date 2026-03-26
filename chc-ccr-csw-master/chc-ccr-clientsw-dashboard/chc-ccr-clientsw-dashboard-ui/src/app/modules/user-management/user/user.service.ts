
import {delay, Observable, of} from 'rxjs';
import { CommonService } from 'src/app/service/common.service';
import { MatTableDataSource } from '@angular/material/table';
import { NotificationDTO, NotificationService, NotificationStatus } from 'src/app/service/notification.service';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialog, DialogData } from '../../confirmation-dialog/confirm-dialog';
import { Location } from '@angular/common';
import { ControllerPath, PagedResultDTO } from '../../common-dto/common-dto';
import { UserDTO, UserFilterDTO } from './dto/user-dto';
import { Injectable } from '@angular/core';
import { UserCommonData } from './user-common';
import { Router } from '@angular/router';

@Injectable({
    providedIn: 'root'
  })
export class UserService{

  dataSource!: MatTableDataSource<UserDTO>;
  length = 0;
  pageSize = 25;
  pageIndex = 0;
  pageSizeOptions: number[] = [25, 50, 100, 150];
  isLoading = false;

  constructor(private http: CommonService, private notifyService: NotificationService,public userCommonData: UserCommonData,
    public dialog: MatDialog,private location: Location, public router: Router) {
  }


  loadData(filter: UserFilterDTO) {

    this.http.post<PagedResultDTO>(ControllerPath.USER_PREFIX + ControllerPath.USER_LIST, filter).subscribe(res => {
      this.dataSource = new MatTableDataSource(res.list)
      this.length = res.totalItems;
      this.initDTO();
    });
  }


  protected initDTO() {
    let dto = new UserDTO();

    dto.username = "";
    dto.fullName = "";
    dto.password = "";
    dto.roles = [];

   this.userCommonData.currentSelection = dto;
}



  postConfiguration(url: string ){
        this.http.post<any>(url, this.userCommonData.currentSelection).subscribe( () => {
        var notificationDTO = new NotificationDTO();
        notificationDTO.message = "success";
        notificationDTO.notificationsStatus = NotificationStatus.SUCCESS;
        this.notifyService.openNotification(notificationDTO);
        this.router.navigateByUrl("/user");

    });
  }

  deleteConfigurations(){
    
    let dataDialog: DialogData = {
      elementId: this.userCommonData.currentSelection.id,
      message: "Are you sure you want to delete this user? the operation is irreversible",
      confirmed: false
    };
    const dialogRef = this.dialog.open(ConfirmDialog, {
      width: '400px',
      data: dataDialog
    });
    

    dialogRef.afterClosed().subscribe((result: DialogData) => {
      if (dataDialog.confirmed) {
        this.http.post<any>(ControllerPath.USER_PREFIX + ControllerPath.USER_DELETE +this.userCommonData.currentSelection.id).subscribe( () => {
          var notificationDTO = new NotificationDTO();
          notificationDTO.message = "user deleted";
          notificationDTO.notificationsStatus = NotificationStatus.DANGER;
          this.notifyService.openNotification(notificationDTO);
          delay(2000);
          this.location.back();
        });
      }
    });
    
  }
}
