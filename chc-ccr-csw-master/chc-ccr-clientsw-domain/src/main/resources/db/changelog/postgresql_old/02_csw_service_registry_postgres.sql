create table csw_service_registry (
       id varchar(60) not null,
        group_id varchar(60),
        host_name varchar(80),
        last_update timestamp,
        port int4,
        roles varchar(255),
        service_status varchar(30),
        primary key (id)
    )
/
    create table csw_service_registry_log (
       id varchar(50) not null,
        insert_date timestamp,
        log varchar(1024),
        log_type varchar(30),
        service_registry_id varchar(50),
        primary key (id)
    )
/