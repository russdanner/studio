CREATE TABLE [dbo].[cstudio_deploymenthistory](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[site] [nvarchar](35) NOT NULL,
	[path] [nvarchar](4000) NOT NULL,
	[publishing_channel] [nvarchar](255) NOT NULL,
	[user] [nvarchar](35) NOT NULL,
	[deployment_date] [datetime] NOT NULL,
 CONSTRAINT [PK_cstudio_deploymenthistory] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY];

CREATE NONCLUSTERED INDEX [cstudio_deploymenthistory_deploymentdate_idx] ON [dbo].[cstudio_deploymenthistory] ( [deployment_date] ASC )WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY];

CREATE NONCLUSTERED INDEX [cstudio_deploymenthistory_channel_idx] ON [dbo].[cstudio_deploymenthistory] ( [publishing_channel] ASC )WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY];

CREATE NONCLUSTERED INDEX [cstudio_deploymenthistory_user_idx] ON [dbo].[cstudio_deploymenthistory] ( [user] ASC )WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY];

CREATE NONCLUSTERED INDEX [cstudio_deploymenthistory_site_idx] ON [dbo].[cstudio_deploymenthistory] ( [site] ASC )WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY];

CREATE NONCLUSTERED INDEX [cstudio_deploymenthistory_sitepath_idx] ON [dbo].[cstudio_deploymenthistory] ( [site], [path] ASC )WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY];