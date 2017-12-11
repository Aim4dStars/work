package com.bt.nextgen.corporateaction.service;

import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDtoParams;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListDtoKey;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionConverter;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionDirectAccountService;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionListDtoServiceBaseImpl;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionListWithMetadataDtoServiceImpl;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionServices;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionGroup;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionStatus;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionType;
import com.bt.nextgen.service.integration.trustee.TrusteeApprovalStatus;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionListWithMetadataDtoServiceImplTest {
	@InjectMocks
	private CorporateActionListWithMetadataDtoServiceImpl corporateActionListWithMetadataDtoServiceImpl;

	@Mock
	private CorporateActionListDtoServiceBaseImpl corporateActionListDtoServiceBaseImpl;

	@Mock
	private CorporateActionConverter converter;

	@Mock
	private UserProfileService userProfileService;

	@Mock
	private CorporateActionDirectAccountService corporateActionDirectAccountService;

	@Mock
	private CorporateActionServices corporateActionServices;

	private Asset trusteeCorporateActionAsset;

	private CorporateActionDtoParams params;

	@Before
	public void setup() {
		trusteeCorporateActionAsset = Mockito.mock(Asset.class);
		Mockito.when(trusteeCorporateActionAsset.getAssetCode()).thenReturn("AMA");
		Mockito.when(trusteeCorporateActionAsset.getAssetName()).thenReturn("AMA Group Limited");

		params = new CorporateActionDtoParams();
		params.setCloseDate(new DateTime());
		params.setAnnouncementDate(new DateTime());
		params.setAsset(trusteeCorporateActionAsset);
		params.setCorporateActionType("SHARE_PURCHASE_PLAN");
		params.setCorporateActionTypeDescription("Share Purchase Plan");
		params.setEligible(null);
		params.setUnconfirmed(null);
		params.setStatus(CorporateActionStatus.CLOSED);
		params.setHoldingLimitPercent(null);
		params.setTrusteeApprovalStatus(TrusteeApprovalStatus.PENDING);
		params.setTrusteeApprovalStatusDate(new DateTime());
		params.setTrusteeApprovalUserId("27090");
		params.setTrusteeApprovalUserName("Boris Yeltsin - SUPER");
		params.setPayDate(new DateTime());

		when(corporateActionServices.
				loadVoluntaryCorporateActionsForIm(
						any(String.class),
						any(DateTime.class),
						any(DateTime.class),
						any(String.class),
						any(ServiceErrors.class))).thenReturn(null);

		when(corporateActionServices.
				loadMandatoryCorporateActionsForIm(
						any(String.class),
						any(DateTime.class),
						any(DateTime.class),
						any(String.class),
						any(ServiceErrors.class))).thenReturn(null);
	}

	@Test
	public void testFind_DealerGroupVoluntary()
	{
		Assert.assertEquals(CorporateActionGroup.VOLUNTARY, CorporateActionType.SHARE_PURCHASE_PLAN.getGroup());

		when(userProfileService.isDealerGroup()).thenReturn(true);
		when(userProfileService.isInvestmentManager()).thenReturn(false);
		when(userProfileService.isInvestor()).thenReturn(false);

		test_find(CorporateActionType.SHARE_PURCHASE_PLAN.getGroup());
	}

	@Test
	public void testFind_DealerGroupMandatory()
	{
		Assert.assertEquals(CorporateActionGroup.MANDATORY, CorporateActionType.BUY_BACK_MANDATORY.getGroup());

		when(userProfileService.isDealerGroup()).thenReturn(true);
		when(userProfileService.isInvestmentManager()).thenReturn(false);
		when(userProfileService.isInvestor()).thenReturn(false);

		test_find(CorporateActionType.BUY_BACK_MANDATORY.getGroup());
	}

	@Test
	public void testFind_InvestmentManagerVoluntary()
	{
		Assert.assertEquals(CorporateActionGroup.VOLUNTARY, CorporateActionType.SHARE_PURCHASE_PLAN.getGroup());

		when(userProfileService.isDealerGroup()).thenReturn(false);
		when(userProfileService.isInvestmentManager()).thenReturn(true);
		when(userProfileService.isInvestor()).thenReturn(false);

		test_find(CorporateActionType.SHARE_PURCHASE_PLAN.getGroup());
	}

	@Test
	public void testFind_InvestmentManagerMandatory()
	{
		Assert.assertEquals(CorporateActionGroup.MANDATORY, CorporateActionType.BUY_BACK_MANDATORY.getGroup());

		when(userProfileService.isDealerGroup()).thenReturn(false);
		when(userProfileService.isInvestmentManager()).thenReturn(true);
		when(userProfileService.isInvestor()).thenReturn(false);

		test_find(CorporateActionType.BUY_BACK_MANDATORY.getGroup());
	}

	@Test
	public void testFind_InvestorVoluntary() {
		Assert.assertEquals(CorporateActionGroup.VOLUNTARY, CorporateActionType.SHARE_PURCHASE_PLAN.getGroup());

		when(userProfileService.isDealerGroup()).thenReturn(false);
		when(userProfileService.isInvestmentManager()).thenReturn(false);
		when(userProfileService.isInvestor()).thenReturn(true);

		test_find(CorporateActionType.SHARE_PURCHASE_PLAN.getGroup());
	}

	@Test
	public void testFind_InvestorMandatory() {
		Assert.assertEquals(CorporateActionGroup.MANDATORY, CorporateActionType.BUY_BACK_MANDATORY.getGroup());

		when(userProfileService.isDealerGroup()).thenReturn(false);
		when(userProfileService.isInvestmentManager()).thenReturn(false);
		when(userProfileService.isInvestor()).thenReturn(true);

		test_find(CorporateActionType.BUY_BACK_MANDATORY.getGroup());
	}

	@Test
	public void testFind_InvestorVoluntaryRealAccount() {
		Assert.assertEquals(CorporateActionGroup.VOLUNTARY, CorporateActionType.SHARE_PURCHASE_PLAN.getGroup());

		when(userProfileService.isDealerGroup()).thenReturn(false);
		when(userProfileService.isInvestmentManager()).thenReturn(false);
		when(userProfileService.isInvestor()).thenReturn(true);

		ServiceErrors serviceErrors = new ServiceErrorsImpl();

		CorporateActionListDtoKey nullDatesKey = new CorporateActionListDtoKey(null, null, CorporateActionType.SHARE_PURCHASE_PLAN.getGroup().getCode(), EncodedString.fromPlainText("1").toString(), null);
		CorporateActionListDto caListDtoResult = corporateActionListWithMetadataDtoServiceImpl.find(nullDatesKey, serviceErrors);
		Assert.assertNull(caListDtoResult);
	}

	@Test
	public void testFind_AdviserVoluntary() {
		Assert.assertEquals(CorporateActionGroup.VOLUNTARY, CorporateActionType.SHARE_PURCHASE_PLAN.getGroup());

		when(userProfileService.isDealerGroup()).thenReturn(false);
		when(userProfileService.isInvestmentManager()).thenReturn(false);
		when(userProfileService.isInvestor()).thenReturn(false);

		test_find(CorporateActionType.SHARE_PURCHASE_PLAN.getGroup());
	}

	@Test
	public void testFind_InvestorAdviser() {
		Assert.assertEquals(CorporateActionGroup.MANDATORY, CorporateActionType.BUY_BACK_MANDATORY.getGroup());

		when(userProfileService.isDealerGroup()).thenReturn(false);
		when(userProfileService.isInvestmentManager()).thenReturn(false);
		when(userProfileService.isInvestor()).thenReturn(false);

		test_find(CorporateActionType.BUY_BACK_MANDATORY.getGroup());
	}

	private void test_find(CorporateActionGroup corporateActionGroup)
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();

		CorporateActionListDtoKey nullDatesKey = new CorporateActionListDtoKey(null, null, corporateActionGroup.getCode(),"", "");
		CorporateActionListDto caListDtoResult = corporateActionListWithMetadataDtoServiceImpl.find(nullDatesKey, serviceErrors);
		Assert.assertNull(caListDtoResult);

		CorporateActionListDtoKey datesKey = new CorporateActionListDtoKey("", "", corporateActionGroup.getCode(),"", "");
		caListDtoResult = corporateActionListWithMetadataDtoServiceImpl.find(datesKey, serviceErrors);
		Assert.assertNull(caListDtoResult);
	}
}
