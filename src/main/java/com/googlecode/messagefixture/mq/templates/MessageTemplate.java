/*
 * Copyright (C) 2007 by Callista Enterprise. All rights reserved.
 * Released under the terms of the GNU General Public License version 2 or later.
 */

package com.googlecode.messagefixture.mq.templates;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.ibm.mq.MQMessage;

public abstract class MessageTemplate {

	private Map<String, String> map = new HashMap<String, String>();
	private int ccsid = 819;
	private String messageID;
	private String format;
	
	protected abstract byte[] createData() throws IOException;
	
	private String pad(String s, int len) {
		StringBuffer sb = new StringBuffer(s);
		for(int i = s.length(); i<len; i++) {
			sb.append(' ');
		}
		return sb.substring(0, 8);
	}
	
	public MQMessage toMessage() throws IOException {
		MQMessage msg = new MQMessage();
		
		msg.write(createData());

		msg.characterSet = ccsid;
		
		if(format != null) {
			msg.format = pad(format, 8);
		}
		return msg;
	}

	public Map<String, String> getMap() {
		return map;
	}

	public void setMap(Map<String, String> map) {
		this.map = map;
	}

	public int getCcsid() {
		return ccsid;
	}

	public void setCcsid(int ccsid) {
		this.ccsid = ccsid;
	}

	public String getMessageID() {
		return messageID;
	}

	public void setMessageID(String messageID) {
		this.messageID = messageID;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

}
