import { CommonInOutDTO, CommonInOutFilterDTO } from "../../../inout/common-inout-dto";

export class MSSSendFilterDTO extends CommonInOutFilterDTO{

	msgId: any;
	id: any;
	status?: string;
	logicalStateList?: string;
	msgSizeList!: number;
	baInsertTimestamp!: Date;
	tur!: string;
	startDate!: String;
	endDate!: String;
	cswInsertTimestamp!: Date;
	remoteRef!: string;
	udr!: string;



}

export class MSSSendDTO extends CommonInOutDTO{


	retryCounter!: number;
	messageType!: string;
	catAppl!: string;
	tur!: string;
	udr!: string;
	msgId!: string;
	remoteRef!: string;
	priority!: number;
	certfReq!: number;
	stsCode!: number;
	status!: string;
	easStatus!: string;
	complete!: number;
	applCheck!: number;
	createDate!: string;
	lastUpdate!: string;
	updateMark!: number;
	seqId!: number;
	messageLeng!: number;
	baReqTime!: string;
	barAcqTime!: string;
	femsiRetryNumber!: number;
	firstEasSubTime!: string;
	lastEasSubTime!: string;
	ferSubTime!: string;
	fenDelTime!: string;
	ferDelTime!: string;
	baInsertTimestamp!: Date;
	cswInsertTimestamp!: Date;
	loadTimestamp!: Date;
	sendReqTimestamp!: Date;
	sendErrTimestamp!: Date;
	sendCnfTimestamp!: Date;
	sendScTimestamp!: Date;
	statusInfo!: string;
	cleanUpLot!: number;
	netMsgId!: string;
	msgDigestAlg!: string;
	msgDigest!: string;
	localAuthInfoAlg!: string;
	localAuthInfo!: string;



	// private CleanUpStatus cleanUpStatus = CleanUpStatus.FREE;
	// private Integer cleanUpLot;
	// private CleanUpType cleanUpType;
	// private String netMsgId;
	// private String msgDigestAlg;
	// private String msgDigest;
	// private String localAuthInfoAlg;
	// private String localAuthInfo;
	
  }


  export const mssDBStatusArray = [
    { code: 'NEW_TRAFFIC', value: 'NEW_TRAFFIC'},
    { code: 'INVALID_BA', value: 'INVALID_BA'},
    { code: 'INVALID_INTERFACE', value: 'INVALID_INTERFACE'},
    { code: 'MARSHALL_ERROR', value: 'MARSHALL_ERROR'},
    { code: 'MSG_SEND_REQUEST', value: 'MSG_SEND_REQUEST'},
    { code: 'MSG_SEND_CONFIRM', value: 'MSG_SEND_CONFIRM'},
    { code: 'MSG_SENT_CONFIRMED', value: 'MSG_SENT_CONFIRMED'},
    { code: 'SENDING_ERROR', value: 'SENDING_ERROR'}

];

export const MSSDBlogicalStateArray = [
    { code: 'SUCCESS', value: ['MSG_SENT_CONFIRMED'],},
    { code: 'INERROR', value: ['INVALID_BA','INVALID_INTERFACE','MARSHALL_ERROR','SENDING_ERROR']},
    { code: 'INPROGRESS', value: ['MSG_SEND_CONFIRM','NEW_TRAFFIC','MSG_SEND_REQUEST']}

];


export const MSSMQStatusArray = [
    { code: 'REJECTED', value: 'REJECTED'},
    { code: 'SENDING', value: 'SENDING'},
    { code: 'LOCALLY_CONFIRMED', value: 'LOCALLY_CONFIRMED'},
    { code: 'REMOTELY_CONFIRMED', value: 'REMOTELY_CONFIRMED'},
    { code: 'IN_ERROR', value: 'IN_ERROR'},
    { code: 'SENT', value: 'SENT'},
    { code: 'CLEANABLE', value: 'CLEANABLE'},
    
];

export const MSSMQlogicalStateArray = [
    { code: 'SUCCESS', value: ['SENT','CLEANABLE'],},
    { code: 'IN_ERROR', value: ['IN_ERROR','REJECTED']},
    { code: 'IN_PROGRESS', value: ['LOCALLY_CONFIRMED','REMOTELY_CONFIRMED','SENDING']}

];