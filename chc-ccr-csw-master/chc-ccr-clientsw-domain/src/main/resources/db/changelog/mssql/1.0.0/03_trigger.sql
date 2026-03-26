IF  EXISTS (SELECT * FROM sys.triggers WHERE object_id = OBJECT_ID(N'[TRG_FAS_MSG_RECV_INS_UPD]'))
DROP TRIGGER TRG_FAS_MSG_RECV_INS_UPD
/
create trigger [TRG_FAS_MSG_RECV_INS_UPD] on [FAS_MSG_RECV]
after insert , update as
begin
	IF  NOT (UPDATE(APPL_CHECK) or UPDATE([STSCODE]))
	BEGIN
		RETURN
	END

declare @sesstimezone varchar(5)='+0000';

if exists (select i.seqid from inserted i
						join deleted d on i.seqid=d.seqid
						where i.complete=0 and i.appl_check<>0 and i.stscode=1220)
begin
	update t set complete=1,
        bar_acq_time = convert(varchar,getutcdate(),12)+replace(convert(varchar,getutcdate(),108),':','')+@sesstimezone
	from inserted i
	join FAS_MSG_RECV t on i.seqid=t.seqid
	join deleted d on i.seqid=d.seqid
	where i.complete=0 and i.appl_check<>0 and i.stscode=1220;
end
END
/
IF  EXISTS (SELECT * FROM sys.triggers WHERE object_id = OBJECT_ID(N'[TRG_FAS_MSG_SEND_INS_UPD]'))
DROP TRIGGER TRG_FAS_MSG_SEND_INS_UPD
/
create trigger [TRG_FAS_MSG_SEND_INS_UPD] on [FAS_MSG_SEND]
after insert , update as
begin
	IF  NOT (UPDATE(APPL_CHECK) or UPDATE([STSCODE]))
	BEGIN
		RETURN
	END

if exists (select i.fas_seqid from inserted i
						join deleted d on i.fas_seqid=d.fas_seqid
						where i.complete=0 and i.appl_check<>0 and i.stscode=1140)
begin
	update t set complete=1 
	from inserted i
	join FAS_MSG_SEND t on i.fas_seqid=t.fas_seqid
	join deleted d on i.fas_seqid=d.fas_seqid
	where i.complete=0 and i.appl_check<>0 and i.stscode=1140;
end

END
/
IF  EXISTS (SELECT * FROM sys.triggers WHERE object_id = OBJECT_ID(N'[TRG_RECV_FILE_INS_UPD]'))
DROP TRIGGER TRG_RECV_FILE_INS_UPD
/
create trigger [TRG_RECV_FILE_INS_UPD] on [RECV_FILE]
after insert , update as
begin
	IF  NOT (UPDATE(APPL_CHECK) or UPDATE([STSCODE]))
	BEGIN
		RETURN
	END

if exists (select i.fas_seqid from inserted i
						join deleted d on i.fas_seqid=d.fas_seqid
						where i.complete=0 and i.appl_check<>0 and i.stscode=511)
begin
	update t set complete=1 
	from inserted i
	join RECV_FILE t on i.fas_seqid=t.fas_seqid
	join deleted d on i.fas_seqid=d.fas_seqid
	where i.complete=0 and i.appl_check<>0 and i.stscode=511;
end

END
/
IF  EXISTS (SELECT * FROM sys.triggers WHERE object_id = OBJECT_ID(N'[TRG_SEND_FILE_INS_UPD]'))
DROP TRIGGER TRG_SEND_FILE_INS_UPD
/
create trigger [TRG_SEND_FILE_INS_UPD] on [SEND_FILE]
after insert , update as
begin
	IF  NOT (UPDATE(APPL_CHECK) or UPDATE([STSCODE]))
	BEGIN
		RETURN
	END

if exists (select i.id_send_file from inserted i
						join deleted d on i.id_send_file=d.id_send_file
						where i.complete=0 and i.appl_check<>0 and i.stscode=305)
begin
	update t set complete=1 
	from inserted i
	join send_file t on i.id_send_file=t.id_send_file
	join deleted d on i.id_send_file=d.id_send_file
	where i.complete=0 and i.appl_check<>0 and i.stscode=305;
end

END
/
IF  EXISTS (SELECT * FROM sys.triggers WHERE object_id = OBJECT_ID(N'[TRG_SYNC_RECV_INS_UPD]'))
DROP TRIGGER TRG_SYNC_RECV_INS_UPD
/
create trigger [TRG_SYNC_RECV_INS_UPD] on [SYNC_RECV]
after insert , update as
begin
	IF  NOT (UPDATE(STCODE_BA) or UPDATE([STCODE_SYNC]))
	BEGIN
		RETURN
	END
if exists (select i.FAS_SEQID from inserted i
		join deleted d on i.FAS_SEQID=d.FAS_SEQID
		where (d.STCODE_BA!=i.STCODE_BA or d.STCODE_SYNC!=i.STCODE_SYNC))
