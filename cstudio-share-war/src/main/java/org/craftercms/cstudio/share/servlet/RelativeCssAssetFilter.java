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
public class RelativeCssAssetFilter implements Filter {
	boolean _development;
	FilterConfig _filterConfig;
	String _cssResourcePattern = "url\\(([^)]*)\\)";// <link rel="stylesheet" type="text/css" href="

	public void init(FilterConfig filterConfig) {
		_filterConfig = filterConfig;
	}

    public void destroy() {

	}
    
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException,
	ServletException {

		final CaptureContentHttpServletResponseWrapper respWrapper = new CaptureContentHttpServletResponseWrapper((HttpServletResponse)resp);
		
		chain.doFilter(req, respWrapper);
		
		String content = respWrapper.getContent();
		String modifiedContent = content;
		HttpServletRequest httpReq = (HttpServletRequest)req;
		String pathToCss = (String)httpReq.getAttribute("javax.servlet.include.request_uri");
		String context = httpReq.getContextPath();
		
		modifiedContent = this.processCSS(content, pathToCss, context);
		
		resp.getOutputStream().println(modifiedContent);
	}

	protected String processCSS(String pContent, String pPathToCss, String pContext) {

		String updatedContent = pContent;
		
		Pattern tagPattern = Pattern.compile(
				_cssResourcePattern, 
				Pattern.CASE_INSENSITIVE | 
				Pattern.DOTALL | 
				Pattern.MULTILINE);
		
		Matcher tagMatcher = tagPattern.matcher(pContent);
		
		while (tagMatcher.find()) {

			try {
				String tag = tagMatcher.group();
				String resource = tagMatcher.group(1);
	
				resource = resource.trim();
				
				if(resource.startsWith("\"") || resource.startsWith("'")) {
					resource = resource.substring(1, resource.length()-1);
				}
				
				if(!resource.startsWith(pContext)) {
					String fullPathResource = "";
					String cssBase = pPathToCss.substring(0, pPathToCss.lastIndexOf("/"));
										
					if(resource.startsWith("/")) {
						resource = resource.substring(1);						
					}
	
					if(cssBase.endsWith("/")) {
						cssBase = cssBase.substring(0, cssBase.length()-1);
					}
					
					fullPathResource = cssBase + "/" + resource;
				
					String origTagRegex = tag.replaceAll("\\(", "\\\\(");
					       origTagRegex = origTagRegex.replaceAll("\\)", "\\\\)");
					       
					String newTag = tag.replaceAll(resource, fullPathResource);
					
					updatedContent = updatedContent.replaceAll(origTagRegex, newTag);
				}
			}
			catch(Exception eErr) {
				System.out.println("error processing css :" + eErr);
			}
		}

		return updatedContent;
	}
}
