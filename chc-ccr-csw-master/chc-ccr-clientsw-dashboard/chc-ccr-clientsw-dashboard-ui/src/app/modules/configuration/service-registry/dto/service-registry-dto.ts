import { CommonConfigDTO, CommonConfigFilterDTO } from "../../common-config-dto";

export class ServiceRegistryFilterDTO extends CommonConfigFilterDTO{

    propertyName!: string;
  
}

export class ServiceRegistryDTO extends CommonConfigDTO{
    groupId!: string;
    hostName!: string;
    lastUpdate!: string;
    port!: number;
    roles!: string[];
    serviceStatus!: string;
}