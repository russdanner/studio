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
package org.craftercms.cstudio.share.service.api;

import java.util.List;

import org.craftercms.cstudio.share.to.ClipboardItemTO;


/**
 * provides Cut copy paste service to article tree/list on UI.
 * 
 * Site specific individual clipboard
 * 
 * A Collection will contain all paths requested by cut/copy, with a flag tagged to 
 * every item, indicating for what operation they are stacked.    
 *  
 * @author tanveer
 *
 */
public interface ClipboardService {
	
	/**
	 * Pushes path of node to Collection.  Last item is checked with 
	 * current item. 
	 * 
	 * @param site 
	 * @param path of item 
	 * 
	 * @return false, if Collection full or matches last clipped item.  
	 */
	public ServiceResponse copy(String site, String path, boolean deep) ;

	/**
	 * Pushes path of node to Collection.  Last item is checked with 
	 * current item.  Same as copy, only difference, it keeps a flag 
	 * for a different handling during paste.     
	 * 
	 * @param itemTree
     * @return false, if Collection full or matches last clipped item.
	 */
	public ServiceResponse cut(String site, String itemTree) throws Exception;
	
	/**
	 * 1) if copy, prepares a second copy without altering the Collection.  
	 * 2) if cut, prepares a second copy and removes original from repository.  
	 *  
	 * @param parent path, under which new item/items would be pasted.  
	 * @return path of new Item.  
	 * @throws Exception
	 */
	public ServiceResponse paste(String site, String parentPath) ;
	
	/**
	 * Runs paste operation for all items in clipboard.  Also takes care 
	 * about 'cut' flag & 'deep' flag  
	 *  
	 * @param parent path, under which new item/items would be pasted.  
	 * @return path of new Item.  
	 * @throws Exception
	 */
	public boolean pasteAll(String site, String parentPath) throws Exception;
	
	/**
	 * 
	 * @return paths in Collection
	 * @throws Exception
	 */
	public List<ClipboardItemTO> getItems(String site) throws Exception;
	
	/**
	 * Pops from site Collection
	 *  
	 * @return Item of popped item 
	 * @throws Exception
	 */
	public ClipboardItemTO removeItem(String site) throws Exception;
	
	/**
	 * Empty Collection for certain site  
	 * 
	 * @throws Exception
	 */
	public void clear(String site) throws Exception;

	/**
	 * Empty all clipboard collections  
	 * 
	 * @throws Exception
	 */
	// TODO: not sure, if it's necessary to implement.  Need to keep 
	// a 'site' list in Session to clean all clipboards.   
	//public void clearAll() throws Exception;
}
