import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { booleanArray } from '../../common-config-dto';
import { GlobalPropertiesDTO } from '../dto/global-properties-dto';
import { GlobalPropertiesService } from '../global-properties-service';
import {Location} from '@angular/common';
import { Router } from '@angular/router';


@Component({
  selector: 'global-properties-edit',
  templateUrl: './global-properties-edit.component.html',
  styleUrls: ['./global-properties-edit.component.scss']
})
export class GlobalPropertiesEditComponent implements OnInit {

    globalPropertiesDTO!: GlobalPropertiesDTO;
    configurationForm = new FormGroup({});
   
    constructor(private globalPropertiesService: GlobalPropertiesService,private _location: Location, private router:Router) { }

    ngOnInit() {
        this.globalPropertiesDTO = this.globalPropertiesService.currentSelection;
        this.initializeForm();
    }

  
     onSubmit(form: FormGroup){  
        let id = this.globalPropertiesService.currentSelection.id;

        let globalProprtiesValue = form.value;
                console.log("FORM VALUE", form.value);
                console.log("SERVICE", this.globalPropertiesService.dataSource)
        for(let key in form.value){
          globalProprtiesValue[key] =  form.value[key] || form.value[key] === false ? form.value[key] : undefined;
        }
        this.globalPropertiesService.currentSelection = globalProprtiesValue;
        this.globalPropertiesService.currentSelection.id = id;
        this.globalPropertiesService.postConfiguration("configuration/global-properties");
    }


    initializeForm() {
        this.configurationForm = new FormGroup({
          propertyName: new FormControl(this.globalPropertiesDTO.propertyName,),
          value: new FormControl(this.globalPropertiesDTO.value,),
          type: new FormControl(this.globalPropertiesDTO.type),
          mandatory: new FormControl(this.globalPropertiesDTO.mandatory),
          requiredMsg: new FormControl(this.globalPropertiesDTO.requiredMsg),
          minLength: new FormControl(this.globalPropertiesDTO.minLength),
          maxLength: new FormControl(this.globalPropertiesDTO.maxLength),
          displayName: new FormControl(this.globalPropertiesDTO.displayName),

        });
    }

    openConfirmDialog() {
        this.globalPropertiesService.deleteConfigurations();
    }
 
    getBooleanArray() {
      return booleanArray;
  }

    goBackToList(){
      this._location.back();
    }

}
