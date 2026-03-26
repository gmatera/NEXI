CREATE OR REPLACE TRIGGER TRG_SYNC_SEND_INS_UPD
before insert or update of STCODE_BA,STCODE_SYNC on SYNC_SEND
for each row
begin
	begin if :new.ID_SYNC_SEND is null then SELECT SYNC_SEND_SEQ.NEXTVAL INTO :NEW.ID_SYNC_SEND FROM dual; end if; end;
	begin if((:new.complete is NULL or :new.complete=0) and (:old.STCODE_BA=4 and :old.STCODE_SYNC=15) and (:new.STCODE_BA=7 and :new.STCODE_SYNC=23))
  then
    :new.complete := 1;
  end if;
  end;
end TRG_SYNC_SEND_INS_UPD;
/
CREATE OR REPLACE TRIGGER TRG_SYNC_RECV_INS_UPD
before insert or update of STCODE_BA,STCODE_SYNC on SYNC_RECV
for each row
begin
	if((:new.complete is NULL or :new.complete=0) and (:old.STCODE_BA=1 and :old.STCODE_SYNC=6) and (:new.STCODE_BA=2 and :new.STCODE_SYNC=15))
	then
		:new.complete := 1;
	end if;
end TRG_SYNC_RECV_INS_UPD;
/
CREATE OR REPLACE TRIGGER TRG_SEND_FILE_INS_UPD
before insert or update of APPL_CHECK,STSCODE on SEND_FILE
for each row
begin 
        begin if :new.ID_SEND_FILE is null then SELECT SEND_FILE_SEQ.NEXTVAL INTO :NEW.ID_SEND_FILE FROM dual; end if; end;
        begin if(:new.complete=0 and :new.appl_check<>0 and :new.stscode=305)
	then
		:new.complete := 1;
	end if;
	end;
end TRG_SEND_FILE_INS_UPD;
/
CREATE OR REPLACE TRIGGER TRG_RECV_FILE_INS_UPD
before insert or update of APPL_CHECK,STSCODE on RECV_FILE
for each row
begin
	if(:new.complete=0 and :new.appl_check<>0 and :new.stscode=511)
	then
		:new.complete := 1;
	end if;
end TRG_RECV_FILE_INS_UPD;
/
CREATE OR REPLACE TRIGGER TRG_FAS_MSG_SEND_INS_UPD
before insert or update of APPL_CHECK,STSCODE on FAS_MSG_SEND
for each row
begin
        begin if :new.FAS_SEQID is null then SELECT FAS_MSG_SEND_SEQ.NEXTVAL INTO :NEW.FAS_SEQID FROM dual; end if; end;
	begin if(:new.complete=0 and :new.appl_check<>0 and :new.stscode=1140)
	then
		:new.complete := 1;
	end if;
	end;
end TRG_FAS_MSG_SEND_UPD;
/
CREATE OR REPLACE TRIGGER TRG_FAS_MSG_RECV_INS_UPD
before insert or update of APPL_CHECK,STSCODE on FAS_MSG_RECV
for each row
begin
	if(:new.complete=0 and :new.appl_check<>0 and :new.stscode=1220)
	then
		:new.complete := 1;
	end if;
end TRG_FAS_MSG_RECV_INS_UPD;
/
