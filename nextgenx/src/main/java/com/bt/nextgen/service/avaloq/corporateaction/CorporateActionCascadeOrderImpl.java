package com.bt.nextgen.service.avaloq.corporateaction;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionStatus;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionType;

@ServiceBean(xpath = "secevt2_struct_list")
public class CorporateActionCascadeOrderImpl implements CorporateActionCascadeOrder {
	@ServiceElement(xpath = "casc_doc_nr/val")
	private String orderNumber;

	@ServiceElement(xpath = "casc_ui_status_id/val", converter = CorporateActionStatusConverter.class)
	private CorporateActionStatus corporateActionStatus;

	@ServiceElement(xpath = "casc_ui_aft_id/val")
	private String corporateActionType;

	@Override
	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	@Override
	public CorporateActionStatus getCorporateActionStatus() {
		return corporateActionStatus;
	}

	public void setCorporateActionStatus(CorporateActionStatus corporateActionStatus) {
		this.corporateActionStatus = corporateActionStatus;
	}

	@Override
	public String getCorporateActionType() {
		return corporateActionType;
	}

	public void setCorporateActionType(String corporateActionType) {
		this.corporateActionType = corporateActionType;
	}
}
