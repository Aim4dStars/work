package com.bt.nextgen.corporateaction.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListResult;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionServicesImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.corporateaction.CorporateActionImpl;
import com.bt.nextgen.service.integration.corporateaction.CorporateAction;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetailsResponse;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOfferType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionSecurityExchangeType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionStatus;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionType;
import com.bt.nextgen.service.integration.corporateaction.ImCorporateActionIntegrationService;
import com.bt.nextgen.service.integration.trustee.TrusteeApprovalStatus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionServicesImplTest {
    @InjectMocks
    private CorporateActionServicesImpl services;

    @Mock
    private CorporateActionIntegrationService corporateActionIntegrationService;

    @Mock
    private ImCorporateActionIntegrationService imCorporateActionIntegrationService;

    @Before
    public void setup() {
    }

    @Test
    public void testLoadVoluntaryCorporateActions_whenThereIsNoSuperAccounts_thenEligibleCountShouldJustBeFromInvestmentAccounts() {
        List<CorporateAction> invCorporateActionList = new ArrayList<>();
        List<CorporateAction> supCorporateActionList = new ArrayList<>();

        CorporateAction invCa1 = mock(CorporateAction.class);
        when(invCa1.getOrderNumber()).thenReturn("0");
        when(invCa1.getCorporateActionType()).thenReturn(CorporateActionType.EXERCISE_RIGHTS);
        when(invCa1.getEligible()).thenReturn(6);
        when(invCa1.getUnconfirmed()).thenReturn(6);

        invCorporateActionList.add(invCa1);

        when(corporateActionIntegrationService.loadVoluntaryCorporateActions(any(DateTime.class), any(DateTime.class),
                anyListOf(String.class), any(ServiceErrors.class))).thenReturn(invCorporateActionList);

        when(corporateActionIntegrationService.loadVoluntaryCorporateActionsForSuper(any(DateTime.class), any(DateTime.class),
                anyListOf(String.class), any(ServiceErrors.class))).thenReturn(supCorporateActionList);

        CorporateActionListResult result =
                services.loadVoluntaryCorporateActions(new DateTime(), new DateTime(), new ArrayList<String>(), null);

        assertEquals(result.getCorporateActions().size(), 1);
        assertEquals(result.getCorporateActions().get(0).getCorporateActionType(), CorporateActionType.EXERCISE_RIGHTS);
        assertEquals(result.getCorporateActions().get(0).getEligible(), new Integer(6));
        assertEquals(result.getCorporateActions().get(0).getUnconfirmed(), new Integer(6));
    }


    @Test
    public void testLoadVoluntaryCorporateActions_whenThereIsNoInvestmentAccounts_thenEligibleCountShouldJustBeFromSuperAccounts() {
        List<CorporateAction> invCorporateActionList = new ArrayList<>();
        List<CorporateAction> supCorporateActionList = new ArrayList<>();

        CorporateActionImpl supCa1 = new CorporateActionImpl();
        supCa1.setOrderNumber("0");
        supCa1.setCorporateActionType(CorporateActionType.EXERCISE_RIGHTS);
        supCa1.setEligible(6);
        supCa1.setUnconfirmed(6);

        supCorporateActionList.add(supCa1);

        when(corporateActionIntegrationService.loadVoluntaryCorporateActions(any(DateTime.class), any(DateTime.class),
                anyListOf(String.class), any(ServiceErrors.class))).thenReturn(invCorporateActionList);

        when(corporateActionIntegrationService.loadVoluntaryCorporateActionsForSuper(any(DateTime.class), any(DateTime.class),
                anyListOf(String.class), any(ServiceErrors.class))).thenReturn(supCorporateActionList);

        CorporateActionListResult result;

        // Trustee approved
        supCa1.setTrusteeApprovalStatus(TrusteeApprovalStatus.APPROVED);

        result = services.loadVoluntaryCorporateActions(new DateTime(), new DateTime(), new ArrayList<String>(), null);

        assertTrue(result.getHasSuperPension());
        assertEquals(result.getCorporateActions().size(), 1);
        assertEquals(result.getCorporateActions().get(0).getCorporateActionType(), CorporateActionType.EXERCISE_RIGHTS);
        assertEquals(result.getCorporateActions().get(0).getEligible(), new Integer(6));
        assertEquals(result.getCorporateActions().get(0).getUnconfirmed(), new Integer(6));

        // Pending trustee approval
        supCa1.setTrusteeApprovalStatus(TrusteeApprovalStatus.PENDING);

        result = services.loadVoluntaryCorporateActions(new DateTime(), new DateTime(), new ArrayList<String>(), null);

        assertTrue(result.getHasSuperPension());
        assertEquals(result.getCorporateActions().size(), 1);
        assertEquals(result.getCorporateActions().get(0).getCorporateActionType(), CorporateActionType.EXERCISE_RIGHTS);
        assertEquals(result.getCorporateActions().get(0).getEligible(), new Integer(6));
        assertEquals(result.getCorporateActions().get(0).getUnconfirmed(), new Integer(6));

        // Declined trustee approval
        supCa1.setTrusteeApprovalStatus(TrusteeApprovalStatus.DECLINED);

        result = services.loadVoluntaryCorporateActions(new DateTime(), new DateTime(), new ArrayList<String>(), null);

        assertTrue(result.getHasSuperPension());
        assertEquals(result.getCorporateActions().size(), 1);
        assertEquals(result.getCorporateActions().get(0).getCorporateActionType(), CorporateActionType.EXERCISE_RIGHTS);
        assertEquals(result.getCorporateActions().get(0).getEligible(), new Integer(6));
        assertEquals(result.getCorporateActions().get(0).getUnconfirmed(), new Integer(6));
    }

    @Test
    public void testMergeCorporateActions_whenCorpActionListsAreEmpty_thenEmptyListReturned()
    {
        when(corporateActionIntegrationService.loadVoluntaryCorporateActions(any(DateTime.class), any(DateTime.class),
            anyListOf(String.class), any(ServiceErrors.class))).thenReturn(null);

        when(corporateActionIntegrationService.loadVoluntaryCorporateActionsForSuper(any(DateTime.class), any(DateTime.class),
            anyListOf(String.class), any(ServiceErrors.class))).thenReturn(null);

        CorporateActionListResult result;

        result = services.loadVoluntaryCorporateActions(new DateTime(), new DateTime(), new ArrayList<String>(), null);

        assertNotNull(result);
        assertEquals(0, result.getCorporateActions().size());
    }

    @Test
    public void testMergeCorporateActions_whenApproveListIsEmpty_thenEmptyListReturned()
    {
        List<CorporateAction> invCorporateActionList = new ArrayList<>();
        List<CorporateAction> supCorporateActionList = new ArrayList<>();

        CorporateActionImpl supCa1 = new CorporateActionImpl();
        supCa1.setOrderNumber("0");
        supCa1.setCorporateActionType(CorporateActionType.EXERCISE_RIGHTS);
        supCa1.setEligible(6);
        supCa1.setUnconfirmed(6);
        supCa1.setTrusteeApprovalStatus(null);

        supCorporateActionList.add(supCa1);

        when(corporateActionIntegrationService.loadVoluntaryCorporateActions(any(DateTime.class), any(DateTime.class),
                anyListOf(String.class), any(ServiceErrors.class))).thenReturn(invCorporateActionList);

        when(corporateActionIntegrationService.loadVoluntaryCorporateActionsForSuper(any(DateTime.class), any(DateTime.class),
                anyListOf(String.class), any(ServiceErrors.class))).thenReturn(supCorporateActionList);

        CorporateActionListResult result;

        result = services.loadVoluntaryCorporateActions(new DateTime(), new DateTime(), new ArrayList<String>(), null);

        assertNotNull(result);
        assertEquals(0, result.getCorporateActions().size());

        result = services.loadMandatoryCorporateActions(new DateTime(), new DateTime(), new ArrayList<String>(), null);

        assertNotNull(result);
        assertEquals(0, result.getCorporateActions().size());
    }

    @Test
    public void testMergeApprovedList_whenUnconfirmedIsNull_thenCountIsZero()
    {
        List<CorporateAction> invCorporateActionList = new ArrayList<>();
        List<CorporateAction> supCorporateActionList = new ArrayList<>();

        CorporateActionImpl supCa1 = new CorporateActionImpl();
        supCa1.setOrderNumber("0");
        supCa1.setCorporateActionType(CorporateActionType.EXERCISE_RIGHTS);
        supCa1.setEligible(6);
        supCa1.setUnconfirmed(null);
        supCa1.setTrusteeApprovalStatus(null);

        supCorporateActionList.add(supCa1);

        CorporateActionImpl invCa1 = new CorporateActionImpl();
        supCa1.setOrderNumber("0");
        supCa1.setCorporateActionType(CorporateActionType.EXERCISE_RIGHTS);
        supCa1.setEligible(6);
        supCa1.setUnconfirmed(null);
        supCa1.setTrusteeApprovalStatus(null);

        supCorporateActionList.add(supCa1);
        invCorporateActionList.add(invCa1);

        when(corporateActionIntegrationService.loadVoluntaryCorporateActions(any(DateTime.class), any(DateTime.class),
                anyListOf(String.class), any(ServiceErrors.class))).thenReturn(invCorporateActionList);

        when(corporateActionIntegrationService.loadVoluntaryCorporateActionsForSuper(any(DateTime.class), any(DateTime.class),
                anyListOf(String.class), any(ServiceErrors.class))).thenReturn(supCorporateActionList);

        CorporateActionListResult result;

        result = services.loadVoluntaryCorporateActions(new DateTime(), new DateTime(), new ArrayList<String>(), null);

        assertNotNull(result);

        result = services.loadMandatoryCorporateActions(new DateTime(), new DateTime(), new ArrayList<String>(), null);

        assertNotNull(result);
     }

    @Test
    public void testMergeApprovedList_whenDeclinedAndNoInvestment_thenNoEligibleSuper()
    {
        List<CorporateAction> invCorporateActionList = new ArrayList<>();
        List<CorporateAction> supCorporateActionList = new ArrayList<>();

        CorporateActionImpl supCa1 = new CorporateActionImpl();
        supCa1.setOrderNumber("0");
        supCa1.setCorporateActionType(CorporateActionType.EXERCISE_RIGHTS);
        supCa1.setEligible(6);
        supCa1.setUnconfirmed(null);
        supCa1.setTrusteeApprovalStatus(TrusteeApprovalStatus.DECLINED);

        supCorporateActionList.add(supCa1);

        CorporateActionImpl invCa1 = new CorporateActionImpl();
        invCa1.setOrderNumber("0");
        invCa1.setCorporateActionType(CorporateActionType.EXERCISE_RIGHTS);
        invCa1.setEligible(6);
        invCa1.setUnconfirmed(null);
        invCa1.setTrusteeApprovalStatus(TrusteeApprovalStatus.DECLINED);

        supCorporateActionList.add(supCa1);
        invCorporateActionList.add(invCa1);

        when(corporateActionIntegrationService.loadVoluntaryCorporateActions(any(DateTime.class), any(DateTime.class),
                anyListOf(String.class), any(ServiceErrors.class))).thenReturn(invCorporateActionList);

        when(corporateActionIntegrationService.loadVoluntaryCorporateActionsForSuper(any(DateTime.class), any(DateTime.class),
                anyListOf(String.class), any(ServiceErrors.class))).thenReturn(supCorporateActionList);

        CorporateActionListResult result;

        result = services.loadVoluntaryCorporateActions(new DateTime(), new DateTime(), new ArrayList<String>(), null);

        assertNotNull(result);
    }

    @Test
    public void testLoadVoluntaryCorporateActions_whenThereAreBothSuperAndInvestmentAccounts_thenEligibleCountShouldBeCombinedBasedOnTrusteeApproval() {
        List<CorporateAction> invCorporateActionList = new ArrayList<>();
        List<CorporateAction> supCorporateActionList = new ArrayList<>();

        CorporateActionImpl invCa1 = new CorporateActionImpl();
        invCa1.setOrderNumber("0");
        invCa1.setCorporateActionType(CorporateActionType.EXERCISE_RIGHTS);
        invCa1.setEligible(6);
        invCa1.setUnconfirmed(6);

        CorporateActionImpl supCa1 = new CorporateActionImpl();
        supCa1.setOrderNumber("0");
        supCa1.setCorporateActionType(CorporateActionType.EXERCISE_RIGHTS);
        supCa1.setEligible(6);
        supCa1.setUnconfirmed(6);

        invCorporateActionList.add(invCa1);
        supCorporateActionList.add(supCa1);

        when(corporateActionIntegrationService.loadVoluntaryCorporateActions(any(DateTime.class), any(DateTime.class),
                anyListOf(String.class), any(ServiceErrors.class))).thenReturn(invCorporateActionList);

        when(corporateActionIntegrationService.loadVoluntaryCorporateActionsForSuper(any(DateTime.class),
                any(DateTime.class), anyListOf(String.class), any(ServiceErrors.class))).thenReturn(supCorporateActionList);

        CorporateActionListResult result;

        // Trustee approved
        supCa1.setTrusteeApprovalStatus(TrusteeApprovalStatus.APPROVED);
        invCa1.setEligible(6);
        invCa1.setUnconfirmed(6);
        supCa1.setEligible(6);
        supCa1.setUnconfirmed(6);

        result = services.loadVoluntaryCorporateActions(new DateTime(), new DateTime(), new ArrayList<String>(), null);

        assertEquals(result.getCorporateActions().size(), 1);
        assertEquals(result.getCorporateActions().get(0).getCorporateActionType(), CorporateActionType.EXERCISE_RIGHTS);
        assertEquals(result.getCorporateActions().get(0).getEligible(), new Integer(12));
        assertEquals(result.getCorporateActions().get(0).getUnconfirmed(), new Integer(12));

        // Pending trustee approval
        supCa1.setTrusteeApprovalStatus(TrusteeApprovalStatus.PENDING);
        invCa1.setEligible(6);
        invCa1.setUnconfirmed(6);
        supCa1.setEligible(6);
        supCa1.setUnconfirmed(6);

        result = services.loadVoluntaryCorporateActions(new DateTime(), new DateTime(), new ArrayList<String>(), null);

        assertEquals(result.getCorporateActions().size(), 1);
        assertEquals(result.getCorporateActions().get(0).getCorporateActionType(), CorporateActionType.EXERCISE_RIGHTS);
        assertEquals(result.getCorporateActions().get(0).getEligible(), new Integer(12));
        assertEquals(result.getCorporateActions().get(0).getUnconfirmed(), new Integer(12));

        // Declined trustee approval
        supCa1.setTrusteeApprovalStatus(TrusteeApprovalStatus.DECLINED);
        invCa1.setEligible(6);
        invCa1.setUnconfirmed(6);
        supCa1.setEligible(6);
        supCa1.setUnconfirmed(6);

        result = services.loadVoluntaryCorporateActions(new DateTime(), new DateTime(), new ArrayList<String>(), null);

        assertEquals(result.getCorporateActions().size(), 1);
        assertEquals(result.getCorporateActions().get(0).getCorporateActionType(), CorporateActionType.EXERCISE_RIGHTS);
        assertEquals(result.getCorporateActions().get(0).getEligible(), new Integer(12));
        assertEquals(result.getCorporateActions().get(0).getUnconfirmed(), new Integer(12));
    }

    @Test
    public void testLoadMandatoryCorporateActions_whenThereIsNoSuperAccounts_thenEligibleCountShouldJustBeFromInvestmentAccounts() {
        List<CorporateAction> invCorporateActionList = new ArrayList<>();
        List<CorporateAction> supCorporateActionList = new ArrayList<>();

        CorporateAction invCa1 = mock(CorporateAction.class);
        when(invCa1.getOrderNumber()).thenReturn("0");
        when(invCa1.getCorporateActionType()).thenReturn(CorporateActionType.EXERCISE_RIGHTS);
        when(invCa1.getEligible()).thenReturn(6);
        when(invCa1.getUnconfirmed()).thenReturn(6);

        invCorporateActionList.add(invCa1);

        when(corporateActionIntegrationService.loadMandatoryCorporateActions(any(DateTime.class), any(DateTime.class),
                anyListOf(String.class), any(ServiceErrors.class))).thenReturn(invCorporateActionList);

        when(corporateActionIntegrationService.loadMandatoryCorporateActionsForSuper(any(DateTime.class),
                any(DateTime.class), anyListOf(String.class), any(ServiceErrors.class))).thenReturn(supCorporateActionList);

        CorporateActionListResult result =
                services.loadMandatoryCorporateActions(new DateTime(), new DateTime(), new ArrayList<String>(), null);

        assertEquals(result.getCorporateActions().size(), 1);
        assertEquals(result.getCorporateActions().get(0).getCorporateActionType(), CorporateActionType.EXERCISE_RIGHTS);
        assertEquals(result.getCorporateActions().get(0).getEligible(), new Integer(6));
        assertEquals(result.getCorporateActions().get(0).getUnconfirmed(), new Integer(6));
    }

    @Test
    public void testLoadMandatoryCorporateActions_whenThereIsNoInvestmentAccounts_thenEligibleCountShouldJustBeFromSuperAccounts() {
        List<CorporateAction> invCorporateActionList = new ArrayList<>();
        List<CorporateAction> supCorporateActionList = new ArrayList<>();

        CorporateActionImpl supCa1 = new CorporateActionImpl();
        supCa1.setOrderNumber("0");
        supCa1.setCorporateActionType(CorporateActionType.EXERCISE_RIGHTS);
        supCa1.setEligible(6);
        supCa1.setUnconfirmed(6);

        supCorporateActionList.add(supCa1);

        when(corporateActionIntegrationService.loadMandatoryCorporateActions(any(DateTime.class), any(DateTime.class),
                anyListOf(String.class), any(ServiceErrors.class))).thenReturn(invCorporateActionList);

        when(corporateActionIntegrationService.loadMandatoryCorporateActionsForSuper(any(DateTime.class),
                any(DateTime.class), anyListOf(String.class), any(ServiceErrors.class))).thenReturn(supCorporateActionList);

        CorporateActionListResult result;

        // Trustee approved
        supCa1.setTrusteeApprovalStatus(TrusteeApprovalStatus.APPROVED);

        result = services.loadMandatoryCorporateActions(new DateTime(), new DateTime(), new ArrayList<String>(), null);

        assertTrue(result.getHasSuperPension());
        assertEquals(result.getCorporateActions().size(), 1);
        assertEquals(result.getCorporateActions().get(0).getCorporateActionType(), CorporateActionType.EXERCISE_RIGHTS);
        assertEquals(result.getCorporateActions().get(0).getEligible(), new Integer(6));
        assertEquals(result.getCorporateActions().get(0).getUnconfirmed(), new Integer(6));

        // Pending trustee approval
        supCa1.setTrusteeApprovalStatus(TrusteeApprovalStatus.PENDING);

        result = services.loadMandatoryCorporateActions(new DateTime(), new DateTime(), new ArrayList<String>(), null);

        assertTrue(result.getHasSuperPension());
        assertEquals(result.getCorporateActions().size(), 1);
        assertEquals(result.getCorporateActions().get(0).getCorporateActionType(), CorporateActionType.EXERCISE_RIGHTS);
        assertEquals(result.getCorporateActions().get(0).getEligible(), new Integer(6));
        assertEquals(result.getCorporateActions().get(0).getUnconfirmed(), new Integer(6));

        // Declined trustee approval
        supCa1.setTrusteeApprovalStatus(TrusteeApprovalStatus.DECLINED);

        result = services.loadMandatoryCorporateActions(new DateTime(), new DateTime(), new ArrayList<String>(), null);

        assertTrue(result.getHasSuperPension());
        assertEquals(result.getCorporateActions().size(), 1);
        assertEquals(result.getCorporateActions().get(0).getCorporateActionType(), CorporateActionType.EXERCISE_RIGHTS);
        assertEquals(result.getCorporateActions().get(0).getEligible(), new Integer(6));
        assertEquals(result.getCorporateActions().get(0).getUnconfirmed(), new Integer(6));

    }

    @Test
    public void testLoadMandatoryCorporateActions_whenThereAreBothSuperAndInvestmentAccounts_thenEligibleCountShouldBeCombinedRegardlessOfTrusteeApproval() {
        List<CorporateAction> invCorporateActionList = new ArrayList<>();
        List<CorporateAction> supCorporateActionList = new ArrayList<>();

        CorporateActionImpl invCa1 = new CorporateActionImpl();
        invCa1.setOrderNumber("0");
        invCa1.setCorporateActionType(CorporateActionType.EXERCISE_RIGHTS);
        invCa1.setEligible(6);
        invCa1.setUnconfirmed(6);

        CorporateActionImpl supCa1 = new CorporateActionImpl();
        supCa1.setOrderNumber("0");
        supCa1.setCorporateActionType(CorporateActionType.EXERCISE_RIGHTS);
        supCa1.setEligible(6);
        supCa1.setUnconfirmed(6);

        invCorporateActionList.add(invCa1);
        supCorporateActionList.add(supCa1);

        when(corporateActionIntegrationService.loadMandatoryCorporateActions(any(DateTime.class), any(DateTime.class),
                anyListOf(String.class), any(ServiceErrors.class))).thenReturn(invCorporateActionList);

        when(corporateActionIntegrationService.loadMandatoryCorporateActionsForSuper(any(DateTime.class),
                any(DateTime.class), anyListOf(String.class), any(ServiceErrors.class))).thenReturn(supCorporateActionList);

        CorporateActionListResult result;

        // Trustee approved
        supCa1.setTrusteeApprovalStatus(TrusteeApprovalStatus.APPROVED);
        invCa1.setEligible(6);
        invCa1.setUnconfirmed(6);
        supCa1.setEligible(6);
        supCa1.setUnconfirmed(6);

        result = services.loadMandatoryCorporateActions(new DateTime(), new DateTime(), new ArrayList<String>(), null);

        assertEquals(result.getCorporateActions().size(), 1);
        assertEquals(result.getCorporateActions().get(0).getCorporateActionType(), CorporateActionType.EXERCISE_RIGHTS);
        assertEquals(result.getCorporateActions().get(0).getEligible(), new Integer(12));
        assertEquals(result.getCorporateActions().get(0).getUnconfirmed(), new Integer(12));

        // Pending trustee approval
        supCa1.setTrusteeApprovalStatus(TrusteeApprovalStatus.PENDING);
        invCa1.setEligible(6);
        invCa1.setUnconfirmed(6);
        supCa1.setEligible(6);
        supCa1.setUnconfirmed(6);

        result = services.loadMandatoryCorporateActions(new DateTime(), new DateTime(), new ArrayList<String>(), null);

        assertEquals(result.getCorporateActions().size(), 1);
        assertEquals(result.getCorporateActions().get(0).getCorporateActionType(), CorporateActionType.EXERCISE_RIGHTS);
        assertEquals(result.getCorporateActions().get(0).getEligible(), new Integer(12));
        assertEquals(result.getCorporateActions().get(0).getUnconfirmed(), new Integer(12));

        // Declined trustee approval - must have no effect for mandatory
        supCa1.setTrusteeApprovalStatus(TrusteeApprovalStatus.DECLINED);
        invCa1.setEligible(6);
        invCa1.setUnconfirmed(6);
        supCa1.setEligible(6);
        supCa1.setUnconfirmed(6);

        result = services.loadMandatoryCorporateActions(new DateTime(), new DateTime(), new ArrayList<String>(), null);

        assertEquals(result.getCorporateActions().size(), 1);
        assertEquals(result.getCorporateActions().get(0).getCorporateActionType(), CorporateActionType.EXERCISE_RIGHTS);
        assertEquals(result.getCorporateActions().get(0).getEligible(), new Integer(12));
        assertEquals(result.getCorporateActions().get(0).getUnconfirmed(), new Integer(12));
    }

    @Test
    public void testLoadVoluntaryCorporateActionsForIm() {
        CorporateAction invCa1 = mock(CorporateAction.class);

        when(imCorporateActionIntegrationService.loadVoluntaryCorporateActions(anyString(), any(DateTime.class),
                any(DateTime.class), anyString(), any(ServiceErrors.class))).thenReturn(Arrays.asList(invCa1));

        CorporateActionListResult result = services.loadVoluntaryCorporateActionsForIm("0", new DateTime(), new DateTime(), "0", null);

        assertEquals(result.getCorporateActions().size(), 1);
    }

    @Test
    public void testLoadMandatoryCorporateActionsForIm() {
        CorporateAction invCa1 = mock(CorporateAction.class);

        when(imCorporateActionIntegrationService.loadMandatoryCorporateActions(anyString(), any(DateTime.class),
                any(DateTime.class), anyString(), any(ServiceErrors.class))).thenReturn(Arrays.asList(invCa1));

        CorporateActionListResult result = services.loadMandatoryCorporateActionsForIm("0", new DateTime(), new DateTime(), "0", null);

        assertEquals(result.getCorporateActions().size(), 1);
    }

    @Test
    public void testLoadVoluntaryCorporateActionsForTrustee() {
        CorporateAction invCa1 = mock(CorporateAction.class);

        when(corporateActionIntegrationService.loadVoluntaryCorporateActionsForApproval(any(DateTime.class),
                any(DateTime.class), any(ServiceErrors.class))).thenReturn(Arrays.asList(invCa1));

        CorporateActionListResult result = services.loadVoluntaryCorporateActionsForApproval(new DateTime(), new DateTime(), null);

        assertEquals(result.getCorporateActions().size(), 1);
    }

    @Test
    public void testLoadCorporateActionDetailsResult() {
        CorporateActionDetailsResponse response = mock(CorporateActionDetailsResponse.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        when(response.getCorporateActionDetailsList()).thenReturn(Arrays.asList(details));
        CorporateActionAccount account = mock(CorporateActionAccount.class);

        when(corporateActionIntegrationService.loadCorporateActionDetails(anyString(), any(ServiceErrors.class))).thenReturn(
                response);
        when(corporateActionIntegrationService.loadCorporateActionAccountsDetails(anyString(), any(ServiceErrors.class))).thenReturn(
                Arrays.asList(account));

        // Include accounts
        CorporateActionContext result = services.loadCorporateActionDetailsContext("0", Boolean.FALSE, null);

        assertNotNull(result.getCorporateActionDetails());
        assertEquals(1, result.getCorporateActionAccountList().size());

        // Summary only (exclude accounts)
        result = services.loadCorporateActionDetailsContext("0", Boolean.TRUE, null);

        assertNotNull(result.getCorporateActionDetails());
        assertNull(result.getCorporateActionAccountList());

        // Summary null
        result = services.loadCorporateActionDetailsContext("0", null, null);
        assertNotNull(result.getCorporateActionDetails());
        assertEquals(1, result.getCorporateActionAccountList().size());

        // CA detail list en
        CorporateActionDetailsResponse responseNullDetails = mock(CorporateActionDetailsResponse.class);
        when(responseNullDetails.getCorporateActionDetailsList()).thenReturn(null);
        when(corporateActionIntegrationService.loadCorporateActionDetails(anyString(), any(ServiceErrors.class))).
                thenReturn(responseNullDetails);
        result = services.loadCorporateActionDetailsContext("0", Boolean.TRUE, null);
        assertNotNull(result);
        assertNull(result.getCorporateActionAccountList());
        assertNull(result.getCorporateActionDetails());

        // CA detail list empty
        List<CorporateActionDetails> emptyDetailsList = new ArrayList<>();
        CorporateActionDetailsResponse responseEmptyDetails = mock(CorporateActionDetailsResponse.class);
        when(responseEmptyDetails.getCorporateActionDetailsList()).thenReturn(emptyDetailsList);
        when(corporateActionIntegrationService.loadCorporateActionDetails(anyString(), any(ServiceErrors.class))).
                thenReturn(responseEmptyDetails);
        result = services.loadCorporateActionDetailsContext("0", Boolean.TRUE, null);
        assertNotNull(result);
        assertNull(result.getCorporateActionAccountList());
        assertNull(result.getCorporateActionDetails());

        // Response null
        when(corporateActionIntegrationService.loadCorporateActionDetails(anyString(), any(ServiceErrors.class))).
                thenReturn(null);
        result = services.loadCorporateActionDetailsContext("0", Boolean.TRUE, null);
        assertNotNull(result);
        assertNull(result.getCorporateActionAccountList());
        assertNull(result.getCorporateActionDetails());
    }

    @Test
    public void testLoadCorporateActionDetailsResultForIm() {
        CorporateActionDetailsResponse response = mock(CorporateActionDetailsResponse.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        when(response.getCorporateActionDetailsList()).thenReturn(Arrays.asList(details));
        CorporateActionAccount account = mock(CorporateActionAccount.class);

        when(corporateActionIntegrationService.loadCorporateActionDetails(anyString(), any(ServiceErrors.class))).thenReturn(
                response);
        when(corporateActionIntegrationService.loadCorporateActionAccountsDetailsForIm(anyString(), anyString(), any(ServiceErrors.class)))
                .thenReturn(Arrays.asList(account));

        CorporateActionContext result = services.loadCorporateActionDetailsContextForIm("0", "0", null);

        assertNotNull(result.getCorporateActionDetails());
        assertEquals(result.getCorporateActionAccountList().size(), 1);
    }

    @Test
    public void testLoadVoluntaryCorporateActions_modelGettersAndSetters() {
        List<CorporateAction> invCorporateActionList = new ArrayList<>();
        List<CorporateAction> supCorporateActionList = new ArrayList<>();

        CorporateActionImpl invCa1 = new CorporateActionImpl();
        invCa1.setOrderNumber("0");
        invCa1.setCorporateActionType(CorporateActionType.EXERCISE_RIGHTS);
        invCa1.setEligible(6);
        invCa1.setUnconfirmed(6);
        invCa1.setAssetId("ABC");
        invCa1.setCloseDate(DateTime.parse("2017-01-01"));
        invCa1.setAnnouncementDate(DateTime.parse("2017-01-01"));
        invCa1.setCorporateActionOfferType(CorporateActionOfferType.PUBLIC_OFFER);
        invCa1.setCorporateActionStatus(CorporateActionStatus.OPEN);
        invCa1.setVoluntaryFlag("N");
        invCa1.setPayDate(DateTime.parse("2017-01-01"));
        invCa1.setNonProRata("1");
        invCa1.setExDate(DateTime.parse("2017-01-01"));
        invCa1.setIncomeRate(BigDecimal.TEN);
        invCa1.setNotificationCnt(BigInteger.ONE);
        invCa1.setCorporateActionSecurityExchangeType(CorporateActionSecurityExchangeType.REINVESTMENT);
        invCa1.setFullyFrankedAmount(BigDecimal.TEN);
        invCa1.setFullyUnfrankedAmount(BigDecimal.TEN);
        invCa1.setCorporateTaxRate(BigDecimal.TEN);
        invCa1.setTrusteeApprovalStatus(TrusteeApprovalStatus.APPROVED);
        invCa1.setTrusteeApprovalStatusDate(DateTime.parse("2017-01-01"));
        invCa1.setTrusteeApprovalUserId("uncle bob");
        invCa1.setTrusteeApprovalUserName("uncle bob");
        invCa1.setEarlyClose("1");

        invCorporateActionList.add(invCa1);

        when(corporateActionIntegrationService.loadVoluntaryCorporateActions(any(DateTime.class), any(DateTime.class),
                anyListOf(String.class), any(ServiceErrors.class))).thenReturn(invCorporateActionList);

        when(corporateActionIntegrationService.loadVoluntaryCorporateActionsForSuper(any(DateTime.class),
                any(DateTime.class), anyListOf(String.class), any(ServiceErrors.class))).thenReturn(supCorporateActionList);

        CorporateActionListResult result = services.loadVoluntaryCorporateActions(new DateTime(), new DateTime(), new ArrayList<String>(), null);

        assertEquals(result.getCorporateActions().size(), 1);

        CorporateAction ca = result.getCorporateActions().get(0);

        assertEquals("ABC", ca.getAssetId());
        assertEquals(DateTime.parse("2017-01-01"), ca.getCloseDate());
        assertEquals(DateTime.parse("2017-01-01"), ca.getAnnouncementDate());
        assertEquals(CorporateActionOfferType.PUBLIC_OFFER, ca.getCorporateActionOfferType());
        assertEquals(CorporateActionStatus.OPEN, ca.getCorporateActionStatus());
        assertEquals("N", ca.getVoluntaryFlag());
        assertEquals(DateTime.parse("2017-01-01"), ca.getPayDate());
        assertEquals(true, ca.isNonProRata());
        assertEquals(DateTime.parse("2017-01-01"), ca.getExDate());
        assertEquals(BigDecimal.TEN, ca.getIncomeRate());
        assertEquals(BigDecimal.TEN, ca.getFullyFrankedAmount());
        assertEquals(BigDecimal.TEN, ca.getFullyUnfrankedAmount());
        assertEquals(BigDecimal.TEN, ca.getCorporateTaxRate());
        assertEquals(BigInteger.ONE, ca.getNotificationCnt());
        assertEquals(CorporateActionSecurityExchangeType.REINVESTMENT, ca.getCorporateActionSecurityExchangeType());
        assertEquals(TrusteeApprovalStatus.APPROVED, ca.getTrusteeApprovalStatus());
        assertEquals(DateTime.parse("2017-01-01"), ca.getTrusteeApprovalStatusDate());
        assertEquals(("uncle bob"), ca.getTrusteeApprovalUserId());
        assertEquals(("uncle bob"), ca.getTrusteeApprovalUserName());
        assertEquals(true, ca.isEarlyClose());

        invCa1.setNonProRata(null);
        assertEquals(false, invCa1.isNonProRata());
        invCa1.setNonProRata("0");
        assertEquals(false, invCa1.isNonProRata());
        invCa1.setEarlyClose(null);
        assertEquals(false, invCa1.isEarlyClose());
        invCa1.setEarlyClose("0");
        assertEquals(false, invCa1.isEarlyClose());
    }
}
