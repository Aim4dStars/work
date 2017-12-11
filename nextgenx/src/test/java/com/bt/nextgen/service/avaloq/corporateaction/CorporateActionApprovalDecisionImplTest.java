package com.bt.nextgen.service.avaloq.corporateaction;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionResponseCode;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionApprovalDecision;
import com.bt.nextgen.service.integration.trustee.TrusteeApprovalStatus;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionApprovalDecisionImplTest {

	private List<CorporateActionApprovalDecision> decisionList;
	private CorporateActionApprovalDecisionImpl trusteeCorporateActionDecision;
	private CorporateActionApprovalDecisionGroupImpl trusteeCorporateActionDecisionGroup1;
	private CorporateActionApprovalDecisionGroupImpl trusteeCorporateActionDecisionGroup2;
	private CorporateActionApprovalDecisionGroupImpl trusteeCorporateActionDecisionGroup3;
	private CorporateActionApprovalDecisionGroupImpl trusteeCorporateActionDecisionGroup4;

	@Before
	public void setup() {
		trusteeCorporateActionDecision =
				new CorporateActionApprovalDecisionImpl("123", null, TrusteeApprovalStatus.APPROVED);

		decisionList = new ArrayList<>();
		decisionList.add(trusteeCorporateActionDecision);

		trusteeCorporateActionDecisionGroup1 =
				new CorporateActionApprovalDecisionGroupImpl();
		trusteeCorporateActionDecisionGroup2 =
				new CorporateActionApprovalDecisionGroupImpl(CorporateActionResponseCode.SUCCESS);
		trusteeCorporateActionDecisionGroup3 =
				new CorporateActionApprovalDecisionGroupImpl(CorporateActionResponseCode.SUCCESS, decisionList);
		trusteeCorporateActionDecisionGroup4 =
				new CorporateActionApprovalDecisionGroupImpl(decisionList);
	}

	@Test
	public void testGettersAndSetters() {
		Assert.assertNotNull(trusteeCorporateActionDecision);
		Assert.assertNotNull(trusteeCorporateActionDecision.getOrderNumber());
		Assert.assertNotNull(trusteeCorporateActionDecision.getTrusteeApprovalStatus());
		Assert.assertEquals("123", trusteeCorporateActionDecision.getOrderNumber());
		trusteeCorporateActionDecision.setOrderNumber("456");
		Assert.assertEquals("456", trusteeCorporateActionDecision.getOrderNumber());
		Assert.assertEquals(TrusteeApprovalStatus.APPROVED, trusteeCorporateActionDecision.getTrusteeApprovalStatus());
		trusteeCorporateActionDecision.setTrusteeApprovalStatus(TrusteeApprovalStatus.DECLINED);
		Assert.assertEquals(TrusteeApprovalStatus.DECLINED, trusteeCorporateActionDecision.getTrusteeApprovalStatus());
	}

	@Test
	public void testGroupGettersAndSetters() {
		Assert.assertNotNull(trusteeCorporateActionDecisionGroup1);
		Assert.assertNotNull(trusteeCorporateActionDecisionGroup2);
		Assert.assertNotNull(trusteeCorporateActionDecisionGroup3);

		trusteeCorporateActionDecisionGroup1.setResponseCode(CorporateActionResponseCode.SUCCESS);
		Assert.assertEquals(CorporateActionResponseCode.SUCCESS, trusteeCorporateActionDecisionGroup1.getResponseCode());

		Assert.assertEquals(CorporateActionResponseCode.SUCCESS, trusteeCorporateActionDecisionGroup2.getResponseCode());
		trusteeCorporateActionDecisionGroup2.setCorporateActionApprovalDecisions(decisionList);
		Assert.assertNotNull(trusteeCorporateActionDecisionGroup2.getCorporateActionApprovalDecisions());
		Assert.assertEquals(1, trusteeCorporateActionDecisionGroup2.getCorporateActionApprovalDecisions().size());

		Assert.assertNotNull(trusteeCorporateActionDecisionGroup3.getCorporateActionApprovalDecisions());
		Assert.assertEquals(CorporateActionResponseCode.SUCCESS, trusteeCorporateActionDecisionGroup3.getResponseCode());
		Assert.assertEquals(1, trusteeCorporateActionDecisionGroup3.getCorporateActionApprovalDecisions().size());

		Assert.assertNotNull(trusteeCorporateActionDecisionGroup4.getCorporateActionApprovalDecisions());
		Assert.assertEquals(1, trusteeCorporateActionDecisionGroup4.getCorporateActionApprovalDecisions().size());
	}
}

