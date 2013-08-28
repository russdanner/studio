CREATE TABLE "cstudio_activity"  (
	"id"            	BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
	"modified_date" 	DATE NOT NULL,
	"creation_date" 	DATE NOT NULL,
	"summary"       	CLOB(5000) NOT NULL,
	"summary_format"	VARCHAR(255) NOT NULL,
	"content_id"    	CLOB(5000) NOT NULL,
	"site_network"  	VARCHAR(255) NOT NULL,
	"activity_type" 	VARCHAR(255) NOT NULL,
	"content_type"  	VARCHAR(255) NOT NULL,
	"post_user_id"  	VARCHAR(255) NOT NULL,
	CONSTRAINT "cstudio_activity_pk" PRIMARY KEY("id")
);

CREATE INDEX "cstudio_activity_user_idx" ON "cstudio_activity" ("post_user_id");
CREATE INDEX "cstudio_activity_site_idx" ON "cstudio_activity" ("site_network");