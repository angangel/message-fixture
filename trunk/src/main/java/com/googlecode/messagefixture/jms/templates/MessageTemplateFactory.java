package com.googlecode.messagefixture.jms.templates;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

public class MessageTemplateFactory {

	public static MessageTemplate create(Message jmsMessage) throws JMSException {
		MessageTemplate template;
		if(jmsMessage instanceof TextMessage) {
			template = new TextMessageTemplate();
			String text = ((TextMessage)jmsMessage).getText();
			((TextMessageTemplate)template).setText(text);
		} else {
			template = new MessageTemplate();
		}

		template.setJmsMessageID(jmsMessage.getJMSMessageID());
		template.setJmsCorrelationID(jmsMessage.getJMSCorrelationID());
		return template;
	}
}
