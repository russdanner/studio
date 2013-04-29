CREATE TABLE [dbo].[cstudio_deploymentdeleteitem] (
  [id] [bigint] IDENTITY(1,1) NOT NULL,
  [site_id] [varchar](50) NOT NULL,
  [path] [varchar](2000) NOT NULL,
  [endpoint] [varchar](255) NOT NULL,
  [username] [varchar](50) NOT NULL,
  [deployed_date] [datetime] DEFAULT NULL,
  [batch_id] [varchar](255) NOT NULL,
  [batch_order] [int] NOT NULL DEFAULT '0',
  [batch_size] [int] NOT NULL DEFAULT '0',
  CONSTRAINT [PK_cstudio_deploymentdeleteitem] PRIMARY KEY CLUSTERED (id));


CREATE NONCLUSTERED INDEX [cstudio_deploymentdeleteitem_site_idx] ON [dbo].[cstudio_deploymentdeleteitem] ( [site_id] );

CREATE NONCLUSTERED INDEX [cstudio_deploymentdeleteitem_endpoint_idx] ON [dbo].[cstudio_deploymentdeleteitem] ( [endpoint] );

CREATE NONCLUSTERED INDEX [cstudio_deploymentdeleteitem_username_idx] ON [dbo].[cstudio_deploymentdeleteitem] ( [username] );

CREATE NONCLUSTERED INDEX [cstudio_deploymentdeleteitem_batch_idx] ON [dbo].[cstudio_deploymentdeleteitem] ( [batch_id] );

CREATE NONCLUSTERED INDEX [cstudio_deploymentdeleteitem_sitepath_idx] ON [dbo].[cstudio_deploymentdeleteitem] ( [site_id], [path] );