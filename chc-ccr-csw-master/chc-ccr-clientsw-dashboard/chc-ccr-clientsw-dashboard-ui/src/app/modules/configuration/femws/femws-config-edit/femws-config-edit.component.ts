import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ConfigFemwSDTO } from '../dto/femws-config-dto';
import { FemwsConfigService } from '../femws-config-service';
import {Location} from '@angular/common';
import { Router } from '@angular/router';


@Component({
  selector: 'femws-config-edit',
  templateUrl: './femws-config-edit.component.html',
  styleUrls: ['./femws-config-edit.component.scss']
})
export class FemwsConfigEditComponent implements OnInit {

  configFemwSDTO!: ConfigFemwSDTO;
    configurationForm = new FormGroup({});
   
    constructor(private femwsConfigService: FemwsConfigService, private _location: Location, private router:Router) { }

    ngOnInit() {
        this.configFemwSDTO = this.femwsConfigService.currentSelection;
        this.initializeForm();
    }

  
    onSubmit(form: FormGroup){  
        let id = this.femwsConfigService.currentSelection.id;

        let femwsConfigValue = form.value;
        
        for(let key in form.value){
          femwsConfigValue[key] =  form.value[key] || form.value[key] === false ? form.value[key] : undefined;
        }
        this.femwsConfigService.currentSelection = femwsConfigValue;
        this.femwsConfigService.currentSelection.id = id;
        this.femwsConfigService.postConfiguration("configuration/femws");
    }


    initializeForm() {
        this.configurationForm = new FormGroup({
          baId: new FormControl(this.configFemwSDTO.baId, [Validators.required]),
          wsSoapAction: new FormControl(this.configFemwSDTO.wsSoapAction),
          webServerUrl: new FormControl(this.configFemwSDTO.webServerUrl)

        });
    }

    openConfirmDialog() {
        this.femwsConfigService.deleteConfigurations();
    }
 
    goBackToList(){
      this._location.back();
    }
}
