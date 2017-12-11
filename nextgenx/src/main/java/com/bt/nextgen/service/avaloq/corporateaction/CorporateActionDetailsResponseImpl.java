package com.bt.nextgen.service.avaloq.corporateaction;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetailsResponse;

import java.util.List;

/**
 * This is the main object that holds the list of mapped CorporateActionSummary object.
 * <p/>
 * This class is not used anywhere else but in the avaloq call.
 */
@ServiceBean(xpath = "/")
public class CorporateActionDetailsResponseImpl extends AvaloqBaseResponseImpl implements CorporateActionDetailsResponse {

	// Main head of the document
	@ServiceElementList(xpath = "//data/doc_list/doc", type = CorporateActionDetailsImpl.class)
	private List<CorporateActionDetails> corporateActionDetailsList;

	/**
	 * Return the list of corporate action summary.  The should be only two items due to the way the XML
	 * is structured.  The 1st contain summary information, plus a list of "options".  The 2nd contain default
	 * options.
	 *
	 * @return list of corporate action summary.
	 * Null if no data or bad mapping.
	 */
	@Override
	public List<CorporateActionDetails> getCorporateActionDetailsList() {
		return corporateActionDetailsList;
	}

	public void setCorporateActionDetailsList(
			List<CorporateActionDetails> corporateActionDetailsList) {
		this.corporateActionDetailsList = corporateActionDetailsList;
	}
}
