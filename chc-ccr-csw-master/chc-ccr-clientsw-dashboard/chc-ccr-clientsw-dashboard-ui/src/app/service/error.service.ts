import { Injectable, NgZone } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ErrorHandler } from '@angular/core';
import { SessionService } from './session.service';
import { Router } from '@angular/router';
import { NotificationStatus } from './notification.service';
import { Msg } from '../common-message';
import { EventBusService } from '../modules/_shared/event-bus.service';
import { EventData } from '../modules/_shared/event.class';

@Injectable({
  providedIn: 'root'
})
export class ErrorService {


  constructor(private snackBar: MatSnackBar, private zone: NgZone, private session: SessionService, private route: Router, private eventBusService: EventBusService) {}

  public httpError(error: HttpErrorResponse) {
    if (error.error) {
      // A client-side or network error occurred. Handle it accordingly.
      console.error('An error occurred:', error.error.localizedMessage);
      if (error.error.fieldErrors) {
        let ex: ErrorMessage = error.error;
        ex.fieldErrors.forEach(e => {
          console.error('ERROR field ' + e.field + ' ' + e.localizedMessage);
        });
      }
    } else {
      // The backend returned an unsuccessful response code.
      // The response body may contain clues as to what went wrong.
      console.error(
        `Backend returned code ${error.status}, ` +
        `body was: ${error.error}`);
    }

    let message = '';
    switch (error.status) {
      case 400:
        message = error.error.localizedMessage;
        break;
      case 401:
        message = "Unauthorized";
        this.eventBusService.emit(new EventData('logout', null));
        break;
      case 403:
        message = "Unauthorized"; 
        this.eventBusService.emit(new EventData('logout', null));
       break;
      case 504:
        message = Msg.TIMEOUT;
        break;
      case 500:
        message = error.error.localizedMessage;
        console.log("From error serivce 500")

        break;
      default:
        {
          message = Msg.GENERIC_ERROR;
          if (error.error.message) {
            message = message + error.error.message ;
          }
        }
    }

    this.zone.run(() => {
      this.snackBar.open(message, 'Close', {
        panelClass: ['snackbar-danger'],
        duration: 10000
      });
    });
  }
}




export class ErrorMessage {
  fieldErrors: FieldErrorBean[] = [];
  localizedMessage!: string;
	code!: string;
}

export class FieldErrorBean {
  field!: string;
  localizedMessage!: string;
}
