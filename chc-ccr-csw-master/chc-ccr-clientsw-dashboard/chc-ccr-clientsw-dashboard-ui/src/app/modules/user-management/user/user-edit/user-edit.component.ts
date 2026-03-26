import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Regex } from 'src/app/enums/regex';
import { ControllerPath } from '../../../common-dto/common-dto';
import { UserDTO } from '../dto/user-dto';
import { UserCommonData } from '../user-common';
import { UserService } from '../user.service';
import {Location} from '@angular/common';


@Component({
  selector: 'user-edit',
  templateUrl: './user-edit.component.html',
  styleUrls: ['./user-edit.component.scss']
})
export class UserEditComponent implements OnInit {

  userDTO!: UserDTO;
  userForm = new FormGroup({});


  rolesList: string[] = ['ROLE_USER', 'ROLE_ADMIN'];

  constructor(private userService: UserService, public router: Router, private userCommonData: UserCommonData,private rout:Router, private _location: Location) {
  }

  ngOnInit() {
    this.userDTO = this.userCommonData.currentSelection;
    this.initializeForm();
  }

   onSubmit(form: FormGroup) {

    let id = this.userCommonData.currentSelection.id;
    let userValue = form.value;
    for (let key in form.value) {
      userValue[key] = form.value[key] || form.value[key] === false ? form.value[key] : undefined;
    }
    this.userCommonData.currentSelection = userValue;
    this.userCommonData.currentSelection.id = id;
    if (id) {
      this.userCommonData.currentSelection.password = this.userDTO.password;
      this.userService.postConfiguration(ControllerPath.USER_PREFIX + ControllerPath.USER_UPDATE);
    } 
    else{
        this.userService.postConfiguration(ControllerPath.USER_PREFIX)
    }
  }


  initializeForm() {
    console.log("userID is", this.userDTO.id);
    this.userForm = new FormGroup({
      username: new FormControl(this.userDTO.username, [Validators.required]),
      fullName: new FormControl(this.userDTO.fullName, [Validators.required]),
      password: new FormControl({ value: this.userDTO.password, disabled: !!this.userDTO?.id }, [Validators.maxLength(64), Validators.minLength(8), Validators.pattern(Regex.Password)]),
      roles: new FormControl(this.userDTO.roles, [Validators.required]),
      secretAnswerOne: new FormControl(this.userDTO.secretAnswerOne, [Validators.required]),
      secretResponseOne: new FormControl(this.userDTO.secretResponseOne, [Validators.required]),
      secretAnswerTwo: new FormControl(this.userDTO.secretAnswerTwo, [Validators.required]),
      secretResponseTwo: new FormControl(this.userDTO.secretResponseTwo, [Validators.required]),

    });
  }

  openConfirmDialog() {
    this.userService.deleteConfigurations();
  }

  goBackToList(){
    this._location.back();
}
}