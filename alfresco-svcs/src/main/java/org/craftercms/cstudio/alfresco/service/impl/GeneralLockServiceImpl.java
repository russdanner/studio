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
package org.craftercms.cstudio.alfresco.service.impl;

import javolution.util.FastMap;
import org.craftercms.cstudio.alfresco.service.AbstractRegistrableService;
import org.craftercms.cstudio.alfresco.service.api.GeneralLockService;

import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class GeneralLockServiceImpl extends AbstractRegistrableService implements GeneralLockService {

    protected Map<String, ReentrantLock> nodeLocks = new FastMap<String, ReentrantLock>();

    @Override
    public void register() {
        getServicesManager().registerService(GeneralLockService.class, this);
    }

    @Override
    public void lock(String objectId) {
        ReentrantLock nodeLock;

        synchronized (this) {
            if (nodeLocks.containsKey(objectId)) {
                nodeLock = nodeLocks.get(objectId);
            } else {
                nodeLock = new ReentrantLock();
                nodeLocks.put(objectId, nodeLock);
            }
        }
        nodeLock.lock();
    }

    @Override
    public boolean tryLock(String objectId) {
        ReentrantLock nodeLock;

        synchronized (this) {
            if (nodeLocks.containsKey(objectId)) {
                nodeLock = nodeLocks.get(objectId);
            } else {
                nodeLock = new ReentrantLock();
                nodeLocks.put(objectId, nodeLock);
            }
        }
        return nodeLock.tryLock();
    }

    @Override
    public void unlock(String objectId) {
        ReentrantLock nodeLock = null;
        synchronized (this) {
            nodeLock = nodeLocks.get(objectId);
        }
        if (nodeLock != null) {
            nodeLock.unlock();
        }
    }
}
