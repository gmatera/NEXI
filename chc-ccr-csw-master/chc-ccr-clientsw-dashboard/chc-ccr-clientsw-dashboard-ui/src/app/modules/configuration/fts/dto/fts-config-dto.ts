import { CommonConfigDTO, CommonConfigFilterDTO } from "../../common-config-dto";

export class FTSConfigFilterDTO extends CommonConfigFilterDTO{
    sndCodePage!: string;
}

export class FtsConfigDTO extends CommonConfigDTO {
    sndCodePage!: string;
    sndLineSeparator!: string;
	sndRecordFormat!: string;
	sndMaxRecLength!: number;
    sndCompressAlgo!: string;
    // sndVfnCreationAlgo!: string;
    sndCompletionAlgo!: boolean;
	
	rcvCodePage!: string;
	rcvLineSeparator!: string;
    rcvAutoRead!: boolean;
    rcvDnsCreationAlgo!: number;
    rcvDsnPrefix!: string;
    rcvCompletionAlgo!: boolean;
    rcvPath!: string;
    rcvPrimConvFormat!: string;
    uploadQName!: string;
    msgSizeDataQueue!: string;
    digestFileAlg!: string;
    
    mqiPosCreateInd!: boolean;


    hubCodePage!: string;
    hubLineSeparator!: string;
	rcvRecordFormat!: string;
	rcvMaxRecLength!: number;
    
}