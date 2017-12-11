package com.bt.nextgen.service.avaloq.installation.impl;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.avaloq.installation.AvaloqChange;

@ServiceBean(xpath="/", type= ServiceBeanType.CONCRETE)
public class AvaloqChangeHolder
{
	@ServiceElement(xpath="chg", type=AvaloqChangeImpl.class)
	private AvaloqChange change;

	public AvaloqChange getChange()
	{
		return this.change;
	}


}
