package com.googlecode.messagefixture.jms.templates;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

public class MessageTemplate {

	private String jmsMessageID;
	private String jmsCorrelationID;
	private Map<String, String> properties = new HashMap<String, String>();

	
	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public String getJmsMessageID() {
		return jmsMessageID;
	}

	public void setJmsMessageID(String messageID) {
		this.jmsMessageID = messageID;
	}

	public String getJmsCorrelationID() {
		return jmsCorrelationID;
	}
	
	public void setJmsCorrelationID(String jmsCorrelationID) {
		this.jmsCorrelationID = jmsCorrelationID;
	}
	
	protected void populateMessage(Message message) throws JMSException {
		message.setJMSMessageID(jmsMessageID);
		message.setJMSCorrelationID(jmsCorrelationID);
		
		for(Entry<String, String> entry: properties.entrySet()) {
			message.setStringProperty(entry.getKey(), entry.getValue());
		}
	}
	
	public Message toMessage(Session session) throws JMSException {
		Message message = session.createMessage();
		populateMessage(message);
		return message;
	}

}
