import { Component, OnInit } from "@angular/core";
import { FormControl, FormGroup, Validators } from "@angular/forms";
import { AddonConfigDTO } from "../dto/addon-config-dto";
import { AddonConfigService } from "../addon-config-service";
import { interfaceTypeArray } from "../../common-config-dto";
import {Location} from '@angular/common';
import { Router } from '@angular/router';


@Component({
    selector: 'addon-config-edit',
    templateUrl: './addon-config-edit.component.html',
    styleUrls: ['./addon-config-edit.component.css']
})
export class AddonConfigEditComponent implements OnInit {

    addonConfigDTO!: AddonConfigDTO;
    configurationForm = new FormGroup({});
    enumKey = Object.keys;
    
    constructor(private addonConfigurationService: AddonConfigService,private _location: Location,  private router:Router) { }

    ngOnInit() {
        this.addonConfigDTO = this.addonConfigurationService.currentSelection;
        this.initializeForm();
    }


    onSubmit(form: FormGroup){ 
        let id = this.addonConfigurationService.currentSelection.id; 

         let addonConfigValue = form.value;
        for(let key in form.value){
            addonConfigValue[key] =  form.value[key] || form.value[key] === false ? form.value[key] : undefined;
        }
        this.addonConfigurationService.currentSelection = addonConfigValue;
        this.addonConfigurationService.currentSelection.id = id;
        let  rcvPath = this.addonConfigurationService.currentSelection.rcvPath;
        let  sndPath = this.addonConfigurationService.currentSelection.sndPath;

        rcvPath = rcvPath.slice(-1) == "/" || rcvPath.slice(-1) == "\\"? rcvPath.slice(0,-1) : rcvPath;
        sndPath = sndPath.slice(-1) == "/" || sndPath.slice(-1) == "\\"? sndPath.slice(0,-1) : sndPath;

        this.addonConfigurationService.currentSelection.rcvPath = rcvPath;
        this.addonConfigurationService.currentSelection.sndPath = sndPath;

        this.addonConfigurationService.postConfiguration("configuration/messages/addon");

    }


    initializeForm() {
        this.configurationForm = new FormGroup({
            localBaId: new FormControl(this.addonConfigDTO.localBaId, [Validators.maxLength(12),Validators.minLength(12),Validators.pattern('[0-9A-Z]{12}'),Validators.required]),
            remoteBaId: new FormControl(this.addonConfigDTO.remoteBaId, [Validators.maxLength(12),Validators.minLength(12),Validators.pattern('[0-9A-Z]{12}'),Validators.required]),

            sndPath: new FormControl(this.addonConfigDTO.sndPath,[Validators.required]),
            rcvPath: new FormControl(this.addonConfigDTO.rcvPath,[Validators.required]),
            sendingPrefix: new FormControl(this.addonConfigDTO.sendingPrefix,[Validators.required] ),
            errorPrefix: new FormControl(this.addonConfigDTO.errorPrefix,[Validators.required] ),
            sentPrefix: new FormControl(this.addonConfigDTO.sentPrefix,[Validators.required] ),
            errorDeliverPrefix: new FormControl(this.addonConfigDTO.errorDeliverPrefix,[Validators.required] ),
    
        });
    }

    getInterfaceArray() {
        return interfaceTypeArray;
    }

    openConfirmDialog() {
        this.addonConfigurationService.deleteConfigurations();
    }

    goBackToList(){
        this._location.back();
    }
}