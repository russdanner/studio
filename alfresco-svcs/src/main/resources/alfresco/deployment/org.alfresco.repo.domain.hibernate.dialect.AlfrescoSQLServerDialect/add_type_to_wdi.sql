ALTER TABLE [dbo].cstudio_deploymentworkitem ADD type varchar(50);
CREATE NONCLUSTERED INDEX [cstudio_deploymentworkitem_type_idx] ON [dbo].[cstudio_deploymentworkitem] ( [type] );