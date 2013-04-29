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
package  org.craftercms.cstudio.share.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.craftercms.cstudio.share.app.SeamlessAppContext;

/**
 * This filter sets the application context in thread local
 * <p>
 * @author Russ Danner (rdanner@devartisan.org)
 * @author Sandra O'Keeffe
 */
public class ApplicationContextForRequestFilter implements Filter {

	@Override
	public void destroy() {
		mLog.debug("destroy method");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

		try {
			SeamlessAppContext applicationContext = new SeamlessAppContext();
	
			applicationContext.setRequest((HttpServletRequest) request);
			applicationContext.setResponse((HttpServletResponse)response);
			applicationContext.setServletContext(this.getFilterConfig().getServletContext());
	
			/* place the context in the current thread */
			SeamlessAppContext.setApplicationContextForThread(applicationContext);
			
			filterChain.doFilter(request, response);
		
		} 
		finally {
			SeamlessAppContext.setApplicationContextForThread(null);
		}        
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		
		/* initialize */
	    this.setFilterConfig(config);

	}

	/**
	 * get the the internal value for the <code>filterConfig</code> property.
	 * <p>
	 * The <code>filterConfig</code> property
	 * 
	 * @return Returns the internal value for the filterConfig property.
	 */
	protected FilterConfig getFilterConfig() {
		return _filterConfig;
	}

	/**
	 * set the internal value for the <code>filterConfig</code> property
	 * @param pFilterConfig The <code>filterConfig</code> to set.
	 */
	protected void setFilterConfig(FilterConfig filterConfig) {
		_filterConfig = filterConfig;
	}
	protected transient Log mLog = LogFactory.getLog(getClass());

	private FilterConfig _filterConfig;
}
