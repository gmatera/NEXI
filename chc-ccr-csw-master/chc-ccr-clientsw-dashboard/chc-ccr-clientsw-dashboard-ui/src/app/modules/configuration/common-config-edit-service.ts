import { FormGroup,FormControl, Validators } from "@angular/forms";
import { booleanArray, codePageArray, digestFileAlgArray, dnsCreationAlgoArray, formatArray, hubCodePageArray, hubLineSeparatorArray, interfaceTypeArray, lineSeparatorArray, recordFormatArray, vfnCreationAlgoArray } from "./common-config-dto";



export abstract class CommonEditConfiguratiorService {


    configurationForm = new FormGroup({});
    sndCodePageValidationArray = ['sndLineSeparator', 'sndRecordFormat', 'sndMaxRecLength'];
    rcvCodePageValidationArray = ['rcvLineSeparator', 'rcvRecordFormat', 'rcvMaxRecLength'];

    dnsPrifixPattern : string = '^[-a-zA-Z0-9.-]+(\s+[a-zA-Z0-9.-]+)*$';


    selectedsndCodePage(codePage: any, form: FormGroup) {
        for (let i = 0; i < this.sndCodePageValidationArray.length; i++) {
            if (codePage == 'BINARY') {
                form.get(this.sndCodePageValidationArray[i])?.clearValidators();                
                if(this.sndCodePageValidationArray[i]=="sndLineSeparator") {
                    form.setControl(this.sndCodePageValidationArray[i],new FormControl("NONE"));
                    form.get(this.sndCodePageValidationArray[i])?.disable();
                 }
                else{
                    form.get(this.sndCodePageValidationArray[i])?.setValidators(Validators.required);
                }
            }
            else {
                form.get(this.sndCodePageValidationArray[i])?.setValidators(Validators.required);
                form.get(this.sndCodePageValidationArray[i])?.enable();
                this.selectedSndRecordFormat(form.get('sndRecordFormat')?.value, form);
            }
            form.get(this.sndCodePageValidationArray[i])?.updateValueAndValidity();
        }
    }

    selectedSndRecordFormat(recordFormat: any, form: FormGroup){
        form.get('sndMaxRecLength')?.markAsUntouched;
        let recLen = form.get('sndMaxRecLength')?.value;

        if(recordFormat != "FIXED" && recordFormat != "VARIABLE"){
            return;
        }

        if(form.get('sndCodePage')?.value == "BINARY") {
            console.log("BINARY doing nothing");
            form.get('sndMaxRecLength')?.setValue(0);
            form.get('sndMaxRecLength')?.clearValidators;
            return;
        } else {

            if(recLen == 0){
                recLen = null;
            }

            console.log("CodePage = " + form.get('sndCodePage')?.value + " recordFormat = " + recordFormat + " recLen = " + recLen);

            if(recordFormat == "FIXED") {
                form.get('sndMaxRecLength')?.setValidators([Validators.required, Validators.min(1), Validators.max(32760)]);
            } else {
                form.get('sndMaxRecLength')?.setValidators([Validators.required, Validators.min(5), Validators.max(32752)]);
            }
            form.get('sndMaxRecLength')?.setValue(recLen);
            form.get('sndMaxRecLength')?.markAsTouched;
        }
    }

    selectedrcvCodePage(codePage: any, form: FormGroup) {
        for (let i = 0; i < this.rcvCodePageValidationArray.length; i++) {
            if (codePage == 'BINARY') {
                form.get(this.rcvCodePageValidationArray[i])?.clearValidators();                
                if(this.rcvCodePageValidationArray[i]=="rcvLineSeparator") {
                    form.setControl(this.rcvCodePageValidationArray[i],new FormControl("NONE"));
                    form.get(this.rcvCodePageValidationArray[i])?.disable();
                }
                else{
                    form.get(this.rcvCodePageValidationArray[i])?.setValidators(Validators.required);
                }
            }
            else {
                form.get(this.rcvCodePageValidationArray[i])?.setValidators(Validators.required);
                form.get(this.rcvCodePageValidationArray[i])?.enable();
            }
            form.get(this.rcvCodePageValidationArray[i])?.updateValueAndValidity();
        }

    }


    selectedLauEnabled(enabled: boolean) {
        if (enabled) {
            this.configurationForm.get('lauKey')?.setValidators([Validators.required]);
            this.configurationForm.get('lauKey')?.enable();
        } else {
            this.configurationForm.get('lauKey')?.clearValidators();
            this.configurationForm.get('lauKey')?.disable();
        }
    }

    selectedhubCodePage(codePage: any) {
        if (codePage == 'BINARY') {
            this.configurationForm.get('hubLineSeparator')?.clearValidators();
            this.configurationForm.get('hubLineSeparator')?.disable();

            this.configurationForm.get('rcvRecordFormat')?.clearValidators();
            this.configurationForm.get('rcvRecordFormat')?.disable();

            this.configurationForm.get('rcvMaxRecLength')?.clearValidators();
            this.configurationForm.get('rcvMaxRecLength')?.disable();

        } else {
            this.configurationForm.get('hubLineSeparator')?.setValidators([Validators.required]);
            this.configurationForm.get('hubLineSeparator')?.enable();

            this.configurationForm.get('rcvRecordFormat')?.setValidators([Validators.required]);
            this.configurationForm.get('rcvRecordFormat')?.enable();

            this.configurationForm.get('rcvMaxRecLength')?.setValidators([Validators.required]);
            this.configurationForm.get('rcvMaxRecLength')?.enable();

        }
        this.configurationForm.get('hubLineSeparator')?.updateValueAndValidity();

    }

