import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Regex } from 'src/app/enums/regex';
import { NotificationDTO, NotificationService, NotificationStatus } from 'src/app/service/notification.service';
import Validation from 'src/app/service/validation.service';
import { AuthService } from '../../_services/auth.service';
import { EventBusService } from '../../_shared/event-bus.service';
import { EventData } from '../../_shared/event.class';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.scss']
})
export class ChangePasswordComponent implements OnInit {

  changePasswordForm = new FormGroup({});

  constructor(private authService: AuthService,private eventBusService: EventBusService,private notifyService: NotificationService) { }

  ngOnInit(): void {
    this.initializeForm();
  }

  initializeForm() {
    this.changePasswordForm = new FormGroup({
      currentPassword: new FormControl('',[Validators.required]),
      password: new FormControl('',[Validators.maxLength(16), Validators.minLength(8), Validators.pattern(Regex.Password)]),
      confirmPassword: new FormControl('', [Validators.required, ]),
    },
    {
      validators: [Validation.match('password', 'confirmPassword')]
    }
    );
  }

  onSubmit(form: FormGroup): void {
    const { currentPassword, password } = form.value;
    this.authService.changePassword(currentPassword, password).subscribe({
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
