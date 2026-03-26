import { Component, OnInit } from '@angular/core';
import { ServiceRegistryService } from './service/ServiceRegistry.service';
import { ServiceRegistryDTO } from './dto/models';
import {MatGridListModule} from '@angular/material/grid-list';


@Component({
    selector: 'service-registry-component',
    templateUrl: './service-registry.component.html',
    styleUrls: ['./service-registry.component.css']
  })

  export class ServiceRegistry implements OnInit {

    serviceRegistryList: ServiceRegistryDTO[] = [];

    constructor(private serviceRegistryService: ServiceRegistryService) {}

    ngOnInit(){
        console.log('ServiceRegistry inittializing...');

        this.serviceRegistryService.getData().subscribe(res => {
            this.serviceRegistryList = res;
            console.log('ServiceRegistry init done '+res);
        });
    }

    getRolesById(id: string){
        let dto =  this.serviceRegistryList.find(x => x.id == id);
        if(dto != null){
            return dto.roles.toString().split(",");
        } else {
            return ["no value found"];
        }
    }

    getNumberOfGroups() {
        return this.serviceRegistryList.length;
    }

    getNumberOfActiveRoles(){
        let roles :string[] = [];
        for(let i = 0; i < this.getNumberOfGroups(); i++){
            if(this.serviceRegistryList[i].serviceStatus == "ACTIVE")
                roles = this.getRolesById(this.serviceRegistryList[i].id)
        }
        if(roles.length > 0)
            return roles.length;
        return 0;
    }

    getNumberOfStanbyRoles(){
        let roles :string[] = [];
        for(let i = 0; i < this.getNumberOfGroups(); i++){
            if(this.serviceRegistryList[i].serviceStatus == "PASSIVE")
                roles = this.getRolesById(this.serviceRegistryList[i].id)
        }
        if(roles.length > 0)
            return roles.length;
        return 0;
    }

    getNumberOfErrorRoles(){
        let roles :string[] = [];
        for(let i = 0; i < this.getNumberOfGroups(); i++){
            if(this.serviceRegistryList[i].serviceStatus == "ERROR")
                roles = this.getRolesById(this.serviceRegistryList[i].id)
        }
        if(roles.length > 0)
            return roles.length;
        return 0;
    }

  }