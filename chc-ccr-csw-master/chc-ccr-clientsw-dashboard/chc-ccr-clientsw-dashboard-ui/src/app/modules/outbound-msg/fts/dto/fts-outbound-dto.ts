import { CommonInOutDTO, CommonInOutFilterDTO } from "../../../inout/common-inout-dto";

export class FTSSendFilterDTO extends CommonInOutFilterDTO{

	vfn! : string;
	fileName!: string;
	status?: string;
	logicalStateList?: string;
	baInsertTimestamp!: Date;
	fileSizeList!: number;
	startDate!: String;
	endDate!: String;
	cswInsertTimestamp!: Date;
	ftsInterface: string = 'DB' ;

}

export class FTSSendDTO extends CommonInOutDTO{

	vfn! : string;
	fileName!: string;
	easStatus!: string;
	stsCode!: number;
	complete!: string;
	createDate!: string;
	lastUpdate!: string;
	applCheck!: number;
	originalFileName!: string;
	updateMark!: number;
	fileSize!: number;
	fileMD5!: string;
	fBlockMoved!: number;
	fileMap!: string;
	operationTimestamp!: Date;
	operation!: string;
	reactivate!: string;
	refDate!: string;
	actReqTime!: string;
	easReqTime!: string;
	quequeInsTime!: string;
	startTime!: string;
	easComplTime!: string;
	baProcTime!: string;
	easElabTime!: string;
	baInsertTimestamp!: Date;
	sendAcceptedTimestamp!: Date;
	sendGftRequestTimestamp!: Date;
	sendRequestTimestamp!: Date;
	sendConfirmedTimestamp!: Date;
	sendCompletedTimestamp!: Date;
	sendErrorTimestamp!: Date;
	statusInfo!: string;
	applicativeDataField!: string;
	applicativeDataFieldLength!: number;
	cleanUpLot!: number;
	fileDigestAlg!: string;
	fileDigest!: string;
	localAuthInfoAlg!: string;
	localAuthInfo!: string;
	localAuthInfoFs!: string;
	version!: number;
	status!: string;
	
	// private TraspType traspType = TraspType.EAS;
	// private CleanUpStatus cleanUpStatus = CleanUpStatus.FREE;
	// private CleanUpType cleanUpType;
	// private FTSInterface ftsInterface = FTSInterface.DB;
	
  }



export const ftsDBStatusArray = [
    { code: 'FILE_TO_BE_PROCESSED', value: 'FILE_TO_BE_PROCESSED'},
    { code: 'INVALID_BA', value: 'INVALID_BA'},
    { code: 'INVALID_INTERFACE', value: 'INVALID_INTERFACE'},
    { code: 'MARSHALL_ERROR', value: 'MARSHALL_ERROR'},
    { code: 'GFT_SENDING', value: 'GFT_SENDING'},
    { code: 'FILE_LOAD_ERROR', value: 'FILE_LOAD_ERROR'},
    { code: 'FILE_LOAD_EMPTY', value: 'FILE_LOAD_EMPTY'},
    { code: 'CREATE_ERROR', value: 'CREATE_ERROR'},
    { code: 'EXPORT_REQUEST', value: 'EXPORT_REQUEST'},
    { code: 'EXPORT_ERROR', value: 'EXPORT_ERROR'},
    { code: 'EXPORT_COMPLETE', value: 'EXPORT_COMPLETE'}
];

export const FTSDBlogicalStateArray = [
    { code: 'SUCCESS', value: ['EXPORT_COMPLETE'],},
    { code: 'IN_ERROR', value: ['INVALID_BA','INVALID_INTERFACE','MARSHALL_ERROR','FILE_LOAD_ERROR','FILE_LOAD_EMPTY','CREATE_ERROR','EXPORT_ERROR']},
    { code: 'IN_PROGRESS', value: ['FILE_TO_BE_PROCESSED','GFT_SENDING','EXPORT_REQUEST']}

];

