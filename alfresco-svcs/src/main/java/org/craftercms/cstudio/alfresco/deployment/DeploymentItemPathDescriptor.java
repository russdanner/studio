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

/**
 *  Use only to send the path and the Type to 
 *  {@link DeploymentDaoService#addBatchDeploymentItems(String, String, String, String, java.util.List, java.util.List)}
 * @author cortiz
 *
 */
public final class DeploymentItemPathDescriptor{
	private String path;
	private String type;
	
	public DeploymentItemPathDescriptor(String path, String type) {
		super();
		this.path = path;
		this.type = type;
	}

	public String getPath() {
		return path;
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return "PathDescriptor [path=" + path + ", type=" + type + "]";
	}	
}