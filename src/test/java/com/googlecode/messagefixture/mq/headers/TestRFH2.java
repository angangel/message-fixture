package com.googlecode.messagefixture.mq.headers;

import com.ibm.mq.MQC;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;

public class TestRFH2 {

    public static void main(String[] args) throws Exception {
        MQQueueManager qm = new MQQueueManager("QM1");
        int oo = MQC.MQOO_OUTPUT + MQC.MQOO_INPUT_SHARED;
        MQQueue q = qm.accessQueue("Q1", oo);
        
        MQMessage msg = new MQMessage();
        MQRFH2 rfh2 = new MQRFH2();
        rfh2.setStringProperty("mcd", "Msd", "mrm");
        rfh2.setStringProperty("usr", "roffe", "foo");
        rfh2.setStringProperty("usr", "kalle", "rune");
        rfh2.toMessage(msg);
        q.put(msg);
    }
}
