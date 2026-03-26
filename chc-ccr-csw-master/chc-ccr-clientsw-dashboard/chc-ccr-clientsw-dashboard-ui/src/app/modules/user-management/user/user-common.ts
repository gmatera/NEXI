import { Injectable } from "@angular/core";
import { UserDTO } from "./dto/user-dto";

@Injectable({
    providedIn: 'root'
  })
export class UserCommonData{
    currentSelection!: UserDTO;
}