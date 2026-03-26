import { CommonConfigDTO, CommonConfigFilterDTO } from "../../common-config-dto";

export class MSSConfigFilterDTO extends CommonConfigFilterDTO{
    sndCodePage!: string;
}

export class MSSConfigDTO extends CommonConfigDTO {
    sndCompletionAlgo!: boolean;
    rcvCompletionAlgo!: boolean;
    rcvPrimConvFormat!: string;
}
