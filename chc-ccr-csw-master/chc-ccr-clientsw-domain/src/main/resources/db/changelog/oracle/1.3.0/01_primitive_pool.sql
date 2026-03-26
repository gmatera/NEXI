create sequence SEQ_PRIMITIVE_POOL start with 1 increment by  1
/
create table PRIMITIVE_POOL (
    id number not null,
    PRIMITIVE_ID varchar2(4 char),
    SERVICE_TYPE varchar2(3 char),
    ENTITY_ID number NOT NULL,
    POSITIVE_PRIMITIVE blob,
    NEGATIVE_PRIMITIVE blob,
    FILENAME varchar2(255),
    DESTINATION_QUEUE varchar2(255),
    STATUS varchar2(50),
    FILE_GROUP_ID varchar2(255)
)
    TABLESPACE ${DB_USER}_TABLE
    LOB(POSITIVE_PRIMITIVE, NEGATIVE_PRIMITIVE) 
    STORE as BASICFILE (TABLESPACE ${DB_USER}_LOB disable storage in row)
/


