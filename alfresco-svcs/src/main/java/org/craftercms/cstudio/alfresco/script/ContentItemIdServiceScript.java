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
package org.craftercms.cstudio.alfresco.script;

import net.sf.json.JSONObject;
import org.alfresco.repo.processor.BaseProcessorExtension;
import org.craftercms.cstudio.alfresco.dm.constant.DmConstants;
import org.craftercms.cstudio.alfresco.dm.service.api.ContentItemIdGenerator;
import org.craftercms.cstudio.alfresco.service.ServicesManager;
import org.craftercms.cstudio.alfresco.service.exception.ServiceException;

import java.util.Map;

public class ContentItemIdServiceScript extends BaseProcessorExtension {

    protected ServicesManager _servicesManager;
    public ServicesManager getServicesManager() {
        return _servicesManager;
    }
    public void setServicesManager(ServicesManager servicesManager) {
        this._servicesManager = servicesManager;
    }

    /**
     * get the next identity.

     * @return next available id
     * @throws org.craftercms.cstudio.alfresco.service.exception.ServiceException
     */
    public String next() throws ServiceException {
        ContentItemIdGenerator contentItemIdGenerator = (ContentItemIdGenerator)this._servicesManager.getService(ContentItemIdGenerator.class);
        Map<String, String> ids = contentItemIdGenerator.getIds();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(DmConstants.KEY_PAGE_ID, ids.get(DmConstants.KEY_PAGE_ID));
        jsonObject.put(DmConstants.KEY_PAGE_GROUP_ID, ids.get(DmConstants.KEY_PAGE_GROUP_ID));
        return jsonObject.toString();
    }
}
