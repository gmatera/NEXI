create table PRIMITIVE_POOL (
    id [bigint] IDENTITY(1,1) NOT FOR REPLICATION NOT NULL,
    PRIMITIVE_ID [varchar](4),
    SERVICE_TYPE [varchar](3),
    ENTITY_ID [bigint] NOT NULL,
    POSITIVE_PRIMITIVE [varbinary](max),
    NEGATIVE_PRIMITIVE [varbinary](max),
    FILENAME [varchar](255),
    DESTINATION_QUEUE [varchar](255),
    STATUS [varchar](50),
    FILE_GROUP_ID [varchar](255),
    CONSTRAINT [pk_id] PRIMARY KEY CLUSTERED ( 
      	[id] ASC 
    )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [${DB_USER}._INDEX]
) 
ON 
    [${DB_USER}._INDEX]
/