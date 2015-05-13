/*
 * Crafter Studio Web-content authoring solution
 * Copyright (C) 2007-2014 Crafter Software Corporation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.craftercms.studio.impl.v1.service.content;

import org.apache.commons.lang.StringUtils;
import org.craftercms.studio.api.v1.constant.CStudioConstants;
import org.craftercms.studio.api.v1.exception.ContentNotFoundException;
import org.craftercms.studio.api.v1.exception.ServiceException;
import org.craftercms.studio.api.v1.log.Logger;
import org.craftercms.studio.api.v1.log.LoggerFactory;
import org.craftercms.studio.api.v1.repository.ContentRepository;
import org.craftercms.studio.api.v1.repository.RepositoryItem;
import org.craftercms.studio.api.v1.service.ConfigurableServiceBase;
import org.craftercms.studio.api.v1.service.configuration.ContentTypesConfig;
import org.craftercms.studio.api.v1.service.configuration.ServicesConfig;
import org.craftercms.studio.api.v1.service.content.ContentService;
import org.craftercms.studio.api.v1.service.content.ContentTypeService;
import org.craftercms.studio.api.v1.service.security.SecurityService;
import org.craftercms.studio.api.v1.to.*;

import java.io.Serializable;
import java.util.*;

/**
 * @author Dejan Brkic
 */
public class ContentTypeServiceImpl extends ConfigurableServiceBase implements ContentTypeService {

    private static final Logger logger = LoggerFactory.getLogger(ContentTypeServiceImpl.class);

    @Override
    public ContentTypeConfigTO getContentTypeForContent(String site, String path) throws ServiceException {
        ContentItemTO itemTO = contentService.getContentItem(site, path);
        if (itemTO != null) {
            String type = itemTO.getContentType();
            if (!StringUtils.isEmpty(type)) {
                return servicesConfig.getContentTypeConfig(site, type);
            } else {
                throw new ServiceException("No content type specified for " + path + " in site: " + site);
            }
        } else {
            throw new ContentNotFoundException(path + " is not found in site: " + site);
        }
    }

    @Override
    public boolean isUserAllowed(Set<String> userRoles, ContentTypeConfigTO item) {
        if (item != null) {
            String name = item.getName();
            Set<String> allowedRoles = item.getAllowedRoles();
            logger.debug("Checking allowed roles on " + name + ". user roles: "
                    + userRoles + ", allowed roles: " + allowedRoles);

            if (allowedRoles == null || allowedRoles.size() == 0) {
                return true;
            } else {
                boolean notAllowed = Collections.disjoint(userRoles, allowedRoles);
                if (notAllowed) {
                    logger.debug(name + " is not allowed for the user.");
                    return false;
                } else {
                    return true;
                }
            }
        } else {
            logger.debug("no content type config provided. returning true for user access to content type checking.");

            return true;
        }
    }

    @Override
    public ContentTypeConfigTO getContentTypeByRelativePath(String site, String relativePath) throws ServiceException {
        ContentItemTO item = contentService.getContentItem(site, relativePath);
        if (item != null) {
            String type = item.getContentType();
            if (!StringUtils.isEmpty(type)) {
                return servicesConfig.getContentTypeConfig(site, type);
            } else {
                throw new ServiceException("No content type specified for " + relativePath + " in site: " + site);
            }
        } else {
            throw new ContentNotFoundException(relativePath + " is not found in site: " + site);
        }
    }

    @Override
    public ContentTypeConfigTO getContentType(String site, String type) {
        return servicesConfig.getContentTypeConfig(site, type);
    }

    @Override
    public List<ContentTypeConfigTO> getAllContentTypes(String site, boolean searchable) {
        return getAllContentTypes(site);
    }

    protected List<ContentTypeConfigTO> getAllContentTypes(String site) {
        String contentTypesRootPath = _configPath.replaceAll(CStudioConstants.PATTERN_SITE, site);
        RepositoryItem[] folders = contentRepository.getContentChildren(contentTypesRootPath);
        List<ContentTypeConfigTO> contentTypes = new ArrayList<>();

        if (folders != null) {
            for (int i = 0; i < folders.length; i++) {
                getContentTypeConfigForChildren(site, folders[i], contentTypes);
            }
        }
        return contentTypes;
    }

