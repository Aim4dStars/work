package com.bt.nextgen.service.integration.corporateaction;

import com.bt.nextgen.service.integration.trustee.IrgApprovalStatus;
import com.bt.nextgen.service.integration.trustee.TrusteeApprovalStatus;

public interface CorporateActionApprovalDecision {
	String getOrderNumber();

	TrusteeApprovalStatus getTrusteeApprovalStatus();

	IrgApprovalStatus getIrgApprovalStatus();
}
