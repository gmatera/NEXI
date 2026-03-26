import { AfterViewInit, Component, OnInit } from '@angular/core';
import { CommonService } from 'src/app/service/common.service';
import { ChartNameValueDTO } from '../common-dto/chart-dto';
import { ServiceRegistry } from './service-registry/service-registry.component';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit, AfterViewInit {

  selectedView: any;

  // https://htmlcolorcodes.com/color-chart/

  
  // err-1 #F5B7B1
  // err-2 #F1948A
  // err-3 #EC7063
  // err-4 #E74C3C

  // success-1 #ABEBC6
  // success-2 #82E0AA
  // success-3 #58D68D
  // success-4 #2ECC71

  // sending-1 #F1C40F
  // submitted-1 #E5E7E9

  // colorScheme DB Outbounbd FMS ---------------------------------
  colorSchemeOutbounbdFMS = [
    {name: "SUBMITTED", value: "#E5E7E9"},

    {name: "INVALID_BA", value: "#F5B7B1"},
    {name: "INVALID_INTERFACE", value: "#F1948A"},
    {name: "SENDING_FAILURE", value: "#E74C3C"},
    {name: "REQUEST_SEND_REJECTED", value: "#EC7063"},

    {name: "SENDING", value: "#F1C40F"},

    {name: "ACCEPTED", value: "#58D68D"},
    {name: "NOTIFY", value: "#2ECC71"},
  ];

    // colorScheme DB Outbounbd FTS ---------------------------------
    colorSchemeOutbounbdFTS = [
      {name: "FILE_TO_BE_PROCESSED", value: "#E5E7E9"},

      {name: "INVALID_BA", value: "#F5B7B1"},
      {name: "INVALID_INTERFACE", value: "#F1948A"},
      {name: "MARSHALL_ERROR", value: "#EC7063"},
      {name: "FILE_LOAD_ERROR", value: "#EC7063"},
      {name: "FILE_LOAD_EMPTY", value: "#EC7063"},
      {name: "CREATE_ERROR", value: "#E74C3C"},
      {name: "EXPORT_ERROR", value: "#E74C3C"},
      
      {name: "GFT_SENDING", value: "#F1C40F"},

      {name: "EXPORT_REQUEST", value: "#58D68D"},
      {name: "EXPORT_COMPLETE", value: "#2ECC71"},
    ];

    // colorScheme DB Outbounbd MSS ---------------------------------
    colorSchemeOutbounbdMSS = [
      {name: "NEW_TRAFFIC", value: "#E5E7E9"},

      {name: "INVALID_BA", value: "#F5B7B1"},
      {name: "INVALID_INTERFACE", value: "#F1948A"},
      {name: "MARSHALL_ERROR", value: "#EC7063"},
      {name: "SENDING_ERROR", value: "#E74C3C"},

      {name: "MSG_SEND_REQUEST", value: "#ABEBC6"},
      {name: "MSG_SEND_CONFIRM", value: "#82E0AA"},
      {name: "MSG_SENT_CONFIRMED", value: "#2ECC71"},
    ];

  // colorScheme DB Inbounbd FMS ---------------------------------
  colorSchemeInbounbdFMS = [
    {name: "RECEIVING", value: "#F1C40F"},
    {name: "RECEIVED", value: "#2ECC71"},
    {name: "RECEPTION_FAILED", value: "#E74C3C"},
  ] ;
  // colorScheme DB Inbounbd FTS ---------------------------------
  colorSchemeInbounbdFTS = [

    {name: "GFT_ERROR", value: "#EC7063"},
    {name: "READ_ERROR", value: "#E74C3C"}, 

    {name: "READ_REQUEST", value: "#A9DFBF"},
    {name: "READ_FILE_DELIVERED", value: "#2ECC71"},
  ] ;
  // colorScheme DB Inbounbd MSS ---------------------------------
  colorSchemeInbounbdMSS = [
    {name: "MSG_CONFIRMED", value: "#2ECC71"},
    {name: "MSG_RECEIVE_ERROR", value: "#E74C3C"}, 
  ] ;

  // colorScheme MQ ---------------------------------
// Outbound MQ
  public colorSchemeMQOutbound = [
    {name: "SENDING", value: "#E5E7E9"},
    
    {name: "CREATE_ERROR", value: "#F1948A"},
    {name: "REJECTED", value: "#EC7063"},
    {name: "IN_ERROR", value: "#E74C3C"},
    
    {name: "CREATING", value: "#F1C40F"},

    {name: "LOCALLY_CONFIRMED", value: "#ABEBC6"},
    {name: "REMOTELY_CONFIRMED", value: "#82E0AA"},
    {name: "SENT", value: "#58D68D"},
    {name: "CLEANABLE", value: "#2ECC71"},
  ];
  // Inbound MQ
  public colorSchemeMQInbound = [
    {name: "RECEIVING", value: "#F1C40F"},
    
    {name: "IN_ERROR", value: "#E74C3C"},
    {name: "RECEIVE_ERROR", value: "#F1948A"},
    {name: "READ_ERROR", value: "#EC7063"},

    {name: "READING", value: "#F1C40F"},

    {name: "RECEIVED", value: "#82E0AA"},
    {name: "DELIVERED", value: "#58D68D"},
    {name: "CLEANABLE", value: "#2ECC71"},
  ];
  // colorScheme MQ ---------------------------------

  monthlyDBMessageOutboud: ChartNameValueDTO[] = [];
  monthlyDBMessageOutboudLabel: string = 'DB Outbound Messages - Total monthly volume';

  monthlyDBMessageInboud: ChartNameValueDTO[] = [];
  monthlyDBMessageInboudLabel: string = 'DB Inbound Messages - Total monthly volume';

  countDBFMSOutboud: ChartNameValueDTO[] = [];
  countDBFMSOutboudLabel: string = 'DB FMS Outbound';

  countDBFTSOutboud: ChartNameValueDTO[] = [];
  countDBFTSOutboudLabel: string = 'DB FTS Outbound';

  countDBMSSOutboud: ChartNameValueDTO[] = [];
  countDBMSSOutboudLabel: string = 'DB MSS Outbound';

  countDBFMSInboud: ChartNameValueDTO[] = [];
  countDBFMSInboudLabel: string = 'DB FMS Inbound';

  countDBFTSInboud: ChartNameValueDTO[] = [];
  countDBFTSInboudLabel: string = 'DB FTS Inbound';

  countDBMSSInboud: ChartNameValueDTO[] = [];
  countDBMSSInboudLabel: string = 'DB MSS Inbound';


  monthlyMQMessageOutboud: ChartNameValueDTO[] = [];
  monthlyMQMessageOutboudLabel: string = 'MQ Outbound Messages - Total monthly volume';

  monthlyMQMessageInboud: ChartNameValueDTO[] = [];
  monthlyMQMessageInboudLabel: string = 'MQ Inbound Messages - Total monthly volume';

  countMQFMSOutboud: ChartNameValueDTO[] = [];
  countMQFMSOutboudLabel: string = 'MQ FMS Outbound';

  countMQFTSOutboud: ChartNameValueDTO[] = [];
  countMQFTSOutboudLabel: string = 'MQ FTS Outbound';

  countMQMSSOutboud: ChartNameValueDTO[] = [];
  countMQMSSOutboudLabel: string = 'MQ MSS Outbound';

  countMQFMSInboud: ChartNameValueDTO[] = [];
  countMQFMSInboudLabel: string = 'MQ FMS Inbound';

  countMQFTSInboud: ChartNameValueDTO[] = [];
  countMQFTSInboudLabel: string = 'MQ FTS Inbound';

  countMQMSSInboud: ChartNameValueDTO[] = [];
  countMQMSSInboudLabel: string = 'MQ MSS Inbound';


  constructor(private http: CommonService) { }

  ngOnInit(): void {
  }
  
  ngAfterViewInit() {
    window.dispatchEvent(new Event('resize'));
////////////////DB Data//////////////////////
    this.http.get<ChartNameValueDTO[]>('/dashboard/db/monthlyMessageOutbound')
      .subscribe(res => {
        this.monthlyDBMessageOutboud = res;
      });

      this.http.get<ChartNameValueDTO[]>('/dashboard/db/monthlyMessageInbound')
      .subscribe(res => {
        this.monthlyDBMessageInboud = res;
      });

      this.http.get<ChartNameValueDTO[]>('/dashboard/db/countFMSOutbound')
      .subscribe(res => {
        this.countDBFMSOutboud = res;
      });

      this.http.get<ChartNameValueDTO[]>('/dashboard/db/countFTSOutbound')
      .subscribe(res => {
        this.countDBFTSOutboud = res;
      });

      this.http.get<ChartNameValueDTO[]>('/dashboard/db/countMSSOutbound')
      .subscribe(res => {
        this.countDBMSSOutboud = res;
      });

      this.http.get<ChartNameValueDTO[]>('/dashboard/db/countFMSInbound')
      .subscribe(res => {
        console.log(res)
        this.countDBFMSInboud = res;
      });

      this.http.get<ChartNameValueDTO[]>('/dashboard/db/countFTSInbound')
      .subscribe(res => {
        console.log(res)
        this.countDBFTSInboud = res;
      });

      this.http.get<ChartNameValueDTO[]>('/dashboard/db/countMSSInbound')
      .subscribe(res => {
        this.countDBMSSInboud = res;
      });


////////////////MQ Data//////////////////////
    this.http.get<ChartNameValueDTO[]>('/dashboard/mq/monthlyMessageOutbound')
    .subscribe(res => {
      this.monthlyMQMessageOutboud = res;
    });

    this.http.get<ChartNameValueDTO[]>('/dashboard/mq/monthlyMessageInbound')
    .subscribe(res => {
      this.monthlyMQMessageInboud = res;
    });

    this.http.get<ChartNameValueDTO[]>('/dashboard/mq/countFMSOutbound')
    .subscribe(res => {
      this.countMQFMSOutboud = res;
    });

    this.http.get<ChartNameValueDTO[]>('/dashboard/mq/countFTSOutbound')
    .subscribe(res => {
      this.countMQFTSOutboud = res;
    });

    this.http.get<ChartNameValueDTO[]>('/dashboard/mq/countMSSOutbound')
    .subscribe(res => {
      this.countMQMSSOutboud = res;
    });

    this.http.get<ChartNameValueDTO[]>('/dashboard/mq/countFMSInbound')
    .subscribe(res => {
      console.log(res)
      this.countMQFMSInboud = res;
    });

    this.http.get<ChartNameValueDTO[]>('/dashboard/mq/countFTSInbound')
    .subscribe(res => {
      console.log(res)
      this.countMQFTSInboud = res;
    });

    this.http.get<ChartNameValueDTO[]>('/dashboard/mq/countMSSInbound')
    .subscribe(res => {
      this.countMQMSSInboud = res;
    });

      
  }
}
