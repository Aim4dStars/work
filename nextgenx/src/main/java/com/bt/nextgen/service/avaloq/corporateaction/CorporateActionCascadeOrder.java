package com.bt.nextgen.service.avaloq.corporateaction;

import javax.validation.constraints.NotNull;

import com.bt.nextgen.service.integration.corporateaction.CorporateActionStatus;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionType;

/**
 * Corporate action interface.  Implementation: CorporateActionImpl
 */
public interface CorporateActionCascadeOrder {
	/**
	 * The CA unique cascade order number
	 *
	 * @return order number
	 */
	@NotNull
	String getOrderNumber();

	/**
	 * The corporate action status.  Open, close or pending.
	 *
	 * @return corporate action status enum object
	 */
	CorporateActionStatus getCorporateActionStatus();

	/**
	 * The corporate action type.  If this is a multi-block
	 *
	 * @return corporate action type enum object
	 */
	String getCorporateActionType();
}

