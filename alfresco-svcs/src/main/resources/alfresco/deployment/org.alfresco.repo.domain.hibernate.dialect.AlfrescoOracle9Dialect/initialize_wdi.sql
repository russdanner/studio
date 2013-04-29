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