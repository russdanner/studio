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

package org.craftercms.cstudio.share.forms.impl.orbeon;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;


/**
 * Response wrapper class to catch the response content
 */
public class BufferedResponseWrapper extends HttpServletResponseWrapper {

    /**
     * wrapper constructor
     */
    public BufferedResponseWrapper(HttpServletResponse pResponse)
    {
        super(pResponse);
    }


    public void setURLEncoder(URLEncoder pUrlEncoder)
    {
        _UrlEncoder = pUrlEncoder;
    }

    @SuppressWarnings("deprecation") public String encodeURL(String pPath)
    {
        if(_UrlEncoder != null)
        {
            return URLEncoder.encode(pPath);
        }
        else
        {
            return super.encodeURL(pPath);
        }
    }

    public String getContentType()
    {
        return _ContentType;
    }

    public void setContentType(String pContentType)
    {
        _ContentType = pContentType;

        super.setContentType(pContentType);
    }

    public void setLocale(Locale pLocale)
    {
        /* do nothing */
    }

    public ServletOutputStream getOutputStream()
    {
        /*
         * if (_callGetWriter) { throw new IllegalStateException(); }
         */

        _CallGetOutputStream = true;

        return _Sos;
    }

    public int getStatus()
    {
        return _Status;
    }

    public void setStatus(int pStatus)
    {
        _Status = pStatus;
    }

    public String getString()
        throws UnsupportedEncodingException
    {
        if(_CallGetOutputStream)
        {
            return _Baos.toString();
        }
        else if(_CallGetWriter)
        {
            return _Sw.toString();
        }
        else
        {
            return "";
        }
    }

    public PrintWriter getWriter()
    {
        /*
         * if (_callGetOutputStream) { throw new IllegalStateException(); }
         */

        _CallGetWriter = true;

        return _Pw;
    }

    public void resetBuffer()
    {
        if(_CallGetOutputStream)
        {
            _Baos.reset();
        }
        else if(_CallGetWriter)
        {
            StringBuffer sb = _Sw.getBuffer();

            sb.delete(0, sb.length());
        }
    }

    public void recycle()
    {
        _UrlEncoder = null;
        _Baos.reset();
        // _sos = new StringServletOutputStream(_baos);
        _Status = SC_OK;
        _Sw = new StringWriter();
        _Pw = new PrintWriter(_Sw);
        _CallGetOutputStream = false;
        _CallGetWriter = false;
    }

    private URLEncoder            _UrlEncoder;

    private String                _ContentType;

    private ByteArrayOutputStream _Baos   = new ByteArrayOutputStream();

    private ServletOutputStream   _Sos    = new StringServletOutputStream(_Baos);

    private int                   _Status = SC_OK;

    private StringWriter          _Sw     = new StringWriter();

    private PrintWriter           _Pw     = new PrintWriter(_Sw);

    private boolean               _CallGetOutputStream;

    private boolean               _CallGetWriter;

    /**
     * <code>StringServletOutputStream</code> wraps a servlet output stream
     * and captures it in a byte array. This is a Liferay class which has been
     * lifted to limit connection with the Liferay source base while it is
     * refactored.
     */
    protected class StringServletOutputStream extends ServletOutputStream
    {

        public StringServletOutputStream(ByteArrayOutputStream pBaos)
        {
            _ByteArrayOutputStream = pBaos;
        }

        public void write(int b)
            throws IOException
        {
            _ByteArrayOutputStream.write(b);
        }

        private ByteArrayOutputStream _ByteArrayOutputStream = null;
    }
}
