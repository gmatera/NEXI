create table CONFIGURATION_FEM_WS (
       id number(19,0) not null,
       BAID varchar2(12) not null,
       WEB_SERVER_URL varchar2(255),
       WS_SOAP_ACTION varchar2(50),
       primary key (id)
    )
/
create sequence seq_configuration_fems_ws start with 1 increment by  1
/
alter table CONFIGURATION_FEM_WS 
       add constraint UNIQUE_CONFIGURATION_FEMS_WS unique (BAID)
/