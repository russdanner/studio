CREATE TABLE cstudio_deploymentdeleteitem (
  id bigserial NOT NULL,
  site_id varchar(50) NOT NULL,
  path varchar(2000) NOT NULL,
  endpoint varchar(255) NOT NULL,
  username varchar(50) NOT NULL,
  deployed_date timestamp,
  batch_id varchar(255) NOT NULL,
  batch_order int NOT NULL DEFAULT '0',
  batch_size int NOT NULL DEFAULT '0',
  CONSTRAINT cstudio_deploymentdeleteitem_pkey PRIMARY KEY (id)
);

CREATE INDEX cstudio_deploymentdeleteitem_site_idx
ON cstudio_deploymentdeleteitem USING BTREE (site_id);

CREATE INDEX cstudio_deploymentdeleteitem_endpoint_idx
ON cstudio_deploymentdeleteitem USING BTREE (endpoint);

CREATE INDEX cstudio_deploymentdeleteitem_username_idx
ON cstudio_deploymentdeleteitem USING BTREE (username);

CREATE INDEX cstudio_deploymentdeleteitem_batch_idx
ON cstudio_deploymentdeleteitem USING BTREE (batch_id);

CREATE INDEX cstudio_deploymentdeleteitem_sitepath_idx
ON cstudio_deploymentdeleteitem USING BTREE (site_id, path);