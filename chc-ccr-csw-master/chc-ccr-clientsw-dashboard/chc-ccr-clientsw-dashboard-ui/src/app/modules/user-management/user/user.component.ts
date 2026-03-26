import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { CommonService } from 'src/app/service/common.service';
import { NotificationService } from 'src/app/service/notification.service';
import { UserService } from './user.service';
import { Location } from '@angular/common';
import { UserDTO, UserFilterDTO } from './dto/user-dto';
import { FormControl, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { UserCommonData } from './user-common';
import { MatSort } from '@angular/material/sort';


@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent extends UserService implements OnInit, AfterViewInit {


  @ViewChild(MatPaginator)
  paginator!: MatPaginator;

  @ViewChild(MatSort, { static: false }) set sort(s: MatSort) {
    if (this.dataSource) {
      this.dataSource.sort = s;
    }
  }
  sortData(sort: MatSort) {
    this.sort = sort;
  }

  filterForm!: FormGroup;
  filter=new  UserFilterDTO();
  constructor(private httpService: CommonService,
    notify: NotificationService, 
    userCommonData:UserCommonData,
    confirmationDialog: MatDialog, 
    locationHistory: Location, private rout : Router) {
    super(httpService, notify,userCommonData, confirmationDialog, locationHistory,rout);
}

  userdisplayedColumns: string[] = ['id', 'username', 'fullName', 'roles'];


  ngOnInit(): void {
    this.filterForm = new FormGroup({
      username: new FormControl(this.filter.username)
  });
  }
  ngAfterViewInit() {
    this.loadData(this.filter);
  }

  handlePageEvent(event: PageEvent) {

    this.filter.maxRow = event.pageSize;
    this.filter.offset = event.pageIndex;


    this.loadData(this.filter);
}

onSubmitFilter(){
  let filterFormValue = this.filterForm.value;
  for(let key in this.filterForm.value){
      filterFormValue[key] =  this.filterForm.value[key] || this.filterForm.value[key] === false ? this.filterForm.value[key] : undefined;
  }
  this.filter = filterFormValue;

  this.loadData(this.filter);
}

editUser(dto: UserDTO) {
  this.userCommonData.currentSelection = dto;
  this.router.navigateByUrl("user/edit");
  //this.router.navigate(["user/edit"], {state: {dto}});
}

clearFilter(){
  this.filterForm.reset();
}
}
