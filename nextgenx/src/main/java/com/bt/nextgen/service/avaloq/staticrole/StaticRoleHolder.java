package com.bt.nextgen.service.avaloq.staticrole;

import java.util.List;
import java.util.Map;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;

@ServiceBean(xpath="/")
public class StaticRoleHolder extends AvaloqBaseResponseImpl
{
	@ServiceElementList(xpath="//data/user_list/user", type=StaticFuntionalRole.class)
	private List<StaticFuntionalRole> staticFunctionalRoleList;

	public List <StaticFuntionalRole> getStaticFunctionalRoleList()
	{
		return staticFunctionalRoleList;
	}

	public void setStaticFunctionalRoleList(List <StaticFuntionalRole> staticFunctionalRoleList)
	{
		this.staticFunctionalRoleList = staticFunctionalRoleList;
	}
}
