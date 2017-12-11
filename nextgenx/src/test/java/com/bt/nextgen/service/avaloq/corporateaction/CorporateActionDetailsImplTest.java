package com.bt.nextgen.service.avaloq.corporateaction;

import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionHelperImpl;
import com.bt.nextgen.api.corporateaction.v1.service.EffectiveCorporateActionType;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionConverterConstants;
import com.bt.nextgen.service.avaloq.account.PensionAccountDetailImpl;
import com.bt.nextgen.service.avaloq.account.PensionType;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.corporateaction.*;
import com.bt.nextgen.service.integration.trustee.TrusteeApprovalStatus;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionDetailsImplTest {

	private CorporateActionDetailsImpl details;

	@Before
	public void setup() {
		details = new CorporateActionDetailsImpl();
	}

	@Test
	public void test_modelGettersAndSetters()
	{
		details.setCorporateActionStatus(CorporateActionStatus.OPEN);
		details.setCloseDate(DateTime.parse("2017-01-01"));
		details.setLastUpdatedDate(DateTime.parse("2017-01-01"));
		details.setPayDate(DateTime.parse("2017-01-01"));
		details.setRecordDate(DateTime.parse("2017-01-01"));
		details.setExDate(DateTime.parse("2017-01-01"));
		details.setOfferDocumentUrl("http://this.is.correct/");
		CorporateActionCascadeOrderImpl cascadeOrder = new CorporateActionCascadeOrderImpl();
		List<CorporateActionCascadeOrder> cascadeOrders = new ArrayList<>();
		cascadeOrder.setOrderNumber("123");
		cascadeOrder.setCorporateActionStatus(CorporateActionStatus.OPEN);
		cascadeOrder.setCorporateActionType(CorporateActionType.BUY_BACK.getCode());
		cascadeOrders.add(cascadeOrder);
		details.setCascadeOrders(cascadeOrders);
		details.setTrusteeApprovalStatus(TrusteeApprovalStatus.APPROVED);
		details.setEarlyClose("1");

		Assert.assertEquals(CorporateActionStatus.OPEN, details.getCorporateActionStatus());
		Assert.assertEquals(DateTime.parse("2017-01-01"), details.getCloseDate());
		Assert.assertEquals(DateTime.parse("2017-01-01"), details.getLastUpdatedDate());
		Assert.assertEquals(DateTime.parse("2017-01-01"), details.getPayDate());
		Assert.assertEquals(DateTime.parse("2017-01-01"), details.getRecordDate());
		Assert.assertEquals(DateTime.parse("2017-01-01"), details.getExDate());
		Assert.assertEquals("http://this.is.correct/", details.getOfferDocumentUrl());
		Assert.assertNotNull(details.getCascadeOrders());
		Assert.assertEquals(1, details.getCascadeOrders().size());
		Assert.assertEquals(TrusteeApprovalStatus.APPROVED, details.getTrusteeApprovalStatus());
		Assert.assertEquals(true, details.isEarlyClose());
		details.setEarlyClose("0");
		Assert.assertEquals(false, details.isEarlyClose());
		details.setEarlyClose(null);
		Assert.assertEquals(false, details.isEarlyClose());

		CorporateActionCascadeOrder order = details.getCascadeOrders().get(0);
		Assert.assertNotNull(order);
		Assert.assertEquals("123", order.getOrderNumber());
		Assert.assertEquals(CorporateActionStatus.OPEN, order.getCorporateActionStatus());
		Assert.assertEquals(CorporateActionType.BUY_BACK.getCode(), order.getCorporateActionType());
	}
}
