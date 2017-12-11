package com.bt.nextgen.service.avaloq.installation.impl;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.avaloq.installation.AvaloqReleasePackage;

@ServiceBean(xpath="/", type= ServiceBeanType.CONCRETE)
public class AvaloqReleaseHolder
{

	@ServiceElement(xpath="release",type=AvaloqReleasePackageImpl.class )
	private AvaloqReleasePackage releasePackage;

	public AvaloqReleasePackage getReleasePackage(){
		return this.releasePackage;
	}
}
