import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { Router } from '@angular/router';
import { AuthService } from '../../_services/auth.service';
import { CommonConfigComponent } from '../common.config-component';
import { MQPrimitiveDTO, MQPrimitiveFilterDTO } from './dto/mq-primitive-dto';
import { MqPrimitiveService } from './mq-primitive-service';


@Component({
  selector: 'mq-primitive-component',
  templateUrl: './mq-primitive.component.html',
  styleUrls: ['./mq-primitive.component.css']
})
export class MqPrimitiveController extends CommonConfigComponent<MQPrimitiveFilterDTO, MQPrimitiveDTO, MqPrimitiveService>  implements OnInit {

  @ViewChild(MatPaginator)
  paginator!: MatPaginator;

  @ViewChild(MatSort, { static: false }) set sort(s: MatSort) {
    if (this.mqPrimitiveService.dataSource) {
      this.mqPrimitiveService.dataSource.sort = s;
    }
  }
  sortData(sort: MatSort) {
    this.sort = sort;
  }

  constructor(private mqPrimitiveService: MqPrimitiveService, private r: Router,public a: AuthService) {
    super(new MQPrimitiveFilterDTO(), mqPrimitiveService, r,a);
  }

  primitiveDisplayedColumns: string[] = ['id','mqChannel', 'primitive', 'queueName', 'toLoad'];

  ngOnInit(): void {
    this.filterForm = new FormGroup({
      mqChannel: new FormControl(this.filter.mqChannel),
      primitive: new FormControl(this.filter.primitive, [Validators.pattern('[0-9A-Z]+(, [0-9A-Z]+)*')]),
      queueName: new FormControl(this.filter.queueName),
  });

  this.primitiveDisplayedColumns =this.auth.isAdmin ?  this.primitiveDisplayedColumns : this.primitiveDisplayedColumns.filter(f=>f!='id');

    super.loadData();

  }

  editConfiguration(dto: MQPrimitiveDTO) {
    this.service.currentSelection = dto;
    this.router.navigateByUrl("configuration/mq-primitive/edit");
  }



}
