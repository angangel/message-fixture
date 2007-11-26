/*
 * Copyright (C) 2007 by Callista Enterprise. All rights reserved.
 * Released under the terms of the GNU General Public License version 2 or later.
 */

package com.googlecode.messagefixture.jms;

import java.io.IOException;
import java.util.Enumeration;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;

import com.googlecode.messagefixture.AbstractMessageService;
import com.googlecode.messagefixture.jms.templates.MessageTemplate;
import com.googlecode.messagefixture.jms.templates.MessageTemplateFactory;
import com.googlecode.messagefixture.jms.templates.TextMessageTemplate;

public class JMSService extends AbstractMessageService {

	private Destination createDestination(Session session, String destinationName) throws JMSException {
		return session.createQueue(destinationName);
	}
	
	public MessageTemplate receive(String destinationName, String selector) throws JMSException {
		return receive(destinationName, selector, null);
	}
	
	public MessageTemplate receive(String destinationName, String selector, String connName) throws JMSException {
		Connection conn = null;
		Session session = null;
		MessageConsumer consumer = null;

		try {
			conn = createConnectionFactory(connName).createConnection();
			conn.start();
			session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			if(selector != null) {
				consumer = session.createConsumer(createDestination(session, destinationName), selector);
			} else {
				consumer = session.createConsumer(createDestination(session, destinationName));
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
	
	public void connect(String connName) throws JMSException {
		Connection conn = null;
		Session session = null;

		try {
			conn = createConnectionFactory(connName).createConnection();
			conn.start();
			session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
		} finally {
			JMSUtils.closeQuitely(conn, session, (MessageConsumer)null);
		}
	}

	private MessageTemplate sendInternal(String connName, String destinationName, MessageTemplate message) throws JMSException, IOException {
		Connection conn = null;
		Session session = null;
		MessageProducer producer = null;

		
		try {
			conn = createConnectionFactory(connName).createConnection();
			session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			producer = session.createProducer(createDestination(session, destinationName));
			
			Message jmsMessage = message.toMessage(session); 
			producer.send(jmsMessage);
	
			message.setJmsMessageID(jmsMessage.getJMSMessageID());
			
			return message;
		} finally {
			JMSUtils.closeQuitely(conn, session, producer);
		}
	}
	
	public MessageTemplate send(String destinationName, MessageTemplate message, String connName) throws JMSException, IOException {
		if(message == null) {
			message = new MessageTemplate();
		}
		return sendInternal(connName, destinationName, message);
	}

	public MessageTemplate sendText(String destinationName, TextMessageTemplate message, String connName) throws JMSException, IOException {
		if(message == null) {
			message = new TextMessageTemplate();
		}
		return sendInternal(connName, destinationName, message);
	}
	
	@SuppressWarnings("unchecked")
	public int count(String destinationName, String selector, String connName) throws JMSException {
		Connection conn = null;
		Session session = null;
		QueueBrowser browser = null;

		try {
			conn = createConnectionFactory(connName).createConnection();
			conn.start();
			session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			if(selector != null) {
				browser = session.createBrowser((Queue) createDestination(session, destinationName), selector);
			} else {
				browser = session.createBrowser((Queue) createDestination(session, destinationName));
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

	public void clean(String destinationName, String selector, String connName) throws JMSException {
		Connection conn = null;
		Session session = null;
		MessageConsumer consumer = null;

		try {
			conn = createConnectionFactory(connName).createConnection();
			conn.start();
			session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			if(selector != null) {
				consumer = session.createConsumer(createDestination(session, destinationName), selector);
			} else {
				consumer = session.createConsumer(createDestination(session, destinationName));
			}

			while(consumer.receiveNoWait() != null);
		} finally {
			JMSUtils.closeQuitely(conn, session, consumer);
		}
	}

}
