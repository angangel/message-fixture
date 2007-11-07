/*
 * Copyright (C) 2007 by Callista Enterprise. All rights reserved.
 * Released under the terms of the GNU General Public License version 2 or later.
 */

package com.googlecode.messagefixture;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.servicefixture.Configuration;
import net.servicefixture.util.InfoLogger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MessageConfiguration {
	private static Log log = LogFactory.getLog(Configuration.class);
	
	private static MessageConfiguration configuration;
	public static final String SERVICEFIXTURE_PROPERTIES = "servicefixture.properties";
	
	private Properties properties = new Properties();
	
	private MessageConfiguration() {
	}
	
	public static MessageConfiguration getInstance() {
		if (configuration == null ) {
			configuration = new MessageConfiguration();
			configuration.loadProperties();
		}
		return configuration;
	}

	private void loadProperties() {
		try {
			properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(SERVICEFIXTURE_PROPERTIES));
		} catch (IOException e) {
			InfoLogger.log(log, "Unable to loaded properties from " + SERVICEFIXTURE_PROPERTIES + " due to:" + e.toString());
		}
	}
	
	public String getProperty(String propertyName) {
		return properties.getProperty(propertyName);
	}
	
	public String[] getPropertyNames(String prefix) {
		List<String> matching = new ArrayList<String>();
		
		Enumeration allProperties = properties.propertyNames(); 
		while (allProperties.hasMoreElements()) {
			
			String propName = (String) allProperties.nextElement();
			if(propName.startsWith(prefix)) {
				matching.add(propName);
			}
		}
		
		return matching.toArray(new String[0]);
	}
	
	public Map<String, String> getAllProperties(String prefix, boolean removePrefix) {
		String[] propNames = getPropertyNames(prefix);
		
		Map<String, String> props = new HashMap<String, String>();
		for (String propName : propNames) {
			if(removePrefix) {
				props.put(propName.substring(prefix.length()), getProperty(propName));
			} else {
				props.put(propName, getProperty(propName));
			}
		}
		
		return props;
	}
	
	public File getFilesDirectory() {
		String dirPath = getInstance().getProperty("servicefixture.file.path");
		
		if(dirPath != null) {
			return new File(dirPath);
		} else {
			return new File("FitNesseRoot/files");
		}
	}
}
