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
package org.craftercms.cstudio.share.to;

/**
 * Value object: Clipboard Item
 *
 * @author tanveer
 */
public class ClipboardItemTO {
    private String _path = "";
    private boolean _cutFlag = false;
    private boolean _deep = false;
    private String body;

    public ClipboardItemTO() {

    }

    /**
     * for cloning
     *
     * @param item
     */
    public ClipboardItemTO(ClipboardItemTO item) {
        this._path = item.getPath();
        this._cutFlag = item.isCutFlag();
        this._deep = item.isDeep();
    }

    /**
     * path of the item
     *
     * @param path
     */
    public void setPath(String path) {
        this._path = path;
    }

    public String getPath() {
        return _path;
    }

    /**
     * true, if path was received for 'Cut'.  False if 'Copy'
     *
     * @param cutFlag
     */
    public void setCutFlag(boolean cutFlag) {
        this._cutFlag = cutFlag;
    }

    public boolean isCutFlag() {
        return _cutFlag;
    }

    /**
     * @param deep the deep to set
     */
    public void setDeep(boolean deep) {
        this._deep = deep;
    }

    public boolean isDeep() {
        return _deep;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
