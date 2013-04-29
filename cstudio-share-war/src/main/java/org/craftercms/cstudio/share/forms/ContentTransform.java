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
package org.craftercms.cstudio.share.forms;

import java.io.InputStream;

/**
 * A Content Transform is any process that takes action on a content document.
 * Transforms can be run one after another (Chained) and are executed prior to
 * loading a form and after submitting the form.  
 * 
 * It is the responsibility of the form service to execute the transformation chains.
 * 
 * Example transformations include:
 * <ul>
 *   <li>changing document encoding</li>
 *   <li>removing or adding server paths to images in RTE content sections</li>
 * </ul>
 * 
 * @author Russ Danner
 */
public interface ContentTransform {

	/**
	 * process content takes and input stream and returns an input stream. 
	 * What happens inside the method is an implementation detail.  We are working
	 * with streams to ensure a extremely generic interface that makes no assumption
	 * about the output of the document.
	 */
	public InputStream processContent(InputStream content);

}
