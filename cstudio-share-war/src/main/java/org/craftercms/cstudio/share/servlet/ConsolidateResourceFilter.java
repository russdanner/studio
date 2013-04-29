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
package org.craftercms.cstudio.share.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.ServletOutputStream;

/**
 * This filter takes the full output of a dynamic page and inspects it for
 * external resource tags which can be concat'd together and served as a single
 * resource.
 * 
 * 1. capture page 2. find resources 3. depoup findings 4. replace all
 * references with a single reference for all objects of a given type
 * 
 * @author rdanner
 */
public class ConsolidateResourceFilter implements Filter {
	boolean _development;
	FilterConfig _filterConfig;
	List<String> _jsExcludes = new ArrayList<String>();
	List<String> _cssExcludes = new ArrayList<String>();
	String _concatServletUri = "NOT-CONFIGRURED";
	String _cssTagPattern = "NOT CONFIGURED"; //<link rel=\"stylesheet\" type=\"text/css\" href=\"([^\"]*)\"\\s*/>
	String _jsTagPattern = "NOT CONFIGURED"; //<script type=\"text/javascript\" src=\"([^\"]*)\"\\s*></script>

	
	public void init(FilterConfig filterConfig) {
		_filterConfig = filterConfig;
		_development = "true".equals(filterConfig.getInitParameter("development"));
		
		if(filterConfig.getInitParameter("jsExcludes") != null)
			_jsExcludes = new ArrayList<String>(Arrays.asList(filterConfig.getInitParameter("jsExcludes").split(",")));

		if(filterConfig.getInitParameter("cssExcludes") != null)
			_cssExcludes = new ArrayList<String>(Arrays.asList(filterConfig.getInitParameter("cssExcludes").split(",")));

		_concatServletUri = filterConfig.getInitParameter("concatServletUri");
		
		_jsTagPattern = filterConfig.getInitParameter("jsTagPattern");

		_cssTagPattern = filterConfig.getInitParameter("cssTagPattern");
	}


	public void destroy() {

	}
    
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException,
	ServletException {

		if(!_development) {
			final CaptureContentHttpServletResponseWrapper respWrapper = new CaptureContentHttpServletResponseWrapper((HttpServletResponse)resp);
			
			chain.doFilter(req, respWrapper);
			
			String content = respWrapper.getContent();
			String modifiedContent = content;
			String contextName = ((HttpServletRequest)req).getContextPath();
			String tags = "";
	
			// process js
			modifiedContent = this.processResourceTags(contextName, _jsTagPattern, "<script type=\"text/javascript\" src=\"", "\"></script>", modifiedContent, _jsExcludes);
			
			// process css
			modifiedContent = this.processResourceTags(contextName, _cssTagPattern, "<link rel=\"stylesheet\" type=\"text/css\" href=\"", "\" />", modifiedContent, _cssExcludes);
	
			resp.getWriter().println(modifiedContent);
		}
		else {
			chain.doFilter(req, resp);
		}
	}

	/**
	 * process the given content for tags which can be combined
	 * @param pPattern tag pattern to combine
	 * @param pTagFrontMatter tag front matter 
	 * @param pTagEndMatter tag end matter
	 * @param pContent content to process
	 * 
	 * @return a string of combined tags
	 */
	protected String processResourceTags(
			String pContextName, 
			String pPattern, 
			String pTagFrontMatter, 
			String pTagEndMatter,  
			String pContent, 
			List<String> pExcludes) {

		Set<String> resourceSet = new HashSet<String>();
		List<String> resourceList = new ArrayList<String>();

		String concatServletUri = pContextName + _concatServletUri;
		
		Pattern tagPattern = Pattern.compile(
				pPattern, 
				Pattern.CASE_INSENSITIVE | 
				Pattern.DOTALL | 
				Pattern.MULTILINE);
		
		Matcher tagMatcher = tagPattern.matcher(pContent);
		
		while (tagMatcher.find()) {
			String tag = tagMatcher.group();
			String resource = tagMatcher.group(1);
			
			try {
				//remove tag			
				pContent = pContent.replaceAll(tag, "");
				
				// track tag in the order it was found
				resourceList.add(resource);
			}
			catch(Exception includeErr) {
				//LOGGER.error("error performing xform include on path:"+include, includeErr);
			}
		}
		
		String combinedResources = concatServletUri;

		int index = 0;
		String resourceTags = "";
		
		// for each items we have in the list determine if it is a dupe, if not, can be combined and if so add it to the tag
		// if it cannot be combined add it as a new tag and start a new combined resource tag
		// this is important because we don't want to mess with the order of assets
		for(String curResource : resourceList) {
			if(!resourceSet.contains(curResource)) {

				// items with query strings are no good - they have to go alone
				String resourceNoContext = (curResource.startsWith(pContextName)) ? curResource.substring(pContextName.length()) : curResource;
				
				if(curResource.contains("?") || pExcludes.contains(curResource) || pExcludes.contains(resourceNoContext)) {
					index = 0;
					resourceTags += pTagFrontMatter + combinedResources + pTagEndMatter + 
					                pTagFrontMatter + curResource + pTagEndMatter;
					combinedResources = concatServletUri;
				}
				else {
					// add resource only if it's not a dup
					if(index!=0) {
						combinedResources += "&";
					}
				
					if(curResource.startsWith(pContextName)) {
						curResource = curResource.substring(pContextName.length());
					}
				
					combinedResources += curResource;
					resourceSet.add(curResource);
					index++;
				}
			}
		}
		
		if(combinedResources != "") {
			resourceTags += "\r\n"+ pTagFrontMatter + combinedResources + pTagEndMatter;
		}
		
		pContent = pContent.replace("<head>", "<head>\r\n\t"+ resourceTags);

		return pContent;
	}
}
