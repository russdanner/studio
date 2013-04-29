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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.Cookie;

public class CaptureContentHttpServletResponseWrapper extends HttpServletResponseWrapper {

	public CaptureContentHttpServletResponseWrapper(HttpServletResponse response) {
		super(response);
	}

	private ByteArrayOutputStream byteArrayOutputStream;
	private ServletOutputStream servletOutputStream;

	private StringWriter stringWriter;
	private PrintWriter printWriter;

	private String encoding;
	private String mediatype;

	public void addCookie(Cookie cookie) {
		super.addCookie(cookie);
	}

	public boolean containsHeader(String string) {
		return super.containsHeader(string);
	}

	public String encodeURL(String string) {
		return super.encodeURL(string);
	}

	public String encodeRedirectURL(String string) {
		return super.encodeRedirectURL(string);
	}

	public String encodeUrl(String string) {
		return super.encodeUrl(string);
	}

	public String encodeRedirectUrl(String string) {
		return super.encodeRedirectUrl(string);
	}

	public void sendError(int i, String string) throws IOException {
		// TODO
	}

	public void sendError(int i) throws IOException {
		// TODO
	}

	public void sendRedirect(String string) throws IOException {
		super.sendRedirect(string);
	}

	public void setDateHeader(String string, long l) {
		// TODO
	}

	public void addDateHeader(String string, long l) {
		// TODO
	}

	public void setHeader(String string, String string1) {
		// TODO
	}

	public void addHeader(String string, String string1) {
		// TODO
	}

	public void setIntHeader(String string, int i) {
		// TODO
	}

	public void addIntHeader(String string, int i) {

		// TODO
	}

	public void setStatus(int i) {
		// TODO
	}

	public void setStatus(int i, String string) {
		// TODO
	}

	public ServletResponse getResponse() {
		return super.getResponse();
	}

	public void setResponse(ServletResponse servletResponse) {
		super.setResponse(servletResponse);
	}

	public String getCharacterEncoding() {
		// TODO: we don't support setLocale()
		return "UTF-8";// DEFAULT_ENCODING; // (encoding == null) ?
						// DEFAULT_ENCODING :
						// encoding;
	}

	public String getMediaType() {
		return mediatype;
	}

	public ServletOutputStream getOutputStream() throws IOException {
		if (byteArrayOutputStream == null) {
			byteArrayOutputStream = new ByteArrayOutputStream();
			servletOutputStream = new ServletOutputStream() {
				public void write(int i) throws IOException {
					byteArrayOutputStream.write(i);
				}
			};
		}
		return servletOutputStream;
	}

	public PrintWriter getWriter() throws IOException {
		if (printWriter == null) {
			stringWriter = new StringWriter();
			printWriter = new PrintWriter(stringWriter);
		}
		return printWriter;
	}

	public void setContentLength(int i) {
		// NOP
	}

	public void setContentType(String contentType) {
		this.encoding = getContentTypeCharset(contentType);
		this.mediatype = getContentTypeMediaType(contentType);
	}

	public void setBufferSize(int i) {
		// NOP
	}

	public int getBufferSize() {
		// We have a buffer, but it is infinite
		return Integer.MAX_VALUE;
	}

	public void flushBuffer() throws IOException {
		// NOPE
	}

	public boolean isCommitted() {
		// We buffer everything so return false all the time
		return false;
	}

	public void reset() {
		resetBuffer();
	}

	public void resetBuffer() {
		if (byteArrayOutputStream != null) {
			try {
				servletOutputStream.flush();
			} catch (IOException e) {
				// ignore?
			}
			byteArrayOutputStream.reset();
		} else if (stringWriter != null) {
			printWriter.flush();
			final StringBuffer sb = stringWriter.getBuffer();
			sb.delete(0, sb.length());
		}
	}

	public void setLocale(Locale locale) {
		// TODO
	}

	public Locale getLocale() {
		return super.getLocale();
	}

	public String getContent() throws IOException {
		if (stringWriter != null) {
			// getWriter() was used
			printWriter.flush();
			return stringWriter.toString();
		} else if (servletOutputStream != null) {
			// getOutputStream() was used
			servletOutputStream.flush();
			return new String(byteArrayOutputStream.toByteArray(),
					getCharacterEncoding());
		} else {
			return null;
		}
	}

	// NOTE: This is borrowed from NetUtils but we don't want the dependency
	public static String getContentTypeCharset(String contentType) {
		if (contentType == null)
			return null;
		int semicolumnIndex = contentType.indexOf(";");
		if (semicolumnIndex == -1)
			return null;
		int charsetIndex = contentType.indexOf("charset=", semicolumnIndex);
		if (charsetIndex == -1)
			return null;

		// FIXME: There may be other attributes after charset, right?
		String afterCharset = contentType.substring(charsetIndex + 8);
		afterCharset = afterCharset.replace('"', ' ');
		return afterCharset.trim();
	}

	// NOTE: This is borrowed from NetUtils but we don't want the dependency
	public static String getContentTypeMediaType(String contentType) {
		if (contentType == null
				|| contentType.equalsIgnoreCase("content/unknown"))
			return null;
		int semicolumnIndex = contentType.indexOf(";");
		if (semicolumnIndex == -1)
			return contentType;

		return contentType.substring(0, semicolumnIndex).trim();
	}
	

}
