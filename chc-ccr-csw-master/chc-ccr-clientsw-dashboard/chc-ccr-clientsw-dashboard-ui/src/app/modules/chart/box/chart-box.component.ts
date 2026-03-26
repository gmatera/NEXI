import { AfterViewInit, Component, Input, OnInit } from "@angular/core";
import { CommonService } from "src/app/service/common.service";
import { ChartNameValueDTO } from "../../common-dto/chart-dto";
  
@Component({
    selector: 'chart-box-component',
    templateUrl: './chart-box.component.html',
    styleUrls: ['./chart-box.component.css']
  })

  export class ChartBoxComponent implements OnInit, AfterViewInit{
    @Input() data: ChartNameValueDTO[] = [];
    @Input() label: string = 'ND';

    public view: [number, number] = [0, 200];

    colorScheme = {
      domain: ['#5AA454', '#E44D25', '#CFC0BB', '#7aa3e5', '#a8385d', '#aae3f5']
    };
    cardColor: string = '#232837';
  
    constructor(private http: CommonService) {
    }
    
    ngOnInit(): void {
      console.log('ChartBoxComponent init done '+this.data);
    }

    ngAfterViewInit() {
      //window.dispatchEvent(new Event('resize'));
    }
    resizeChart(width: any, offsetHeight: any): void {
      this.view = [width, offsetHeight]
    }
  }
