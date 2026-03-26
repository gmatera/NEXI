import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { booleanArray } from '../../common-config-dto';
import { ConfigRouteInterfaceDTO } from '../dto/route-interface-dto';
import { RouteInterfaceService } from '../route-interface-service';

@Component({
  selector: 'route-interface-edit',
  templateUrl: './route-interface-edit.component.html',
  styleUrls: ['./route-interface-edit.component.scss']
})
export class RouteInterfaceEditComponent implements OnInit {

    configRouteInterfaceDTO!: ConfigRouteInterfaceDTO;
    configurationForm = new FormGroup({});
   
    constructor(private routeInterfaceService: RouteInterfaceService) { }

    ngOnInit() {
        this.configRouteInterfaceDTO = this.routeInterfaceService.currentSelection;
        this.initializeForm();
    }

  
    onSubmit(form: FormGroup){  
        let id = this.routeInterfaceService.currentSelection.id;

        let routeInterfaceValue = form.value;
        
        for(let key in form.value){
          routeInterfaceValue[key] =  form.value[key] || form.value[key] === false ? form.value[key] : undefined;
        }
        this.routeInterfaceService.currentSelection = routeInterfaceValue;
        this.routeInterfaceService.currentSelection.id = id;
        this.routeInterfaceService.postConfiguration("configuration/route-interface");
    }


    initializeForm() {
        this.configurationForm = new FormGroup({
          localBaId: new FormControl(this.configRouteInterfaceDTO.localBaId, [Validators.required]),
          remoteBaId: new FormControl(this.configRouteInterfaceDTO.remoteBaId, [Validators.required]),
          interFace: new FormControl(this.configRouteInterfaceDTO.interFace, [Validators.required]),
          service: new FormControl(this.configRouteInterfaceDTO.service, [Validators.required])

        });
    }

    openConfirmDialog() {
        this.routeInterfaceService.deleteConfigurations();
    }
 
}
