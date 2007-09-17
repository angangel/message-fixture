package com.googlecode.messagefixture.jms;

import java.util.Enumeration;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;


import com.googlecode.messagefixture.jms.templates.MessageTemplate;
import com.googlecode.messagefixture.jms.templates.MessageTemplateFactory;
import com.googlecode.messagefixture.jms.templates.TextMessageTemplate;
import com.ibm.mq.jms.MQConnectionFactory;
import com.ibm.mq.jms.MQQueue;

public class JMSService {

	private ConnectionFactory createConnectionFactory() throws JMSException {
		MQConnectionFactory cf = new MQConnectionFactory();
		cf.setQueueManager("QM1");
		
		return cf;
	}
	
	private Destination createDestination(String destinationName) throws JMSException {
		return new MQQueue(destinationName);
	}
	
	public MessageTemplate receive(String destinationName, String selector) throws JMSException {
		Connection conn = null;
		Session session = null;
		MessageConsumer consumer = null;

		try {
			conn = createConnectionFactory().createConnection();
			conn.start();
			session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			if(selector != null) {
				consumer = session.createConsumer(createDestination(destinationName), selector);
			} else {
				consumer = session.createConsumer(createDestination(destinationName));
			}
			
			Message jmsMessage = consumer.receive(10000);
			
			if(jmsMessage == null) {
				// TODO think about exception structure
				throw new RuntimeException("No message on queue");
			}
			
			MessageTemplate message = MessageTemplateFactory.create(jmsMessage);
			return message;
		} finally {
			JMSUtils.closeQuitely(conn, session, consumer);
		}
		
		
	}

	private MessageTemplate sendInternal(String destinationName, MessageTemplate message) throws JMSException {
		Connection conn = null;
		Session session = null;
		MessageProducer producer = null;

		
		try {
			conn = createConnectionFactory().createConnection();
			session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			producer = session.createProducer(createDestination(destinationName));
			
			Message jmsMessage = message.toMessage(session); 
			producer.send(jmsMessage);
	
			message.setJmsMessageID(jmsMessage.getJMSMessageID());
			
			return message;
		} finally {
			JMSUtils.closeQuitely(conn, session, producer);
		}
	}
	
	public MessageTemplate send(String destinationName, MessageTemplate message) throws JMSException {
		if(message == null) {
			message = new MessageTemplate();
		}
		return sendInternal(destinationName, message);
	}

	public MessageTemplate sendText(String destinationName, TextMessageTemplate message) throws JMSException {
		if(message == null) {
			message = new TextMessageTemplate();
		}
		return sendInternal(destinationName, message);
	}
	
	@SuppressWarnings("unchecked")
	public int count(String destinationName, String selector) throws JMSException {
		Connection conn = null;
		Session session = null;
		QueueBrowser browser = null;

		try {
			conn = createConnectionFactory().createConnection();
			conn.start();
			session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			if(selector != null) {
				browser = session.createBrowser((Queue) createDestination(destinationName), selector);
			} else {
				browser = session.createBrowser((Queue) createDestination(destinationName));
			}
			
			Enumeration<Message> messages = browser.getEnumeration();
			
			int no = 0;
			while(messages.hasMoreElements()) {
				no++;
				messages.nextElement();
			}
			
			return no;
		} finally {
			JMSUtils.closeQuitely(conn, session, browser);
		}
	}

	public void clean(String destinationName, String selector) throws JMSException {
		Connection conn = null;
		Session session = null;
		MessageConsumer consumer = null;

		try {
			conn = createConnectionFactory().createConnection();
			conn.start();
			session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			if(selector != null) {
				consumer = session.createConsumer(createDestination(destinationName), selector);
			} else {
				consumer = session.createConsumer(createDestination(destinationName));
			}

			while(consumer.receiveNoWait() != null);
		} finally {
			JMSUtils.closeQuitely(conn, session, consumer);
		}
	}

}
