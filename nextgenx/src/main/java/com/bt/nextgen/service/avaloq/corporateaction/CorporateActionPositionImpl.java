package com.bt.nextgen.service.avaloq.corporateaction;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionPosition;

@ServiceBean(xpath = "pos")
public class CorporateActionPositionImpl implements CorporateActionPosition {
	@ServiceElement(xpath = "pos_id/val")
	private String id;

	private ContainerType containerType;

	public CorporateActionPositionImpl() {
	}

	public CorporateActionPositionImpl(String id) {
		this.id = id;
	}

	public CorporateActionPositionImpl(String id, ContainerType containerType) {
		this.id = id;
		this.containerType = containerType;
	}

	@Override
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ContainerType getContainerType() {
		return containerType;
	}

	public void setContainerType(ContainerType containerType) {
		this.containerType = containerType;
	}
}
