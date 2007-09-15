package com.googlecode.messagefixture.mq;

import net.servicefixture.ext.pojo.PojoServiceFixture;

public abstract class AbstractMQFixture extends PojoServiceFixture {

	@Override
	protected Object getPojo() {
		return new MQService();
	}
}
