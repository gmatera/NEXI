create table GLOBAL_CONFIGURATION (
       id number(19,0) not null,
        PROPERTY varchar2(255) not null,
        VALUE varchar2(255) not null,
        DISPLAY_NAME varchar2(255),
        MANDATORY number(1,0),
        MAX_LENGTH number(10,0),
        MIN_LENGTH number(10,0),
        REQUIRED_MSG varchar2(255),
        TYPE varchar2(255),
        primary key (id)
    )
/
create sequence SEQ_GLOBAL_CONFIGURATION start with 10 increment by  1
/