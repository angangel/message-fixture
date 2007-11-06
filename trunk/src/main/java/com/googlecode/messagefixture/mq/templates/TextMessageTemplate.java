/*
 * Copyright (C) 2007 by Callista Enterprise. All rights reserved.
 * Released under the terms of the GNU General Public License version 2 or later.
 */

package com.googlecode.messagefixture.mq.templates;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.ibm.mq.MQMessage;

public class TextMessageTemplate extends MessageTemplate {

	private String text;

	public TextMessageTemplate() {
		setFormat("MQSTR");
	}
	
	public TextMessageTemplate(MQMessage msg) throws IOException {
		this();
		
		setCcsid(msg.characterSet);
		setMessageID(new String(msg.messageId));
		
		byte[] data = new byte[msg.getDataLength()];
		msg.readFully(data);
		
		text = new String(data, "Cp" + msg.characterSet);
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	protected byte[] createData() throws UnsupportedEncodingException {
		if(text != null) {
			return text.getBytes("Cp" + getCcsid());
		} else {
			return new byte[0];
		}
	}
}
