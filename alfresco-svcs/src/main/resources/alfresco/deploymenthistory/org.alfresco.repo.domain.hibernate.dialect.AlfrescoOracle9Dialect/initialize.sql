CREATE TABLE cstudio_deploymenthistory (
        id NUMBER (19, 0) PRIMARY KEY,
        site varchar(35) NOT NULL,
        path varchar(2000) NOT NULL,
        publishing_channel varchar(255) NOT NULL,
        "user" varchar(35) NOT NULL,
        deployment_date DATE NOT NULL
);

CREATE SEQUENCE CSTUDIO_DEPLOYMENTHISTORY_SEQ
START WITH 1
INCREMENT BY 1;

CREATE OR REPLACE TRIGGER cs_dephistory_trigger BEFORE INSERT ON cstudio_deploymenthistory REFERENCING NEW AS NEW FOR EACH ROW BEGIN SELECT CSTUDIO_DEPLOYMENTHISTORY_SEQ.nextval INTO :NEW.ID FROM dual;END;;

CREATE INDEX cs_dephistory_site_idx ON cstudio_deploymenthistory (site);
CREATE INDEX cs_dephistory_sitepath_idx ON cstudio_deploymenthistory (site, path);
CREATE INDEX cs_dephistory_user_idx ON cstudio_deploymenthistory ("user");
CREATE INDEX cs_dephistory_channel_idx ON cstudio_deploymenthistory (publishing_channel);
CREATE INDEX cs_dephistory_depdate_idx ON cstudio_deploymenthistory (deployment_date);