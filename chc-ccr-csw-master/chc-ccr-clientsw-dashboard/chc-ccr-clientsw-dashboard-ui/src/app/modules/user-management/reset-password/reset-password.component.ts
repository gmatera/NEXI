import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { NotificationDTO, NotificationService, NotificationStatus } from 'src/app/service/notification.service';
import { UserDTO } from '../user/dto/user-dto';
import { AuthService } from '../../_services/auth.service';
import { EventBusService } from '../../_shared/event-bus.service';
import { EventData } from '../../_shared/event.class';
import { CommonService } from 'src/app/service/common.service';
import { ControllerPath } from '../../common-dto/common-dto';
import { MatTableDataSource } from '@angular/material/table';
import Validation from 'src/app/service/validation.service';
import { Regex } from 'src/app/enums/regex';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent implements OnInit {

  userDTO=new  UserDTO();
  dataSource!: MatTableDataSource<UserDTO>;
  resetPasswordForm = new FormGroup({});

  constructor(private http: CommonService,private authService: AuthService,private eventBusService: EventBusService,private notifyService: NotificationService) { }

  ngOnInit(): void {
    this.initializeForm();
    this.userDTO.secretAnswerOne = "Secret Answer One";
    this.userDTO.secretAnswerTwo = "Secret Answer Two";

  }

  initializeForm() {
    this.resetPasswordForm = new FormGroup({
      username: new FormControl(this.userDTO.username, [Validators.required,]),
      secretResponseOne: new FormControl(this.userDTO.secretResponseOne, [Validators.required,]),
      secretResponseTwo: new FormControl(this.userDTO.secretResponseTwo, [Validators.required,]),
      password: new FormControl('',[Validators.maxLength(64), Validators.minLength(8), Validators.pattern(Regex.Password)]),
      confirmPassword: new FormControl('', [Validators.required, ]),
    },
    {
      validators: [Validation.match('password', 'confirmPassword')]
    }
    
    );

}


loadData(userDTO: UserDTO) {
  this.http.post<UserDTO>(ControllerPath.USER_PREFIX + ControllerPath.SECRET_DETAILS, userDTO).subscribe(res => {
    this.userDTO = res;
  });
}


getUserDetails(){
  this.userDTO.username = this.resetPasswordForm.value.username;
  this.loadData(this.userDTO);
}

onSubmit(form: FormGroup): void {
 this.userDTO = form.value;
 console.error("user dato reset pass", this.userDTO);
  this.authService.resetPassword(this.userDTO).subscribe({
    next: data => {
      var notificationDTO = new NotificationDTO();
      notificationDTO.message = "password changed successfully! login with new password";
      notificationDTO.notificationsStatus = NotificationStatus.SUCCESS;
      this.notifyService.openNotification(notificationDTO);
     this.eventBusService.emit(new EventData('logout', null));
    }
  });
 
}

}
