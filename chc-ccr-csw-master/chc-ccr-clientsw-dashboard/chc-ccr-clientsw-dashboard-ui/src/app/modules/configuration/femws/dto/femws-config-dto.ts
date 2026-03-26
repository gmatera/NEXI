import { CommonConfigDTO, CommonConfigFilterDTO } from "../../common-config-dto";

export class FemwsConfigFilterDTO extends CommonConfigFilterDTO{
    baId!: string;
}

export class ConfigFemwSDTO extends CommonConfigDTO {

    baId!: string;
    wsSoapAction!: string;
    webServerUrl!: string;

    
}