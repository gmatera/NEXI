import { CommonConfigDTO, CommonConfigFilterDTO } from "../../common-config-dto";

export class MQPrimitiveFilterDTO extends CommonConfigFilterDTO{

    mqChannel!: string;
    primitive!: string;
    queueName!: string;
   
}

export class MQPrimitiveDTO extends CommonConfigDTO{

    mqChannel!: string;
    primitive!: string;
    queueName!: string;
    toLoad!: boolean;
 
}