import { Router } from "@angular/router";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { ConfiguratiorService } from "./common-config.service";
import { FormControl, FormGroup, Validators } from "@angular/forms";
import { CommonConfigDTO, CommonConfigFilterDTO, interfaceTypeArray } from "./common-config-dto";
import { ControllerPath } from "../common-dto/common-dto";
import { AuthService } from "../_services/auth.service";

export abstract class CommonConfigComponent<F extends CommonConfigFilterDTO, D extends CommonConfigDTO, S extends ConfiguratiorService<D, F>>{

    dbDisplayedColumns: string[] = ['id', 'interfaceType', 'localBaId', 'remoteBaId', 'lauEnabled', 'lauKey',
        'sndCodePage', 'sndLineSeparator', 'sndRecordFormat', 'sndMaxRecLength', 'rcvCodePage', 'rcvLineSeparator', 'sndCompletionAlgo',
        'rcvCompletionAlgo', 'rcvDnsCreationAlgo', 'rcvDsnPrefix', 'rcvPath'];

    mqDisplayedColumns: string[] = ['id', 'interfaceType', 'localBaId', 'remoteBaId', 'lauEnabled', 'lauKey',
        'sndCodePage', 'sndLineSeparator', 'sndRecordFormat', 'sndMaxRecLength', 'rcvCodePage', 'rcvLineSeparator', 'sndCompletionAlgo',
        'rcvCompletionAlgo', 'rcvPath'];

    mssdisplayedColumns: string[] = ['id', 'interfaceType', 'localBaId', 'remoteBaId', 'lauEnabled', 'lauKey',
        'sndCompletionAlgo', 'rcvCompletionAlgo'];

    addondisplayedColumns: string[] = ['id', 'localBaId', 'remoteBaId', 'sndPath', 'rcvPath', 'sendingPrefix', 'sentPrefix', 'errorPrefix', 'errorDeliverPrefix'];

    systemdisplayedColumns: string[] = ['id', 'paramKey', 'paramValue'];

    filterForm!: FormGroup;
    displayedColumns = this.dbDisplayedColumns;

    constructor(public filter: F, public service: S, public router: Router, public auth: AuthService) {
        this.displayedColumns = this.auth.isAdmin ? this.displayedColumns : this.displayedColumns.filter(f => f != 'id');
        this.dbDisplayedColumns = this.auth.isAdmin ? this.dbDisplayedColumns : this.dbDisplayedColumns.filter(f => f != 'id');
        this.mqDisplayedColumns = this.auth.isAdmin ? this.mqDisplayedColumns : this.mqDisplayedColumns.filter(f => f != 'id');
        this.mssdisplayedColumns = this.auth.isAdmin ? this.mssdisplayedColumns : this.mssdisplayedColumns.filter(f => f != 'id');
        this.addondisplayedColumns = this.auth.isAdmin ? this.addondisplayedColumns : this.addondisplayedColumns.filter(f => f != 'id');
        this.systemdisplayedColumns = this.auth.isAdmin ? this.systemdisplayedColumns : this.systemdisplayedColumns.filter(f => f != 'id');
    }

    buildCommonFilterForm() {
        this.filterForm = new FormGroup({
            localBaId: new FormControl(this.filter.localBaId,),
            remoteBaId: new FormControl(this.filter.remoteBaId,),
            interfaceType: new FormControl(this.filter.interfaceType, []),
            lauEnabled: new FormControl(this.filter.lauEnabled),
            lauFormat: new FormControl(this.filter.lauFormat),

        });
    }


    loadData() {
        this.service.loadData(this.filter);
    }

    handlePageEvent(event: PageEvent) {

        this.filter.maxRow = event.pageSize;
        this.filter.offset = event.pageIndex;

        this.service.loadData(this.filter);
    }

    onSubmitFilter() {

        let filterFormValue = this.filterForm.value;

        for (let key in this.filterForm.value) {
            filterFormValue[key] = this.filterForm.value[key] || this.filterForm.value[key] === false ? this.filterForm.value[key] : undefined;
        }
        this.filter = filterFormValue;

        this.loadData();
    }
    clearFilter() {
        this.filterForm.reset();
    }

    getInterfaceArray() {
        return interfaceTypeArray;
    }

    protected abstract editConfiguration(dto: D): void;

  
}