/*
 * Copyright (C) 2007 by Callista Enterprise. All rights reserved.
 * Released under the terms of the GNU General Public License version 2 or later.
 */

package com.googlecode.messagefixture.mq.util;

import java.io.IOException;

import com.ibm.mq.MQException;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.pcf.CMQC;
import com.ibm.mq.pcf.CMQCFC;
import com.ibm.mq.pcf.MQCFIN;
import com.ibm.mq.pcf.MQCFST;
import com.ibm.mq.pcf.PCFAgent;
import com.ibm.mq.pcf.PCFParameter;

public class PcfUtil {

	public static void putDisableQueue(MQQueueManager qm, String queueName) throws MQException, IOException, PcfException {
		ableQueue(qm, queueName, CMQC.MQIA_INHIBIT_PUT, CMQC.MQQA_PUT_INHIBITED);
	}
	public static void putEnableQueue(MQQueueManager qm, String queueName) throws MQException, IOException, PcfException {
		ableQueue(qm, queueName, CMQC.MQIA_INHIBIT_PUT, CMQC.MQQA_PUT_ALLOWED);
	}
	public static void getDisableQueue(MQQueueManager qm, String queueName) throws MQException, IOException, PcfException {
		ableQueue(qm, queueName, CMQC.MQIA_INHIBIT_GET, CMQC.MQQA_GET_INHIBITED);
	}
	public static void getEnableQueue(MQQueueManager qm, String queueName) throws MQException, IOException, PcfException {
		ableQueue(qm, queueName, CMQC.MQIA_INHIBIT_GET, CMQC.MQQA_GET_ALLOWED);
	}

	private static void ableQueue(MQQueueManager qm, String queueName,
			int command, int value) throws MQException, IOException, PcfException {
		PCFAgent agent = new PCFAgent(qm);
		
		PcfResponse details = queueDetails(agent, queueName);
		int queueType = details.getIntValue(CMQC.MQIA_Q_TYPE);
		if (queueType != CMQC.MQQT_ALIAS && queueType != CMQC.MQQT_LOCAL) {
			throw new PcfException("Queue type can not be disabled");
		}
		
		PCFParameter[] parameters = new PCFParameter[] {
				new MQCFST(CMQC.MQCA_Q_NAME, queueName),
				new MQCFIN(CMQC.MQIA_Q_TYPE, queueType),
				new MQCFIN(command, value), };
		
		MQMessage[] pcfResponses = agent.send(CMQCFC.MQCMD_CHANGE_Q, parameters);
		PcfResponse response = new PcfResponse(pcfResponses[0]);
		
		// assume only the one response.
		if (!response.isOkay()) {
			throw new PcfException("Failed to disable queue, reasone code:" + response.getReasonCode());
		}
	}

	private static PcfResponse queueDetails(PCFAgent agent, String queueName) {

		PCFParameter[] parameters = new PCFParameter[] { new MQCFST(
				CMQC.MQCA_Q_NAME, queueName), };

		try {
			MQMessage[] pcfResponses = agent.send(CMQCFC.MQCMD_INQUIRE_Q,
					parameters);

			PcfResponse response = new PcfResponse(pcfResponses[0]);
			// assume only the one response.
			return response;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
