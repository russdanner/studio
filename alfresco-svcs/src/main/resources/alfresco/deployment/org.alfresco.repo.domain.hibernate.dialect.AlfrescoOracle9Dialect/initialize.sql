CREATE  TABLE cstudio_deploymentworkqueue (
  id NUMBER (19, 0) PRIMARY KEY ,
  site_id VARCHAR(50) NOT NULL ,
  batch_id VARCHAR(255) NOT NULL ,
  batch_size NUMBER (19, 0) NOT NULL ,
  ready_flag NUMBER (19, 0) DEFAULT 0 NOT NULL,
  state_flag VARCHAR(100) NOT NULL ,
  submission_comment CLOB ,
  golive_datetime DATE ,
  submit_datetime DATE ,
  last_attempted_time DATE ,
  number_of_retries NUMBER (19, 0) DEFAULT 0 ,
  cluster_node_id VARCHAR(100)
);

CREATE SEQUENCE CSTUDIO_DEPLOYWORKQUEUE_SEQ
START WITH 1
INCREMENT BY 1;

CREATE OR REPLACE TRIGGER cs_depworkqueue_trigger BEFORE INSERT ON cstudio_deploymentworkqueue REFERENCING NEW AS NEW FOR EACH ROW BEGIN SELECT CSTUDIO_DEPLOYWORKQUEUE_SEQ.nextval INTO :NEW.ID FROM dual;END;;

CREATE UNIQUE INDEX cs_depworkqueue_batch_unique ON cstudio_deploymentworkqueue (batch_id);
CREATE INDEX cs_depworkqueue_site_idx ON cstudio_deploymentworkqueue (site_id);
CREATE INDEX cs_depworkqueue_ready_idx ON cstudio_deploymentworkqueue (ready_flag);
CREATE INDEX cs_depworkqueue_state_idx ON cstudio_deploymentworkqueue (state_flag);


CREATE TABLE cstudio_deploymentworkitem (
  id NUMBER(19, 0) PRIMARY KEY,
  site_id varchar(50) NOT NULL,
  path varchar(2000) NOT NULL,
  endpoint varchar(255) NOT NULL,
  username varchar(50) NOT NULL,
  deployed_date date DEFAULT NULL,
  batch_id varchar(255) NOT NULL,
  batch_order NUMBER (19, 0) DEFAULT 0 NOT NULL,
  batch_size NUMBER (19, 0) DEFAULT 0 NOT NULL,
  type varchar(50)
);

CREATE SEQUENCE CSTUDIO_DEPLOYWORKITEM_SEQ
START WITH 1
INCREMENT BY 1;

CREATE OR REPLACE TRIGGER cs_depworkitem_trigger BEFORE INSERT ON cstudio_deploymentworkitem REFERENCING NEW AS NEW FOR EACH ROW BEGIN SELECT CSTUDIO_DEPLOYWORKITEM_SEQ.nextval INTO :NEW.ID FROM dual;END;;

CREATE INDEX cs_depworkitem_site_idx ON cstudio_deploymentworkitem (site_id);
CREATE INDEX cs_depworkitem_endpoint_idx ON cstudio_deploymentworkitem (endpoint);
CREATE INDEX cs_depworkitem_username_idx ON cstudio_deploymentworkitem (username);
CREATE INDEX cs_depworkitem_batch_idx ON cstudio_deploymentworkitem (batch_id);
CREATE INDEX cs_depworkitem_sitepath_idx ON cstudio_deploymentworkitem (site_id, path);
CREATE INDEX cs_depworkitem_type_idx ON cstudio_deploymentworkitem(type);

CREATE TABLE cstudio_deploymentdeleteitem (
  id NUMBER(19, 0) PRIMARY KEY,
  site_id varchar(50) NOT NULL,
  path varchar(2000) NOT NULL,
  endpoint varchar(255) NOT NULL,
  username varchar(50) NOT NULL,
  deployed_date date DEFAULT NULL,
  batch_id varchar(255) NOT NULL,
  batch_order NUMBER (19, 0) DEFAULT 0 NOT NULL,
  batch_size NUMBER (19, 0) DEFAULT 0 NOT NULL
);

CREATE SEQUENCE CSTUDIO_DEPLOYDELETEITEM_SEQ
START WITH 1
INCREMENT BY 1;

CREATE OR REPLACE TRIGGER cs_depdeleteitem_trigger BEFORE INSERT ON cstudio_deploymentdeleteitem REFERENCING NEW AS NEW FOR EACH ROW BEGIN SELECT CSTUDIO_DEPLOYDELETEITEM_SEQ.nextval INTO :NEW.ID FROM dual;END;;

CREATE INDEX cs_depdeleteitem_site_idx ON cstudio_deploymentdeleteitem (site_id);
CREATE INDEX cs_depdeleteitem_endpoint_idx ON cstudio_deploymentdeleteitem (endpoint);
CREATE INDEX cs_depdeleteitem_username_idx ON cstudio_deploymentdeleteitem (username);
CREATE INDEX cs_depdeleteitem_batch_idx ON cstudio_deploymentdeleteitem (batch_id);
CREATE INDEX cs_depdeleteitem_sitepath_idx ON cstudio_deploymentdeleteitem (site_id, path);
