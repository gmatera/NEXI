import { CommonInOutDTO, CommonInOutFilterDTO } from "../../../inout/common-inout-dto";
import { FTSRecvDTO, FTSRecvFilterDTO } from "../../fts/dto/fts-inbound-dto";

export class ADDONOutFilterDTO extends FTSRecvFilterDTO{

	override ftsInterface: string = 'FS' ;
    override interfaceType: string = 'FS';

}

export class ADDONOutDTO extends FTSRecvDTO{
	
  }

  export const ftsFSStatusArray = [
    { code: 'READ_REQUEST', value: 'READ_REQUEST'},
    { code: 'READ_ERROR', value: 'READ_ERROR'},
    { code: 'READ_FILE_DELIVERED', value: 'READ_FILE_DELIVERED'},
   
];

export const FTSFSlogicalStateArray = [
    { code: 'SUCCESS', value: ['READ_FILE_DELIVERED'],},
    { code: 'IN_ERROR', value: ['GFT_ERROR','READ_ERROR']},
    { code: 'IN_PROGRESS', value: ['READ_REQUEST']}

];