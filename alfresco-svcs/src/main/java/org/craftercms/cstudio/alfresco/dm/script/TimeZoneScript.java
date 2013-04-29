/*******************************************************************************
 * Crafter Studio Web-content authoring solution
 *     Copyright (C) 2007-2013 Crafter Software Corporation.
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.craftercms.cstudio.alfresco.dm.script;

import org.alfresco.repo.processor.BaseProcessorExtension;
import org.craftercms.cstudio.alfresco.service.ServicesManager;
import org.craftercms.cstudio.alfresco.service.api.ServicesConfig;
import org.craftercms.cstudio.alfresco.util.TimeZoneUtils;

public class TimeZoneScript extends BaseProcessorExtension {

    protected ServicesManager serviceManager;

    public String getTimeZone(String site){
        String s = serviceManager.getService(ServicesConfig.class).getDefaultTimezone(site);
        return TimeZoneUtils.getTimeZoneDisplay(s);
    }

	public void setServiceManager(ServicesManager serviceManager) {
		this.serviceManager = serviceManager;
	}
}
