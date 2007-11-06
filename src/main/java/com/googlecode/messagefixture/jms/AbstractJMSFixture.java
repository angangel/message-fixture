/*
 * Copyright (C) 2007 by Callista Enterprise. All rights reserved.
 * Released under the terms of the GNU General Public License version 2 or later.
 */

package com.googlecode.messagefixture.jms;

import net.servicefixture.ext.pojo.PojoServiceFixture;

public abstract class AbstractJMSFixture extends PojoServiceFixture {

	@Override
	protected Object getPojo() {
		return new JMSService();
	}

    public final void validate(String[] dataCells) {
    	right(currentRow.more.more);
    }
}
