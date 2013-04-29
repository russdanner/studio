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


import java.util.Arrays;
import java.util.List;

public enum DeploymentBatchStatus {
    UNPROCESSED,
    QUEUED,
    DEPLOYING,
    DEPLOYED,
    NOT_DEPLOYED;

    public final static List<DeploymentBatchStatus> IN_PROGRESS_STATES =
            Arrays.asList(
                    QUEUED,
                    DEPLOYING
            );

    public final static List<String> IN_PROGRESS_STATES_STR =
            Arrays.asList(
                    QUEUED.toString(),
                    DEPLOYING.toString()
            );

    public final static List<DeploymentBatchStatus> UDEPLOYED_STATES =
            Arrays.asList(
                    UNPROCESSED,
                    NOT_DEPLOYED
            );

    public final static List<String> UNDEPLOYED_STATES_STR =
            Arrays.asList(
                    UNPROCESSED.toString(),
                    NOT_DEPLOYED.toString()
            );
}
