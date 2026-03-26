import { Component, OnInit } from "@angular/core";
import { FormControl, FormGroup, Validators } from "@angular/forms";
import { ControllerPath } from "src/app/modules/common-dto/common-dto";
import { booleanArray, codePageArray, digestFileAlgArray, dnsCreationAlgoArray, formatArray, interfaceTypeArray, lineSeparatorArray, recordFormatArray, vfnCreationAlgoArray } from "../../common-config-dto";
import { CommonEditConfiguratiorService } from "../../common-config-edit-service";
import { FmsConfigDTO } from "../dto/fms-config-dto";
import { FMSConfigService } from "../fms-config-service";
import {Location} from '@angular/common';
import { Router } from '@angular/router';
import { NotificationDTO, NotificationService, NotificationStatus } from "src/app/service/notification.service";


@Component({
    selector: 'fms-config-edit',
    templateUrl: './fms-config-edit.component.html',
    styleUrls: ['./fms-config-edit.component.css']
})
export class FMSConfigEditComponent extends CommonEditConfiguratiorService implements OnInit {

    fmsConfigDTO!: FmsConfigDTO;
    enumKey = Object.keys;

    validationArrayMq = ['rcvAutoRead', 'rcvPrimConvFormat', 'uploadQName' , 'mqiPosCreateInd'];
    validationArrayDB = ['rcvDnsCreationAlgo'];

    constructor(private fmsConfigurationService: FMSConfigService, private _location: Location,  private router:Router,
        private notifyService: NotificationService) {
        super();
    }


    ngOnInit() {
        this.fmsConfigDTO = this.fmsConfigurationService.currentSelection;
        this.initializeForm();
        if(this.fmsConfigDTO != undefined){
            this.selectedInterface(this.fmsConfigDTO.interfaceType,this.configurationForm)
        }
        this.selectedsndCodePage(this.fmsConfigDTO.sndCodePage, this.configurationForm);
        this.selectedrcvCodePage(this.fmsConfigDTO.rcvCodePage, this.configurationForm);
        this.selectedSndRecordFormat(this.fmsConfigDTO.sndRecordFormat, this.configurationForm);
        // this.selectedRcvRecordFormat(this.fmsConfigDTO.rcvRecordFormat, this.configurationForm);
        this.selectedhubCodePage(this.fmsConfigDTO.hubCodePage);
        this.selectedLauEnabled(this.fmsConfigDTO.lauEnabled);
    }


    selectedInterface(interfaceType: any, form: FormGroup) {

        if (interfaceType == 'DB') {
            this.fmsConfigurationService.updateServiceUri(ControllerPath.FMSDB_PREFIX);

            for (let i = 0; i < this.validationArrayMq.length; i++) {
                form.get(this.validationArrayMq[i])?.clearValidators();
                form.get(this.validationArrayMq[i])?.updateValueAndValidity();
            }

            for (let i = 0; i < this.validationArrayDB.length; i++) {
                form.get(this.validationArrayDB[i])?.setValidators(Validators.required);
                form.get(this.validationArrayDB[i])?.updateValueAndValidity();
            }

        }
        else {
            this.fmsConfigurationService.updateServiceUri(ControllerPath.FMSMQ_PREFIX);

            for (let i = 0; i < this.validationArrayDB.length; i++) {
                form.get(this.validationArrayDB[i])?.clearValidators();
                form.get(this.validationArrayDB[i])?.updateValueAndValidity();
            }

            for (let i = 0; i < this.validationArrayMq.length; i++) {
                form.get(this.validationArrayMq[i])?.setValidators(Validators.required);
                form.get(this.validationArrayMq[i])?.updateValueAndValidity();
            }

        }

    }


