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
package org.craftercms.cstudio.alfresco.publishing;

import org.alfresco.service.namespace.QName;

import java.util.regex.Pattern;

/**
 * Crafter Studio file system publishing model
 * @author Dejan Brkic
 */
public interface CrafterCMSPublishingModel {

    public static final String REMOTE_NAMESPACE = "http://cstudio.com/model/publishing/remote/1.0";
    public static final String REMOTE_PREFIX = "cstudio-publishing-remote";

    public static final String REMOTE_CHANNEL_TYPE_ID = "Crafter CMS Remote Deployment";

    public static final QName TYPE_REMOTE_DELIVERY_CHANNEL = QName.createQName(REMOTE_NAMESPACE, "DeliveryChannel");

    public static final QName ASPECT_REMOTE_DELIVERY_CHANNEL = QName.createQName(REMOTE_NAMESPACE, "DeliveryChannelAspect");

    public static final QName PROP_REMOTE_ENABLED = QName.createQName(REMOTE_NAMESPACE, "enabled");
    public static final QName PROP_REMOTE_SERVER = QName.createQName(REMOTE_NAMESPACE, "server");
    public static final QName PROP_REMOTE_PORT = QName.createQName(REMOTE_NAMESPACE, "port");
    public static final QName PROP_REMOTE_URL = QName.createQName(REMOTE_NAMESPACE, "url");
    public static final QName PROP_REMOTE_PASSWORD = QName.createQName(REMOTE_NAMESPACE, "password");
    public static final QName PROP_REMOTE_TARGET = QName.createQName(REMOTE_NAMESPACE, "target");
    public static final QName PROP_PUBLISH_METADATA = QName.createQName(REMOTE_NAMESPACE, "publishMetadata");
    public static final QName PROP_INCLUDE_PATHS = QName.createQName(REMOTE_NAMESPACE, "includePaths");
    public static final QName PROP_EXCLUDE_PATHS = QName.createQName(REMOTE_NAMESPACE, "excludePaths");

    public static final Pattern DM_REPO_TYPE_PATH_PATTERN = Pattern.compile("(/wem-projects/[-\\w]*/)([-\\w]*/)(work-area|live|draft)(/.*)");
}
