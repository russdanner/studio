ALTER TABLE cstudio_deploymentworkitem ADD type varchar(50);
CREATE INDEX cs_depworkitem_type_idx ON cstudio_deploymentworkitem(type)