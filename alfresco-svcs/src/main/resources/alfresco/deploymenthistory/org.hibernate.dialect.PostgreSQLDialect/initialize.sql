CREATE TABLE cstudio_deploymenthistory
(
  id bigserial NOT NULL,
  site varchar(35) NOT NULL,
  path text NOT NULL,
  publishing_channel varchar(255) NOT NULL,
  "user" varchar(35) NOT NULL,
  deployment_date timestamp NOT NULL,
  CONSTRAINT cstudio_deploymenthistory_pkey PRIMARY KEY (id)
);

CREATE INDEX cstudio_deploymenthistory_channel_idx
ON cstudio_deploymenthistory USING btree (publishing_channel);

CREATE INDEX cstudio_deploymenthistory_deploymentdate_idx
ON cstudio_deploymenthistory USING btree (deployment_date);

CREATE INDEX cstudio_deploymenthistory_site_idx
ON cstudio_deploymenthistory USING btree (site);

CREATE INDEX cstudio_deploymenthistory_sitepath_idx
ON cstudio_deploymenthistory USING btree (site, path);

CREATE INDEX cstudio_deploymenthistory_user_idx
ON cstudio_deploymenthistory USING btree ("user");

