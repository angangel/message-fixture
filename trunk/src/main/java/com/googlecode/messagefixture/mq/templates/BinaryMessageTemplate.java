package com.googlecode.messagefixture.mq.templates;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import net.servicefixture.ServiceFixtureException;

import com.googlecode.messagefixture.MessageConfiguration;
import com.ibm.mq.MQMessage;

public class BinaryMessageTemplate extends MessageTemplate {

	private File msgFile;
	private byte[] data;
	
	
	public BinaryMessageTemplate() {
		setFormat("        ");
	}
	
	public BinaryMessageTemplate(MQMessage msg) throws IOException {
		this();
		
		setCcsid(msg.characterSet);
		setMessageID(new String(msg.messageId));
		
		this.data = new byte[msg.getDataLength()];
		msg.readFully(data);
	}
	
	
	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getFile() {
		return msgFile.getPath();
	}

	public void setFile(String filePath) {
		File filesDir = MessageConfiguration.getInstance().getFilesDirectory();
		this.msgFile = new File(filesDir, filePath);
		
		if(!msgFile.exists()) {
			throw new ServiceFixtureException("File does not exists: " + msgFile.getAbsolutePath());
		}
	}
	
	protected byte[] createData() throws IOException {
		if(data != null) {
			return data;
		} else if(msgFile != null){
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(msgFile));
			
			byte[] b = new byte[(int)msgFile.length()];
			bis.read(b);
			return b;
		} else {
			throw new ServiceFixtureException("Failed to create binary data, no data or file provided");
		}
	}
}
