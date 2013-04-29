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
/**
 * 
 */
package org.craftercms.cstudio.share.servlet;

import java.io.Serializable;

import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.craftercms.cstudio.share.exception.CStudioException;
import org.craftercms.commons.crypto.SimpleAesCipher;
import org.craftercms.commons.crypto.Base64;

/**
 * This class implements the functions of CookieManager Interface
 * 
 * @author Chander Shankar
 * 
 */
public class CookieManagerImpl implements CookieManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(CookieManagerImpl.class);
	private SimpleAesCipher _cipher;
	private boolean _encryptCookies;
	String _cookieDomain;
	
	public CookieManagerImpl() {
		_encryptCookies = false;
	}

	/**
	 * user level authentication
	 */
	public String getCookieDomain() {
		return _cookieDomain;
	}

	/**
	 * setter for session level authentication
	 */
	public void setCookieDomain(String domain) {
		_cookieDomain = domain;
	}

	/*
	 * (non-Javadoc)
	 * @see org.craftercms.cstudio.share.servlet.CookieManager#getCookieValue(javax.servlet.http.HttpServletRequest, java.lang.String)
	 */
	public Serializable getCookieValue(HttpServletRequest request, String key) 
			throws CStudioException {
		Cookie[] cookieArray = request.getCookies();
		Serializable serializableCookie = null;
		if (cookieArray != null) {
			for (Cookie cookie : cookieArray) {
				if (cookie.getName().equals(key)) {
					String cookieValue = cookie.getValue();
					if (!StringUtils.isEmpty(cookieValue)) {
						if(_encryptCookies) {
							serializableCookie 
								= getDecryptedStringAsObject(cookieValue);
						}
						else {
							serializableCookie = cookieValue;
						}
					}
				}
			}
		}
		return serializableCookie;
	}

	/*
	 * (non-Javadoc)
	 * @see org.craftercms.cstudio.share.servlet.CookieManager#putCookieValue(javax.servlet.http.HttpServletResponse, java.lang.String, int, java.io.Serializable)
	 */
	public void putCookieValue(HttpServletRequest request, HttpServletResponse response, String key,
			int age, Serializable value) throws CStudioException {
		putCookieValue(request, response, null, key, age, value);
	}

	/*
	 * (non-Javadoc)
	 * @see org.craftercms.cstudio.share.servlet.CookieManager#putCookieValue(javax.servlet.http.HttpServletResponse, java.lang.String, java.lang.String, int, java.io.Serializable)
	 */
	public void putCookieValue(HttpServletRequest request, HttpServletResponse response, String path,
			String key, int age, Serializable value) throws CStudioException {
		
		String cookieValue = null;
		
		if(_encryptCookies) {
			cookieValue = getEncryptedObjectAsString(value);
		}
		else {
			cookieValue = value.toString();
		}
			
		
		Cookie cookie = new Cookie(key, cookieValue);
		if (!StringUtils.isEmpty(path)) {
			cookie.setPath(path);
		} else {
			cookie.setPath("/");
		}
		cookie.setMaxAge(age);

		//if(request.getServerName().indexOf(".") != -1) {
		//	String validForDomain = request.getServerName().substring(request.getServerName().indexOf("."));
		//	cookie.setDomain(validForDomain);
		//}

		if(_cookieDomain != null) {
			cookie.setDomain(_cookieDomain);
		}
		
		response.addCookie(cookie);
	}

	public void destroyCookie(HttpServletRequest request,
			HttpServletResponse response, String key, String path) {
		Cookie[] cookieArray = request.getCookies();
		if (cookieArray != null) {
			for (Cookie cookie : cookieArray) {
				String name = cookie.getName();
				if (name != null && name.equals(key)) {
					if (!StringUtils.isEmpty(path)) {
						cookie.setPath(path);
					} else {
						cookie.setPath("/");
					}
					cookie.setMaxAge(0);
					cookie.setValue(null);

					
					if(_cookieDomain != null) {
						cookie.setDomain(_cookieDomain);
					}

					response.addCookie(cookie);
				}
			}
		}
	}

	public void destroyCookie(HttpServletRequest request,
			HttpServletResponse response, String key) {
		destroyCookie(request, response, key, null);
	}

	/**
	 * Returns an encrypted string representation of the Serializable object
	 * passed as param
	 * 
	 * @param cookie
	 * @return
	 * @throws CStudioException 
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	private String getEncryptedObjectAsString(Serializable cookie) throws CStudioException {
		if (cookie != null) {
			try {
				String encrypted = serializeToString(cookie);
				if (StringUtils.isEmpty(encrypted)) {
					throw new CStudioException("Failed to encode cookie value: "
							+ cookie + ".");
				} else {
					return Base64.encodeBytes(_cipher.encrypt(encrypted.getBytes()),
							Base64.DONT_BREAK_LINES);
				}
			} catch (InvalidKeyException e) {
				throw new CStudioException("Failed to encode cookie value: "
						+ cookie, e);
			} catch (IllegalBlockSizeException e) {
				throw new CStudioException("Failed to encode cookie value: "
						+ cookie, e);
			} catch (BadPaddingException e) {
				throw new CStudioException("Failed to encode cookie value: "
						+ cookie, e);
			}
		} else {
			throw new CStudioException("Failed to encode value. Cookie value cannot be null.");
		}
	}

	/**
	 * Returns the Seriazable object after decryption of the string passed as
	 * param
	 * 
	 * @param cookieData
	 * @return decrypted cookie value
	 * @throws CStudioException 
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */

	private Serializable getDecryptedStringAsObject(String cookieData) throws CStudioException {
		try {
			String decrypted = new String(_cipher.decrypt(Base64.decode(cookieData)));
			if (StringUtils.isEmpty(decrypted)) {
				throw new CStudioException("Failed to decode cookie value: "
						+ cookieData + ".");
			} else {
				return deserialize(decrypted);
			}
		} catch (InvalidKeyException e) {
			throw new CStudioException("Failed to decode cookie value: "
					+ cookieData, e);
		} catch (IllegalBlockSizeException e) {
			throw new CStudioException("Failed to decode cookie value: "
					+ cookieData, e);
		} catch (BadPaddingException e) {
			throw new CStudioException("Failed to decode cookie value: "
					+ cookieData, e);
		}
	}

	/**
	 * Serialize a given object to a string
	 * 
	 * @param object
	 *            Serializable
	 * @return a string that represents the given object
	 * 
	 */
	private String serializeToString(Serializable object) {
		if (object != null) {
			return Base64.encodeObject(object, Base64.DONT_BREAK_LINES);
		} else {
			return null;
		}

	}

	/**
	 * Deserialize a given string to Serializable
	 * 
	 * @param value
	 * @return
	 */
	private Serializable deserialize(String value) {
		if (!StringUtils.isEmpty(value)) {
			return (Serializable) Base64.decodeToObject(value);
		} else {
			return null;
		}

	}

	/**
	 * @param cipher
	 *            the cipher to set
	 */
	public void setCipher(SimpleAesCipher cipher) {
		_cipher = cipher;
	}

}
