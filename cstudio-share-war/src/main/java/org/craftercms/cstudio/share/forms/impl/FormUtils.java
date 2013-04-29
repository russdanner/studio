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
package org.craftercms.cstudio.share.forms.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FormUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(FormUtils.class);
	
	public static String getMd5ForFile(InputStream input) {
        String result = null;
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");

            md.reset();
            byte[] bytes = new byte[1024];
            int numBytes;
            while ((numBytes = input.read(bytes)) != -1) {
                md.update(bytes, 0, numBytes);
            }
            byte[] digest = md.digest();
            result = new String(Hex.encodeHex(digest));
            input.reset();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Error while creating MD5 digest", e);
        } catch (IOException e) {
            logger.error("Error while reading input stream", e);
        }
        return result;
    }
    
    public static String getMd5ForFile(String data) {
    	InputStream is = null;
    	String fileName = null;
    
    	try {
    		is = new ByteArrayInputStream(data.getBytes("UTF-8"));
        
    		fileName = getMd5ForFile(is);
    	} catch(UnsupportedEncodingException e) {
    		logger.error("Error while creating MD5 digest", e);
    	}
    	return fileName;
    }

}
