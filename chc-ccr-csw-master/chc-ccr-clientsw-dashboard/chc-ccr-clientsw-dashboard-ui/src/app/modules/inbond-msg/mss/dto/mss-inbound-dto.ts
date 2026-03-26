import { CommonInOutDTO, CommonInOutFilterDTO } from "../../../inout/common-inout-dto";

export class MSSRecvFilterDTO extends CommonInOutFilterDTO{

	msgId: any;
	status?: string;
	logicalStateList?: string;
	msgSizeList!: number;
	receiveTimestamp!: Date;
	tur!: string;
	startDate!: String;
	endDate!: String;
	cswInsertTimestamp!: Date;
	recvRcTimestamp!:Date;
	udr!: string;
	remoteRef!: string;

}

export class MSSRecvDTO extends CommonInOutDTO{


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
	recvRcTimestamp!: Date;
	baProcessTms!: Date;




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
    { code: 'MSG_CONFIRMED', value: 'MSG_CONFIRMED'},
    { code: 'MSG_RECEIVE_ERROR', value: 'MSG_RECEIVE_ERROR'}

];

export const MSSDBlogicalStateArray = [
    { code: 'SUCCESS', value: ['MSG_CONFIRMED'],},
    { code: 'IN_ERROR', value: ['MSG_RECEIVE_ERROR']}

];

export const mssMQStatusArray = [
    { code: 'RECEIVING', value: 'RECEIVING'},
    { code: 'IN_ERROR', value: 'IN_ERROR'},
    { code: 'DELIVERED', value: 'DELIVERED'},
    { code: 'CLEANABLE', value: 'CLEANABLE'}
];