-- *************************************************************************
-- USER TABLE (Password is admin)
-- *************************************************************************

create sequence SEQ_USERS start with 1 increment by  1
/
create table USERS ( id bigint not null, username varchar(255) not null, full_Name varchar(255), roles varchar(300) not null,  password varchar(255) not null, primary key (id))
/
alter table USERS add constraint UNIQUE_USER unique (USERNAME)
/
insert into USERS (id,username, full_Name , roles , password) VALUES (1,'admin', 'admin', 'ROLE_ADMIN,', '$2a$10$UOwdkH.WGjMfHbm34WWfkuIpW/YfifuYWsslxGZacaxWaKL1la4si')
/