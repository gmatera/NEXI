import { CommonConfigDTO, CommonConfigFilterDTO } from "../../common-config-dto";

export class SystemConfigFilterDTO extends CommonConfigFilterDTO{
   
}

export class SystemConfigDTO extends CommonConfigDTO{
    paramKey!: string;
    paramValue!: string;
    module!: string;
 
}