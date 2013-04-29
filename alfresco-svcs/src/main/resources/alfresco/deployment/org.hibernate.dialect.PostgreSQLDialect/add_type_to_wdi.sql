ALTER TABLE cstudio_deploymentworkitem ADD type varchar(50);
CREATE INDEX cstudio_deploymentworkitem_type_idx
ON cstudio_deploymentworkitem USING BTREE (type);