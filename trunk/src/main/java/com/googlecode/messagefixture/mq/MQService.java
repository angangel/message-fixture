/*
 * Copyright (C) 2007 by Callista Enterprise. All rights reserved.
 * Released under the terms of the GNU General Public License version 2 or later.
 */

package com.googlecode.messagefixture.mq;

import java.io.IOException;
import java.util.Map;

import net.servicefixture.ServiceFixtureException;

import com.googlecode.messagefixture.AbstractMessageService;
import com.googlecode.messagefixture.MessageConfiguration;
import com.googlecode.messagefixture.mq.templates.BinaryMessageTemplate;
import com.googlecode.messagefixture.mq.templates.MessageTemplate;
import com.googlecode.messagefixture.mq.templates.TextMessageTemplate;
import com.googlecode.messagefixture.mq.util.PcfException;
import com.googlecode.messagefixture.mq.util.PcfUtil;
import com.ibm.mq.MQC;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;

public class MQService extends AbstractMessageService  {

	private String initMqEnvironment(String connName) {
		MessageConfiguration config = MessageConfiguration.getInstance();
		
		if(connName == null) {
			connName = config.getProperty("servicefixture.jms.default");
		}
		
		if(connName == null) {
			throw new ServiceFixtureException("A connection name must be provided, or a default set in the "
					+ MessageConfiguration.SERVICEFIXTURE_PROPERTIES + " file.");			
		}
		
		String prefix = "servicefixture.jms." + connName + ".";
		
		Map<String, String> allProps = config.getAllProperties(prefix, true);
		
		if(allProps.containsKey("hostName")) {
			MQEnvironment.hostname = allProps.get("hostName");
		} else {
			MQEnvironment.hostname = null;
		}
		
		if(allProps.containsKey("port")) {
			MQEnvironment.port = Integer.parseInt(allProps.get("port"));
		} else {
			MQEnvironment.port = 1414;
		}
		
		if(allProps.containsKey("channel")) {
			MQEnvironment.channel = allProps.get("channel");
		} else {
			MQEnvironment.channel = null;
		}
		
		return allProps.get("queueManager");
	}
	
	private MQQueueManager createQM(String connName) throws MQException {
		MQQueueManager qm = new MQQueueManager(initMqEnvironment(connName));
		
		return qm;
	}
	
	public MessageTemplate putBinary(String destinationName, BinaryMessageTemplate message, String connName) throws MQException, IOException {
		MQQueueManager qm = createQM(connName);
		
		int oo = MQC.MQOO_OUTPUT;
		MQQueue queue = qm.accessQueue(destinationName, oo);
		
		MQMessage msg = message.toMessage(); 
		queue.put(msg);
		
		message.setMessageID(new String(msg.messageId));
		
		return message;
	}
	
	public MessageTemplate putText(String destinationName, TextMessageTemplate message, String connName) throws MQException, IOException {
		MQQueueManager qm = createQM(connName);
		
		int oo = MQC.MQOO_OUTPUT;
		MQQueue queue = qm.accessQueue(destinationName, oo);
		
		MQMessage msg = message.toMessage(); 
		queue.put(msg);
		
		message.setMessageID(new String(msg.messageId));
		
		return message;
	}

	public TextMessageTemplate getText(String destinationName, String connName) throws MQException, IOException {
		MQQueueManager qm = createQM(connName);
		
		int oo = MQC.MQOO_INPUT_SHARED;
		MQQueue queue = qm.accessQueue(destinationName, oo);
		
		MQGetMessageOptions gmo = new MQGetMessageOptions();
		gmo.options = MQC.MQGMO_WAIT + MQC.MQGMO_CONVERT;
		gmo.waitInterval = 10000;
		
		
		MQMessage msg = new MQMessage();
		queue.get(msg, gmo);
		
		TextMessageTemplate message = new TextMessageTemplate(msg);
		
		return message;
	}
	
	public BinaryMessageTemplate getBinary(String destinationName, String connName) throws MQException, IOException {
		MQQueueManager qm = createQM(connName);
		
		int oo = MQC.MQOO_INPUT_SHARED;
		MQQueue queue = qm.accessQueue(destinationName, oo);
		
		MQMessage msg = new MQMessage();
		queue.get(msg);
		
		BinaryMessageTemplate message = new BinaryMessageTemplate(msg);
		
		return message;
	}

	public void clean(String destinationName, String connName) throws MQException {
		MQQueueManager qm = createQM(connName);
		
		int oo = MQC.MQOO_INPUT_SHARED;
		MQQueue queue = qm.accessQueue(destinationName, oo);

		try {
			while(true) {
				MQMessage msg = new MQMessage();
				queue.get(msg);
			}
		} catch(MQException e) {
			if(e.reasonCode == 2033) {
				// ok, we're done
			} else {
				throw e;
			}
		}
	}
	
	public void inhibit(String destinationName, String connName) throws MQException, IOException, PcfException {
		MQQueueManager qm = createQM(connName);
		
		PcfUtil.putDisableQueue(qm, destinationName);
		PcfUtil.getDisableQueue(qm, destinationName);
	}

	public void enable(String destinationName, String connName) throws MQException, IOException, PcfException {
		MQQueueManager qm = createQM(connName);
		
		PcfUtil.putEnableQueue(qm, destinationName);
		PcfUtil.getEnableQueue(qm, destinationName);
	}
	
	public int count(String destinationName, String connName) throws MQException {
		MQQueueManager qm = createQM(connName);
		
		int oo = MQC.MQOO_INPUT_SHARED + MQC.MQOO_BROWSE;
		MQQueue queue = qm.accessQueue(destinationName, oo);

		int count = 0;
		try {
			MQGetMessageOptions gmo = new MQGetMessageOptions();
			gmo.options = MQC.MQGMO_WAIT;
			gmo.waitInterval = 10000;
			
			gmo.options = MQC.MQGMO_BROWSE_FIRST;
			while(true) {

				MQMessage msg = new MQMessage();
				queue.get(msg, gmo);
				count++;
				
				gmo.options = MQC.MQGMO_BROWSE_NEXT;
			}
			
		} catch(MQException e) {
			if(e.reasonCode == 2033) {
				// ok, we're done
				return count;
			} else {
				throw e;
			}
		}
	}

	public void connect(String channelName, String connName) throws MQException, IOException {

		String qmName = initMqEnvironment(connName);
		MQEnvironment.channel = channelName;
		
		try {
			MQQueueManager qm = new MQQueueManager(qmName);
	
			qm.getDescription();
			
			if(!qm.isConnected()) {
				throw new ServiceFixtureException("Failed to connect");
			}
		} catch(MQException e) {
			throw new ExtendedMQException(e);
			
		}
	}
	
}
