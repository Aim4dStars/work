package com.bt.nextgen.corporateaction.service;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDetailsBaseDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDtoKey;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionResponseCode;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSavedDetails;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionPortfolioModelDto;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionAccountDetailsDtoService;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionCommonService;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionDetailsDtoServiceImpl;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionHelper;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionPersistenceDtoService;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionServices;
import com.bt.nextgen.api.corporateaction.v1.service.EffectiveCorporateActionType;
import com.bt.nextgen.api.corporateaction.v1.service.ImCorporateActionPortfolioModelDtoService;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionConverterFactory;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionResponseConverterService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionGroup;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOfferType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionStatus;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionType;
import com.bt.nextgen.service.integration.trustee.TrusteeApprovalStatus;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionDetailsDtoServiceImplTest {
    @InjectMocks
    private CorporateActionDetailsDtoServiceImpl corporateActionDetailsDtoServiceImpl;

    @Mock
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetIntegrationService;

    @Mock
    private CorporateActionHelper helper;

    @Mock
    private CorporateActionAccountDetailsDtoService corporateActionAccountDetails;

    @Mock
    private CorporateActionPersistenceDtoService corporateActionPersistenceDtoService;

    @Mock
    private CorporateActionCommonService corporateActionCommonService;

    @Mock
    private CorporateActionConverterFactory corporateActionConverterFactory;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private ImCorporateActionPortfolioModelDtoService imCorporateActionPortfolioModelDtoService;

    @Mock
    private CorporateActionServices corporateActionServices;

    @Mock
    private CorporateActionResponseConverterService corporateActionResponseConverterService;

    @Mock
    private CorporateActionAccountDetailsDto corporateActionAccountDetailsDto;

    @Mock
    private CorporateActionAccount corporateActionAccount;

    @Mock
    private CorporateActionDetails corporateActionDetails;

    @Mock
    private CorporateActionContext corporateActionContext;

    private DateTime referenceDateTime = new DateTime(2016, 12, 1, 0, 0);

    @Before
    public void setup() {
        Asset asset = mock(Asset.class);
        when(asset.getAssetId()).thenReturn("0");
        when(asset.getAssetCode()).thenReturn("BHP");
        when(asset.getAssetName()).thenReturn("BHP");

        when(assetIntegrationService.loadAsset(any(String.class), any(ServiceErrors.class))).thenReturn(asset);

        CorporateActionOptionDto corporateActionOptionDto1 = mock(CorporateActionOptionDto.class);
        when(corporateActionOptionDto1.getId()).thenReturn(0);
        when(corporateActionOptionDto1.getSummary()).thenReturn("1:2 ANZ");
        when(corporateActionOptionDto1.getIsDefault()).thenReturn(Boolean.FALSE);
        when(corporateActionOptionDto1.getTitle()).thenReturn("Option A");
        when(corporateActionOptionDto1.getIsNoAction()).thenReturn(Boolean.FALSE);

        CorporateActionOptionDto corporateActionOptionDto2 = mock(CorporateActionOptionDto.class);
        when(corporateActionOptionDto2.getId()).thenReturn(0);
        when(corporateActionOptionDto2.getSummary()).thenReturn("Do not participate");
        when(corporateActionOptionDto2.getIsDefault()).thenReturn(Boolean.TRUE);
        when(corporateActionOptionDto2.getTitle()).thenReturn("Option B");
        when(corporateActionOptionDto2.getIsNoAction()).thenReturn(Boolean.TRUE);

        when(corporateActionResponseConverterService.toElectionOptionDtos(any(CorporateActionContext.class), any(ServiceErrors.class)))
                .thenReturn(Arrays.asList(corporateActionOptionDto1, corporateActionOptionDto2));

        when(corporateActionCommonService.getUserProfileService()).thenReturn(userProfileService);
        when(corporateActionResponseConverterService.toSummaryList(any(CorporateActionContext.class), any(ServiceErrors.class)))
                .thenReturn(Arrays.asList("Summary"));
        when(corporateActionAccountDetails
                .toCorporateActionAccountDtoList(any(CorporateActionContext.class), anyList(), any(CorporateActionSavedDetails.class),
                        any(ServiceErrors.class))).thenReturn(Arrays.asList(corporateActionAccountDetailsDto));

        when(userProfileService.isInvestmentManager()).thenReturn(Boolean.FALSE);
        when(userProfileService.isDealerGroup()).thenReturn(Boolean.FALSE);
        when(userProfileService.isInvestor()).thenReturn(Boolean.FALSE);
        when(userProfileService.isPortfolioManager()).thenReturn(Boolean.FALSE);

        when(corporateActionDetails.getOrderNumber()).thenReturn("0");
        when(corporateActionDetails.getAssetId()).thenReturn("0");
        when(corporateActionDetails.getPayDate()).thenReturn(referenceDateTime);
        when(corporateActionDetails.getCloseDate()).thenReturn(referenceDateTime.plusMonths(3));
        when(corporateActionDetails.getCorporateActionType()).thenReturn(CorporateActionType.MULTI_BLOCK);
        when(corporateActionDetails.getCorporateActionOfferType()).thenReturn(CorporateActionOfferType.PUBLIC_OFFER);
        when(corporateActionDetails.getCorporateActionStatus()).thenReturn(CorporateActionStatus.OPEN);
        when(corporateActionDetails.getExDate()).thenReturn(referenceDateTime);
        when(corporateActionDetails.getLastUpdatedDate()).thenReturn(referenceDateTime);
        when(corporateActionDetails.getOfferDocumentUrl()).thenReturn("http://www.google.com.au");
        when(corporateActionDetails.getSummary()).thenReturn("Summary");
        when(corporateActionDetails.getTrusteeApprovalStatus()).thenReturn(TrusteeApprovalStatus.PENDING);
        when(corporateActionDetails.getRecordDate()).thenReturn(referenceDateTime);
        when(corporateActionDetails.getTakeoverLimit()).thenReturn(BigDecimal.TEN);

        when(corporateActionContext.getCorporateActionDetails()).thenReturn(corporateActionDetails);

        when(corporateActionAccount.getAccountId()).thenReturn("0");
        when(corporateActionContext.getCorporateActionAccountList()).thenReturn(Arrays.asList(corporateActionAccount));

        when(corporateActionServices.loadCorporateActionDetailsContext(anyString(), any(Boolean.class),
                any(ServiceErrors.class))).thenReturn(corporateActionContext);

        when(userProfileService.isInvestor()).thenReturn(Boolean.FALSE);
        when(corporateActionConverterFactory.getResponseConverterService(any(CorporateActionDetails.class)))
                .thenReturn(corporateActionResponseConverterService);

        when(helper.getEffectiveCorporateActionType(any(CorporateActionDetails.class)))
                .thenReturn(new EffectiveCorporateActionType(CorporateActionType.MULTI_BLOCK, CorporateActionOfferType.PUBLIC_OFFER.name(),
                        CorporateActionOfferType.PUBLIC_OFFER.getDescription()));

        when(helper.generateCorporateActionStatus(any(CorporateActionGroup.class), any(CorporateActionStatus.class), any(DateTime.class),
                any(DateTime.class), any(DateTime.class))).thenReturn(CorporateActionStatus.OPEN);

        when(helper.allowPartialElection(any(CorporateActionType.class), any(CorporateActionOfferType.class))).thenReturn(Boolean.FALSE);

        CorporateActionSavedDetails corporateActionSavedDetails = mock(CorporateActionSavedDetails.class);
        when(corporateActionSavedDetails.getResponseCode()).thenReturn(CorporateActionResponseCode.SUCCESS);

        when(corporateActionPersistenceDtoService.loadAndValidateElectedOptions(anyString(), anyList()))
                .thenReturn(corporateActionSavedDetails);

        when(corporateActionCommonService.getAssetPrice(any(Asset.class), any(ServiceErrors.class))).thenReturn(new BigDecimal(28.7));

        // DG/IM
        when(corporateActionServices.loadCorporateActionDetailsContextForIm(anyString(), anyString(),
                any(ServiceErrors.class))).thenReturn(corporateActionContext);

        when(helper.filterByManagedPortfolioAccounts(anyList(), anyString())).thenReturn(Arrays.asList(corporateActionAccount));

        ImCorporateActionPortfolioModelDto imCorporateActionPortfolioModelDto = mock(ImCorporateActionPortfolioModelDto.class);
        when(imCorporateActionPortfolioModelDtoService
                .toCorporateActionPortfolioModelDto(any(CorporateActionContext.class), anyList(), any(CorporateActionSavedDetails.class),
                        any(ServiceErrors.class))).thenReturn(Arrays.asList(imCorporateActionPortfolioModelDto));
    }

    @Test
    public void testFind_whenNotDealerGroupOrInvestmentManager_thenReturnThePopulatedCorporateActionDetails() {
        CorporateActionDetailsBaseDto corporateActionDetailsBaseDto =
                corporateActionDetailsDtoServiceImpl.find(new CorporateActionDtoKey("0"), null);

        assertTrue(corporateActionDetailsBaseDto instanceof CorporateActionDetailsDto);

        CorporateActionDetailsDto corporateActionDetailsDto = (CorporateActionDetailsDto) corporateActionDetailsBaseDto;

        assertEquals(corporateActionDetailsDto.getCorporateActionType(), CorporateActionOfferType.PUBLIC_OFFER.name());
        assertEquals(corporateActionDetailsDto.getCorporateActionTypeDescription(), CorporateActionOfferType.PUBLIC_OFFER.getDescription());
        assertEquals("BHP", corporateActionDetailsDto.getCompanyCode());
        assertEquals("BHP", corporateActionDetailsDto.getCompanyName());
        assertEquals(CorporateActionStatus.OPEN, corporateActionDetailsDto.getStatus());
        assertEquals(1, corporateActionDetailsDto.getAccounts().size());
        assertTrue(corporateActionDetailsDto.getCurrentPrice().compareTo(new BigDecimal(28.7)) == 0);
        assertEquals(referenceDateTime, corporateActionDetailsDto.getExDate());
        assertEquals(referenceDateTime, corporateActionDetailsDto.getPayDate());
        assertEquals(referenceDateTime, corporateActionDetailsDto.getRecordDate());
        assertEquals(corporateActionDetailsDto.getPanoramaCloseDate(), referenceDateTime.plusMonths(3).plusHours(12));
        assertEquals("http://www.google.com.au", corporateActionDetailsDto.getOfferDocumentUrl());
        assertEquals(2, corporateActionDetailsDto.getOptions().size());
        assertEquals(1, corporateActionDetailsDto.getSummary().size());
        assertEquals("Summary", corporateActionDetailsDto.getSummary().get(0));
        assertEquals(referenceDateTime, corporateActionDetailsDto.getLastUpdated());
        assertEquals(Boolean.FALSE, corporateActionDetailsDto.getMandatory());
        assertEquals(TrusteeApprovalStatus.PENDING, corporateActionDetailsDto.getTrusteeApprovalStatus());
        assertEquals(Boolean.FALSE, corporateActionDetailsDto.isEarlyClose());
        assertNull(corporateActionDetailsDto.getCorporateActionPrice());
        assertNull(corporateActionDetailsDto.getMinPrices());
        assertNull(corporateActionDetailsDto.getApplicableRatio());
        assertNull(corporateActionDetailsDto.getErrorMessage());
        assertNull(corporateActionDetailsDto.getKey());
        assertFalse(corporateActionDetailsDto.getPartialElection());
        assertNull(corporateActionDetailsDto.getMaxTakeUpPercent());
    }

    @Test
    public void testFind_whenNotDealerGroupOrInvestmentManagerAndTakeOverPercentIsZeroOr100_thenPercentOptionFlagShouldBeFalse() {
        when(corporateActionDetails.getTakeoverLimit()).thenReturn(BigDecimal.ZERO);

        CorporateActionDetailsBaseDto corporateActionDetailsBaseDto =
                corporateActionDetailsDtoServiceImpl.find(new CorporateActionDtoKey("0"), null);

        CorporateActionDetailsDto corporateActionDetailsDto = (CorporateActionDetailsDto) corporateActionDetailsBaseDto;

        assertFalse(corporateActionDetailsDto.getPartialElection());
        assertNull(corporateActionDetailsDto.getMaxTakeUpPercent());

        when(corporateActionDetails.getTakeoverLimit()).thenReturn(BigDecimal.valueOf(100.0));

        corporateActionDetailsBaseDto =
                corporateActionDetailsDtoServiceImpl.find(new CorporateActionDtoKey("0"), null);

        corporateActionDetailsDto = (CorporateActionDetailsDto) corporateActionDetailsBaseDto;

        assertFalse(corporateActionDetailsDto.getPartialElection());
        assertNull(corporateActionDetailsDto.getMaxTakeUpPercent());

        when(corporateActionDetails.getTakeoverLimit()).thenReturn(null);

        corporateActionDetailsBaseDto =
                corporateActionDetailsDtoServiceImpl.find(new CorporateActionDtoKey("0"), null);

        corporateActionDetailsDto = (CorporateActionDetailsDto) corporateActionDetailsBaseDto;

        assertFalse(corporateActionDetailsDto.getPartialElection());
        assertNull(corporateActionDetailsDto.getMaxTakeUpPercent());
    }

    @Test
    public void testFind_whenNotDealerGroupOrInvestmentWhenNotMultiBlockPublicOffer_thenPercentOptionFlagShouldBeFalse() {
        when(corporateActionDetails.getCorporateActionOfferType()).thenReturn(CorporateActionOfferType.CAPITAL_CALL);
        when(corporateActionDetails.getTakeoverLimit()).thenReturn(BigDecimal.TEN);

        CorporateActionDetailsBaseDto corporateActionDetailsBaseDto =
                corporateActionDetailsDtoServiceImpl.find(new CorporateActionDtoKey("0"), null);

        CorporateActionDetailsDto corporateActionDetailsDto = (CorporateActionDetailsDto) corporateActionDetailsBaseDto;

        assertFalse(corporateActionDetailsDto.getPartialElection());
        assertNull(corporateActionDetailsDto.getMaxTakeUpPercent());
    }

    @Test
    public void
    testFind_whenNotDealerGroupOrInvestmentManagerAndHasSavedDetails_thenTheResponseCodeStatusMustMatchSavedDetailsResponseCode() {
        CorporateActionSavedDetails corporateActionSavedDetails = mock(CorporateActionSavedDetails.class);
        when(corporateActionSavedDetails.getResponseCode()).thenReturn(CorporateActionResponseCode.SUCCESS);

        when(corporateActionPersistenceDtoService.loadAndValidateElectedOptions(anyString(), anyList()))
                .thenReturn(corporateActionSavedDetails);

        CorporateActionDetailsBaseDto corporateActionDetailsBaseDto =
                corporateActionDetailsDtoServiceImpl.find(new CorporateActionDtoKey("0"), null);

        assertEquals(CorporateActionResponseCode.SUCCESS, corporateActionDetailsBaseDto.getLoadStatus());
    }

    @Test
    public void testFind_whenNotDealerGroupOrInvestmentManagerAndHasNoCorporateActionDetails_thenEmptyCorporateActionDetailsDtoIsReturned
            () {
        when(corporateActionContext.getCorporateActionDetails()).thenReturn(null);

        CorporateActionDetailsBaseDto corporateActionDetailsBaseDto =
                corporateActionDetailsDtoServiceImpl.find(new CorporateActionDtoKey("0"), null);

        assertNotNull(corporateActionDetailsBaseDto);
        assertNull(corporateActionDetailsBaseDto.getCompanyCode());
    }

    @Test
    public void testFind_whenIsInvestmentManager_thenReturnThePopulatedCorporateActionDetailsWithPortfolioModels() {
        when(userProfileService.isInvestmentManager()).thenReturn(Boolean.TRUE);
        when(userProfileService.getPositionId()).thenReturn("0");

        CorporateActionDetailsBaseDto corporateActionDetailsBaseDto =
                corporateActionDetailsDtoServiceImpl.find(new CorporateActionDtoKey("0"), null);

        assertTrue(corporateActionDetailsBaseDto instanceof ImCorporateActionDetailsDto);

        ImCorporateActionDetailsDto corporateActionDetailsDto = (ImCorporateActionDetailsDto) corporateActionDetailsBaseDto;

        assertEquals(corporateActionDetailsDto.getCorporateActionType(), CorporateActionOfferType.PUBLIC_OFFER.name());
        assertEquals(corporateActionDetailsDto.getCorporateActionTypeDescription(), CorporateActionOfferType.PUBLIC_OFFER.getDescription());
        assertEquals("BHP", corporateActionDetailsDto.getCompanyCode());
        assertEquals(CorporateActionStatus.OPEN, corporateActionDetailsDto.getStatus());
        assertEquals(1, corporateActionDetailsDto.getPortfolioModels().size());
        assertTrue(corporateActionDetailsDto.getCurrentPrice().compareTo(new BigDecimal(28.7)) == 0);
        assertEquals(referenceDateTime, corporateActionDetailsDto.getExDate());
        assertEquals(referenceDateTime, corporateActionDetailsDto.getPayDate());
        assertEquals(referenceDateTime, corporateActionDetailsDto.getRecordDate());
        assertEquals(referenceDateTime.plusMonths(3).plusHours(12), corporateActionDetailsDto.getPanoramaCloseDate());
        assertEquals("http://www.google.com.au", corporateActionDetailsDto.getOfferDocumentUrl());
        assertEquals(2, corporateActionDetailsDto.getOptions().size());
        assertEquals(1, corporateActionDetailsDto.getSummary().size());
        assertEquals("Summary", corporateActionDetailsDto.getSummary().get(0));
        assertEquals(referenceDateTime, corporateActionDetailsDto.getLastUpdated());
        assertEquals(Boolean.FALSE, corporateActionDetailsDto.getMandatory());
        assertEquals(TrusteeApprovalStatus.PENDING, corporateActionDetailsDto.getTrusteeApprovalStatus());
        assertEquals(Boolean.FALSE, corporateActionDetailsDto.getOversubscribe());
        assertNull(corporateActionDetailsDto.getElectionMinMax());
        assertEquals(Boolean.FALSE, corporateActionDetailsDto.isEarlyClose());
    }

    @Test
    public void testFind_whenIsInvestmentManagerAndHasNoCorporateActionDetails_thenEmptyCorporateActionDetailsDtoIsReturned() {
        when(corporateActionContext.getCorporateActionDetails()).thenReturn(null);
        when(userProfileService.isInvestmentManager()).thenReturn(Boolean.TRUE);
        when(userProfileService.isDealerGroup()).thenReturn(Boolean.FALSE);
        when(userProfileService.isPortfolioManager()).thenReturn(Boolean.FALSE);
        when(userProfileService.getPositionId()).thenReturn("0");

        CorporateActionContext result = mock(CorporateActionContext.class);
        when(result.getCorporateActionDetails()).thenReturn(null);

        when(corporateActionServices.loadCorporateActionDetailsContextForIm(anyString(), anyString(),
                any(ServiceErrors.class))).thenReturn(result);

        CorporateActionDetailsBaseDto corporateActionDetailsBaseDto =
                corporateActionDetailsDtoServiceImpl.find(new CorporateActionDtoKey("0", null, "0", null), null);

        assertNotNull(corporateActionDetailsBaseDto);
        assertTrue(corporateActionDetailsBaseDto instanceof ImCorporateActionDetailsDto);
        assertNull(corporateActionDetailsBaseDto.getCompanyCode());
    }

    @Test
    public void testFind_whenIsDealerGroupAndHasNoCorporateActionDetails_thenEmptyCorporateActionDetailsDtoIsReturned() {
        when(corporateActionContext.getCorporateActionDetails()).thenReturn(null);
        when(userProfileService.isInvestmentManager()).thenReturn(Boolean.FALSE);
        when(userProfileService.isDealerGroup()).thenReturn(Boolean.TRUE);
        when(userProfileService.isPortfolioManager()).thenReturn(Boolean.FALSE);
        when(userProfileService.getPositionId()).thenReturn("0");

        CorporateActionDetailsBaseDto corporateActionDetailsBaseDto = corporateActionDetailsDtoServiceImpl.find(
                new CorporateActionDtoKey("0"), null);

        assertNotNull(corporateActionDetailsBaseDto);
        assertTrue(corporateActionDetailsBaseDto instanceof ImCorporateActionDetailsDto);
        assertNull(corporateActionDetailsBaseDto.getCompanyCode());
    }

    @Test
    public void testFind_whenIsDealerGroupAndHasIpsId_thenReturnCorporateActionDetailsWithAccounts() {
        when(corporateActionContext.getCorporateActionDetails()).thenReturn(corporateActionDetails);
        when(userProfileService.isInvestmentManager()).thenReturn(Boolean.FALSE);
        when(userProfileService.isDealerGroup()).thenReturn(Boolean.TRUE);
        when(userProfileService.isPortfolioManager()).thenReturn(Boolean.FALSE);
        when(userProfileService.getPositionId()).thenReturn("0");

        CorporateActionDetailsBaseDto corporateActionDetailsBaseDto = corporateActionDetailsDtoServiceImpl.find(
                new CorporateActionDtoKey("0", null, "0", Boolean.FALSE), null);

        assertNotNull(corporateActionDetailsBaseDto);
        assertTrue(corporateActionDetailsBaseDto instanceof ImCorporateActionDetailsDto);

        ImCorporateActionDetailsDto corporateActionDetailsDto = (ImCorporateActionDetailsDto) corporateActionDetailsBaseDto;
        assertEquals(1, corporateActionDetailsDto.getAccounts().size());
    }

    @Test
    public void testFind_whenIsPortfolioManagerAndHasNoCorporateActionDetails_thenEmptyCorporateActionDetailsDtoIsReturned() {
        when(corporateActionContext.getCorporateActionDetails()).thenReturn(null);
        when(userProfileService.isInvestmentManager()).thenReturn(Boolean.FALSE);
        when(userProfileService.isDealerGroup()).thenReturn(Boolean.FALSE);
        when(userProfileService.isPortfolioManager()).thenReturn(Boolean.TRUE);
        when(userProfileService.getPositionId()).thenReturn("0");

        CorporateActionDetailsBaseDto corporateActionDetailsBaseDto = corporateActionDetailsDtoServiceImpl.find(
                new CorporateActionDtoKey("0"), null);

        assertNotNull(corporateActionDetailsBaseDto);
        assertTrue(corporateActionDetailsBaseDto instanceof ImCorporateActionDetailsDto);
        assertNull(corporateActionDetailsBaseDto.getCompanyCode());
    }

    @Test
    public void testFind_whenIsPortfolioManagerAndHasIpsId_thenReturnCorporateActionDetailsWithAccounts() {
        when(corporateActionContext.getCorporateActionDetails()).thenReturn(corporateActionDetails);
        when(userProfileService.isInvestmentManager()).thenReturn(Boolean.FALSE);
        when(userProfileService.isDealerGroup()).thenReturn(Boolean.FALSE);
        when(userProfileService.isPortfolioManager()).thenReturn(Boolean.TRUE);
        when(userProfileService.getPositionId()).thenReturn("0");

        CorporateActionDetailsBaseDto corporateActionDetailsBaseDto = corporateActionDetailsDtoServiceImpl
                .find(new CorporateActionDtoKey("0", null, "0", Boolean.FALSE), null);

        assertNotNull(corporateActionDetailsBaseDto);
        assertTrue(corporateActionDetailsBaseDto instanceof ImCorporateActionDetailsDto);

        ImCorporateActionDetailsDto corporateActionDetailsDto = (ImCorporateActionDetailsDto) corporateActionDetailsBaseDto;
        assertEquals(1, corporateActionDetailsDto.getAccounts().size());
    }

    @Test
    public void testFind_whenNotDealerGroupOrInvestmentManagerAndHasNoSavedDetails_thenTheLoadStatusShouldBeNull() {
        when(corporateActionPersistenceDtoService.loadAndValidateElectedOptions(anyString(), anyList())).thenReturn(null);

        CorporateActionDetailsBaseDto corporateActionDetailsBaseDto = corporateActionDetailsDtoServiceImpl.find(new CorporateActionDtoKey
                ("0"), null);

        assertNull(corporateActionDetailsBaseDto.getLoadStatus());
    }

    @Test
    public void testFind_whenInvestorOrInvestmentManager_thenThereShouldBeNoSavedDataAndTheLoadStatusShouldBeNull() {
        when(corporateActionPersistenceDtoService.loadAndValidateElectedOptions(anyString(), anyList())).thenReturn(null);
        when(userProfileService.isInvestor()).thenReturn(Boolean.TRUE);
        when(userProfileService.isInvestmentManager()).thenReturn(Boolean.FALSE);
        when(userProfileService.isDealerGroup()).thenReturn(Boolean.FALSE);
        when(userProfileService.isPortfolioManager()).thenReturn(Boolean.FALSE);

        CorporateActionDetailsBaseDto corporateActionDetailsBaseDto = corporateActionDetailsDtoServiceImpl.find(new CorporateActionDtoKey
                ("0"), null);

        assertNull(corporateActionDetailsBaseDto.getLoadStatus());

        when(userProfileService.isInvestor()).thenReturn(Boolean.FALSE);
        when(userProfileService.isInvestmentManager()).thenReturn(Boolean.TRUE);
        when(userProfileService.isDealerGroup()).thenReturn(Boolean.FALSE);
        when(userProfileService.isPortfolioManager()).thenReturn(Boolean.FALSE);

        corporateActionDetailsBaseDto = corporateActionDetailsDtoServiceImpl.find(new CorporateActionDtoKey("0"), null);

        assertNull(corporateActionDetailsBaseDto.getLoadStatus());
    }

    @Test
    public void testFind_whenNotDealerGroupOrInvestmentManagerAndHasAccountIdSpecified_thenEnsureTheAccountIsProcessed() {
        when(corporateActionContext.getAccountId()).thenReturn("0");

        CorporateActionDetailsBaseDto corporateActionDetailsBaseDto =
                corporateActionDetailsDtoServiceImpl.find(new CorporateActionDtoKey("0", "0", null, false), null);

        assertTrue(corporateActionDetailsBaseDto instanceof CorporateActionDetailsDto);

        when(corporateActionContext.getAccountId()).thenReturn("10");
        corporateActionDetailsBaseDto = corporateActionDetailsDtoServiceImpl.find(new CorporateActionDtoKey("0", "1", null, false), null);

        assertTrue(corporateActionDetailsBaseDto instanceof CorporateActionDetailsDto);
    }
}
