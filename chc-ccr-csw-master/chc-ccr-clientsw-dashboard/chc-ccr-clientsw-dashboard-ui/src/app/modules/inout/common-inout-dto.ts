import { ControllerPath, PageableDTO } from "../common-dto/common-dto";

export class ConfigPathInOut{
   
    public static LIST: string = "/list";
    public static RETRY_INVALID_BA_AND_INVALID_INTERFACE = "/retryInvalidBa";

    public static INBOUND: string = "/inbound";
    public static OUTBOUND: string = "/outbound";
}

export class CommonInOutFilterDTO extends PageableDTO{

    interfaceType: string = 'DB';
	localBaId!: string;
	remoteBaId!: string;
	fromTime!:String;
	toTime!:String
}

export class CommonInOutDTO{

    interfaceType!: string;
	localBaId!: string;
	remoteBaId!: string;
	
}

export const interfaceTypeArray = [
    { code: 'DB', value: 'DB'},
    { code: 'MQ', value: 'MQ'}

];

export const fsInterfaceTypeArray = [
    { code: 'FS', value: 'FS'}
];

export const directionArray = [
    { code: 'INBOUND', value: 'INBOUND'},
    { code: 'OUTBOUND', value: 'OUTBOUND'}

];

export const msgSizeArray = [
    { code: '0-400000', value: [0,400000]},
    { code: '400001-800000',value: [400001,800000]},
    { code: '800001-1200000',value: [800001,1200000]}

];

export const fileSizeArray = [
    { code: '0-400000', value: [0,400000]},
    { code: '400001-800000',value: [400001,800000]},
    { code: '800001-1200000',value: [800001,1200000]},
    { code: '1200000-8000000000',value: [1200000,8000000000]}

];

export const MQStatusArray = [
    { code: 'REJECTED', value: 'REJECTED'},
    { code: 'CREATING', value: 'CREATING'},
    { code: 'CREATE_ERROR', value: 'CREATE_ERROR'},
    { code: 'SENDING', value: 'SENDING'},
    { code: 'LOCALLY_CONFIRMED', value: 'LOCALLY_CONFIRMED'},
    { code: 'REMOTELY_CONFIRMED', value: 'REMOTELY_CONFIRMED'},
    { code: 'IN_ERROR', value: 'IN_ERROR'},
    { code: 'SENT', value: 'SENT'},
    { code: 'CLEANABLE', value: 'CLEANABLE'},
    
];

export const MQlogicalStateArray = [
    { code: 'SUCCESS', value: ['SENT','CLEANABLE'],},
    { code: 'INERROR', value: ['IN_ERROR','CREATE_ERROR','REJECTED']},
    { code: 'INPROGRESS', value: ['LOCALLY_CONFIRMED','REMOTELY_CONFIRMED','SENDING','CREATING']}

];

export const InboundMQStatusArray = [
    { code: 'RECEIVING', value: 'RECEIVING'},
    { code: 'DELIVERED', value: 'DELIVERED'},
    { code: 'CLEANABLE', value: 'CLEANABLE'},
    { code: 'RECEIVE_ERROR', value: 'RECEIVE_ERROR'},
    { code: 'RECEIVED', value: 'RECEIVED'},
    { code: 'READING', value: 'READING'},
    { code: 'READ_ERROR', value: 'READ_ERROR'}
    
];

export const InboundMQlogicalStateArray = [
    { code: 'SUCCESS', value: ['CLEANABLE','RECEIVED']},
    { code: 'INERROR', value: ['IN_ERROR','RECEIVE_ERROR']},
    { code: 'INPROGRESS', value: ['RECEIVING','DELIVERED']}

];
