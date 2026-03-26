-- *************************************************************************
-- USER TABLE (Password is admin)
-- *************************************************************************

create sequence SEQ_USERS start with 1 increment by  1
/
create table USERS ( id number(19,0) not null, username varchar2(255 char) not null, full_Name varchar2(255 char), roles varchar2(300 char) not null,  password varchar2(255 char) not null, primary key (id))
/
alter table USERS add constraint UNIQUE_USER unique (USERNAME)
/
insert into USERS (id,username, full_Name , roles , password) VALUES (1,'admin', 'admin', 'ROLE_ADMIN,', '$2a$10$UOwdkH.WGjMfHbm34WWfkuIpW/YfifuYWsslxGZacaxWaKL1la4si')
/