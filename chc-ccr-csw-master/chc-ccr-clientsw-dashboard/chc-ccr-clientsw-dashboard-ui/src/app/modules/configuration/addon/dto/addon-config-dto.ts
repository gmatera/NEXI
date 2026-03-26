import { CommonConfigDTO, CommonConfigFilterDTO } from "../../common-config-dto";

export class AddonConfigFilterDTO extends CommonConfigFilterDTO{
   
}

export class AddonConfigDTO extends CommonConfigDTO{
    sndPath!: string;
    rcvPath!: string;
    sendingPrefix!: string;
    errorPrefix!: string;
    sentPrefix!: string;
    errorDeliverPrefix!: string;
    csc!: string;
}