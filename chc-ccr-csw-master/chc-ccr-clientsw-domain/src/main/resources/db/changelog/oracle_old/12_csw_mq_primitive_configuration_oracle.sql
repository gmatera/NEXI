create table CONFIGURATION_MQ_PRIMITIVE(
       id number(19,0) not null,
       MQ_CHANNEL varchar2(255) not null,
       PRIMITIVE varchar2(255) not null,
       QUEUE_NAME varchar2(255) not null,
       TO_LOAD number(1,0),
       primary key (id)
    )
/
create sequence SEQ_MQ_PRIMITIVE start with 1 increment by  1
/