import { Component, OnInit } from "@angular/core";
import { FormControl, FormGroup, Validators } from "@angular/forms";
import { SystemConfigDTO } from "../dto/system-config-dto";
import { SystemConfigService } from "../system-config-service";

@Component({
  selector: 'system-config-edit',
  templateUrl: './system-config-edit.component.html',
  styleUrls: ['./system-config-edit.component.css']
})
export class SystemConfigEditComponent implements OnInit {

    systemConfigDTO!: SystemConfigDTO;
    configurationForm = new FormGroup({});
    enumKey = Object.keys;
    modules = ["DASH","POLLER"];
  
    constructor(private systemConfigurationService: SystemConfigService) { }

    ngOnInit() {
        this.systemConfigDTO = this.systemConfigurationService.currentSelection;
        this.initializeForm();
    }

  
    onSubmit(form: FormGroup){  
        console.log("System form value id",this.systemConfigurationService.currentSelection.id);
        let id = this.systemConfigurationService.currentSelection.id;

        let systemConfigValue = form.value;
        
        for(let key in form.value){
          systemConfigValue[key] =  form.value[key] || form.value[key] === false ? form.value[key] : undefined;
        }
        this.systemConfigurationService.currentSelection = systemConfigValue;
        this.systemConfigurationService.currentSelection.id = id;
        console.log("System form value ",this.systemConfigurationService.currentSelection);
        this.systemConfigurationService.postConfiguration("configuration/system");
    }


    initializeForm() {
        this.configurationForm = new FormGroup({
            paramKey: new FormControl(this.systemConfigDTO.paramKey,),
            paramValue: new FormControl(this.systemConfigDTO.paramValue, ),
            module: new FormControl(this.systemConfigDTO.module)
        });
    }

    openConfirmDialog() {
        this.systemConfigurationService.deleteConfigurations();
    }
 

}
