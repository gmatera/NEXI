import { Component, OnInit } from "@angular/core";
import { FormControl, FormGroup, Validators } from "@angular/forms";
import { ControllerPath } from "src/app/modules/common-dto/common-dto";
import { CommonEditConfiguratiorService } from "../../common-config-edit-service";
import { FtsConfigDTO } from "../dto/fts-config-dto";
import { FTSConfigService } from "../fts-config-service";
import {Location} from '@angular/common';
import { Router } from "@angular/router";


@Component({
    selector: 'fts-config-edit',
    templateUrl: './fts-config-edit.component.html',
    styleUrls: ['./fts-config-edit.component.css']
})
export class FTSConfigEditComponent extends CommonEditConfiguratiorService implements OnInit {

    
    ftsConfigDTO!: FtsConfigDTO;
    enumKey = Object.keys;

    validationArrayMq = ['rcvAutoRead', 'rcvPrimConvFormat', 'uploadQName' , 'mqiPosCreateInd'];
    validationArrayDB = ['rcvDnsCreationAlgo'];

    constructor(private ftsConfigurationService: FTSConfigService, private _location: Location,  private router:Router) {
        super();
    }

    ngOnInit() {
        this.ftsConfigDTO = this.ftsConfigurationService.currentSelection;
        this.initializeForm();
        this.selectedInterface(this.ftsConfigDTO.interfaceType,this.configurationForm);
        this.selectedsndCodePage(this.ftsConfigDTO.sndCodePage, this.configurationForm); //TO DO Remove the form
        this.selectedrcvCodePage(this.ftsConfigDTO.rcvCodePage, this.configurationForm);
        this.selectedhubCodePage(this.ftsConfigDTO.hubCodePage);
        this.selectedLauEnabled(this.ftsConfigDTO.lauEnabled);
    }




    selectedInterface(interfaceType: any, form: FormGroup) {

        if (interfaceType == 'DB') {
            this.ftsConfigurationService.updateServiceUri(ControllerPath.FTSDB_PREFIX);

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
            this.ftsConfigurationService.updateServiceUri(ControllerPath.FTSMQ_PREFIX);

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

        let id = this.ftsConfigurationService.currentSelection.id;
        let ftsConfigValue = form.value;
        // for (let key in form.value) {
        //     ftsConfigValue[key] = form.value[key] || form.value[key] === false ? form.value[key] : undefined;
        // }
        if(ftsConfigValue["sndCodePage"]=="BINARY")ftsConfigValue["sndLineSeparator"] ="NONE";
        if(ftsConfigValue["rcvCodePage"]=="BINARY")ftsConfigValue["rcvLineSeparator"] ="NONE";
        this.ftsConfigurationService.currentSelection = ftsConfigValue;
        this.ftsConfigurationService.currentSelection.id = id;
        let  rcvPath = this.ftsConfigurationService.currentSelection.rcvPath;
        rcvPath = rcvPath.slice(-1) == "/" || rcvPath.slice(-1) == "\\"? rcvPath.slice(0,-1) : rcvPath;
        this.ftsConfigurationService.currentSelection.rcvPath = rcvPath;
        this.ftsConfigurationService.postConfiguration("configuration/messages/fts");

    }


    initializeForm() {
        this.configurationForm = new FormGroup({
            localBaId: new FormControl(this.ftsConfigDTO.localBaId, [Validators.maxLength(12),Validators.minLength(12),Validators.pattern('[0-9A-Z]{12}')]),
            remoteBaId: new FormControl(this.ftsConfigDTO.remoteBaId,  [Validators.maxLength(12),Validators.minLength(12),Validators.pattern('[0-9A-Z]{12}')]),
            interfaceType: new FormControl(this.ftsConfigDTO.interfaceType, [Validators.required]),

            sndCodePage: new FormControl(this.ftsConfigDTO.sndCodePage, [Validators.required]),
            sndLineSeparator: new FormControl(this.ftsConfigDTO.sndLineSeparator,),
            sndRecordFormat: new FormControl(this.ftsConfigDTO.sndRecordFormat,),
            sndMaxRecLength: new FormControl(this.ftsConfigDTO.sndMaxRecLength,),
            // sndVfnCreationAlgo: new FormControl(this.ftsConfigDTO.sndVfnCreationAlgo,),
            sndCompletionAlgo: new FormControl(this.ftsConfigDTO.sndCompletionAlgo, [Validators.required]),
            mqiPosCreateInd: new FormControl(this.ftsConfigDTO.mqiPosCreateInd, [Validators.required]),


            rcvCodePage: new FormControl(this.ftsConfigDTO.rcvCodePage, [Validators.required]),
            rcvLineSeparator: new FormControl(this.ftsConfigDTO.rcvLineSeparator,),
            rcvAutoRead: new FormControl(this.ftsConfigDTO.rcvAutoRead,),
            rcvDnsCreationAlgo: new FormControl(this.ftsConfigDTO.rcvDnsCreationAlgo, [Validators.required]),
            rcvDsnPrefix: new FormControl(this.ftsConfigDTO.rcvDsnPrefix,),
            rcvCompletionAlgo: new FormControl(this.ftsConfigDTO.rcvCompletionAlgo, [Validators.required]),
            rcvPath: new FormControl(this.ftsConfigDTO.rcvPath, [Validators.required]),
            rcvPrimConvFormat: new FormControl(this.ftsConfigDTO.rcvPrimConvFormat,),
            uploadQName: new FormControl(this.ftsConfigDTO.uploadQName, [Validators.required]),
            digestFileAlg: new FormControl(this.ftsConfigDTO.digestFileAlg),

            lauEnabled: new FormControl(this.ftsConfigDTO.lauEnabled, [Validators.required]),
            lauFormat: new FormControl(this.ftsConfigDTO.lauFormat),
            lauKey: new FormControl(this.ftsConfigDTO.lauKey, [Validators.required]),
            rightLauKey: new FormControl(this.ftsConfigDTO.rightLauKey),

            hubCodePage: new FormControl(this.ftsConfigDTO.hubCodePage, [Validators.required]),
            hubLineSeparator: new FormControl(this.ftsConfigDTO.hubLineSeparator, [Validators.required]),
            rcvRecordFormat: new FormControl(this.ftsConfigDTO.rcvRecordFormat, [Validators.required]),
            rcvMaxRecLength: new FormControl(this.ftsConfigDTO.rcvMaxRecLength, [Validators.required])

        });
    }



    openConfirmDialog() {
        this.ftsConfigurationService.deleteConfigurations();
    }

    goBackToList(){
        this._location.back();
    }
}