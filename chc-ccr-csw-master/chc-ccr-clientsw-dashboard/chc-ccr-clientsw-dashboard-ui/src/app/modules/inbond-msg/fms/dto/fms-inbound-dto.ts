import { CommonInOutDTO, CommonInOutFilterDTO } from "src/app/modules/inout/common-inout-dto";


export class FMSRecvFilterDTO extends CommonInOutFilterDTO{

	vfn! : string;
	fileName!: string;
	status?: string;
	//status?: string;
	logicalStateList?: string;
	receiveTimestamp!: Date;
	fileSizeList!: number;
	udr!: string;
	tur!: string;
	startDate!: String;
	endDate!: String;
	cswInsertTimestamp!: Date;
	baProcessedTmp!: Date;

}

export class FMSRecvDTO extends CommonInOutDTO{

    vfn! : string;
	status!: string;
	fileName!: string;
	fileSize!: number;
	udr!: string;
	tur!: string;
	baInsertTimestamp!: Date;
	complete!: number;
	statusInfo!: string;
	baProcessTms!: Date;
	baProcessedTmp!: Date;

	
  }

  export const fmsDBStatusBaArray = [
    { code: 'RECEIVING', value: 'RECEIVING'},
    { code: 'RECEIVED', value: 'RECEIVED'},
    { code: 'RECEPTION_FAILED', value: 'RECEPTION_FAILED'},
];

export const FMSDBlogicalStateArray = [
    { code: 'SUCCESS', value: ['RECEIVED'],},
    { code: 'IN_ERROR', value: ['RECEPTION_FAILED']},
    { code: 'IN_PROGRESS', value: ['RECEIVING']}
];

