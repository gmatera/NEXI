import { Injectable } from "@angular/core";
import { MatSnackBar, MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition } from "@angular/material/snack-bar";

@Injectable({
    providedIn: 'root'
  })
export class NotificationService {

    horizontalPosition: MatSnackBarHorizontalPosition = 'right';
    verticalPosition: MatSnackBarVerticalPosition = 'top';
    
    constructor(private snackBar: MatSnackBar) {}

    openNotification(notificationDTO: NotificationDTO) {
        this.snackBar.open(notificationDTO.message, "Ok", {
            horizontalPosition: this.horizontalPosition,
            verticalPosition: this.verticalPosition,
            panelClass: [this.getNotificationColor(notificationDTO.notificationsStatus)],
            duration: 2000,
          });
    }

    getNotificationColor(status: NotificationStatus){
        switch(status) {
            case NotificationStatus.INFO:
                return "snackbar-info";
            case NotificationStatus.SUCCESS:
                return "snackbar-success";
            case NotificationStatus.WARNING:
                return "snackbar-warning";
            case NotificationStatus.DANGER:
                return "snackbar-danger";
        }
    }

}

export enum NotificationStatus {
   INFO, SUCCESS, WARNING, DANGER
}

export class NotificationDTO {
    message!: string;
    notificationsStatus!: NotificationStatus;
}