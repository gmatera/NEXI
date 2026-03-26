import { ControllerPath, PageableDTO } from "../common-dto/common-dto";

export class ConfigPath{
   
    public static CONFIG_PATH: string = "/configurations";

    public static CONFIG_LIST: string = ConfigPath.CONFIG_PATH + "/list";
    public static CONFIG_SAVE: string = ConfigPath.CONFIG_PATH + "/save";
    public static CONFIG_DELETE: string = ConfigPath.CONFIG_PATH + "/delete/";


    public static INBOUND: string = "/inbound";
    public static OUTBOUND: string = "/outbound";
}

export const lineSeparatorArray = [
    { code: "CRLF_0X0D0A", value: "CRLF_0X0D0A"},
    { code: "LF_0X0A", value: "LF_0X0A" },
    { code: "NONE", value: "NONE" }
];

export const hubLineSeparatorArray = [
    { code: "CRLF_0X0D0A", value: "CRLF_0X0D0A"}
];

export const codePageArray = [ 
    { code: "BINARY", value: "BINARY"},
    { code: "ASCII", value: "ASCII"},
    { code: "EBCDIC", value: "EBCDIC"},
];

export const hubCodePageArray = [ 
    { code: "BINARY", value: "BINARY"},
    { code: "ASCII", value: "ASCII"}
];

export const mssRcvPrimConvFormat = [ 
    { code: "ASCII", value: "ASCII"},
    { code: "EBCDIC", value: "EBCDIC"}
];

export const recordFormatArray = [
    { code: "VARIABLE", value: "VARIABLE"},
    { code: "FIXED", value: "FIXED"}
];

export const interfaceTypeArray = [
    { code: 'DB', value: 'DB'},
    { code: 'MQ', value: 'MQ'}
];


export const vfnCreationAlgoArray = [
    { code: '-2', value: '-2'},
    { code: '-1', value: '-1'},
    { code: '1', value: '1'},
    { code: '2', value: '2'},
    { code: '3', value: '3'},
    { code: '4', value: '4'},
    { code: '5', value: '5'},
    { code: '6', value: '6'},
    { code: '7', value: '7'},
    { code: '8', value: '8'},
    { code: '9', value: '9'},
    { code: '10', value: '10'},
];

export const dnsCreationAlgoArray = [
    { code: 'DSN_MINUS_2', value: -2},
    { code: 'DSN_MINUS_1', value: -1},
    { code: 'DSN_1', value: 1},
    { code: 'DSN_2', value: 2},
    { code: 'DSN_3', value: 3},
    { code: 'DSN_4', value: 4},
    { code: 'DSN_5', value: 5},
    { code: 'DSN_6', value: 6},
    { code: 'DSN_7', value: 7},
    { code: 'DSN_8', value: 8},
    { code: 'DSN_9', value: 9},
    { code: 'DSN_10', value: 10},


];

export const booleanArray = [
    { code: true, value: 1},
    { code: false, value: 0}

];

export const digestFileAlgArray = [
    { code: 'SHA-256', value: 'SHA-256'}

];

export const formatArray = [
    { code: 'STRING', value: 'STRING'},
    { code: 'INTEGER', value: 'INTEGER'}

];


export class CommonConfigDTO {
    id?: number;
    interfaceType!: string;
    localBaId!: string;
    remoteBaId!: string;

    lauKey!: string;

    lauEnabled!: boolean;
    lauFormat!: string;
    // leftLauKey!: string;
    rightLauKey!: string;
}

export class CommonConfigFilterDTO extends PageableDTO{

    interfaceType: string = 'DB';
	localBaId!: string;
	remoteBaId!: string;
    lauEnabled!: boolean;
	lauFormat!: string;
    
    paramKey!: string;
	
}