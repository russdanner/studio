/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
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
package org.craftercms.cstudio.alfresco.dm.service.impl;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.lock.LockStatus;
import org.alfresco.service.cmr.publishing.PublishingDetails;
import org.alfresco.service.cmr.publishing.PublishingEvent;
import org.alfresco.service.cmr.publishing.Status;
import org.alfresco.service.cmr.publishing.channels.Channel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.namespace.QName;
import org.craftercms.cstudio.alfresco.deployment.DeploymentEndpointConfigTO;
import org.craftercms.cstudio.alfresco.deployment.DeploymentItemPathDescriptor;
import org.craftercms.cstudio.alfresco.dm.constant.DmConstants;
import org.craftercms.cstudio.alfresco.dm.filter.DmFilterWrapper;
import org.craftercms.cstudio.alfresco.dm.service.api.DmContentService;
import org.craftercms.cstudio.alfresco.dm.service.api.DmPublishService;
import org.craftercms.cstudio.alfresco.dm.to.DmPathTO;
import org.craftercms.cstudio.alfresco.dm.workflow.MultiChannelPublishingContext;
import org.craftercms.cstudio.alfresco.publishing.CrafterCMSPublishingModel;
import org.craftercms.cstudio.alfresco.service.AbstractRegistrableService;
import org.craftercms.cstudio.alfresco.service.api.*;
import org.craftercms.cstudio.alfresco.service.exception.ServiceException;
import org.craftercms.cstudio.alfresco.to.PublishingChannelConfigTO;
import org.craftercms.cstudio.alfresco.to.PublishingChannelGroupConfigTO;
import org.craftercms.cstudio.alfresco.to.PublishingChannelTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class DmPublishServiceImpl extends AbstractRegistrableService implements DmPublishService {

    private static final Logger logger = LoggerFactory.getLogger(DmPublishServiceImpl.class);

    private final static int NO_SCHEDULED_EVENTS = 0;
    private final static int EVENTS_NOT_CANCELED = 1;
    private final static int EVENTS_CANCELED = 2;

    protected boolean enableNewDeploymentEngine = false;
    protected DmFilterWrapper dmFilterWrapper;
    protected org.craftercms.cstudio.api.service.deployment.DeploymentService deploymentService;
    
    public void setDmFilterWrapper(DmFilterWrapper dmFilterWrapper) {
		this.dmFilterWrapper = dmFilterWrapper;
	}
	public void setEnableNewDeploymentEngine(boolean enableNewDeploymentEngine) {
        this.enableNewDeploymentEngine = enableNewDeploymentEngine;
    }

    public void setDeploymentService(org.craftercms.cstudio.api.service.deployment.DeploymentService deploymentService) {
        this.deploymentService = deploymentService;
    }

    @Override
    public void register() {
        this._servicesManager.registerService(DmPublishService.class, this);
    }

    @Override
    public void publish(String site, String path, Date launchDate) {
        ServicesConfig servicesConfig = getService(ServicesConfig.class);
        PersistenceManagerService persistenceManagerService = getService(PersistenceManagerService.class);
        String fullPath = servicesConfig.getRepositoryRootPath(site) + path;
        List<NodeRef> nodesToPublish = new FastList<NodeRef>();
        NodeRef nodeRef = persistenceManagerService.getNodeRef(fullPath);
        if (!persistenceManagerService.getLockStatus(nodeRef).equals(LockStatus.NO_LOCK)) {
            persistenceManagerService.unlock(nodeRef);
        }
        if (persistenceManagerService.hasAspect(nodeRef, ContentModel.ASPECT_COPIEDFROM)) {
            persistenceManagerService.removeAspect(nodeRef, ContentModel.ASPECT_COPIEDFROM);
        }
        nodesToPublish.add(nodeRef);
        _publish(site, nodesToPublish, launchDate);
    }

    @Override
    public void publish(String site, List<String> paths, Date launchDate) {
        PersistenceManagerService persistenceManagerService = getService(PersistenceManagerService.class);
        List<NodeRef> nodesToPublish = new FastList<NodeRef>();
        for (String path : paths) {
            NodeRef nodeRef = persistenceManagerService.getNodeRef(path);
            if (!persistenceManagerService.getLockStatus(nodeRef).equals(LockStatus.NO_LOCK)) {
                persistenceManagerService.unlock(nodeRef);
            }
            if (persistenceManagerService.hasAspect(nodeRef, ContentModel.ASPECT_COPIEDFROM)) {
                persistenceManagerService.removeAspect(nodeRef, ContentModel.ASPECT_COPIEDFROM);
            }
            nodesToPublish.add(nodeRef);
            Date timestamp = new Date();
            if (launchDate != null && (launchDate.after(timestamp))) {
                persistenceManagerService.transition(nodeRef, ObjectStateService.TransitionEvent.SUBMIT_WITHOUT_WORKFLOW_SCHEDULED);
                persistenceManagerService.setSystemProcessing(nodeRef, false);
            } else {
                persistenceManagerService.transition(nodeRef, ObjectStateService.TransitionEvent.SUBMIT_WITHOUT_WORKFLOW_UNSCHEDULED);
            }
        }
        _publish(site, nodesToPublish, launchDate);
    }

	@Override
	public void publish(String site, List<String> paths, Date launchDate,
			MultiChannelPublishingContext mcpContext) {
        List<String> pathsToPublish = new FastList<String>();
        for (String p : paths) {
            DmPathTO dmPathTO = new DmPathTO(p);
            pathsToPublish.add(dmPathTO.getRelativePath());
        }
        if (launchDate == null) {
            launchDate = new Date();
        }
        String approver = AuthenticationUtil.getFullyAuthenticatedUser();
        deploymentService.deploy(site, "live", pathsToPublish, launchDate, approver, mcpContext.getSubmissionComment());
    }

    //@Override
    public void publishOld(String site, List<String> paths, Date launchDate,
            MultiChannelPublishingContext mcpContext) {
		if (hasChannelsConfigure(site, mcpContext)) {
            String batchId = null;
			PersistenceManagerService persistenceManagerService = getService(PersistenceManagerService.class);
			List<NodeRef> nodesToPublish = new FastList<NodeRef>();
            List<String> nodeIds = new FastList<String>();
			for (String path : paths) {
				NodeRef nodeRef = persistenceManagerService.getNodeRef(path);
				if (!persistenceManagerService.getLockStatus(nodeRef).equals(LockStatus.NO_LOCK)) {
					persistenceManagerService.unlock(nodeRef);
				}
				if (persistenceManagerService.hasAspect(nodeRef, ContentModel.ASPECT_COPIEDFROM)) {
					persistenceManagerService.removeAspect(nodeRef, ContentModel.ASPECT_COPIEDFROM);
				}
				int cancelResult = cancelScheduledEvents(nodeRef, launchDate);
				if (cancelResult != EVENTS_NOT_CANCELED) {
					nodesToPublish.add(nodeRef);
                    nodeIds.add(nodeRef.getId());
				}
			}
            if (launchDate != null) {
                persistenceManagerService.transitionBulk(nodeIds, ObjectStateService.TransitionEvent.SUBMIT_WITHOUT_WORKFLOW_SCHEDULED, ObjectStateService.State.NEW_SUBMITTED_NO_WF_SCHEDULED);
            } else {
                persistenceManagerService.transitionBulk(nodeIds, ObjectStateService.TransitionEvent.SUBMIT_WITHOUT_WORKFLOW_UNSCHEDULED, ObjectStateService.State.NEW_SUBMITTED_NO_WF_UNSCHEDULED);
            }
            if (!enableNewDeploymentEngine) {
                processLiveRepo(nodesToPublish, true);
            }
			if (logger.isDebugEnabled()) {
				logger.debug("[WORKFLOW] done processing live repo.");
			}
			DmVersionService dmVersionService = getService(DmVersionService.class);
			dmVersionService.disableVersionable();

			if (!enableNewDeploymentEngine) {
				_publish(site, nodesToPublish, launchDate, mcpContext);
			} else {
				batchId = _publishNew(site, nodesToPublish, launchDate, mcpContext);
			}
			if (logger.isDebugEnabled()) {
				logger.debug("[WORKFLOW] done publishing to the deployer.");
			}
			dmVersionService.enableVersionable();
            if (!enableNewDeploymentEngine) {
			    dmVersionService.createNextMajorVersion(site, paths, mcpContext.getSubmissionComment());
            }

            if (batchId != null) {
                DeploymentService deploymentService = getService(DeploymentService.class);
                deploymentService.markBatchReady(batchId);
            }
            if (launchDate != null) {
                persistenceManagerService.setSystemProcessingBulk(nodeIds, false);
            }
		} else {
			if (logger.isWarnEnabled()) {
				PublishingChannelGroupConfigTO channles = getServicesManager().getService(SiteService.class).getPublishingChannelGroupConfigs(site).get(mcpContext.getPublishingChannelGroup());
				logger.warn(" Specified target "+channles.getChannels().toString()+" was not found. Please check if an endpoint or channel with name "+channles.getChannels().toString()+" exists in site configuration");
			}
		}
	}

    protected void _publish(String site, List<NodeRef> nodesToPublish, Date launchDate) {
        PersistenceManagerService persistenceManagerService = getService(PersistenceManagerService.class);
        Channel channel = getPublishingChannel(site);
        if (channel != null) {
            PublishingDetails publishingDetails = persistenceManagerService.createPublishingDetails();
            publishingDetails.addNodesToPublish(nodesToPublish);
            Calendar schedule = null;
            if (launchDate != null) {
                schedule = Calendar.getInstance();
                schedule.setTime(launchDate);
            }
            publishingDetails.setSchedule(schedule);
            publishingDetails.setPublishChannelId(channel.getId());
            String eventId = persistenceManagerService.scheduleNewEvent(publishingDetails);
        } else {
            if (logger.isErrorEnabled()) {
                logger.error("Could not find any publishing channel for site: " + site);
            }
        }

    }

    @SuppressWarnings("unchecked")
    protected void _publish(String site, List<NodeRef> nodesToPublish, Date launchDate, MultiChannelPublishingContext mcpContext) {
        PersistenceManagerService persistenceManagerService = getService(PersistenceManagerService.class);
        SiteService siteService = getService(SiteService.class);
        if (mcpContext != null) {
            PublishingChannelGroupConfigTO configTO = siteService.getPublishingChannelGroupConfigs(site).get(mcpContext.getPublishingChannelGroup());
            for (PublishingChannelConfigTO channelConfigTO : configTO.getChannels()) {
                List<NodeRef> nodesToPublishOnChannel = new FastList<NodeRef>();
                Channel channel = persistenceManagerService.getChannelByName(channelConfigTO.getName());
                if (channel != null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("[WORKFLOW] publishing to " + channelConfigTO.getName());
                    }
                    Map<QName, Serializable> channelProps = channel.getProperties();
                    List<String> includePaths = (List<String>) DefaultTypeConverter.INSTANCE.convert(List.class, channelProps.get(CrafterCMSPublishingModel.PROP_INCLUDE_PATHS));
                    List<String> excludePaths = (List<String>) DefaultTypeConverter.INSTANCE.convert(List.class, channelProps.get(CrafterCMSPublishingModel.PROP_EXCLUDE_PATHS));
                    PublishingDetails publishingDetails = persistenceManagerService.createPublishingDetails();
                    publishingDetails.setStatusMessage(mcpContext.getStatusMessage());
                    Calendar schedule = null;
                    if (launchDate != null) {
                        schedule = Calendar.getInstance();
                        schedule.setTime(launchDate);
                    }
                    publishingDetails.setSchedule(schedule);
                    for (NodeRef nodeRef : nodesToPublish) {
                        boolean exclude = false;
                        String fullPath = persistenceManagerService.getNodePath(nodeRef);
                        DmPathTO dmPathTO = new DmPathTO(fullPath);
                        Pattern regexPattern;

                        if (excludePaths != null) {
                            for (String excludePath : excludePaths) {
                                try {
                                    regexPattern = Pattern.compile(excludePath);
                                    Matcher m = regexPattern.matcher(dmPathTO.getRelativePath());
                                    if (m.matches()) {
                                        exclude = true;
                                    }
                                } catch (PatternSyntaxException e) {
                                    if (logger.isErrorEnabled()) {
                                        logger.error("The exclude Path " + excludePath + " Is not a valid Regex", e);
                                    }
                                }
                            }
                        }

                        if (includePaths != null) {
                            for (String includePath : includePaths) {
                                try {
                                    regexPattern = Pattern.compile(includePath);
                                    Matcher m = regexPattern.matcher(dmPathTO.getRelativePath());
                                    if (m.matches()) {
                                        exclude = false;
                                    }

                                } catch (PatternSyntaxException e) {
                                    if (logger.isErrorEnabled()) {
                                        logger.error("The include Path " + includePath + " Is not a valid Regex", e);
                                    }
                                }
                            }
                        }
                        if (!exclude) {
                            nodesToPublishOnChannel.add(nodeRef);
                        }
                    }
                    publishingDetails.addNodesToPublish(nodesToPublishOnChannel);
                    publishingDetails.setPublishChannelId(channel.getId());
                    String eventId = persistenceManagerService.scheduleNewEvent(publishingDetails);
                    if (logger.isDebugEnabled()) {
                        logger.debug("[WORKFLOW] scheduled a publishing even " + eventId);
                    }
                } else {
                    if (logger.isWarnEnabled()) {
                        logger.warn("Publishing channel " + channelConfigTO.getName() + " not found.");
                    }
                }
            }
        }
    }

    protected String _publishNew(String site, List<NodeRef> nodesToPublish, Date launchDate, MultiChannelPublishingContext mcpContext) {
        PersistenceManagerService persistenceManagerService = getService(PersistenceManagerService.class);
        DeploymentService deploymentService = getService(DeploymentService.class);
        SiteService siteService = getService(SiteService.class);
        String currentUser = persistenceManagerService.getCurrentUserName();
        if (mcpContext != null) {
            PublishingChannelGroupConfigTO configTO = siteService.getPublishingChannelGroupConfigs(site).get(mcpContext.getPublishingChannelGroup());
            String batchId = null;
            for (PublishingChannelConfigTO channelConfigTO : configTO.getChannels()) {
                List<DeploymentItemPathDescriptor> pathsToPublish = new FastList<DeploymentItemPathDescriptor>();
                DeploymentEndpointConfigTO endpoint = siteService.getDeploymentEndpoint(site, channelConfigTO.getName());
                if (endpoint != null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("[WORKFLOW] publishing to " + channelConfigTO.getName());
                    }
                    List<String> includePaths = endpoint.getIncludePattern();
                    List<String> excludePaths = endpoint.getExcludePattern();
                    Calendar schedule = null;
                    if (launchDate != null) {
                        schedule = Calendar.getInstance();
                        schedule.setTime(launchDate);
                    }
                    for (NodeRef nodeRef : nodesToPublish) {
                        boolean exclude = false;
                        String fullPath = persistenceManagerService.getNodePath(nodeRef);
                        DmPathTO dmPathTO = new DmPathTO(fullPath);
                        Pattern regexPattern;
                        if (includePaths != null) {
                            for (String includePath : includePaths) {
                                regexPattern = Pattern.compile(includePath);
                                Matcher m = regexPattern.matcher(dmPathTO.getRelativePath());
                                if (m.matches()) {
                                    exclude = false;
                                }
                            }
                        }
                        if (excludePaths != null) {
                            for (String excludePath : excludePaths) {
                                regexPattern = Pattern.compile(excludePath);
                                Matcher m = regexPattern.matcher(dmPathTO.getRelativePath());
                                if (m.matches()) {
                                    exclude = true;
                                }
                            }
                        }
                        if (!exclude) {
                            pathsToPublish.add(new DeploymentItemPathDescriptor(dmPathTO.getRelativePath(),getTypeForPath(dmPathTO)));
                        }
                    }
                    if (batchId == null) {
                        batchId = deploymentService.createDeploymentBatch(site, launchDate, currentUser, mcpContext.getSubmissionComment());
                    }
                    if (batchId != null) {
                        deploymentService.addBatchDeploymentItems(batchId, site, endpoint.getName(), currentUser, pathsToPublish, new FastList<String>());
                    }

                    //deploymentService.markBatchReady(batchId);

                    if (logger.isDebugEnabled()) {
                        logger.debug("[WORKFLOW] scheduled a deployment batch " + batchId);
                    }
                    return batchId;
                } else {
                	//HEre	
                    if (logger.isWarnEnabled()) {
                        logger.warn("Publishing channel " + channelConfigTO.getName() + " not found.");
                    }
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * Using {@link DmFilterWrapper} gets the type of a given URL
     * @param dmPathTO Path to get the type
     * @return Depending on siteConfig and url : <ul>
     * 						<li> DmConstants.CONTENT_TYPE_COMPONENT</li>
     * 						<li> DmConstants.CONTENT_TYPE_ASSET</li>
     * 						<li>DmConstants.CONTENT_TYPE_RENDERING_TEMPLATE</li>
     * 				        <li>DmConstants.CONTENT_TYPE_PAGE</li>
     * 						<li>DmConstants.CONTENT_TYPE_DOCUMENT</li>
     * 					</ul> 
     */	
    protected String getTypeForPath(DmPathTO dmPathTO) {
    	 	String relativePath=dmPathTO.getRelativePath();
    	 	String site=dmPathTO.getSiteName();
    	if (dmFilterWrapper.accept(site, relativePath, DmConstants.CONTENT_TYPE_COMPONENT)) {
             return DmConstants.CONTENT_TYPE_COMPONENT;
         }else if  (dmFilterWrapper.accept(site, relativePath, DmConstants.CONTENT_TYPE_ASSET)){
        	  return DmConstants.CONTENT_TYPE_ASSET;
         }else if  (dmFilterWrapper.accept(site, relativePath, DmConstants.CONTENT_TYPE_RENDERING_TEMPLATE)){
        	  return DmConstants.CONTENT_TYPE_RENDERING_TEMPLATE;
         }else if  (dmFilterWrapper.accept(site, relativePath, DmConstants.CONTENT_TYPE_PAGE)){
        	  return DmConstants.CONTENT_TYPE_PAGE;
         }else if  (dmFilterWrapper.accept(site, relativePath, DmConstants.CONTENT_TYPE_DOCUMENT)){
        	  return DmConstants.CONTENT_TYPE_DOCUMENT;
         } else {
             return null;
         }
	}

	protected Channel getPublishingChannel(String site) {
        SiteService siteService = getService(SiteService.class);
        PersistenceManagerService persistenceManagerService = getService(PersistenceManagerService.class);
        Channel channel = null;
        Map<String, PublishingChannelGroupConfigTO> channelGroupConfigTOs = siteService.getPublishingChannelGroupConfigs(site);
        // TODO: fix this with next releas, for now just pick first channel (assuming one channel per site)
        if (channelGroupConfigTOs.size() > 0) {
            PublishingChannelGroupConfigTO channelGroupConfigTO = channelGroupConfigTOs.get(0);
            //if (StringUtils.isNotBlank(channelGroupConfigTO.getName()))
            //    return persistenceManagerService.getChannelByName(channelConfigTO.getName());
            //if (StringUtils.isNotBlank(channelConfigTO.getId()))
            //    return persistenceManagerService.getChannelById(channelConfigTO.getId());
        }
        return null;
    }

    @Override
    public void unpublish(String site, List<String> paths, String approver) {
        unpublish(site, paths, approver, null);
    }

    @Override
    public void unpublish(String site, List<String> paths,  String approver, Date scheduleDate) {
        if (scheduleDate == null) {
            scheduleDate = new Date();
        }
        deploymentService.delete(site, "live", paths, approver, scheduleDate);
    }

    public void unpublishOld(String site, List<String> paths, Date scheduleDate) {
        if (enableNewDeploymentEngine) {
            String batchId = unpublishNew(site, paths, scheduleDate);
            DeploymentService deploymentService = getService(DeploymentService.class);
            deploymentService.markBatchReady(batchId);
        } else {
            ServicesConfig servicesConfig = getService(ServicesConfig.class);
            PersistenceManagerService persistenceManagerService = getService(PersistenceManagerService.class);
            DmContentService dmContentService = getService(DmContentService.class);
            List<NodeRef> nodesToUnpublish = new FastList<NodeRef>();
            List<String> toDelete = new FastList<String>();
            String siteRepoPath = servicesConfig.getRepositoryRootPath(site);
            for (String path : paths) {
                String fullPath = siteRepoPath + path;
                NodeRef nodeRef = persistenceManagerService.getNodeRef(fullPath);
                //NodeRef liveRepoNode = copyToLiveRepo(site, nodeRef);
                if (nodeRef != null) {
                    if (nodeWasPublished(nodeRef)) {
                        nodesToUnpublish.add(nodeRef);
                    } else {
                        //if (scheduleDate != null) {
                        toDelete.add(path);
                        //}
                    }
                } else {
                    // rename -> node is already deleted; get it from live repo
                    DmPathTO dmPathTO = new DmPathTO(dmContentService.getContentFullPath(site, path));
                    dmPathTO.setAreaName(DmConstants.DM_LIVE_REPO_FOLDER);
                    String livePath = dmPathTO.toString();
                    //We don't use Noderef to delete , but to  check that node exist to
                    //avoid any kind of null pointer
                    nodeRef = persistenceManagerService.getNodeRef(livePath);
                    if (nodeRef != null) {
                        toDelete.add(livePath);
                    }
                }
            }
            if (!nodesToUnpublish.isEmpty()) {
                _unpublish(site, nodesToUnpublish, scheduleDate);
            }
            if (toDelete.size() > 0) {
                try {
                    dmContentService.deleteContents(site, toDelete, true, null);
                } catch (ServiceException e) {
                    if (logger.isErrorEnabled()) {
                        logger.error("Error while deleting paths: " + toDelete.toString(), e);
                    }
                }
            }
        }
    }

    protected String unpublishNew(String site, List<String> paths, Date scheduleDate) {
        PersistenceManagerService persistenceManagerService = getService(PersistenceManagerService.class);
        SiteService siteService = getService(SiteService.class);
        DeploymentService deploymentService = getService(DeploymentService.class);
        String currentUser = persistenceManagerService.getCurrentUserName();
        Map<String, PublishingChannelGroupConfigTO> groupConfigTOs = siteService.getPublishingChannelGroupConfigs(site);
        List<Channel> channels = new FastList<Channel>();
        boolean eventCreated = false;
        String batchId = null;
        for (PublishingChannelGroupConfigTO groupConfigTO : groupConfigTOs.values()) {
            List<PublishingChannelConfigTO> channelConfigTOs = groupConfigTO.getChannels();
            for (PublishingChannelConfigTO channelConfigTO : channelConfigTOs) {
                DeploymentEndpointConfigTO endpoint = siteService.getDeploymentEndpoint(site, channelConfigTO.getName());
                if (endpoint != null) {
                    if (batchId == null) {
                        batchId = deploymentService.createDeploymentBatch(site, scheduleDate, currentUser);
                    }
                    if (batchId != null) {
                        deploymentService.addBatchDeploymentItems(batchId, site, endpoint.getName(), currentUser, new FastList<DeploymentItemPathDescriptor>(), paths);
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug("[WORKFLOW] scheduled a deployment batch " + batchId);
                    }
                    return batchId;
                } else {
                    if (logger.isWarnEnabled()) {
                        logger.warn("Publishing channel " + channelConfigTO.getName() + " not found.");
                    }
                }
            }
        }
        return null;
    }

    protected void _unpublish(String site, List<NodeRef> nodesToUnpublish, Date scheduleDate) {
        PersistenceManagerService persistenceManagerService = getService(PersistenceManagerService.class);
        SiteService siteService = getService(SiteService.class);
        Map<String, PublishingChannelGroupConfigTO> groupConfigTOs = siteService.getPublishingChannelGroupConfigs(site);
        List<Channel> channels = new FastList<Channel>();
        boolean eventCreated = false;
        for (PublishingChannelGroupConfigTO groupConfigTO : groupConfigTOs.values()) {
            List<PublishingChannelConfigTO> channelConfigTOs = groupConfigTO.getChannels();
            for (PublishingChannelConfigTO channelConfigTO : channelConfigTOs) {
                Channel channel = persistenceManagerService.getChannelByName(channelConfigTO.getName());
                if (channel != null && !channels.contains(channel)) {
                    PublishingDetails publishingDetails = persistenceManagerService.createPublishingDetails();
                    for (NodeRef nodeRef : nodesToUnpublish) {
                        List<PublishingEvent> publishingEvents = persistenceManagerService.getPublishEventsForNode(nodeRef);
                        if (!publishingEvents.isEmpty()) {
                            for (PublishingEvent event : publishingEvents) {
                                if (event.getChannelId().equals(channel.getId())) {
                                    publishingDetails.addNodesToUnpublish(nodeRef);
                                }
                            }
                        }
                    }
                    Calendar schedule = null;
                    if (scheduleDate != null) {
                        schedule = Calendar.getInstance();
                        schedule.setTime(scheduleDate);
                    }
                    publishingDetails.setSchedule(schedule);
                    publishingDetails.setPublishChannelId(channel.getId());
                    String eventId = persistenceManagerService.scheduleNewEvent(publishingDetails);
                    eventCreated = true;
                }
                channels.add(channel);
                if (eventCreated) {
                    break;
                }
            }
            if (eventCreated) {
                break;
            }
        }
    }

    @Override
    public void cancelScheduledItem(String site, String path) {
        ServicesConfig servicesConfig = getService(ServicesConfig.class);
        PersistenceManagerService persistenceManagerService = getService(PersistenceManagerService.class);
        String liveRepoPath = servicesConfig.getLiveRepositoryPath(site);
        String fullPath = liveRepoPath + path;
        NodeRef liveRepoNode = persistenceManagerService.getNodeRef(fullPath);
        if (liveRepoNode != null) {
            List<PublishingEvent> events = persistenceManagerService.getPublishEventsForNode(liveRepoNode);
            if (events != null && events.size() > 0) {
                for (PublishingEvent event : events) {
                    if (event.getScheduledTime() != null && event.getScheduledTime().after(Calendar.getInstance())) {
                        Set<NodeRef> eventNodes = event.getPackage().getNodesToPublish();
                        List<NodeRef> nodesToPublish = new FastList<NodeRef>();
                        for (NodeRef eventNode : eventNodes) {
                            if (!eventNode.equals(liveRepoNode)) nodesToPublish.add(eventNode);
                        }
                        Calendar scheduledDate = event.getScheduledTime();
                        String channelId = event.getChannelId();
                        persistenceManagerService.cancelPublishingEvent(event.getId());
                        if (nodesToPublish.size() > 0) {
                            PublishingDetails publishingDetails = persistenceManagerService.createPublishingDetails();
                            publishingDetails.addNodesToPublish(nodesToPublish);
                            publishingDetails.setSchedule(scheduledDate);
                            publishingDetails.setPublishChannelId(channelId);
                            persistenceManagerService.scheduleNewEvent(publishingDetails);
                        }
                    }
                }
            }
        }
    }

    @Override
    public List<PublishingChannelTO> getAvailablePublishingChannelGroups(String site, String path) {
        List<PublishingChannelTO> channelTOs = new FastList<PublishingChannelTO>();
        List<String> channels = getPublishingChannels(site);
        for (String ch : channels) {
            PublishingChannelTO chTO = new PublishingChannelTO();
            chTO.setName(ch);
            chTO.setPublish(true);
            chTO.setUpdateStatus(false);
            channelTOs.add(chTO);
        }
        return channelTOs;
    }

    protected List<String> getPublishingChannels(String site) {
        SiteService siteService = getService(SiteService.class);
        PersistenceManagerService persistenceManagerService = getService(PersistenceManagerService.class);
        List<String> channels = new FastList<String>();
        Map<String, PublishingChannelGroupConfigTO> channelGroupConfigTOs = siteService.getPublishingChannelGroupConfigs(site);
        for (PublishingChannelGroupConfigTO configTO : channelGroupConfigTOs.values()) {
            channels.add(configTO.getName());
        }
        return channels;
    }

    protected void processLiveRepo(List<NodeRef> publishedNodes, boolean publish) {
        PersistenceManagerService persistenceManagerService = getServicesManager().getService(PersistenceManagerService.class);
        GeneralLockService lockService = getServicesManager().getService(GeneralLockService.class);
        String fullPath;
        String site;
        if (publish) {
            for (NodeRef nodeRef : publishedNodes) {
                lockService.lock(nodeRef.getId());
                try {
                    fullPath = persistenceManagerService.getNodePath(nodeRef);
                    Matcher m = CrafterCMSPublishingModel.DM_REPO_TYPE_PATH_PATTERN.matcher(fullPath);
                    if (m.matches()) {
                        site = m.group(2).length() != 0 ? m.group(2) : "";
                    } else {
                        site = "";
                    }
                    copyToLiveRepo(site, nodeRef);
                } finally {
                    lockService.unlock(nodeRef.getId());
                }
            }
        }
    }

    protected void copyToLiveRepo(String site, NodeRef nodeRef) {
        ServicesConfig servicesConfig = getServicesManager().getService(ServicesConfig.class);
        PersistenceManagerService persistenceManagerService = getServicesManager().getService(PersistenceManagerService.class);
        String liveRepoPath = servicesConfig.getLiveRepositoryPath(site);
        NodeRef liveRepoRoot = persistenceManagerService.getNodeRef(liveRepoPath);
        if (liveRepoRoot == null)
            liveRepoRoot = createLiveRepository(site, DmConstants.DM_LIVE_REPO_FOLDER);
        String fullPath = persistenceManagerService.getNodePath(nodeRef);
        Matcher m = DmConstants.DM_REPO_TYPE_PATH_PATTERN.matcher(fullPath);
        if (m.matches()) {
            String relativePath = m.group(4);
            NodeRef liveNode = persistenceManagerService.getNodeRef(liveRepoRoot, relativePath);
            if (liveNode == null) {
                liveNode = createLiveRepositoryCopy(liveRepoRoot, relativePath, nodeRef);
            } else {
                persistenceManagerService.copy(nodeRef, liveNode);
            }
            Map<QName, Serializable> nodeProps = persistenceManagerService.getProperties(liveNode);
            for (QName propName : DmConstants.SUBMITTED_PROPERTIES) {
                nodeProps.remove(propName);
            }
            persistenceManagerService.setProperties(liveNode, nodeProps);
        }
    }
    
    

    protected NodeRef createLiveRepository(String site, String liveRepoName) {
        ServicesConfig servicesConfig = getServicesManager().getService(ServicesConfig.class);
        PersistenceManagerService persistenceManagerService = getServicesManager().getService(PersistenceManagerService.class);
        String siteRepoPath = servicesConfig.getRepositoryRootPath(site);
        NodeRef siteRepoRoot = persistenceManagerService.getNodeRef(siteRepoPath);
        NodeRef result = persistenceManagerService.createNewFolder(siteRepoRoot, liveRepoName);
        return result;
    }

    protected NodeRef createLiveRepositoryCopy(NodeRef liveRepoRoot, String relativePath, NodeRef nodeRef) {
        if (logger.isDebugEnabled()) {
            logger.debug("[PUBLISHING POST PROCESSOR] creating live repository copy of " + relativePath);
        }
        PersistenceManagerService persistenceManagerService = getServicesManager().getService(PersistenceManagerService.class);
        NodeRef result = null;

        String[] pathSegments = relativePath.split("/");
        NodeRef helperNode = liveRepoRoot;
        NodeRef parent = null;
        for (int i = 0; i < pathSegments.length - 1; i++) {
            if (!"".equals(pathSegments[i])) {
                parent = helperNode;
                helperNode = persistenceManagerService.getChildByName(helperNode, ContentModel.ASSOC_CONTAINS, pathSegments[i]);
                if (helperNode == null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("[WORKFLOW] creating a node with name: " + pathSegments[i]);
                    }
                    Map<QName, Serializable> properties = new FastMap<QName, Serializable>();
                    properties.put(ContentModel.PROP_NAME, pathSegments[i]);
                    helperNode = persistenceManagerService.createNewFolder(parent, pathSegments[i], properties);
                }
            }
        }
        String nodeName = (String) persistenceManagerService.getProperty(nodeRef, ContentModel.PROP_NAME);
        QName assocQName = QName.createQName(ContentModel.TYPE_CONTENT.getNamespaceURI(), QName.createValidLocalName(nodeName));
        result = persistenceManagerService.copy(nodeRef, helperNode, ContentModel.ASSOC_CONTAINS, assocQName);
        persistenceManagerService.setProperty(result, ContentModel.PROP_NAME, nodeName);
        return result;
    }

    protected int cancelScheduledEvents(NodeRef node, Date launchDate) {
        int toRet = NO_SCHEDULED_EVENTS;
        if (!enableNewDeploymentEngine) {
            PersistenceManagerService persistenceManagerService = getService(PersistenceManagerService.class);
            String currentUser = persistenceManagerService.getCurrentUserName();
            List<PublishingEvent> events = persistenceManagerService.getPublishEventsForNode(node);
            Calendar launchCalendar = Calendar.getInstance();
            if (launchDate != null) {
                launchCalendar.setTime(launchDate);
            }
            if (events != null && events.size() > 0) {
                for (PublishingEvent event : events) {
                    if (event.getStatus().equals(Status.SCHEDULED)) {
                        if (event.getScheduledTime() != null && event.getScheduledTime().after(Calendar.getInstance()) && event.getScheduledTime().after(launchCalendar)) {
                            Set<NodeRef> eventNodes = event.getPackage().getNodesToPublish();
                            List<NodeRef> nodesToPublish = new FastList<NodeRef>();
                            for (NodeRef eventNode : eventNodes) {
                                if (!eventNode.equals(node)) nodesToPublish.add(eventNode);
                            }
                            Calendar scheduledDate = event.getScheduledTime();
                            String channelId = event.getChannelId();
                            AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
                            persistenceManagerService.cancelPublishingEvent(event.getId());
                            if (nodesToPublish.size() > 0) {
                                PublishingDetails publishingDetails = persistenceManagerService.createPublishingDetails();
                                publishingDetails.addNodesToPublish(nodesToPublish);
                                publishingDetails.setSchedule(scheduledDate);
                                publishingDetails.setPublishChannelId(channelId);
                                persistenceManagerService.scheduleNewEvent(publishingDetails);
                            }
                            toRet = EVENTS_CANCELED;
                            AuthenticationUtil.setFullyAuthenticatedUser(currentUser);
                        } else {
                            if (toRet != EVENTS_CANCELED) {
                                toRet = EVENTS_NOT_CANCELED;
                            }
                        }
                    }
                }
            }
        }
        return toRet;
    }
    
    

    protected boolean nodeWasPublished(NodeRef node) {
        boolean toRet = false;
        PersistenceManagerService persistenceManagerService = getService(PersistenceManagerService.class);
        List<PublishingEvent> events = persistenceManagerService.getPublishEventsForNode(node);
        if (events.isEmpty()) {
            return false;
        } else {
            for (PublishingEvent event : events) {
                if (event.getStatus().equals(Status.COMPLETED)) {
                    toRet = true;
                    break;
                }
            }
        }
        return toRet;
    }
    
    /**
     * Checks if there are any publishing channels configure
     * @return true if there is at least one publishing channel config
     */
    @Override
	public boolean hasChannelsConfigure(String site, MultiChannelPublishingContext mcpContext){
    	SiteService siteService = getService(SiteService.class);
    	PersistenceManagerService persistenceManagerService = getService(PersistenceManagerService.class);
    	boolean toReturn = false;
        if (mcpContext != null) {
            Map<String, PublishingChannelGroupConfigTO> publishingChannelGroupConfigs = siteService.getPublishingChannelGroupConfigs(site);
            PublishingChannelGroupConfigTO configTO = publishingChannelGroupConfigs.get(mcpContext.getPublishingChannelGroup());
            if (configTO != null) {
                for (PublishingChannelConfigTO channelConfigTO : configTO.getChannels()) {
                    if (channelConfigTO != null) {
                        if (enableNewDeploymentEngine) {
                            if (siteService.getDeploymentEndpoint(site, channelConfigTO.getName()) != null) {
                                return true;
                            }
                        } else {
                            if (persistenceManagerService.getChannelByName(channelConfigTO.getName()) != null) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
    	return false;
    }
}
