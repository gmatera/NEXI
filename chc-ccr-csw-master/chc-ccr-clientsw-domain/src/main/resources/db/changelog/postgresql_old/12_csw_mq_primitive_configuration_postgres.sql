create table CONFIGURATION_MQ_PRIMITIVE(
       id bigint not null,
       MQ_CHANNEL varchar(255) not null,
       PRIMITIVE varchar(255) not null,
       QUEUE_NAME varchar(255) not null,
       TO_LOAD boolean,
       primary key (id)
    )
/
create sequence SEQ_MQ_PRIMITIVE start with 1 increment by  1
/