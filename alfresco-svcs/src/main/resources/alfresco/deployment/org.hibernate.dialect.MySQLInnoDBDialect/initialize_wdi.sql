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