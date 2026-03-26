import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { booleanArray } from '../../common-config-dto';
import { MQPrimitiveDTO } from '../dto/mq-primitive-dto';
import { MqPrimitiveService } from '../mq-primitive-service';
import {Location} from '@angular/common';
import { Router } from "@angular/router";


@Component({
  selector: 'mq-primitive-edit',
  templateUrl: './mq-primitive-edit.component.html',
  styleUrls: ['./mq-primitive-edit.component.scss']
})
export class MqPrimitiveEditComponent implements OnInit {

  mqPrimitiveDTO!: MQPrimitiveDTO;
    configurationForm = new FormGroup({});
   
    constructor(private mqPrimitiveService: MqPrimitiveService,private _location: Location, private router:Router) { }

    ngOnInit() {
        this.mqPrimitiveDTO = this.mqPrimitiveService.currentSelection;
        this.initializeForm();
    }

  
    onSubmit(form: FormGroup){  
        let id = this.mqPrimitiveService.currentSelection.id;

        let mqPrimitiveValue = form.value;
        
        for(let key in form.value){
          mqPrimitiveValue[key] =  form.value[key] || form.value[key] === false ? form.value[key] : undefined;
        }
        this.mqPrimitiveService.currentSelection = mqPrimitiveValue;
        this.mqPrimitiveService.currentSelection.id = id;
        this.mqPrimitiveService.postConfiguration("configuration/mq-primitive");
        
    }


    initializeForm() {
        this.configurationForm = new FormGroup({
          mqChannel: new FormControl(this.mqPrimitiveDTO.mqChannel,),
          primitive: new FormControl(this.mqPrimitiveDTO.primitive, [Validators.pattern('[0-9A-Z]+(,[0-9A-Z]+)*')]),
          queueName: new FormControl(this.mqPrimitiveDTO.queueName),
          toLoad: new FormControl(this.mqPrimitiveDTO.toLoad)

        });
    }

    openConfirmDialog() {
        this.mqPrimitiveService.deleteConfigurations();
    }
 
    getBooleanArray() {
      return booleanArray;
  }

  goBackToList(){
    this._location.back();
}
}
