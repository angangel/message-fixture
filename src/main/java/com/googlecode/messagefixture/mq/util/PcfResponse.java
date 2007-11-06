/*
 * Copyright (C) 2007 by Callista Enterprise. All rights reserved.
 * Released under the terms of the GNU General Public License version 2 or later.
 */

package com.googlecode.messagefixture.mq.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.ibm.mq.MQException;
import com.ibm.mq.MQMessage;
import com.ibm.mq.pcf.MQCFH;
import com.ibm.mq.pcf.PCFParameter;

public class PcfResponse  {

	private static final long serialVersionUID = 8403878755048417131L;
	
	private Map<Integer, Object> properties = new HashMap<Integer, Object>();

	private int reasonCode;

	public PcfResponse(MQMessage message) throws MQException, IOException {
		MQCFH cfh = new MQCFH(message);
		reasonCode = cfh.reason;

		if (isOkay()) {
			for (int i = 0; i < cfh.parameterCount; i++) {
				PCFParameter p = PCFParameter.nextParameter(message);
				properties.put(new Integer(p.getParameter()), p.getValue());
			}
		}
	}

	public boolean isOkay() {
		return reasonCode == 0;
	}

	public int getReasonCode() {
		return reasonCode;
	}

	public int getIntValue(int key) {
		return ((Integer) properties.get(new Integer(key))).intValue();
	}

	public int[] getIntArray(int key) {
		return (int[]) properties.get(new Integer(key));
	}

	public String getStringValue(int key) {
		return ((String) properties.get(new Integer(key))).trim();
	}

	public String[] getStringArray(int key) {
		String[] paddedStrings = (String[]) properties.get(new Integer(key));
		String[] result = new String[paddedStrings.length];
		for (int i = 0; i < paddedStrings.length; i++) {
			result[i] = paddedStrings[i].trim();
		}
		return result;
	}
}
