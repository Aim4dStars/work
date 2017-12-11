package com.bt.nextgen.service.group.customer.groupesb.maintainarrangementandiparrangementrelationships;

import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;

import au.com.westpac.gn.common.xsd.identifiers.v1.AccountArrangementIdentifier;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.common.xsd.v1.Brand;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.common.xsd.v1.FinancialTransactionCard;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.common.xsd.v1.MaintenanceAuditContext;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.common.xsd.v1.Product;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.common.xsd.v1.ProductArrangement;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainarrangementandiparrangementrelationships.v1.svc0256.ActionCode;

public class ArrangementAndRelationshipManagementRequest {

	private ActionCode requestedAction;
	private List<ProductArrangement> arrangement;
	private List<FinancialTransactionCard> financialTransactionCard;
	public ActionCode getRequestedAction() {
		return requestedAction;
	}
	public void setRequestedAction(ActionCode requestedAction) {
		this.requestedAction = requestedAction;
	}
	public List<ProductArrangement> getArrangement() {
		return arrangement;
	}
	public void setArrangement(List<ProductArrangement> arrangement) {
		this.arrangement = arrangement;
	}
	public List<FinancialTransactionCard> getFinancialTransactionCard() {
		return financialTransactionCard;
	}
	public void setFinancialTransactionCard(
			List<FinancialTransactionCard> financialTransactionCard) {
		this.financialTransactionCard = financialTransactionCard;
	}
}
