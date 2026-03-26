import { Component, Inject } from "@angular/core";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";

@Component({
    selector: 'confirm-dialog',
    templateUrl: 'confirm-dialog.html',
  })
export class ConfirmDialog {
    constructor(
        public dialogRef: MatDialogRef<ConfirmDialog>,
        @Inject(MAT_DIALOG_DATA) public data: DialogData,
      ) {}
    
      closeDialog(): void {
        this.data.confirmed = false;
        this.dialogRef.close();
      }

      confirmOperation(): void {
          this.data.confirmed = true;
          this.dialogRef.close();
      }
}

export interface DialogData {
    message: string;
    elementId?: number;
    confirmed: boolean;
  }