begin	
	if exists (select i.FAS_SEQID from inserted i
			join deleted d on i.FAS_SEQID=d.FAS_SEQID
			where ((d.STCODE_BA=1 and d.STCODE_SYNC=6) and ((i.STCODE_BA!=2 or i.STCODE_SYNC!=15) and (i.STCODE_BA!=0 or i.STCODE_SYNC!=8)) or (i.STCODE_BA=2 and i.STCODE_SYNC=15) and (d.STCODE_BA!=1 or d.STCODE_SYNC!=6)))
	begin
		declare	@oldBa as int,@oldSync as int,@newBa as int,@newSync as int;
		
		select top 1 @oldBa=d.stcode_ba,@oldSync=d.stcode_sync,@newBa=i.stcode_ba,@newSync=i.stcode_sync 
		from deleted d join inserted i on i.FAS_SEQID=d.FAS_SEQID
		where ((d.STCODE_BA=1 and d.STCODE_SYNC=6) and (i.STCODE_BA!=2 or i.STCODE_SYNC!=15) or (i.STCODE_BA=2 and i.STCODE_SYNC=15) and (d.STCODE_BA!=1 or d.STCODE_SYNC!=6));
		
		RAISERROR('Invalid STCODE transition from (%d,%d) to (%d,%d)',16,1,@oldBa,@oldSync,@newBa,@newSync)
		ROLLBACK TRANSACTION
		RETURN
	end
end


if exists (select i.FAS_SEQID from inserted i
						join deleted d on i.FAS_SEQID=d.FAS_SEQID
						where (i.complete is NULL or i.complete=0) and (d.STCODE_BA=1 and d.STCODE_SYNC=6) and (i.STCODE_BA=2 and i.STCODE_SYNC=15))
begin
	update t set complete=1 
	from inserted i
	join SYNC_RECV t on i.FAS_SEQID=t.FAS_SEQID
	join deleted d on i.FAS_SEQID=d.FAS_SEQID
	where (i.complete is NULL or i.complete=0) and (d.STCODE_BA=1 and d.STCODE_SYNC=6) and (i.STCODE_BA=2 and i.STCODE_SYNC=15);
end
END
/
IF  EXISTS (SELECT * FROM sys.triggers WHERE object_id = OBJECT_ID(N'[TRG_SYNC_SEND_INS_UPD]'))
DROP TRIGGER TRG_SYNC_SEND_INS_UPD
/
create trigger [TRG_SYNC_SEND_INS_UPD] on [SYNC_SEND]
after insert , update as
begin
	IF  NOT (UPDATE(STCODE_BA) or UPDATE([STCODE_SYNC]))
	BEGIN
		RETURN
	END

if exists (select i.ID_SYNC_SEND from inserted i
		join deleted d on i.ID_SYNC_SEND=d.ID_SYNC_SEND
		where (d.STCODE_BA!=i.STCODE_BA or d.STCODE_SYNC!=i.STCODE_SYNC))
begin		
	if exists (select i.ID_SYNC_SEND from inserted i
			join deleted d on i.ID_SYNC_SEND=d.ID_SYNC_SEND
			where ((d.STCODE_BA=4 and d.STCODE_SYNC=15) and ((i.STCODE_BA!=7 or i.STCODE_SYNC!=23) and (i.STCODE_BA!=2 or i.STCODE_SYNC!=8)) or (i.STCODE_BA=7 and i.STCODE_SYNC=23) and (d.STCODE_BA!=4 or d.STCODE_SYNC!=15)))
	begin
		declare	@oldBa as int,@oldSync as int,@newBa as int,@newSync as int;
		
		select top 1 @oldBa=d.stcode_ba,@oldSync=d.stcode_sync,@newBa=i.stcode_ba,@newSync=i.stcode_sync 
		from deleted d join inserted i on i.ID_SYNC_SEND=d.ID_SYNC_SEND
		where ((d.STCODE_BA=4 and d.STCODE_SYNC=15) and (i.STCODE_BA!=7 or i.STCODE_SYNC!=23) or (i.STCODE_BA=7 and i.STCODE_SYNC=23) and (d.STCODE_BA!=4 or d.STCODE_SYNC!=15));
		
		RAISERROR('Invalid STCODE transition from (%d,%d) to (%d,%d)',16,1,@oldBa,@oldSync,@newBa,@newSync)
		ROLLBACK TRANSACTION
		RETURN
	end
end

if exists (select i.ID_SYNC_SEND from inserted i
						join deleted d on i.ID_SYNC_SEND=d.ID_SYNC_SEND
						where (i.complete is NULL or i.complete=0) and (d.STCODE_BA=4 and d.STCODE_SYNC=15) and (i.STCODE_BA=7 and i.STCODE_SYNC=23))
begin
	update t set complete=1 
	from inserted i
	join SYNC_SEND t on i.ID_SYNC_SEND=t.ID_SYNC_SEND
	join deleted d on i.ID_SYNC_SEND=d.ID_SYNC_SEND
	where (i.complete is NULL or i.complete=0) and (d.STCODE_BA=4 and d.STCODE_SYNC=15) and (i.STCODE_BA=7 and i.STCODE_SYNC=23);
end
END
/
