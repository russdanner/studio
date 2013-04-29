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
package org.craftercms.cstudio.share.service.impl;

import javax.servlet.http.Cookie;

import javax.servlet.http.HttpServletRequest;

import org.craftercms.cstudio.share.service.api.ClipboardKeyGenerator;

public class TicketKeyGenerator implements ClipboardKeyGenerator {

	/*
	 * (non-Javadoc)
	 * @see org.craftercms.cstudio.share.service.api.ClipboardKeyGenerator#generateKey()
	 */
	public String generateKey(HttpServletRequest httpReq) {
        Cookie cookies[] = httpReq.getCookies();
        if(cookies != null && cookies.length > 0) {
            for(Cookie cookie:cookies) {
                if("alf_ticket".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return httpReq.getParameter("alf_ticket");
	}

}
