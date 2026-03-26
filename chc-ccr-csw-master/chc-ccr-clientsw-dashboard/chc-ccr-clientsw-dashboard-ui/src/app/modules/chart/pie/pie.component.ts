import { Component, Input, OnInit } from "@angular/core";
import { LegendPosition } from "@swimlane/ngx-charts";
import { ChartNameValueDTO } from "../../common-dto/chart-dto";

  
@Component({
    selector: 'pie-component',
    templateUrl: './pie.component.html',
    styleUrls: ['./pie.component.css']
  })

  export class PieComponent implements OnInit{
    @Input() data: ChartNameValueDTO[] = [];
    @Input() label: string = 'ND';
    @Input() colorScheme: any[] = [];
    
    view: [number, number] = [200, 300];
  
    // options
    gradient: boolean = true;
    showLegend: boolean = false;
    showLabels: boolean = false;
    isDoughnut: boolean = false;
    legendPosition: LegendPosition = LegendPosition.Right;
  
    
    constructor() {
    }

    ngOnInit(): void {
      console.log('PieComponent init done '+this.data);
    }


    resizeChart(width: any, offsetHeight: any): void {
      this.view = [width, offsetHeight]
    }
  }
