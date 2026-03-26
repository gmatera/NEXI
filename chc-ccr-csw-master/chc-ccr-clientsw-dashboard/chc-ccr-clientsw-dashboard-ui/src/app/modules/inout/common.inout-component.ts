import { Router } from "@angular/router";
import { FormControl, FormGroup, Validators } from "@angular/forms";
import { CommonInOutDTO, CommonInOutFilterDTO, directionArray, interfaceTypeArray, fileSizeArray, msgSizeArray } from "./common-inout-dto";
import { CommonInOutMessagesService } from "./inout-messages.service";
import { PageEvent } from "@angular/material/paginator";

export abstract class CommonInOutComponent<F extends CommonInOutFilterDTO, D extends CommonInOutDTO, S extends CommonInOutMessagesService<D, F>>{

    filterForm!: FormGroup;

    constructor(public filter: F, public service: S){
    }

    buildCommonFilterForm(formControls:any={}){
        this.filterForm = new FormGroup({
            localBaId: new FormControl(this.filter.localBaId,),
            remoteBaId: new FormControl(this.filter.remoteBaId,),
            interfaceType: new FormControl(this.filter.interfaceType),
            fromTime: new FormControl(this.filter.fromTime),
            toTime: new FormControl(this.filter.toTime),
            ...formControls
        });
    }

    onSelectionFilter(){
        this.onSubmitFilter();
    }


    loadData(){
        this.service.loadData(this.filter);
    }

    handlePageEvent(event: PageEvent) {

        this.filter.maxRow = event.pageSize;
        this.filter.offset = event.pageIndex;

        this.service.loadData(this.filter);
      }

    onSubmitFilter(){
        let filterFormValue = this.filterForm.value;
        for(let key in this.filterForm.value){
            filterFormValue[key] =  this.filterForm.value[key] || this.filterForm.value[key] === false ? this.filterForm.value[key] : undefined;
        }
        this.filter = filterFormValue;
        this.loadData();
    }
    
    clearFilter(){
        this.filterForm.reset({
            interfaceType: new FormControl(this.filter.interfaceType).value
        });
    }

    retryInvalidBa(){
        this.service.retryInvalidBa();
    }
    
    getInterfaceArray() {
        return interfaceTypeArray;
    }

    getDirectionArray(){
        return directionArray;
    }

    getFileSizeArray(){
        return fileSizeArray;
    }

    getMsgSizeArray(){
        return msgSizeArray;
    }

}