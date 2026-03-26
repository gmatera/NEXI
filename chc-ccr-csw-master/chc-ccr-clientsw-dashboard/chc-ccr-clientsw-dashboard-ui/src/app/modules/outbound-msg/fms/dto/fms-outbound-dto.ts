import { CommonInOutDTO, CommonInOutFilterDTO } from "src/app/modules/inout/common-inout-dto";


export class FMSSendFilterDTO extends CommonInOutFilterDTO{

	vfn! : string;
	fileName!: string;
	status?: string;
	//status?: string;
	logicalStateList?: string;
	baInsertTimestamp!: Date;
	fileSizeList!: number;
	udr!: string;
	tur!: string;
	startDate!: String;
	endDate!: String;
	cswInsertTimestamp!: Date;
}

export class FMSSendDTO extends CommonInOutDTO{

    vfn! : string;
	fileName!: string;
	status!: string;
	statusSync!: string;
	statusCodeBA!: number;
	statusCodeSync!: number;
	udr!: string;
	priority!: number;
	baMsgId!: string;
	acceptTime!: Date;
	tur!: string;
	baInsertTimestamp!: Date;
	fileSize!: number;
	ftsSendTime!: Date;
	ftsCompleteTime!: Date;
	msSendTime!: Date;
	msCompleteTime!: Date;
	notifyTime!: Date;
	modTime!: Date;
	userDataRemoteLen!: number;
	messageLeng!: number;
	messageType!: string;
	catAppl!: string;
	errorTime!: Date;
	ferSubFstFtsTmp!: Date;
	ferSubLstFtsTmp!: Date;
	ferDlvFstFtsTmp!: Date;
	ferDlvLstFtsTmp!: Date;
	fenSubMssTmp!: Date;
	fenDlvMssTmp!: Date;
	crtSubMssTmp!: Date;
	crtDlvMssTmp!: Date;
	dataSetName!: string;
	localData!: string;
	certfReq!: number;
	fileMD5!: string;
	fBlockMoved!: number;
	fMap!: string;
	complete!: number;
	lastUpdate!: number;
	sendAcceptedTimestamp!: Date;
	sendRequestTimestamp!: Date;
	sendConfirmedTimestamp!: Date;
	sendCompletedTimestamp!: Date;
	sendErrorTimestamp!: Date;
	statusInfo!: string;
	operationTimestamp!: Date;
	operation!: string;
	reactivate!: string;
	cleanUpLot!: number;
	fileDigestAlg!: string;
	fileDigest!: string;
	msgDigestAlg!: string;
	msgDigest!: string;
	localAuthInfoAlg!: string;
	localAuthInfo!: string;


    // TODO to complete

	// private CleanUpStatus cleanUpStatus = CleanUpStatus.FREE;
	// private CleanUpType cleanUpType;
  }


  export const fmsDBStatusBaArray = [
    { code: 'SUBMITTED', value: 'SUBMITTED'},
    { code: 'INVALID_BA', value: 'INVALID_BA'},
    { code: 'INVALID_INTERFACE', value: 'INVALID_INTERFACE'},
    { code: 'ACCEPTED', value: 'ACCEPTED'},
    { code: 'SENDING', value: 'SENDING'},
    { code: 'SENDING_FAILURE', value: 'SENDING_FAILURE'},
    { code: 'REQUEST_SEND_REJECTED', value: 'REQUEST_SEND_REJECTED'},
    { code: 'NOTIFY', value: 'NOTIFY'}
];

export const FMSDBlogicalStateArray = [
    { code: 'SUCCESS', value: ['NOTIFY'],},
    { code: 'IN_ERROR', value: ['REQUEST_SEND_REJECTED','SENDING_FAILURE','INVALID_BA','INVALID_INTERFACE']},
    { code: 'IN_PROGRESS', value: ['SUBMITTED','SENDING','ACCEPTED']}
];

