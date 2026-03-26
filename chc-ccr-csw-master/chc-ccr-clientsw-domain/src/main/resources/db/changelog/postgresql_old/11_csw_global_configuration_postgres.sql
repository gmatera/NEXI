create table GLOBAL_CONFIGURATION (
       id bigint not null,
        PROPERTY varchar(255) not null,
        VALUE varchar(3000) not null,   -- era 255
        DISPLAY_NAME varchar(255),
        MANDATORY boolean,
        MAX_LENGTH int4,
        MIN_LENGTH int4,
        REQUIRED_MSG varchar(255),
        TYPE varchar(255),
        primary key (id)
    )
/
create sequence SEQ_GLOBAL_CONFIGURATION start with 10 increment by  1
/