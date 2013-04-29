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
package org.craftercms.cstudio.alfresco.deployment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class DeploymentCommandFactory {

    private final static Logger LOGGER = LoggerFactory.getLogger(DeploymentCommandFactory.class);

    protected Map<String, Class> commandTypes;
    public void setCommandTypes(Map<String, Class> commandTypes) {
        this.commandTypes = commandTypes;
    }

    public AbstractDeploymentCommand createDeploymentCommand(String commandType) {
        if (commandTypes != null && commandTypes.containsKey(commandType)) {
            Class targetClass = commandTypes.get(commandType);
            try {
                AbstractDeploymentCommand command = (AbstractDeploymentCommand)targetClass.newInstance();
                return command;
            } catch (InstantiationException e) {
                LOGGER.error("Error while creating deployment command " + commandType, e );
            } catch (IllegalAccessException e) {
                LOGGER.error("Error while creating deployment command " + commandType, e );
            }
        } else {
            LOGGER.error("Unknown deployment command type " + commandType);
        }
        return null;
    }
}