    /**
     * Traverse file folder -- recursive!, searching for config.xml
     *  @param site
     * @param node
     */
    protected void getContentTypeConfigForChildren(String site, RepositoryItem node, List<ContentTypeConfigTO> contentTypes) {
        String fullPath = node.path + "/" + node.name;
        logger.debug("Get Content Type Config fot Children path = {0}", fullPath );
        RepositoryItem[] folders = contentRepository.getContentChildren(fullPath);
        if (folders != null) {
            for (int i = 0; i < folders.length; i++) {
                if (folders[i].isFolder) {
                    ContentItemTO configNode = contentService.getContentItem(folders[i].path + "/" + folders[i].name + "/" + _configFileName);
                    if (configNode != null) {
                        ContentTypeConfigTO config = contentTypesConfig.loadConfiguration(site, configNode);
                        if (config != null) {
                            contentTypes.add(config);
                        }
                    }
                    // traverse the children file-folder structure

                    getContentTypeConfigForChildren(site, folders[i], contentTypes);
                }
            }
        }
    }

    @Override
    protected TimeStamped getConfigurationById(String key) {
        // not used
        return null;
    }

    @Override
    protected void removeConfiguration(String key) {
        // not used
    }

    @Override
    protected void loadConfiguration(String key) {
        // not used
    }

    @Override
    public List<ContentTypeConfigTO> getAllowedContentTypesForPath(String site, String relativePath) throws ServiceException {
        this.getAllContentTypes(site);
        String user = securityService.getCurrentUser();
        Set<String> userRoles = securityService.getUserRoles(site, user);
        SiteContentTypePathsTO pathsConfig = contentTypesConfig.getPathMapping(site);
        if (pathsConfig != null && pathsConfig.getConfigs() != null) {
            List<ContentTypeConfigTO> contentTypes = new ArrayList<ContentTypeConfigTO>();
            Set<String> contentKeys = new HashSet<String>();
            for (ContentTypePathTO pathConfig : pathsConfig.getConfigs()) {
                // check if the path matches one of includes paths
                if (relativePath.matches(pathConfig.getPathInclude())) {
                    logger.debug(relativePath + " matches " + pathConfig.getPathInclude());
                    Set<String> allowedContentTypes = pathConfig.getAllowedContentTypes();
                    if (allowedContentTypes != null) {
                        for (String key : allowedContentTypes) {
                            if (!contentKeys.contains(key)) {
                                logger.debug("Checking an allowed content type: " + key);
                                ContentTypeConfigTO typeConfig = contentTypesConfig.getContentTypeConfig(key);
                                if (typeConfig != null) {
                                    boolean isMatch = true;
                                    if (typeConfig.getPathExcludes() != null) {
                                        for (String excludePath : typeConfig.getPathExcludes()) {
                                            if (relativePath.matches(excludePath)) {
                                                logger.debug(relativePath + " matches an exclude path: " + excludePath);
                                                isMatch = false;
                                                break;
                                            }
                                        }
                                    }
                                    if (isMatch) {
                                        // if a match is found, populate the content type information
                                        logger.debug("adding " + key + " to content types.");
                                        addContentTypes(site, userRoles, typeConfig, contentTypes);
                                    }
                                } else {
                                    logger.warn("no configuration found for " + key);
                                }
                                contentKeys.add(key);
                            } else {
                                logger.debug(key + " is already added. skipping the content type.");
                            }
                        }
                    }
                }
            }
            return contentTypes;
        } else {
            logger.error("No content type path configuration is found for site: " + site);
            return null;
        }
    }

    protected void addContentTypes(String site, Set<String> userRoles, ContentTypeConfigTO config, List<ContentTypeConfigTO> contentTypes) {
        boolean isAllowed = this.isUserAllowed(userRoles, config);
        if (isAllowed) {
            contentTypes.add(config);
        }
    }

    @Override
    public void register() {
        getServicesManager().registerService(ContentTypeService.class, this);
    }

    public ContentService getContentService() { return contentService; }
    public void setContentService(ContentService contentService) { this.contentService = contentService; }

    public ServicesConfig getServicesConfig() { return servicesConfig; }
    public void setServicesConfig(ServicesConfig servicesConfig) { this.servicesConfig = servicesConfig; }

    public ContentTypesConfig getContentTypesConfig() { return contentTypesConfig; }
    public void setContentTypesConfig(ContentTypesConfig contentTypesConfig) { this.contentTypesConfig = contentTypesConfig; }

    public SecurityService getSecurityService() { return securityService; }
    public void setSecurityService(SecurityService securityService) { this.securityService = securityService; }

    public ContentRepository getContentRepository() { return contentRepository; }
    public void setContentRepository(ContentRepository contentRepository) { this.contentRepository = contentRepository; }

    protected ContentService contentService;
    protected ServicesConfig servicesConfig;
    protected ContentTypesConfig contentTypesConfig;
    protected SecurityService securityService;
    protected ContentRepository contentRepository;
}