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
package org.craftercms.cstudio.alfresco.script;

import java.util.List;
import java.util.ArrayList;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.alfresco.repo.processor.BaseProcessorExtension;
import org.craftercms.cstudio.alfresco.service.ServicesManager;
import org.craftercms.cstudio.alfresco.service.api.ObjectStateService;
import org.craftercms.cstudio.alfresco.service.api.PersistenceManagerService;
import org.craftercms.cstudio.alfresco.service.api.ServicesConfig;
import org.craftercms.cstudio.alfresco.to.ObjectStateTO;
public class ObjectStateServiceScript extends BaseProcessorExtension {

    protected static final String JSON_KEY_STATES = "states";
    protected static final String JSON_KEY_TRANSITION_EVENTS = "transitionEvents";
    protected static final String JSON_KEY_TRANSITION_MATRIX = "transitionMatrix";

    protected ServicesManager servicesManager;
    public ServicesManager getServicesManager() {
        return servicesManager;
    }
    public void setServicesManager(ServicesManager servicesManager) {
        this.servicesManager = servicesManager;
    }

    public List<ObjectStateTO> getObjectStateByStates(String site, List<String> states) {
    	List<ObjectStateService.State> enumStates = new ArrayList<ObjectStateService.State>();
    	
    	for(String state : states) {
    		enumStates.add(ObjectStateService.State.valueOf(state));
    	}
    	
    	return getServicesManager().getService(ObjectStateService.class).getObjectStateByStates(site, enumStates);
    }
    public String setObjectState(String site, String path, String state, boolean systemProcessing) {
        ServicesConfig servicesConfig = getServicesManager().getService(ServicesConfig.class);
        String fullPath = servicesConfig.getRepositoryRootPath(site) + path;
        PersistenceManagerService persistenceManagerService = getServicesManager().getService(PersistenceManagerService.class);
        ObjectStateService.State objectState = persistenceManagerService.getObjectState(fullPath);
        if (objectState == null) {
            persistenceManagerService.insertNewObjectEntry(fullPath);
        }
        persistenceManagerService.setObjectState(fullPath, ObjectStateService.State.valueOf(state));
        persistenceManagerService.setSystemProcessing(fullPath, systemProcessing);
        return "Success";
    }

    public String getTransitionMapping() {
        ObjectStateService.State[][] transitionMapping = getServicesManager().getService(ObjectStateService.class).getTransitionMapping();

        int cntStates = transitionMapping.length;
        ObjectStateService.State[] states = ObjectStateService.State.values();
        JSONArray statesJson = new JSONArray();
        for (int i = 0; i < cntStates; i++) {
            statesJson.add(states[i].name());
        }

        JSONArray transitionEventsJson = new JSONArray();
        for (ObjectStateService.TransitionEvent event : ObjectStateService.TransitionEvent.values()) {
            transitionEventsJson.add(event.name());
        }

        JSONArray transitionMappingJson = new JSONArray();
        for (int i = 0; i < cntStates; i++) {
            JSONArray stateTransitionsArray = new JSONArray();
            for (int j = 0; j < transitionMapping[i].length; j++) {
                stateTransitionsArray.add(transitionMapping[i][j].name());
            }
            JSONObject stateTransitionsJson = new JSONObject();
            stateTransitionsJson.put(states[i].name(), stateTransitionsArray);
            transitionMappingJson.add(stateTransitionsJson);
        }
        JSONObject response = new JSONObject();
        response.put(JSON_KEY_STATES, statesJson);
        response.put(JSON_KEY_TRANSITION_EVENTS, transitionEventsJson);
        response.put(JSON_KEY_TRANSITION_MATRIX, transitionMappingJson);
        return response.toString();
    }
}
