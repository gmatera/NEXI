import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { CommonService } from 'src/app/service/common.service';
import { ServiceRegistryDTO } from "../dto/models";

@Injectable({
    // declares that this service should be created
    // by the root application injector.
    providedIn: 'root',
  })
  
export class ServiceRegistryService {

    private serviceRegistryDTO: ServiceRegistryDTO[] = [
        {id: 'ID', hostName: 'host', port: 11, roles: 'roles', serviceStatus: 'ACTIVE', groupId: "group"}
    ];
    constructor(private http: CommonService) {}

    getData (): Observable<ServiceRegistryDTO[]> {
      return this.http.get<ServiceRegistryDTO[]>('/serviceRegistry');
    }

}