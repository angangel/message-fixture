package com.googlecode.messagefixture.mq.headers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ibm.mq.MQMessage;

public class MQRFH2 {

    private final static String RFH2_FORMAT = "MQHRF2  ";
    private final static String STRUC_ID = "RFH ";
    private final static int STRUC_LENGTH = 36;
    
    private int version = 2;
    private int encoding = 546;
    private int codedCharSetId = 437;
    private String format = "";
    private int flags = 0;
    private int nameValueCodedCharSetId = 1208;    
    
    private Map<String, Map<String, Object>> folders = new HashMap<String, Map<String, Object>>();

    public MQRFH2() {
        // default cstr
    }

    public MQRFH2(MQMessage msg) throws IOException {
        // create from message
        parseMessage(msg);
    }
    
    private void parseMessage(MQMessage msg) throws IOException {
        if (!RFH2_FORMAT.equals(msg.format)) {
            // no RFH2 header, skip
            return;
        }

        // skip strucid
        msg.seek(4);
        version = msg.readInt();
        int length = msg.readInt();
        encoding = msg.readInt();
        codedCharSetId = msg.readInt();
        format = msg.readString(8);
        flags = msg.readInt();
        nameValueCodedCharSetId = msg.readInt();
        
//         TODO: parse folders
    }
    
    public void toMessage(MQMessage msg) throws IOException {
        int foldersLen = 0;
        List<String> folderStrings = new ArrayList<String>();
        for (Entry<String, Map<String, Object>> entry : folders.entrySet()) {
            String folderName = entry.getKey();
            Map<String, Object> folder = entry.getValue();
            
            String folderString = buildFolder(folderName, folder);
            
            // add to length for prepending length int
            foldersLen += 4;
            foldersLen += folderString.length();
            folderStrings.add(folderString);
        }
        
        msg.seek(0);

        msg.format = RFH2_FORMAT;
        
        msg.writeString(STRUC_ID);
        msg.writeInt(version);
        msg.writeInt(STRUC_LENGTH + foldersLen);
        msg.writeInt(encoding);
        msg.writeInt(codedCharSetId);
        msg.writeString(pad(format, 8));
        msg.writeInt(flags);
        msg.writeInt(nameValueCodedCharSetId);

        for (String folderString : folderStrings) {
            msg.writeInt(folderString.length());
            msg.write(folderString.getBytes("UTF-8"));
        }
    }
    
    private int getPaddedFolderLength(int headerLen)
    {
        return ((headerLen - 1) / 4) * 4;
    }

    private StringBuffer padFolder(StringBuffer sb)
    {
        int folderLen = sb.length();
        int len = getPaddedFolderLength(folderLen);

        for(int i = folderLen; i<len; i++) {
            sb.append(' ');
        }
        return sb;
    }
    
    private String buildFolder(String folderName, Map<String, Object> folder) {
        StringBuffer sb = new StringBuffer();
        sb.append("<").append(folderName).append(">");
        
        for (Entry<String, Object> entry : folder.entrySet()) {
            sb.append("<").append(entry.getKey()).append(">");
            sb.append(entry.getValue().toString());
            sb.append("</").append(entry.getKey()).append(">");
        }
        
        sb.append("</").append(folderName).append(">");
        return padFolder(sb).toString();
    }

    private String pad(String s, int len) {
        StringBuffer sb = new StringBuffer(s);
        for(int i = s.length(); i<len; i++) {
            sb.append(' ');
        }
        return sb.toString();
    }

    
    
    public String getStringProperty(String folderName, String propName) {
        Map<String, Object> folder = folders.get(folderName);
        if(folder != null) {
            return (String) folder.get(propName);
        } else {
            return null;
        }
    }
    
    public void setStringProperty(String folderName, String propName, String value) {
        Map<String, Object> folder = folders.get(folderName);
        if(folder == null) {
            folder = new HashMap<String, Object>();
            folders.put(folderName, folder);
        }
        
        folder.put(propName, value);
    }
    
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getEncoding() {
        return encoding;
    }

    public void setEncoding(int encoding) {
        this.encoding = encoding;
    }

    public int getCodedCharSetId() {
        return codedCharSetId;
    }

    public void setCodedCharSetId(int codedCharSetId) {
        this.codedCharSetId = codedCharSetId;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public int getNameValueCodedCharSetId() {
        return nameValueCodedCharSetId;
    }

    public void setNameValueCodedCharSetId(int nameValueCodedCharSetId) {
        this.nameValueCodedCharSetId = nameValueCodedCharSetId;
    }
}
