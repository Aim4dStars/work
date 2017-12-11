package com.bt.nextgen.service.avaloq.corporateaction;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccountResponse;

import java.util.List;

/**
 * This is the main object that holds the list of mapped CorporateActionAccount object.
 * <p/>
 * This class is not used anywhere else but in the avaloq call.
 */

@ServiceBean(xpath = "/")
public class CorporateActionAccountResponseImpl extends AvaloqBaseResponseImpl implements CorporateActionAccountResponse {
	// Main head of the document
	// TODO: proper mapping for portfolio model
	@ServiceElementList(xpath = "//data/trig_pos_list/trig_pos", type = CorporateActionAccountImpl.class)
	private List<CorporateActionAccount> corporateActionAccounts;

	/**
	 * Return the list of corporate action accounts
	 *
	 * @return list of corporate action accounts
	 * Null if no data or bad mapping.
	 */

	@Override
	public List<CorporateActionAccount> getCorporateActionAccounts() {
		return corporateActionAccounts;
	}

	/**
	 * @param corporateActionAccounts the corporateactionsAccounts to set
	 */
	public void setCorporateActionAccounts(List<CorporateActionAccount> corporateActionAccounts) {
		this.corporateActionAccounts = corporateActionAccounts;
	}
}
