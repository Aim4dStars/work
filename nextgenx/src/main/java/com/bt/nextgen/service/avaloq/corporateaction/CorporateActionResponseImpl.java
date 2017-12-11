package com.bt.nextgen.service.avaloq.corporateaction;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;
import com.bt.nextgen.service.integration.corporateaction.CorporateAction;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionResponse;

import java.util.List;

/**
 * This is the main object that holds the list of mapped CorporateAction object.
 * <p/>
 * This class is not used anywhere else but the avaloq call.
 */
@ServiceBean(xpath = "/")
public class CorporateActionResponseImpl extends AvaloqBaseResponseImpl implements CorporateActionResponse {

	// Main head of the document
	@ServiceElementList(xpath = "//data/doc_list/doc", type = CorporateActionImpl.class)
	private List<CorporateAction> corporateActions;

	/**
	 * Return the list of corporate actions
	 *
	 * @return list of corporate actions.
	 * Null if no data or bad mapping.
	 */
	@Override
	public List<CorporateAction> getCorporateActions() {
		return corporateActions;
	}
}
