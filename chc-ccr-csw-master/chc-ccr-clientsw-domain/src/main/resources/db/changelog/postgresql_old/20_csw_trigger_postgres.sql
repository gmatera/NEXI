---// SYNC_RECV 
CREATE OR REPLACE FUNCTION TriggerTrgSyncRecvInsUpd_proc()
RETURNS TRIGGER 
AS $$
BEGIN
if(((new.complete is NULL or new.complete = 0) and (old.STCODE_BA = 1 and old.STCODE_SYNC = 6) and (new.STCODE_BA = 2 and new.STCODE_SYNC = 15) ) or ((new.STCODE_BA = 2 and new.STCODE_SYNC = 15) and (new.complete is NULL or new.complete = 0)))
 THEN
    new.complete := 1;
end if;
RETURN NEW;
END;
$$ language plpgsql;
/
create trigger trigger_fms_recv
before insert or update
on SYNC_RECV
for each row
execute procedure TriggerTrgSyncRecvInsUpd_proc();
/
---// SYNC_SEND 
CREATE OR REPLACE FUNCTION TriggerTrgSyncSendInsUpd_proc()
RETURNS TRIGGER 
AS $$
BEGIN
if(new.ID_SYNC_SEND is null)
 then
  NEW.ID_SYNC_SEND := (SELECT nextval('SYNC_SEND_SEQ'));

end if;
if(((new.complete is NULL or new.complete = 0) and (old.STCODE_BA = 4 and old.STCODE_SYNC = 15) and (new.STCODE_BA = 7 and new.STCODE_SYNC = 23) ) or ((new.STCODE_BA = 7 and new.STCODE_SYNC = 23) and (new.complete is NULL or new.complete = 0)))
 THEN
    new.complete := 1;
end if;

RETURN NEW;
END;
$$ language plpgsql;
/
create trigger trigger_fms_send
before insert or update of STCODE_BA,STCODE_SYNC on SYNC_SEND
for each row
execute procedure TriggerTrgSyncSendInsUpd_proc();
/
---// SEND_FILE
CREATE OR REPLACE FUNCTION TriggerTrgSendFileInsUpd_proc()
RETURNS TRIGGER 
AS $$
BEGIN
if(new.ID_SEND_FILE is null)
 then
 	NEW.ID_SEND_FILE := (SELECT nextval('SEND_FILE_SEQ'));

end if;
if(new.complete = 0 and new.appl_check<>0 and new.stscode=305)
 THEN
    new.complete := 1;
end if;

RETURN NEW;
END;
$$ language plpgsql;
/
create trigger trigger_fts_send
before insert or update of APPL_CHECK,STSCODE on SEND_FILE
for each row
execute procedure TriggerTrgSendFileInsUpd_proc();
/
---// RECV_FILE
CREATE OR REPLACE FUNCTION TriggerTrgRecvFileInsUpd_proc()
RETURNS TRIGGER 
AS $$
BEGIN
if(new.complete = 0 and new.appl_check<>0 and new.stscode=511)
 THEN
    new.complete := 1;
end if;

RETURN NEW;
END;
$$ language plpgsql;
/
create trigger trigger_fts_recv
before insert or update of APPL_CHECK,STSCODE on RECV_FILE
for each row
execute procedure TriggerTrgRecvFileInsUpd_proc();
/
---// MSS_SEND
CREATE OR REPLACE FUNCTION TriggerTrgFasMsgSendInsUpd_proc()
RETURNS TRIGGER 
AS $$
BEGIN
if(new.FAS_SEQID is null)
 then
 	NEW.FAS_SEQID := (SELECT nextval('FAS_MSG_SEND_SEQ'));

end if;
if(new.complete = 0 and new.appl_check<>0 and new.stscode=1140)
 THEN
    new.complete := 1;
end if;

RETURN NEW;
END;
$$ language plpgsql;
/
create trigger trigger_fas_msg_send
before insert or update of APPL_CHECK,STSCODE on FAS_MSG_SEND
for each row
execute procedure TriggerTrgFasMsgSendInsUpd_proc();
/
---// MSS_RECV
CREATE OR REPLACE FUNCTION TriggerTrgFasMsgRecvInsUpd_proc()
RETURNS TRIGGER 
AS $$
BEGIN
if(new.complete = 0 and new.appl_check<>0 and new.stscode=1220)
 THEN
    new.complete := 1;
end if;

RETURN NEW;
END;
$$ language plpgsql;
/
create trigger trigger_fas_msg_recv
before insert or update of APPL_CHECK,STSCODE on FAS_MSG_RECV
for each row
execute procedure TriggerTrgFasMsgRecvInsUpd_proc();
/
