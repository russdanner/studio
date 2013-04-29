CREATE  TABLE cstudio_deploymentworkqueue (
  id bigserial NOT NULL ,
  site_id VARCHAR(50) NOT NULL ,
  batch_id VARCHAR(255) NOT NULL ,
  batch_size int NOT NULL ,
  ready_flag INT NOT NULL DEFAULT 0 ,
  state_flag VARCHAR(100) NOT NULL ,
  submission_comment TEXT NULL ,
  golive_datetime timestamp NULL ,
  submit_datetime timestamp NULL ,
  last_attempted_time timestamp NULL ,
  number_of_retries INT NULL DEFAULT 0 ,
  cluster_node_id VARCHAR(100) ,
  CONSTRAINT cstudio_deploymentworkqueue_pkey PRIMARY KEY (id)
);

CREATE UNIQUE INDEX cstudio_deploymentworkqueue_batch_unique
ON cstudio_deploymentworkqueue USING BTREE (batch_id);

CREATE INDEX cstudio_deploymentworkqueue_site_idx
ON cstudio_deploymentworkqueue USING BTREE (site_id);

CREATE INDEX cstudio_deploymentworkqueue_ready_idx
ON cstudio_deploymentworkqueue USING BTREE (ready_flag);

CREATE INDEX cstudio_deploymentworkqueue_state_idx
ON cstudio_deploymentworkqueue USING BTREE (state_flag);


CREATE TABLE cstudio_deploymentworkitem (
  id bigserial NOT NULL,
  site_id varchar(50) NOT NULL,
  path varchar(2000) NOT NULL,
  endpoint varchar(255) NOT NULL,
  username varchar(50) NOT NULL,
  deployed_date timestamp,
  batch_id varchar(255) NOT NULL,
  batch_order int NOT NULL DEFAULT '0',
  batch_size int NOT NULL DEFAULT '0',
  type varchar(50),
  CONSTRAINT cstudio_deploymentworkitem_pkey PRIMARY KEY (id)
);

CREATE INDEX cstudio_deploymentworkitem_site_idx
ON cstudio_deploymentworkitem USING BTREE (site_id);

CREATE INDEX cstudio_deploymentworkitem_endpoint_idx
ON cstudio_deploymentworkitem USING BTREE (endpoint);

CREATE INDEX cstudio_deploymentworkitem_username_idx
ON cstudio_deploymentworkitem USING BTREE (username);

CREATE INDEX cstudio_deploymentworkitem_batch_idx
ON cstudio_deploymentworkitem USING BTREE (batch_id);

CREATE INDEX cstudio_deploymentworkitem_sitepath_idx
ON cstudio_deploymentworkitem USING BTREE (site_id, path);

CREATE INDEX cstudio_deploymentworkitem_type_idx
ON cstudio_deploymentworkitem USING BTREE (type);


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
