import { FTSSendDTO, FTSSendFilterDTO } from "../../fts/dto/fts-outbound-dto";

export class ADDONOutFilterDTO extends FTSSendFilterDTO{

	
	override ftsInterface: string = 'FS' ;
	override interfaceType: string = 'FS';
}

export class ADDONOutDTO extends FTSSendDTO{
	
  }



export const ftsFSStatusArray = [
    { code: 'FILE_TO_BE_PROCESSED', value: 'FILE_TO_BE_PROCESSED'},
    { code: 'INVALID_BA', value: 'INVALID_BA'},
    { code: 'INVALID_INTERFACE', value: 'INVALID_INTERFACE'},
    { code: 'GFT_SENDING', value: 'GFT_SENDING'},
    { code: 'FILE_LOAD_ERROR', value: 'FILE_LOAD_ERROR'},
    { code: 'FILE_LOAD_EMPTY', value: 'FILE_LOAD_EMPTY'},
    { code: 'CREATE_ERROR', value: 'CREATE_ERROR'},
    { code: 'EXPORT_REQUEST', value: 'EXPORT_REQUEST'},
    { code: 'EXPORT_ERROR', value: 'EXPORT_ERROR'},
    { code: 'EXPORT_COMPLETE', value: 'EXPORT_COMPLETE'}
];

export const FTSFSlogicalStateArray = [
    { code: 'SUCCESS', value: ['EXPORT_COMPLETE'],},
    { code: 'IN_ERROR', value: ['INVALID_BA','INVALID_INTERFACE','MARSHALL_ERROR','FILE_LOAD_ERROR','FILE_LOAD_EMPTY','CREATE_ERROR','EXPORT_ERROR']},
    { code: 'IN_PROGRESS', value: ['FILE_TO_BE_PROCESSED','GFT_SENDING','EXPORT_REQUEST']}

];

