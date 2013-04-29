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

package org.craftercms.cstudio.share.app;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * provide access to spring in non spring oriented contexts
 */
public class BeanResolver implements BeanFactoryAware {
	
	protected static final Log LOGGER = LogFactory.getLog(BeanResolver.class);
	
	private BeanFactory _owningBeanFactory;
	private static BeanResolver _singletonBeanResolver;
	
	/**
	 * getter for <code>mActualResolverInstance</code> property
	 * <p>
	 * 
	 * @return
	 */
	public static BeanResolver getActualResolverInstance() {

		return _singletonBeanResolver;
	}
	
	/**
	 * setter for <code>mActualResolverInstance</code> property
	 * <p>
	 * 
	 * @param pActualResolverInstance
	 *            -
	 */
	public static void setActualResolverInstance(BeanResolver beanResolver) {

		LOGGER.info("Variable Resolver - Singleton Wrapper now delegating factory to ' " + beanResolver + " '");
		_singletonBeanResolver = beanResolver;
	}
	
	public static BeanResolver singletonInstance() {

		return getActualResolverInstance();
	}
	
	/**
	 * getter for <code>owningBeanFactory</code> property
	 * <p>
	 * 
	 * @return
	 */
	protected BeanFactory getOwningBeanFactory() {

		return _owningBeanFactory;
	}
	
	/**
	 * setter for <code>owningBeanFactory</code> property
	 * <p>
	 * 
	 * @param pOwningBeanFactory
	 *            -
	 */
	protected void setOwningBeanFactory(BeanFactory owningBeanFactory) {

		this._owningBeanFactory = owningBeanFactory;
	}
	
	/**
	 * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
	 * @param pOwningFactory
	 *            - spring bean factory that created this instance
	 */
	public void setBeanFactory(BeanFactory owningFactory) throws BeansException {

		this.setOwningBeanFactory(owningFactory);
		setActualResolverInstance(this);
	}
	
	/**
	 * resolove a "bean" given a "component name" or "bean" name
	 * 
	 * @param pComponentName
	 *            - name of component to resolve
	 */
	public Object resolveValueForName(String pComponentName) {

		Object retResolvedObject = null;
		BeanFactory instOwningBeanFactory = null;
		
		instOwningBeanFactory = this.getOwningBeanFactory();
		
		if (instOwningBeanFactory != null) {
			retResolvedObject = instOwningBeanFactory.getBean(pComponentName);
		}
		else {
			/* value for owning factory property never set */
			LOGGER.error("setBeanFactory method never called by owning spring factory "
					+ "- cannot resolve object for name [" + pComponentName + "]");
		}
		
		return retResolvedObject;
	}
	
}
