package com.googlecode.messagefixture;

import java.util.Map;

import javax.jms.ConnectionFactory;

import net.servicefixture.ServiceFixtureException;

import org.apache.commons.beanutils.BeanUtils;

public abstract class AbstractMessageService {
	
	protected ConnectionFactory createConnectionFactory(String connName) {
		MessageConfiguration config = MessageConfiguration.getInstance();
		
		if(connName == null) {
			connName = config.getProperty("servicefixture.jms.default");
		}
		
		if(connName == null) {
			throw new ServiceFixtureException("A connection name must be provided, or a default set in the "
					+ MessageConfiguration.SERVICEFIXTURE_PROPERTIES + " file.");			
		}
		
		String prefix = "servicefixture.jms." + connName + ".";
		
		String clazz = config.getProperty(prefix + "class");
		
		if(clazz == null) {
			throw new ServiceFixtureException("Please configure database:"
					+ connName + " in the "
					+ MessageConfiguration.SERVICEFIXTURE_PROPERTIES + " file.");
		}
		
		Map<String, String> allProps = config.getAllProperties(prefix, true);
		
		ConnectionFactory cf;
		try {
			cf = (ConnectionFactory) Class.forName(clazz).newInstance();
			BeanUtils.populate(cf, allProps);
		} catch (Exception e) {
			throw new ServiceFixtureException("Failed to configure connection to messaging provider", e);
		}
		
		return cf;
	}
}
