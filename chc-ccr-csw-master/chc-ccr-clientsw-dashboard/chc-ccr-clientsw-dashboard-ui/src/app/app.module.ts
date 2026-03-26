import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { MaterialModule } from './modules/material/material.module';
import { HttpClientModule } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FlexLayoutModule } from '@angular/flex-layout';
import { DashboardComponent } from './modules/dashboard/dashboard.component';
import { SessionService } from './service/session.service';
import { CommonService } from './service/common.service';
import { ServiceRegistry } from './modules/dashboard/service-registry/service-registry.component';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatExpansionModule} from '@angular/material/expansion';
import { MatInputModule} from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { FTSConfigEditComponent } from './modules/configuration/fts/fts-config-edit/fts-config-edit.component';
import { MatAutocompleteModule} from '@angular/material/autocomplete';
import {MatCheckboxModule} from '@angular/material/checkbox';
import { PieComponent } from './modules/chart/pie/pie.component';
import { ChartBoxComponent } from './modules/chart/box/chart-box.component';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import {MatDialogModule} from '@angular/material/dialog';
import { LoginComponent } from './modules/user-management/login/login.component';
import { authInterceptorProviders } from './modules/_helpers/auth.interceptor';
import { MatTableModule } from '@angular/material/table';
import {MatNativeDateModule} from '@angular/material/core';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatButtonToggleModule} from '@angular/material/button-toggle';
import { FMSConfigController } from './modules/configuration/fms/fms-config.component';
import { FTSConfigController } from './modules/configuration/fts/fts-config.component';
import { FMSOutboundComponent } from './modules/outbound-msg/fms/fms-outbound.component';
import { FMSConfigEditComponent } from './modules/configuration/fms/fms-config-edit/fms-config-edit.component';
import { ConfirmDialog } from './modules/confirmation-dialog/confirm-dialog';
import { MSSConfigController } from './modules/configuration/mss/mss-config.component';
import { MSSConfigEditComponent } from './modules/configuration/mss/mss-config-edit/mss-config-edit.component';
import { AddonConfigController } from './modules/configuration/addon/addon-config.component';
import { AddonConfigEditComponent } from './modules/configuration/addon/addon-config-edit/addon-config-edit.component';
import { FTSOutboundComponent } from './modules/outbound-msg/fts/fts-outbound.component';
import { MSSOutboundComponent } from './modules/outbound-msg/mss/mss-outbound.component';
import { APP_BASE_HREF } from '@angular/common';
import { SystemConfigController } from './modules/configuration/system/system-config.component';
import { SystemConfigEditComponent } from './modules/configuration/system/system-config-edit/system-config-edit.component';
import { MqPrimitiveEditComponent } from './modules/configuration/mq-primitive/mq-primitive-edit/mq-primitive-edit.component';
import { MqPrimitiveController } from './modules/configuration/mq-primitive/mq-primitive.component';
import { GlobalPropertiesController } from './modules/configuration/global-properties/global-properties.component';
import { GlobalPropertiesEditComponent } from './modules/configuration/global-properties/global-properties-edit/global-properties-edit.component';
import { FMSInboundComponent } from './modules/inbond-msg/fms/fms-inbound.component';
import { FTSInboundComponent } from './modules/inbond-msg/fts/fts-inbound.component';
import { FemwsConfigEditComponent } from './modules/configuration/femws/femws-config-edit/femws-config-edit.component';
import { FemwsConfigController } from './modules/configuration/femws/femws-config.component';
import { MSSInboundComponent } from './modules/inbond-msg/mss/mss-inbound.component';
import { UserComponent } from './modules/user-management/user/user.component';
import { UserEditComponent } from './modules/user-management/user/user-edit/user-edit.component';
import { ChangePasswordComponent } from './modules/user-management/change-password/change-password.component';
import { ResetPasswordComponent } from './modules/user-management/reset-password/reset-password.component';
import { RouteInterfaceComponent } from './modules/configuration/route-interface/route-interface.component';
import { RouteInterfaceEditComponent } from './modules/configuration/route-interface/route-interface-edit/route-interface-edit.component';
import { AddonOutboundComponent } from './modules/outbound-msg/addon/addon-outbound.component';
import { AddonInboundComponent } from './modules/inbond-msg/addon/addon-inbound.component';
import { SidenavComponent } from './modules/sidenav/sidenav.component';
import { ServiceRegistryController} from './modules/configuration/service-registry/service-registry.component';
import { MomentDateModule } from '@angular/material-moment-adapter';
import { MAT_DATE_FORMATS } from '@angular/material/core';

export const MY_DATE_FORMATS = {
    parse: {
      dateInput: ['YYYY-MM-DD']
  },
  display: {
      dateInput: 'YYYY-MM-DD',
      monthYearLabel: 'MMM YYYY',
      dateA11yLabel: 'LL',
      monthYearA11yLabel: 'MMMM YYYY',
  },
};

@NgModule({
  declarations: [
    AppComponent,
    DashboardComponent,
    FMSOutboundComponent,
    ServiceRegistry,
    FMSConfigController,
    FMSConfigEditComponent,
    FTSConfigController,
    FTSConfigEditComponent,
    PieComponent,
    ChartBoxComponent,
    ConfirmDialog,
    LoginComponent,
    MSSConfigController,
    MSSConfigEditComponent,
    AddonConfigController,
    AddonConfigEditComponent,
    FTSOutboundComponent,
    MSSOutboundComponent,
    SystemConfigController,
    SystemConfigEditComponent,
    MqPrimitiveEditComponent,
    MqPrimitiveController,
    GlobalPropertiesController,
    GlobalPropertiesEditComponent,
    ServiceRegistryController,
    FMSInboundComponent,
    FTSInboundComponent,
    FemwsConfigController,
    FemwsConfigEditComponent,
    MSSInboundComponent,
    UserComponent,
    UserEditComponent,
    ChangePasswordComponent,
    ResetPasswordComponent,
    RouteInterfaceComponent,
    RouteInterfaceEditComponent,
    AddonOutboundComponent,
    AddonInboundComponent,
    SidenavComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,  
    BrowserAnimationsModule ,
    MaterialModule,
    MatGridListModule,
    FlexLayoutModule,
    MatExpansionModule,
    MatInputModule,
    MatSelectModule,
    MatPaginatorModule,
    MatSortModule,
    ReactiveFormsModule,
    MatAutocompleteModule,
    MatCheckboxModule,
    NgxChartsModule,
    MatDialogModule,
    MatProgressBarModule,
    MatTableModule,
    MatNativeDateModule,
    MatDatepickerModule,
    MatButtonToggleModule,
    MomentDateModule
  ],
  providers: [{ provide: MAT_DATE_FORMATS, useValue: MY_DATE_FORMATS },SessionService, CommonService, authInterceptorProviders, {provide: APP_BASE_HREF, useValue: '/ui'}],
  bootstrap: [AppComponent]
})
export class AppModule { }
