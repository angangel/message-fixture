package com.googlecode.messagefixture.jms.templates;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

public class MessageTemplateFactory {

	@SuppressWarnings("unchecked")
	public static MessageTemplate create(Message jmsMessage) throws JMSException {
		MessageTemplate template;
		if(jmsMessage instanceof TextMessage) {
			template = new TextMessageTemplate();
			String text = ((TextMessage)jmsMessage).getText();
			((TextMessageTemplate)template).setText(text);
		} else {
			template = new MessageTemplate();
		}

		Map<String, String> properties = new HashMap<String, String>();
		Enumeration<String> propNames = jmsMessage.getPropertyNames(); 
		while(propNames.hasMoreElements()) {
			String propName = propNames.nextElement();
			
			properties.put(propName, jmsMessage.getStringProperty(propName));
		}
		
		template.setProperties(properties);
		template.setJmsMessageID(jmsMessage.getJMSMessageID());
		template.setJmsCorrelationID(jmsMessage.getJMSCorrelationID());
		return template;
	}
}
