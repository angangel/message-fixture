package com.googlecode.messagefixture.jms.templates;

import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

public class TextMessageTemplate extends MessageTemplate {

	public static final String LINENDING_CRLF = "CRLF";
	public static final String LINENDING_LF = "LF";
	public static final String LINENDING_NONE = "none";
	
	public static final char[] CRLF = new char[]{'\r', '\n'};
	public static final char[] LF = new char[]{'\n'};

	private String eol = LINENDING_CRLF;
	private String eom = LINENDING_NONE;
	private String text;
	private List<String> lines = new ArrayList<String>();

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getLine() {
		if(lines.size() == 0) {
			return null;
		} else {
			return lines.get(lines.size() - 1);
		}
	}
	
	public void setLine(String line) {
		lines.add(line);
	}
	
	private char[] createLineEnding(String eo) {
		if(eo.equalsIgnoreCase(LINENDING_LF)) {
			return LF;
		} else if(eo.equalsIgnoreCase(LINENDING_NONE)) {
			return new char[0];
		} else {
			return CRLF;
		}
	}
	
	public Message toMessage(Session session) throws JMSException {
		StringBuffer sb = new StringBuffer();
		
		if(text != null) {
			sb.append(text);
		} else {
			for (int i = 0; i<lines.size(); i++) {
				String line = lines.get(i);
				sb.append(line);
				
				if(i != lines.size() - 1) {
					sb.append(createLineEnding(eol));
				} else {
					sb.append(createLineEnding(eom));
				}
			}
		}
		
		TextMessage message = session.createTextMessage(sb.toString());
		populateMessage(message);
		return message;
	}

	public String getEol() {
		return eol;
	}

	public void setEol(String eol) {
		this.eol = eol;
	}

	public String getEom() {
		return eom;
	}

	public void setEom(String eom) {
		this.eom = eom;
	}
}
