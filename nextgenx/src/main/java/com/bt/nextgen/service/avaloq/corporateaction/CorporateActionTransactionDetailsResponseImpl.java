package com.bt.nextgen.service.avaloq.corporateaction;

import java.util.List;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionTransactionDetailsResponse;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionTransactionDetails;

/**
 * This is the main object that holds the list of mapped CorporateActionAccount object.
 * <p/>
 * This class is not used anywhere else but in the avaloq call.
 */

@ServiceBean(xpath = "/")
public class CorporateActionTransactionDetailsResponseImpl extends AvaloqBaseResponseImpl implements CorporateActionTransactionDetailsResponse {
	// Main head of the document
	@ServiceElementList(xpath = "//data/pos_list/pos", type = CorporateActionTransactionDetailsImpl.class)
	
	private List<CorporateActionTransactionDetails> corporateactionsTransDetails;
	/**
	 * Return the list of corporate action accounts
	 *
	 * @return list of corporate action accounts
	 * Null if no data or bad mapping.
	 */

	@Override
	public List<CorporateActionTransactionDetails> getCorporateActionTransactionDetails() {
		return corporateactionsTransDetails;
	}
}
