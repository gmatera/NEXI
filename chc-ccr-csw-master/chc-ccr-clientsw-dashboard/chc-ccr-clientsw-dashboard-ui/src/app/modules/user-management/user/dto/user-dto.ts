import { PageableDTO } from "../../../common-dto/common-dto";

export class UserFilterDTO extends PageableDTO {
    username!: string;
}

export class UserDTO {

    id!: number;
    username!: string;
    password!: string;
    fullName!: string;
    roles!: string[];
    secretAnswerOne!: string;
    secretResponseOne!: string;
    secretAnswerTwo!: string;
    secretResponseTwo!: string;

}
