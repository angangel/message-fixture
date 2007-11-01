package com.googlecode.messagefixture.mq;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import net.servicefixture.ServiceFixtureException;

import com.googlecode.messagefixture.AbstractMessageService;
import com.googlecode.messagefixture.MessageConfiguration;
import com.googlecode.messagefixture.mq.templates.BinaryMessageTemplate;
import com.googlecode.messagefixture.mq.templates.MessageTemplate;
import com.googlecode.messagefixture.mq.templates.TextMessageTemplate;
import com.ibm.mq.MQC;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;

public class MQService extends AbstractMessageService  {

	private MQQueueManager createQM(String connName) throws MQException {
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
		}
		
		if(allProps.containsKey("port")) {
			MQEnvironment.port = Integer.parseInt(allProps.get("port"));
		}
		
		if(allProps.containsKey("channel")) {
			MQEnvironment.channel = allProps.get("channel");
		}
		
		MQQueueManager qm = new MQQueueManager(allProps.get("queueManager"));
		
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
		
		MQMessage msg = new MQMessage();
		queue.get(msg);
		
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
}
