package com.googlecode.messagefixture.mq;

import java.io.IOException;

import com.googlecode.messagefixture.mq.templates.MessageTemplate;
import com.googlecode.messagefixture.mq.templates.TextMessageTemplate;
import com.ibm.mq.MQC;
import com.ibm.mq.MQException;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;

public class MQService {

	public MessageTemplate putText(String destinationName, TextMessageTemplate message) throws MQException, IOException {
		MQQueueManager qm = new MQQueueManager("QM1");
		
		int oo = MQC.MQOO_OUTPUT;
		MQQueue queue = qm.accessQueue(destinationName, oo);
		
		MQMessage msg = message.toMessage(); 
		queue.put(msg);
		
		message.setMessageID(new String(msg.messageId));
		
		return message;
	}

	public TextMessageTemplate getText(String destinationName) throws MQException, IOException {
		MQQueueManager qm = new MQQueueManager("QM1");
		
		int oo = MQC.MQOO_INPUT_SHARED;
		MQQueue queue = qm.accessQueue(destinationName, oo);
		
		MQMessage msg = new MQMessage();
		queue.get(msg);
		
		TextMessageTemplate message = new TextMessageTemplate(msg);
		
		return message;
	}
}
