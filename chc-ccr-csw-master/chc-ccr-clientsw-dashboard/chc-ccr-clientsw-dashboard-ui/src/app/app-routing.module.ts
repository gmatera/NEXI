import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DashboardComponent } from './modules/dashboard/dashboard.component';
import { FMSConfigEditComponent } from './modules/configuration/fms/fms-config-edit/fms-config-edit.component';
import { LoginComponent } from './modules/user-management/login/login.component';
import { FMSConfigController } from './modules/configuration/fms/fms-config.component';
import { FMSOutboundComponent } from './modules/outbound-msg/fms/fms-outbound.component';
import { FTSConfigEditComponent } from './modules/configuration/fts/fts-config-edit/fts-config-edit.component';
import { FTSConfigController } from './modules/configuration/fts/fts-config.component';
import { MSSConfigController } from './modules/configuration/mss/mss-config.component';
import { MSSConfigEditComponent } from './modules/configuration/mss/mss-config-edit/mss-config-edit.component';
import { AddonConfigController } from './modules/configuration/addon/addon-config.component';
import { AddonConfigEditComponent } from './modules/configuration/addon/addon-config-edit/addon-config-edit.component';
import { FTSOutboundComponent } from './modules/outbound-msg/fts/fts-outbound.component';
import { MSSOutboundComponent } from './modules/outbound-msg/mss/mss-outbound.component';
import { SystemConfigController } from './modules/configuration/system/system-config.component';
import { SystemConfigEditComponent } from './modules/configuration/system/system-config-edit/system-config-edit.component';
import { AuthService } from './modules/_services/auth.service';
import { Role } from './enums/role.enum';
import { MqPrimitiveController } from './modules/configuration/mq-primitive/mq-primitive.component';
import { MqPrimitiveEditComponent } from './modules/configuration/mq-primitive/mq-primitive-edit/mq-primitive-edit.component';
import { GlobalPropertiesController } from './modules/configuration/global-properties/global-properties.component';
import { ServiceRegistryController } from './modules/configuration/service-registry/service-registry.component';
import { GlobalPropertiesEditComponent } from './modules/configuration/global-properties/global-properties-edit/global-properties-edit.component';
import { FMSInboundComponent } from './modules/inbond-msg/fms/fms-inbound.component';
import { FTSInboundComponent } from './modules/inbond-msg/fts/fts-inbound.component';
import { FemwsConfigController } from './modules/configuration/femws/femws-config.component';
import { FemwsConfigEditComponent } from './modules/configuration/femws/femws-config-edit/femws-config-edit.component';
import { MSSInboundComponent } from './modules/inbond-msg/mss/mss-inbound.component';
import { UserComponent } from './modules/user-management/user/user.component';
import { UserEditComponent } from './modules/user-management/user/user-edit/user-edit.component';
import { ChangePasswordComponent } from './modules/user-management/change-password/change-password.component';
import { ResetPasswordComponent } from './modules/user-management/reset-password/reset-password.component';
import { RouteInterfaceComponent } from './modules/configuration/route-interface/route-interface.component';
import { RouteInterfaceEditComponent } from './modules/configuration/route-interface/route-interface-edit/route-interface-edit.component';
import { AddonOutboundComponent } from './modules/outbound-msg/addon/addon-outbound.component';
import { AddonInboundComponent } from './modules/inbond-msg/addon/addon-inbound.component';






const routes: Routes = [
  {path: 'home', component: DashboardComponent},
  {path: 'user', component: UserComponent, canActivate: [AuthService], data : {roles: [Role.ADMIN]}},
  {path: 'user/edit', component: UserEditComponent, canActivate: [AuthService], data : {roles: [Role.ADMIN]}},
  {path: 'changepassword', component: ChangePasswordComponent},
  {path: 'resetpassword', component: ResetPasswordComponent},
  {path: 'outbound/fms', component: FMSOutboundComponent},
  {path: 'outbound/fts', component: FTSOutboundComponent},
  {path: 'outbound/mss', component: MSSOutboundComponent},
  {path: 'outbound/addon', component: AddonOutboundComponent},
  {path: 'inbound/fms', component: FMSInboundComponent},
  {path: 'inbound/fts', component: FTSInboundComponent},
  {path: 'inbound/mss', component: MSSInboundComponent},  
  {path: 'inbound/addon', component: AddonInboundComponent},  
  {path: 'configuration/messages/fms', component: FMSConfigController},
  {path: 'configuration/fms/edit', component: FMSConfigEditComponent, canActivate: [AuthService], data : {roles: [Role.ADMIN]}},
  {path: 'configuration/messages/fts', component: FTSConfigController },
  {path: 'configuration/fts/edit', component: FTSConfigEditComponent, canActivate: [AuthService], data : {roles: [Role.ADMIN]}},
  {path: 'configuration/messages/mss', component: MSSConfigController},
  {path: 'configuration/mss/edit', component: MSSConfigEditComponent, canActivate: [AuthService], data : {roles: [Role.ADMIN]}},
  {path: 'configuration/messages/addon', component: AddonConfigController},
  {path: 'configuration/addon/edit', component: AddonConfigEditComponent, canActivate: [AuthService], data : {roles: [Role.ADMIN]}},
  {path: 'configuration/system', component: SystemConfigController,  canActivate: [AuthService], data : {roles: [Role.ADMIN,Role.USER]}},
  {path: 'configuration/system/edit', component: SystemConfigEditComponent,
  canActivate: [AuthService], data : {roles: [Role.ADMIN]}},
  {path: 'configuration/mq-primitive', component: MqPrimitiveController},
  {path: 'configuration/mq-primitive/edit', component: MqPrimitiveEditComponent,canActivate: [AuthService], data : {roles: [Role.ADMIN]}},
  {path: 'configuration/global-properties', component: GlobalPropertiesController},
  {path: 'configuration/service-registry', component: ServiceRegistryController},
  {path: 'configuration/global-properties/edit', component: GlobalPropertiesEditComponent,canActivate: [AuthService], data : {roles: [Role.ADMIN]}},
  {path: 'configuration/femws', component: FemwsConfigController },
  {path: 'configuration/femws/edit', component: FemwsConfigEditComponent,canActivate: [AuthService], data : {roles: [Role.ADMIN]} },
  {path: 'configuration/route-interface', component: RouteInterfaceComponent },
  {path: 'configuration/route-interface/edit', component: RouteInterfaceEditComponent,canActivate: [AuthService], data : {roles: [Role.ADMIN]} },


  {path: 'login', component: LoginComponent},
  {path: '', redirectTo: 'home', pathMatch: 'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
