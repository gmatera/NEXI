create table csw_service_registry (
       id varchar2(60) not null,
        group_id varchar2(60),
        host_name varchar2(80),
        last_update timestamp,
        port number(10,0),
        roles varchar2(255),
        service_status varchar2(30),
        primary key (id)
    )
/
    create table csw_service_registry_log (
       id varchar2(50) not null,
        insert_date timestamp,
        log varchar2(1024),
        log_type varchar2(30),
        service_registry_id varchar2(50),
        primary key (id)
    )
/