import { Component, OnInit } from "@angular/core";
import { FormControl, FormGroup, Validators } from "@angular/forms";
import { ControllerPath } from "src/app/modules/common-dto/common-dto";
import { booleanArray, formatArray, interfaceTypeArray, mssRcvPrimConvFormat} from "../../common-config-dto";
import { MSSConfigDTO } from "../dto/mss-config-dto";
import { MSSConfigService } from "../mss-config-service";
import { NotificationDTO, NotificationService, NotificationStatus } from 'src/app/service/notification.service';
import {Location} from '@angular/common';
import { Router } from "@angular/router";
import { CommonEditConfiguratiorService } from "../../common-config-edit-service";


@Component({
  selector: 'mss-config-edit',
  templateUrl: './mss-config-edit.component.html',
  styleUrls: ['./mss-config-edit.component.css']
})
export class MSSConfigEditComponent extends CommonEditConfiguratiorService implements OnInit {

    mssConfigDTO!: MSSConfigDTO;
    enumKey = Object.keys;
    
    constructor(private mssConfigurationService: MSSConfigService, private _location: Location,  private router:Router) {
        super();
    }
   

    ngOnInit() {
        this.mssConfigDTO = this.mssConfigurationService.currentSelection;
        this.initializeForm();
        this.selectedLauEnabled(this.mssConfigDTO.lauEnabled);
    }

  
    onSubmit(form: FormGroup){  
        let id = this.mssConfigurationService.currentSelection.id;
        let mmsConfigValue = form.value;
        for(let key in form.value){
          mmsConfigValue[key] =  form.value[key] || form.value[key] === false ? form.value[key] : undefined;
        }
        this.mssConfigurationService.currentSelection = mmsConfigValue;
        this.mssConfigurationService.currentSelection.id = id;
        this.mssConfigurationService.postConfiguration("configuration/messages/mss");
    }


    initializeForm() {
        this.configurationForm = new FormGroup({
            localBaId: new FormControl(this.mssConfigDTO.localBaId, [Validators.maxLength(12),Validators.minLength(12),Validators.pattern('[0-9A-Z]{12}')]),
            remoteBaId: new FormControl(this.mssConfigDTO.remoteBaId, [Validators.maxLength(12),Validators.minLength(12),Validators.pattern('[0-9A-Z]{12}')]),
            interfaceType: new FormControl(this.mssConfigDTO.interfaceType, [Validators.required]),

            sndCompletionAlgo: new FormControl(this.mssConfigDTO.sndCompletionAlgo, [Validators.required]),
            rcvCompletionAlgo: new FormControl(this.mssConfigDTO.rcvCompletionAlgo,[Validators.required]),
            rcvPrimConvFormat: this.getRcvPrimConvFormatFormControl(this.mssConfigDTO.interfaceType),
          
            lauEnabled: new FormControl(this.mssConfigDTO.lauEnabled, [Validators.required]),
            lauFormat: new FormControl(this.mssConfigDTO.lauFormat),
            lauKey: new FormControl(this.mssConfigDTO.lauKey, [Validators.required]),
            rightLauKey: new FormControl(this.mssConfigDTO.rightLauKey),
    
        });
    }

    getRcvPrimConvFormatFormControl(interfaceType: string): FormControl{
        if(interfaceType == 'DB'){
            return new FormControl(this.mssConfigDTO.rcvPrimConvFormat);
        } else {
            return new FormControl(this.mssConfigDTO.rcvPrimConvFormat,[Validators.required]);
        }
    }


    selectedInterface(interfaceType: any) {
        if (interfaceType == 'DB') {
            this.mssConfigurationService.updateServiceUri(ControllerPath.MSSDB_PREFIX);
            this.configurationForm.get('rcvPrimConvFormat')?.clearValidators();
            this.configurationForm.get('rcvPrimConvFormat')?.updateValueAndValidity();
        }
        else {
            this.mssConfigurationService.updateServiceUri(ControllerPath.MSSMQ_PREFIX);
            this.configurationForm.get('rcvPrimConvFormat')?.setValidators(Validators.required);
            this.configurationForm.get('rcvPrimConvFormat')?.updateValueAndValidity();
        }

    }
 
    getMssRcvPrimConvFormat() {
        return mssRcvPrimConvFormat;
    }


    openConfirmDialog() {
        this.mssConfigurationService.deleteConfigurations();
    }


    goBackToList(){
        this._location.back();
    }
}