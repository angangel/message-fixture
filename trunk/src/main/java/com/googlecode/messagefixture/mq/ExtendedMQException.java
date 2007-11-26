package com.googlecode.messagefixture.mq;

import com.ibm.mq.MQException;

public class ExtendedMQException extends MQException {

	public ExtendedMQException(MQException e) {
		super(e.completionCode, e.reasonCode, e.exceptionSource);
	}
	
	public int getReasonCode() {
		return reasonCode;
	}
	
}
