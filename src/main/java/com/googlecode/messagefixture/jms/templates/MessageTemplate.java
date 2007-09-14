package com.googlecode.messagefixture.jms.templates;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

public class MessageTemplate {

	private String jmsMessageID;
	private String jmsCorrelationID;

	
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
	}
	
	public Message toMessage(Session session) throws JMSException {
		Message message = session.createMessage();
		populateMessage(message);
		return message;
	}

}