    selectedrcvDnsCreationAlgo(rcvDnsCreationAlgo: any) {

        switch (rcvDnsCreationAlgo) {
            case 'DSN_MINUS_2':
                this.configurationForm.get('rcvDsnPrefix')?.clearValidators();
                this.configurationForm.get('rcvDsnPrefix')?.disable();
                break;
            case 'DSN_MINUS_1':
                this.configurationForm.get('rcvDsnPrefix')?.clearValidators();
                this.configurationForm.get('rcvDsnPrefix')?.disable();
                break;
            case 'DSN_1':
                this.configurationForm.get('rcvDsnPrefix')?.enable();
                this.configurationForm.get('rcvDsnPrefix')?.setValidators([Validators.required, Validators.minLength(1), Validators.maxLength(14), Validators.pattern(this.dnsPrifixPattern)]);
                break;
            case 'DSN_2':
                this.configurationForm.get('rcvDsnPrefix')?.enable();
                this.configurationForm.get('rcvDsnPrefix')?.setValidators([Validators.required, Validators.minLength(1), Validators.maxLength(21), Validators.pattern(this.dnsPrifixPattern)]);
                break;
            case 'DSN_3':
                this.configurationForm.get('rcvDsnPrefix')?.enable();
                this.configurationForm.get('rcvDsnPrefix')?.setValidators([Validators.required, Validators.minLength(1), Validators.maxLength(25), Validators.pattern(this.dnsPrifixPattern)]);
                break;
            case 'DSN_4':
                this.configurationForm.get('rcvDsnPrefix')?.enable();
                this.configurationForm.get('rcvDsnPrefix')?.setValidators([Validators.required, Validators.minLength(1), Validators.maxLength(18), Validators.pattern(this.dnsPrifixPattern)]);
                break;
            case 'DSN_5':
                this.configurationForm.get('rcvDsnPrefix')?.enable();
                this.configurationForm.get('rcvDsnPrefix')?.setValidators([Validators.required, Validators.minLength(1), Validators.maxLength(20), Validators.pattern(this.dnsPrifixPattern)]);
                break;
            case 'DSN_6':
                this.configurationForm.get('rcvDsnPrefix')?.enable();
                this.configurationForm.get('rcvDsnPrefix')?.setValidators([Validators.required, Validators.minLength(1), Validators.maxLength(20), Validators.pattern(this.dnsPrifixPattern)]);
                break;
            case 'DSN_7':
                this.configurationForm.get('rcvDsnPrefix')?.enable();
                this.configurationForm.get('rcvDsnPrefix')?.setValidators([Validators.required, Validators.minLength(1), Validators.maxLength(18), Validators.pattern(this.dnsPrifixPattern)]);
                break;
            case 'DSN_8':
                this.configurationForm.get('rcvDsnPrefix')?.enable();
                this.configurationForm.get('rcvDsnPrefix')?.setValidators([Validators.required, Validators.minLength(1), Validators.maxLength(11), Validators.pattern(this.dnsPrifixPattern)]);
                break;
            case 'DSN_9':
                this.configurationForm.get('rcvDsnPrefix')?.enable();
                this.configurationForm.get('rcvDsnPrefix')?.setValidators([Validators.required, Validators.minLength(1), Validators.maxLength(15), Validators.pattern(this.dnsPrifixPattern)]);
                break;
            case 'DSN_10':
                this.configurationForm.get('rcvDsnPrefix')?.enable();
                this.configurationForm.get('rcvDsnPrefix')?.setValidators([Validators.required, Validators.minLength(1), Validators.maxLength(29), Validators.pattern(this.dnsPrifixPattern)]);
                break;

            default:
                break;
        }


        this.configurationForm.get('rcvDsnPrefix')?.updateValueAndValidity();

    }

    getInterfaceArray() {
        return interfaceTypeArray;
    }

    getCodePageArray() {
        return codePageArray;
    }

    getHubCodePageArray() {
        return hubCodePageArray;
      }

    getLineSeparatorArray() {
        return lineSeparatorArray;
    }

    getHubLineSeparatorArray() {
        return hubLineSeparatorArray;
    }

    getRecordFormatArray() {
        return recordFormatArray;
    }

    getVfnCreationAlgoArray() {
        return vfnCreationAlgoArray;
    }

    getDnsCreationAlgoArray() {
        return dnsCreationAlgoArray;
    }

    getBooleanArray() {
        return booleanArray;
    }

    getDigestFileAlgArray() {
        return digestFileAlgArray;
    }

    getFormatArray() {
        return formatArray;
    }


}