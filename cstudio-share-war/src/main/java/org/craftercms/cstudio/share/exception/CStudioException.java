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
package org.craftercms.cstudio.share.exception;

/**
 * Base class exception
 * 
 * @author Russ Danner
 */
public class CStudioException extends Exception {
	private static final long serialVersionUID = 1L;
	private static final String MSG = "CStudio Exception";

	/**
	 * default constructor
	 */
	public CStudioException() {
		super(MSG);
	}

	/**
	 * message constructor
	 * 
	 * @param msg
	 */
	public CStudioException(String msg) {
		super(msg);
	}

	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public CStudioException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * 
	 * @param cause
	 */
	public CStudioException(Throwable cause) {
		super((cause == null ? MSG : cause.toString()), cause);
	}
}
