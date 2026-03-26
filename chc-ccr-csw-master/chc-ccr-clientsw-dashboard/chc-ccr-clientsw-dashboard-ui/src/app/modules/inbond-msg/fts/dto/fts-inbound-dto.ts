import { CommonInOutDTO, CommonInOutFilterDTO } from "../../../inout/common-inout-dto";

export class FTSRecvFilterDTO extends CommonInOutFilterDTO{

	vfn! : string;
	fileName!: string;
	status?: string;
	logicalStateList?: string;
	receiveTimestamp!: Date;
	createDate!: Date;
	fileSizeList!: number;
	startDate!: String;
	endDate!: String;
	cswInsertTimestamp!: Date;
	ftsInterface: string = 'DB' ;

}

export class FTSRecvDTO extends CommonInOutDTO{

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
	baProcessTms!: Date;
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
	recvReceiveTimestamp!: Date;
	
	// private TraspType traspType = TraspType.EAS;
	// private CleanUpStatus cleanUpStatus = CleanUpStatus.FREE;
	// private CleanUpType cleanUpType;
	// private FTSInterface ftsInterface = FTSInterface.DB;
	
  }

  export const ftsDBStatusArray = [
    { code: 'READ_REQUEST', value: 'READ_REQUEST'},
    { code: 'READ_ERROR', value: 'READ_ERROR'},
    { code: 'READ_FILE_DELIVERED', value: 'READ_FILE_DELIVERED'},
   
];

export const FTSDBlogicalStateArray = [
    { code: 'SUCCESS', value: ['READ_FILE_DELIVERED'],},
    { code: 'IN_ERROR', value: ['GFT_ERROR','READ_ERROR']},
    { code: 'IN_PROGRESS', value: ['READ_REQUEST']}

];