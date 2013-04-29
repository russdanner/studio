CREATE  TABLE `cstudio_deploymentworkqueue` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `site_id` VARCHAR(50) NOT NULL ,
  `batch_id` VARCHAR(255) NOT NULL ,
  `batch_size` INT NOT NULL ,
  `ready_flag` INT NOT NULL DEFAULT 0 ,
  `state_flag` VARCHAR(100) NOT NULL ,
  `submission_comment` TEXT NULL ,
  `golive_datetime` DATETIME NULL ,
  `submit_datetime` DATETIME NULL ,
  `last_attempted_time` DATETIME NULL ,
  `number_of_retries` INT NULL DEFAULT 0 ,
  `cluster_node_id` VARCHAR(100) ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `cstudio_deploymentworkqueue_batch_unique` (`batch_id` ASC) ,
  INDEX `cstudio_deploymentworkqueue_site_idx` (`site_id` ASC) ,
  INDEX `cstudio_deploymentworkqueue_ready_idx` (`ready_flag` ASC) ,
  INDEX `cstudio_deploymentworkqueue_state_idx` (`state_flag` ASC) );



CREATE TABLE `cstudio_deploymentworkitem` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `site_id` varchar(50) NOT NULL,
  `path` text NOT NULL,
  `endpoint` varchar(255) NOT NULL,
  `username` varchar(50) NOT NULL,
  `deployed_date` datetime DEFAULT NULL,
  `batch_id` varchar(255) NOT NULL,
  `batch_order` int(11) NOT NULL DEFAULT '0',
  `batch_size` int(11) NOT NULL DEFAULT '0',
  `type` varchar(50),
  PRIMARY KEY (`id`),
  KEY `cstudio_deploymentworkitem_site_idx` (`site_id`),
  KEY `cstudio_deploymentworkitem_endpoint_idx` (`endpoint`),
  KEY `cstudio_deploymentworkitem_username_idx` (`username`),
  KEY `cstudio_deploymentworkitem_batch_idx` (`batch_id`),
  KEY `cstudio_deploymentworkitem_sitepath_idx` (`site_id`,`path`(250)),
  KEY `cstudio_deploymentworkitem_type_idx` (`type`, `path`(50))
);

CREATE TABLE `cstudio_deploymentdeleteitem` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `site_id` varchar(50) NOT NULL,
  `path` text NOT NULL,
  `endpoint` varchar(255) NOT NULL,
  `username` varchar(50) NOT NULL,
  `deployed_date` datetime DEFAULT NULL,
  `batch_id` varchar(255) NOT NULL,
  `batch_order` int(11) NOT NULL DEFAULT '0',
  `batch_size` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `cstudio_deploymentdeleteitem_site_idx` (`site_id`),
  KEY `cstudio_deploymentdeleteitem_endpoint_idx` (`endpoint`),
  KEY `cstudio_deploymentdeleteitem_username_idx` (`username`),
  KEY `cstudio_deploymentdeleteitem_batch_idx` (`batch_id`),
  KEY `cstudio_deploymentdeleteitem_sitepath_idx` (`site_id`,`path`(250))
);