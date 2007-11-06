/*
 * Copyright (C) 2007 by Callista Enterprise. All rights reserved.
 * Released under the terms of the GNU General Public License version 2 or later.
 */

package com.googlecode.messagefixture.mq;

import net.servicefixture.ext.pojo.PojoServiceFixture;

public abstract class AbstractMQFixture extends PojoServiceFixture {

	@Override
	protected Object getPojo() {
		return new MQService();
	}
}
