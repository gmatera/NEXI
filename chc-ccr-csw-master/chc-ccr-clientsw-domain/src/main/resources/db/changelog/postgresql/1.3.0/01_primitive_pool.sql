create sequence SEQ_PRIMITIVE_POOL start with 1 increment by  1;

    create table PRIMITIVE_POOL (
        ID int8 not null,
        PRIMITIVE_ID varchar(10),
        SERVICE_TYPE varchar(10),
        ENTITY_ID bigint not null,
        POSITIVE_PRIMITIVE bytea,
        NEGATIVE_PRIMITIVE bytea,
        FILENAME varchar(255),
        DESTINATION_QUEUE varchar(255),
        STATUS varchar(50) not null,
        FILE_GROUP_ID varchar(255),
        primary key (ID)
    );