import { CommonConfigDTO, CommonConfigFilterDTO } from "../../common-config-dto";

export class ConfigRouteInterfaceFilterDTO extends CommonConfigFilterDTO{


}

export class ConfigRouteInterfaceDTO extends CommonConfigDTO{

    interFace!: string;
    service!: string;
  
 
}