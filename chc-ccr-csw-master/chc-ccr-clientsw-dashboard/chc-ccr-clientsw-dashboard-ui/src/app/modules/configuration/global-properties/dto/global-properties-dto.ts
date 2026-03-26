import { CommonConfigDTO, CommonConfigFilterDTO } from "../../common-config-dto";

export class GlobalPropertiesFilterDTO extends CommonConfigFilterDTO{

    propertyName!: string;
  
}

export class GlobalPropertiesDTO extends CommonConfigDTO{

    propertyName!: string;
    value!: string;
    type!: string;
    mandatory!: boolean;
    requiredMsg!: string;
    minLength!: number;
    maxLength!: number;
    displayName!: string;

 
}