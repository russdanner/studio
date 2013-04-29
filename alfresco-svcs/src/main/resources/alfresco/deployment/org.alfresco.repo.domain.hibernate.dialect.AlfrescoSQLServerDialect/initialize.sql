CREATE  TABLE [dbo].[cstudio_deploymentworkqueue] (
  [id] [bigint] IDENTITY(1,1) NOT NULL,
  [site_id] [NVARCHAR](50) NOT NULL ,
  [batch_id] [NVARCHAR](255) NOT NULL ,
  [batch_size] [INT] NOT NULL ,
  [ready_flag] [INT] NOT NULL DEFAULT 0 ,
  [state_flag] [VARCHAR](100) NOT NULL ,
  [submission_comment] [NVARCHAR](4000) ,
  [golive_datetime] [DATETIME] ,
  [submit_datetime] [DATETIME] ,
  [last_attempted_time] [DATETIME] ,
  [number_of_retries] [INT] DEFAULT 0 ,
  [cluster_node_id] [VARCHAR](100) ,
  CONSTRAINT [PK_cstudio_deploymentworkqueue] PRIMARY KEY CLUSTERED (id));

CREATE UNIQUE NONCLUSTERED INDEX [cstudio_deploymentworkqueue_batch_unique] ON [dbo].[cstudio_deploymentworkqueue] ( [batch_id] );

CREATE NONCLUSTERED INDEX [cstudio_deploymentworkqueue_site_idx] ON [dbo].[cstudio_deploymentworkqueue] ( [site_id] );

CREATE NONCLUSTERED INDEX [cstudio_deploymentworkqueue_ready_idx] ON [dbo].[cstudio_deploymentworkqueue] ( [ready_flag] );

CREATE NONCLUSTERED INDEX [cstudio_deploymentworkqueue_state_idx] ON [dbo].[cstudio_deploymentworkqueue] ( [state_flag] );




CREATE TABLE [dbo].[cstudio_deploymentworkitem] (
  [id] [bigint] IDENTITY(1,1) NOT NULL,
  [site_id] [varchar](50) NOT NULL,
  [path] [varchar](2000) NOT NULL,
  [endpoint] [varchar](255) NOT NULL,
  [username] [varchar](50) NOT NULL,
  [deployed_date] [datetime] DEFAULT NULL,
  [batch_id] [varchar](255) NOT NULL,
  [batch_order] [int] NOT NULL DEFAULT '0',
  [batch_size] [int] NOT NULL DEFAULT '0',
  [type] [varchar](50),
  CONSTRAINT [PK_cstudio_deploymentworkitem] PRIMARY KEY CLUSTERED (id));


CREATE NONCLUSTERED INDEX [cstudio_deploymentworkitem_site_idx] ON [dbo].[cstudio_deploymentworkitem] ( [site_id] );

CREATE NONCLUSTERED INDEX [cstudio_deploymentworkitem_endpoint_idx] ON [dbo].[cstudio_deploymentworkitem] ( [endpoint] );

CREATE NONCLUSTERED INDEX [cstudio_deploymentworkitem_username_idx] ON [dbo].[cstudio_deploymentworkitem] ( [username] );

CREATE NONCLUSTERED INDEX [cstudio_deploymentworkitem_batch_idx] ON [dbo].[cstudio_deploymentworkitem] ( [batch_id] );

CREATE NONCLUSTERED INDEX [cstudio_deploymentworkitem_sitepath_idx] ON [dbo].[cstudio_deploymentworkitem] ( [site_id], [path] );

CREATE NONCLUSTERED INDEX [cstudio_deploymentworkitem_type_idx] ON [dbo].[cstudio_deploymentworkitem] ( [type] )



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