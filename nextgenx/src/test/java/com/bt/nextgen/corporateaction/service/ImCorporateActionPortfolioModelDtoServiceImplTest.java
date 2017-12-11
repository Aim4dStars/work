package com.bt.nextgen.corporateaction.service;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountElectionsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSavedDetails;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionPortfolioModelDto;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionAccountHelper;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionTransactionDetailsConverter;
import com.bt.nextgen.api.corporateaction.v1.service.ImCorporateActionPortfolioModelDtoServiceImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.corporateaction.CorporateActionAccountImpl;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccountParticipationStatus;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOfferType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionStatus;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionTransactionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionTransactionStatus;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionType;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementIntegrationService;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementInterface;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.modelportfolio.common.ModelType;
import com.bt.nextgen.service.integration.modelportfolio.detail.ModelPortfolioDetail;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ImCorporateActionPortfolioModelDtoServiceImplTest {
    @InjectMocks
    private ImCorporateActionPortfolioModelDtoServiceImpl imCorporateActionPortfolioModelDtoService;

    @Mock
    private InvestmentPolicyStatementIntegrationService ipsIntegrationService;

    @Mock
    private CorporateActionAccountHelper corporateActionAccountHelper;

    @Mock
    private CorporateActionIntegrationService corporateActionService;

    @Mock
    private CorporateActionTransactionDetailsConverter transactionDetailsConverter;

    @Mock
    private CorporateActionContext context;

    @Mock
    private CorporateActionDetails corporateActionDetails;

    @Mock
    private CorporateActionTransactionDetails transactionDetails;

    private CorporateActionAccount corporateActionAccount1;

    private CorporateActionAccount corporateActionAccount2;

    @Mock
    private CorporateActionAccount shadowPortfolioAccount;

    @Mock
    private ModelPortfolioDetail modelPortfolioDetail;

    @Mock
    private CorporateActionAccountElectionsDto corporateActionAccountElectionsDto1;

    @Before
    public void setup() throws Exception {
        // Note: unfortunately Lambdaj sumFrom does not work with mocks, hence the use of real objects
        corporateActionAccount1 = new CorporateActionAccountImpl();
        CorporateActionAccountImpl corporateActionAccount1Impl = (CorporateActionAccountImpl) corporateActionAccount1;

        corporateActionAccount1Impl.setPositionId("0");
        corporateActionAccount1Impl.setContainerType(ContainerType.MANAGED_PORTFOLIO);
        corporateActionAccount1Impl.setIpsId("123");
        corporateActionAccount1Impl.setElectionStatus(CorporateActionAccountParticipationStatus.SUBMITTED);
        corporateActionAccount1Impl.setEligibleQuantity(BigDecimal.valueOf(100));
        corporateActionAccount1Impl.setAvailableQuantity(BigDecimal.valueOf(100));

        corporateActionAccount2 = new CorporateActionAccountImpl();
        CorporateActionAccountImpl corporateActionAccount2Impl = (CorporateActionAccountImpl) corporateActionAccount2;
        corporateActionAccount2Impl.setPositionId("1");
        corporateActionAccount2Impl.setContainerType(ContainerType.MANAGED_PORTFOLIO);
        corporateActionAccount2Impl.setIpsId("456");
        corporateActionAccount2Impl.setElectionStatus(CorporateActionAccountParticipationStatus.NOT_SUBMITTED);
        corporateActionAccount2Impl.setEligibleQuantity(BigDecimal.valueOf(200));
        corporateActionAccount2Impl.setAvailableQuantity(BigDecimal.valueOf(200));

        when(shadowPortfolioAccount.getPositionId()).thenReturn("100");
        when(shadowPortfolioAccount.getIpsId()).thenReturn("123");
        when(shadowPortfolioAccount.getContainerType()).thenReturn(ContainerType.SHADOW_MANAGED_PORTFOLIO);

        when(corporateActionDetails.getCorporateActionType()).thenReturn(CorporateActionType.MULTI_BLOCK);
        when(corporateActionDetails.getCorporateActionOfferType()).thenReturn(CorporateActionOfferType.PUBLIC_OFFER);
        when(corporateActionDetails.getCorporateActionStatus()).thenReturn(CorporateActionStatus.OPEN);
        when(corporateActionDetails.getExDate()).thenReturn(new DateTime(2016, 12, 01, 0, 0, 0));

        when(context.getCorporateActionDetails()).thenReturn(corporateActionDetails);

        Map<IpsKey, InvestmentPolicyStatementInterface> policyStatementInterfaceMap = new HashMap<>();
        InvestmentPolicyStatementInterface investmentPolicyStatement1 = mock(InvestmentPolicyStatementInterface.class);
        when(investmentPolicyStatement1.getIpsKey()).thenReturn(IpsKey.valueOf("123"));
        when(investmentPolicyStatement1.getCode()).thenReturn("BNS100");
        when(investmentPolicyStatement1.getApirCode()).thenReturn("BNS100");
        when(investmentPolicyStatement1.getInvestmentName()).thenReturn("DNS Capital");

        InvestmentPolicyStatementInterface investmentPolicyStatement2 = mock(InvestmentPolicyStatementInterface.class);
        when(investmentPolicyStatement2.getIpsKey()).thenReturn(IpsKey.valueOf("456"));
        when(investmentPolicyStatement2.getCode()).thenReturn("BHP100");
        when(investmentPolicyStatement2.getApirCode()).thenReturn("BHP100");
        when(investmentPolicyStatement2.getInvestmentName()).thenReturn("BHP Capital");

        policyStatementInterfaceMap.put(investmentPolicyStatement1.getIpsKey(), investmentPolicyStatement1);
        policyStatementInterfaceMap.put(investmentPolicyStatement2.getIpsKey(), investmentPolicyStatement2);

        when(ipsIntegrationService.getInvestmentPolicyStatements(any(ServiceErrors.class))).thenReturn(
                policyStatementInterfaceMap);

        Map<IpsKey, ModelPortfolioDetail> modelPortfolioDetailMap = new HashMap<>();
        when(modelPortfolioDetail.getId()).thenReturn("123");
        when(modelPortfolioDetail.getAccountType()).thenReturn(ModelType.SUPERANNUATION.getId());
        modelPortfolioDetailMap.put(IpsKey.valueOf("123"), modelPortfolioDetail);
        when(ipsIntegrationService.getModelDetails(anyListOf(IpsKey.class), any(ServiceErrors.class))).thenReturn(modelPortfolioDetailMap);

        when(transactionDetails.getAccountId()).thenReturn("0");
        when(transactionDetails.getPositionId()).thenReturn("100");
        when(transactionDetails.getTransactionNumber()).thenReturn(12345);
        when(transactionDetails.getTransactionDescription()).thenReturn("DNS split @ $10.00");

        when(transactionDetailsConverter.loadTransactionDetailsForIm(any(CorporateActionContext.class), any(ServiceErrors.class)))
                .thenReturn(Arrays.asList(transactionDetails));
    }

    @Test
    public void testToCorporateActionPortfolioModelDto_whenAccountsIsNull_thenReturnAnEmptyList() {
        List<ImCorporateActionPortfolioModelDto> portfolioModelDtoList = imCorporateActionPortfolioModelDtoService
                .toCorporateActionPortfolioModelDto(context, null, null, null);

        assertNotNull(portfolioModelDtoList);
        assertTrue(portfolioModelDtoList.isEmpty());
    }

    @Test
    public void
    testToCorporateActionPortfolioModelDto_whenThereAreAccountsAndCorporateActionIsOpen_thenReturnAListOfImCorporateActionPortfolioModelDto() {
        when(corporateActionAccountHelper.getSubmittedElections(any(CorporateActionContext.class), any(CorporateActionAccount.class)))
                .thenReturn(corporateActionAccountElectionsDto1, corporateActionAccountElectionsDto1, null, null);

        when(corporateActionAccountHelper.getSavedElections(any(CorporateActionContext.class), anyString(), any
                (CorporateActionSavedDetails.class))).thenReturn(corporateActionAccountElectionsDto1,
                corporateActionAccountElectionsDto1, null, null);

        List<ImCorporateActionPortfolioModelDto> portfolioModelDtoList = imCorporateActionPortfolioModelDtoService
                .toCorporateActionPortfolioModelDto(context,
                        Arrays.asList(corporateActionAccount1, corporateActionAccount2, shadowPortfolioAccount), null, null);

        assertEquals(portfolioModelDtoList.size(), 2);
        assertEquals(portfolioModelDtoList.get(0).getIpsId(), "123");
        assertEquals(portfolioModelDtoList.get(0).getPortfolioCode(), "BNS100");
        assertEquals(portfolioModelDtoList.get(0).getPortfolioName(), "DNS Capital");
        assertEquals(portfolioModelDtoList.get(0).getElectionStatus(), CorporateActionAccountParticipationStatus.SUBMITTED);
        assertTrue(portfolioModelDtoList.get(0).getEligibleHolding() == 100);
        assertTrue(portfolioModelDtoList.get(0).getInvestors() == 1);
        assertTrue(portfolioModelDtoList.get(0).getInvestorElectionsSubmitted() == 1);
        assertNotNull(portfolioModelDtoList.get(0).getSubmittedElections());
        assertNotNull(portfolioModelDtoList.get(0).getSavedElections());
        assertNull(portfolioModelDtoList.get(0).getTransactionStatus());
        assertNull(portfolioModelDtoList.get(0).getTransactionDescription());
        assertTrue(portfolioModelDtoList.get(0).isTrusteeApproval());

        assertEquals(portfolioModelDtoList.get(1).getIpsId(), "456");
        assertEquals(portfolioModelDtoList.get(1).getPortfolioCode(), "BHP100");
        assertEquals(portfolioModelDtoList.get(1).getPortfolioName(), "BHP Capital");
        assertEquals(portfolioModelDtoList.get(1).getElectionStatus(), CorporateActionAccountParticipationStatus.NOT_SUBMITTED);
        assertTrue(portfolioModelDtoList.get(1).getEligibleHolding() == 200);
        assertTrue(portfolioModelDtoList.get(1).getInvestors() == 1);
        assertTrue(portfolioModelDtoList.get(1).getInvestorElectionsSubmitted() == 0);
        assertNull(portfolioModelDtoList.get(1).getSubmittedElections());
        assertNotNull(portfolioModelDtoList.get(1).getSavedElections());
        assertNull(portfolioModelDtoList.get(1).getTransactionStatus());
        assertNull(portfolioModelDtoList.get(1).getTransactionDescription());
        assertFalse(portfolioModelDtoList.get(1).isTrusteeApproval());

        when(modelPortfolioDetail.getAccountType()).thenReturn(ModelType.INVESTMENT.getId());

        portfolioModelDtoList = imCorporateActionPortfolioModelDtoService
                .toCorporateActionPortfolioModelDto(context,
                        Arrays.asList(corporateActionAccount1, corporateActionAccount2, shadowPortfolioAccount), null, null);

        assertFalse(portfolioModelDtoList.get(0).isTrusteeApproval());
    }

    @Test
    public void
    testToCorporateActionPortfolioModelDto_whenThereAreAccountsAndCorporateActionIsClosed_thenReturnAListOfImCorporateActionPortfolioModelDto() {
        when(corporateActionAccountHelper.getSubmittedElections(any(CorporateActionContext.class), any(CorporateActionAccount.class)))
                .thenReturn(corporateActionAccountElectionsDto1, corporateActionAccountElectionsDto1, null, null);
        when(corporateActionDetails.getCorporateActionStatus()).thenReturn(CorporateActionStatus.CLOSED);
        when(transactionDetailsConverter.loadTransactionDetailsForIm(any(CorporateActionContext.class), any(ServiceErrors.class)))
                .thenReturn(Arrays.asList(transactionDetails), null);

        List<ImCorporateActionPortfolioModelDto> portfolioModelDtoList = imCorporateActionPortfolioModelDtoService
                .toCorporateActionPortfolioModelDto(context,
                        Arrays.asList(corporateActionAccount1, corporateActionAccount2, shadowPortfolioAccount), null, null);

        assertEquals(portfolioModelDtoList.size(), 2);

        assertEquals(portfolioModelDtoList.get(0).getPortfolioCode(), "BNS100");
        assertEquals(portfolioModelDtoList.get(0).getPortfolioName(), "DNS Capital");
        assertEquals(portfolioModelDtoList.get(0).getElectionStatus(), CorporateActionAccountParticipationStatus.SUBMITTED);
        assertTrue(portfolioModelDtoList.get(0).getEligibleHolding() == 100);
        assertTrue(portfolioModelDtoList.get(0).getInvestors() == 1);
        assertTrue(portfolioModelDtoList.get(0).getInvestorElectionsSubmitted() == 1);
        assertNotNull(portfolioModelDtoList.get(0).getSubmittedElections());
        assertNull(portfolioModelDtoList.get(0).getSavedElections());
        assertEquals(portfolioModelDtoList.get(0).getTransactionStatus(), CorporateActionTransactionStatus.POST_EX_DATE);
        assertEquals(portfolioModelDtoList.get(0).getTransactionDescription(), "DNS split @ $10.00");

        assertEquals(portfolioModelDtoList.get(1).getIpsId(), "456");
        assertEquals(portfolioModelDtoList.get(1).getPortfolioCode(), "BHP100");
        assertEquals(portfolioModelDtoList.get(1).getPortfolioName(), "BHP Capital");
        assertEquals(portfolioModelDtoList.get(1).getElectionStatus(), CorporateActionAccountParticipationStatus.NOT_SUBMITTED);
        assertTrue(portfolioModelDtoList.get(1).getEligibleHolding() == 200);
        assertTrue(portfolioModelDtoList.get(1).getInvestors() == 1);
        assertTrue(portfolioModelDtoList.get(1).getInvestorElectionsSubmitted() == 0);
        assertNull(portfolioModelDtoList.get(1).getSubmittedElections());
        assertNull(portfolioModelDtoList.get(1).getSavedElections());
        assertEquals(portfolioModelDtoList.get(1).getTransactionStatus(), CorporateActionTransactionStatus.PRE_EX_DATE);
        assertNull(portfolioModelDtoList.get(1).getTransactionDescription());
    }

    @Test
    public void
    testToCorporateActionPortfolioModelDto_whenThereAreAccountsAndCorporateActionIsClosedAndItIsMandatory_thenReturnAListOfImCorporateActionPortfolioModelDto() {
        when(corporateActionAccountHelper.getSubmittedElections(any(CorporateActionContext.class), any(CorporateActionAccount.class)))
                .thenReturn(corporateActionAccountElectionsDto1, corporateActionAccountElectionsDto1, null, null);

        when(corporateActionDetails.getCorporateActionStatus()).thenReturn(CorporateActionStatus.CLOSED);
        when(corporateActionDetails.getCorporateActionType()).thenReturn(CorporateActionType.MERGER_WITH_FRACTION);
        when(transactionDetailsConverter.loadTransactionDetailsForIm(any(CorporateActionContext.class), any(ServiceErrors.class)))
                .thenReturn(Arrays.asList(transactionDetails), null);

        List<ImCorporateActionPortfolioModelDto> portfolioModelDtoList =
                imCorporateActionPortfolioModelDtoService.toCorporateActionPortfolioModelDto(context,
                        Arrays.asList(corporateActionAccount1, corporateActionAccount2, shadowPortfolioAccount), null, null);

        assertEquals(portfolioModelDtoList.size(), 2);

        assertEquals(portfolioModelDtoList.get(0).getTransactionStatus(), CorporateActionTransactionStatus.POST_EX_DATE);
        assertEquals(portfolioModelDtoList.get(0).getTransactionDescription(), "DNS split @ $10.00");

        assertEquals(portfolioModelDtoList.get(1).getTransactionStatus(), CorporateActionTransactionStatus.PRE_EX_DATE);
        assertNull(portfolioModelDtoList.get(1).getTransactionDescription());
    }

    @Test
    public void testToCorporateActionPortfolioModelDto_whenThereAreAccountsAndMandatoryCorporateActionIsClosedButPreExDate_thenReturnAListOfImCorporateActionPortfolioModelDto() {
        when(
                corporateActionAccountHelper.getSubmittedElections(any(CorporateActionContext.class),
                        any(CorporateActionAccount.class))).thenReturn(corporateActionAccountElectionsDto1,
                corporateActionAccountElectionsDto1, null, null);

        when(corporateActionDetails.getExDate()).thenReturn(DateTime.now().plusMonths(1));
        when(context.getCorporateActionDetails()).thenReturn(corporateActionDetails);

        when(corporateActionDetails.getCorporateActionStatus()).thenReturn(CorporateActionStatus.CLOSED);
        when(corporateActionDetails.getCorporateActionType()).thenReturn(CorporateActionType.MERGER_WITH_FRACTION);
        when(transactionDetailsConverter.loadTransactionDetailsForIm(any(CorporateActionContext.class), any(ServiceErrors.class)))
                .thenReturn(null);

        List<ImCorporateActionPortfolioModelDto> portfolioModelDtoList = imCorporateActionPortfolioModelDtoService
                .toCorporateActionPortfolioModelDto(context,
                        Arrays.asList(corporateActionAccount1, corporateActionAccount2, shadowPortfolioAccount), null, null);

        assertEquals(portfolioModelDtoList.size(), 2);

        assertNull(portfolioModelDtoList.get(0).getTransactionStatus());
        assertNull(portfolioModelDtoList.get(0).getTransactionDescription());

        assertNull(portfolioModelDtoList.get(1).getTransactionStatus());
        assertNull(portfolioModelDtoList.get(1).getTransactionDescription());
    }

    @Test
    public void
    testToCorporateActionPortfolioModelDto_forDG_whenThereAreAccountsAndCorporateActionIsOpen_thenReturnAListOfImCorporateActionPortfolioModelDto() {
        when(corporateActionAccountHelper.getSubmittedElections(any(CorporateActionContext.class), any(CorporateActionAccount.class)))
                .thenReturn(corporateActionAccountElectionsDto1, corporateActionAccountElectionsDto1, null, null);

        when(context.isDealerGroup()).thenReturn(Boolean.TRUE);

        List<ImCorporateActionPortfolioModelDto> portfolioModelDtoList = imCorporateActionPortfolioModelDtoService
                .toCorporateActionPortfolioModelDto(context,
                        Arrays.asList(corporateActionAccount1, corporateActionAccount2, shadowPortfolioAccount), null, null);

        assertEquals(portfolioModelDtoList.size(), 2);
        assertEquals(shadowPortfolioAccount.getElectionStatus(), portfolioModelDtoList.get(0).getElectionStatus());
    }
}
