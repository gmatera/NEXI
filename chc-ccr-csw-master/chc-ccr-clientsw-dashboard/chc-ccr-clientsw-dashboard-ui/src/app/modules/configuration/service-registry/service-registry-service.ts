import { Injectable } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { CommonService } from "src/app/service/common.service";
import { NotificationService } from "src/app/service/notification.service";
import { ConfiguratiorService } from "../common-config.service";
import { Location } from '@angular/common';
import { ControllerPath, PagedResultDTO } from "../../common-dto/common-dto";
import { ServiceRegistryDTO, ServiceRegistryFilterDTO } from "./dto/service-registry-dto";
import { MatTableDataSource } from "@angular/material/table";

@Injectable({
    providedIn: 'root'
  })
export class ServiceRegistryService{

    dataSource! : MatTableDataSource<ServiceRegistryDTO>;

    constructor(private http: CommonService,
        notify: NotificationService, 
        confirmationDialog: MatDialog, 
        locationHistory: Location) {
        
    }

    public loadFromServer() {
        let dto = new ServiceRegistryDTO();

        this.http.get<ServiceRegistryDTO[]>("/getAllServiceRegistry").subscribe(res => {
            this.dataSource = new MatTableDataSource(res)
          });
        

       
    }


}