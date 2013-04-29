CREATE TABLE `cstudio_deploymenthistory` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `site` varchar(35) NOT NULL,
  `path` text NOT NULL,
  `publishing_channel` varchar(255) NOT NULL,
  `user` varchar(35) NOT NULL,
  `deployment_date` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `cstudio_deploymenthistory_site_idx` (`site`),
  KEY `cstudio_deploymenthistory_sitepath_idx` (`site`,`path`(255)),
  KEY `cstudio_deploymenthistory_user_idx` (`user`),
  KEY `cstudio_deploymenthistory_channel_idx` (`publishing_channel`),
  KEY `cstudio_deploymenthistory_deploymentdate_idx` (`deployment_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
