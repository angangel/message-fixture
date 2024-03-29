/*
 * Copyright (C) 2007 by Callista Enterprise. All rights reserved.
 * Released under the terms of the GNU General Public License version 2 or later.
 */

package com.googlecode.messagefixture.jms.templates;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.servicefixture.ServiceFixtureException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.googlecode.messagefixture.MessageConfiguration;
import com.googlecode.messagefixture.util.IOUtil;

public class TextMessageTemplate extends MessageTemplate {

	public static final String LINEENDING_CRLF = "CRLF";
	public static final String LINEENDING_LF = "LF";
	public static final String LINEENDING_NONE = "none";
	
	public static final char[] CRLF = new char[]{'\r', '\n'};
	public static final char[] LF = new char[]{'\n'};

	private String eol = LINEENDING_CRLF;
	private String eom = LINEENDING_NONE;
	private File msgFile;
	private String text;
	private List<String> lines = new ArrayList<String>();

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public Map getXml() throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		// TODO revisit
		dbf.setNamespaceAware(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new InputSource(new StringReader(text)));
		
		return new DocumentConverter().documentToMap(doc);
	}

	public void setXml(Document xml) {
		throw new ServiceFixtureException("Setting XML not yet supported");
		
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
		if(eo.equalsIgnoreCase(LINEENDING_LF)) {
			return LF;
		} else if(eo.equalsIgnoreCase(LINEENDING_NONE)) {
			return new char[0];
		} else {
			return CRLF;
		}
	}
	
	public Message toMessage(Session session) throws JMSException, IOException {
		StringBuffer sb = new StringBuffer();
		
		if(text != null) {
			sb.append(text);
		} else if(msgFile != null) {
			sb.append(IOUtil.readFull(msgFile));
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

	public boolean mask(String expected, String maskCharacter){
		char maskChar = maskCharacter.charAt(0);
		
		if(expected.length()!=text.length())
			return false;
		
		for(int pos=0;pos<text.length();pos++){
			char c = expected.charAt(pos); 
			if(c!=maskChar){
				if(c!=text.charAt(pos)){
					return false;
				}
			}
		}
		return true;
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

	public String getFile() {
		return msgFile.getAbsolutePath();
	}

	public void setFile(String filePath) {
		File filesDir = MessageConfiguration.getInstance().getFilesDirectory();
		this.msgFile = new File(filesDir, filePath);
		
		if(!msgFile.exists()) {
			throw new ServiceFixtureException("File does not exists: " + msgFile.getAbsolutePath());
		}

	}
}