    onSubmit(form: FormGroup) {
        let id = this.fmsConfigurationService.currentSelection.id;
        
        let fmsConfigValue = form.value;
        // for (let key in form.value) {
        //     fmsConfigValue[key] = form.value[key] || form.value[key] === false ? form.value[key] : undefined;
        // }
        if(fmsConfigValue["sndCodePage"]=="BINARY")fmsConfigValue["sndLineSeparator"] ="NONE";
        if(fmsConfigValue["rcvCodePage"]=="BINARY")fmsConfigValue["rcvLineSeparator"] ="NONE";
        console.log("Fms config values", fmsConfigValue)
        this.fmsConfigurationService.currentSelection = fmsConfigValue;
        this.fmsConfigurationService.currentSelection.id = id;
        let  rcvPath = this.fmsConfigurationService.currentSelection.rcvPath;
        rcvPath = rcvPath.slice(-1) == "/" || rcvPath.slice(-1) == "\\"? rcvPath.slice(0,-1) : rcvPath;
        this.fmsConfigurationService.currentSelection.id = id;
        this.fmsConfigurationService.currentSelection.rcvPath = rcvPath;
        this.fmsConfigurationService.postConfiguration("configuration/messages/fms");

    }


    initializeForm() {

        this.configurationForm = new FormGroup({
            localBaId: new FormControl(this.fmsConfigDTO.localBaId, [Validators.maxLength(12), Validators.minLength(12), Validators.pattern('[0-9A-Z]{12}')]),
            remoteBaId: new FormControl(this.fmsConfigDTO.remoteBaId, [Validators.maxLength(12), Validators.minLength(12), Validators.pattern('[0-9A-Z]{12}')]),
            interfaceType: new FormControl(this.fmsConfigDTO.interfaceType, [Validators.required]),

            sndCodePage: new FormControl(this.fmsConfigDTO.sndCodePage, [Validators.required]),
            sndLineSeparator: new FormControl(this.fmsConfigDTO.sndLineSeparator,),
            sndRecordFormat: new FormControl(this.fmsConfigDTO.sndRecordFormat,[Validators.required]),
            sndMaxRecLength: new FormControl(this.fmsConfigDTO.sndMaxRecLength,[Validators.required]),
            // sndVfnCreationAlgo: new FormControl(this.fmsConfigDTO.sndVfnCreationAlgo,),
            sndCompletionAlgo: new FormControl(this.fmsConfigDTO.sndCompletionAlgo, [Validators.required]),
            mqiPosCreateInd: new FormControl(this.fmsConfigDTO.mqiPosCreateInd, [Validators.required]),

            rcvCodePage: new FormControl(this.fmsConfigDTO.rcvCodePage, [Validators.required]),
            rcvLineSeparator: new FormControl(this.fmsConfigDTO.rcvLineSeparator,),
            rcvAutoRead: new FormControl(this.fmsConfigDTO.rcvAutoRead,),
            rcvDnsCreationAlgo: new FormControl(this.fmsConfigDTO.rcvDnsCreationAlgo, [Validators.required]),
            rcvDsnPrefix: new FormControl(this.fmsConfigDTO.rcvDsnPrefix,),
            rcvCompletionAlgo: new FormControl(this.fmsConfigDTO.rcvCompletionAlgo, [Validators.required]),
            rcvPath: new FormControl(this.fmsConfigDTO.rcvPath, [Validators.required]),
            rcvPrimConvFormat: new FormControl(this.fmsConfigDTO.rcvPrimConvFormat,),
            uploadQName: new FormControl(this.fmsConfigDTO.uploadQName, [Validators.required]),
            digestFileAlg: new FormControl(this.fmsConfigDTO.digestFileAlg),

            lauEnabled: new FormControl(this.fmsConfigDTO.lauEnabled, [Validators.required]),
            lauFormat: new FormControl(this.fmsConfigDTO.lauFormat),
            lauKey: new FormControl(this.fmsConfigDTO.lauKey),
            rightLauKey: new FormControl(this.fmsConfigDTO.rightLauKey),
            hubCodePage: new FormControl(this.fmsConfigDTO.hubCodePage, [Validators.required]),
            hubLineSeparator: new FormControl(this.fmsConfigDTO.hubLineSeparator, [Validators.required]),
            rcvRecordFormat: new FormControl(this.fmsConfigDTO.rcvRecordFormat, [Validators.required]),
            rcvMaxRecLength: new FormControl(this.fmsConfigDTO.rcvMaxRecLength, [Validators.required])

        });
    }


    openConfirmDialog() {
        this.fmsConfigurationService.deleteConfigurations();
    }


    goBackToList(){
        this._location.back();
    }
}