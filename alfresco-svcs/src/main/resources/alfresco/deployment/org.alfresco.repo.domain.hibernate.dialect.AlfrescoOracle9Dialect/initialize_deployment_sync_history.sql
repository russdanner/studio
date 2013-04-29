CREATE  TABLE cstudio_deploymentsynchistory (
  id NUMBER (19, 0) PRIMARY KEY ,
  syncdate DATE NOT NULL ,
  site VARCHAR(50) NOT NULL ,
  environment VARCHAR(20) NOT NULL ,
  path CLOB NOT NULL ,
  target VARCHAR(50) NOT NULL ,
  username VARCHAR(25) NOT NULL ,
  contenttypeclass VARCHAR(25) NOT NULL
);

CREATE SEQUENCE CSTUDIO_DEPSYNCHISTORY_SEQ
START WITH 1
INCREMENT BY 1;

CREATE OR REPLACE TRIGGER cs_depsynchistory_trigger BEFORE INSERT ON cstudio_deploymentsynchistory REFERENCING NEW AS NEW FOR EACH ROW BEGIN SELECT CSTUDIO_DEPSYNCHISTORY_SEQ.nextval INTO :NEW.ID FROM dual;END;;

CREATE INDEX cstudio_ptt_site_idx ON cstudio_deploymentsynchistory (site);
CREATE INDEX cstudio_ptt_environment_idx ON cstudio_deploymentsynchistory (environment);
CREATE INDEX cstudio_ptt_sitepath_idx ON cstudio_deploymentsynchistory (site, path);
CREATE INDEX cstudio_ptt_target_idx ON cstudio_deploymentsynchistory (target);
CREATE INDEX cstudio_ptt_username_idx ON cstudio_deploymentsynchistory (username);
CREATE INDEX cstudio_ptt_contenttypeclass_idx ON cstudio_deploymentsynchistory (contenttypeclass);
