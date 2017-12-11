package com.bt.nextgen.api.policy.service;

import com.bt.nextgen.api.policy.model.*;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerUserImpl;
import com.bt.nextgen.service.avaloq.insurance.model.*;
import com.bt.nextgen.service.avaloq.insurance.service.PolicyIntegrationService;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.btfin.panorama.service.integration.broker.BrokerType;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class PolicyTrackingDtoServiceImplTest {

    @InjectMocks
    private PolicyTrackingDtoServiceImpl policyTrackingDtoService;

    @Mock
    private PolicyIntegrationService policyIntegrationService;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private PolicyUserPreferencesService policyUserPreferencesService;

    @Mock
    private PolicyUtility policyUtility;

    @Before
    public void setup() {
        List<PolicyApplications> policyApplications = new ArrayList<>();

        PolicyApplicationsImpl PolicyApplications1 = new PolicyApplicationsImpl();
        PolicyApplicationsImpl PolicyApplications2 = new PolicyApplicationsImpl();
        PolicyApplicationsImpl PolicyApplications3 = new PolicyApplicationsImpl();
        PolicyApplicationsImpl PolicyApplications4 = new PolicyApplicationsImpl();
        PolicyApplicationsImpl PolicyApplications5 = new PolicyApplicationsImpl();

        PolicyApplications1.setCustomerNumber("2514260");
        PolicyApplications1.setInsuredPersonGivenName("STACIE");
        PolicyApplications1.setInsuredPersonLastName("HURLEY");
        PolicyApplications1.setPolicyNumber("CF436983");
        PolicyApplications1.setTotalPremium(new BigDecimal("180.60"));
        PolicyApplications1.setApplicationReceivedDate(new DateTime("2016-05-10"));
        PolicyApplications1.setPolicyStatus(PolicyStatusCode.IN_FORCE);

        PolicyApplications2.setCustomerNumber("2514260");
        PolicyApplications2.setInsuredPersonGivenName("STACIE");
        PolicyApplications2.setInsuredPersonLastName("HURLEY");
        PolicyApplications2.setPolicyNumber("CL436983");
        PolicyApplications2.setTotalPremium(new BigDecimal("960.30"));
        PolicyApplications2.setApplicationReceivedDate(new DateTime("2016-05-09"));
        PolicyApplications2.setPolicyStatus(PolicyStatusCode.IN_FORCE);

        PolicyApplications3.setCustomerNumber("3159635");
        PolicyApplications3.setInsuredPersonGivenName("KADN");
        PolicyApplications3.setInsuredPersonLastName("DENNIS");
        PolicyApplications3.setPolicyNumber("CF436986");
        PolicyApplications3.setTotalPremium(new BigDecimal("211.92"));
        PolicyApplications3.setApplicationReceivedDate(new DateTime("2016-05-14"));
        PolicyApplications3.setPolicyStatus(PolicyStatusCode.IN_SUSPENSE);

        PolicyApplications4.setCustomerNumber("3159635");
        PolicyApplications4.setInsuredPersonGivenName("KADN");
        PolicyApplications4.setInsuredPersonLastName("DENNIS");
        PolicyApplications4.setPolicyNumber("SL436985");
        PolicyApplications4.setTotalPremium(new BigDecimal("611.64"));
        PolicyApplications4.setApplicationReceivedDate(new DateTime("2016-05-13"));
        PolicyApplications4.setPolicyStatus(PolicyStatusCode.IN_FORCE);

        PolicyApplications5.setCustomerNumber("3159635");
        PolicyApplications5.setInsuredPersonGivenName("KADN");
        PolicyApplications5.setInsuredPersonLastName("DENNIS");
        PolicyApplications5.setPolicyNumber("SL436985");
        PolicyApplications5.setTotalPremium(new BigDecimal("611.64"));
        PolicyApplications5.setApplicationReceivedDate(new DateTime("2016-05-13"));
        PolicyApplications5.setPolicyStatus(PolicyStatusCode.CANCELLED);

        policyApplications.add(PolicyApplications1);
        policyApplications.add(PolicyApplications2);
        policyApplications.add(PolicyApplications3);
        policyApplications.add(PolicyApplications4);

        BrokerImpl broker = new BrokerImpl( com.bt.nextgen.service.integration.broker.BrokerKey.valueOf("13245"), BrokerType.ADVISER);
        broker.setAdviserPPid("123456");

        List<Broker> brokerList = new ArrayList();
        brokerList.add(broker);

        List<PolicyTracking> insurancesTrackings = new ArrayList<>();
        PolicyTrackingImpl policyTracking = new PolicyTrackingImpl();
        policyTracking.setFNumber("F0123174");
        insurancesTrackings.add(policyTracking);

        Mockito.when(policyIntegrationService.getRecentLivesInsured(Matchers.anyList(), (ServiceErrors) Matchers.anyObject())).thenReturn(policyApplications);
        Mockito.when(brokerIntegrationService.getBroker((com.bt.nextgen.service.integration.broker.BrokerKey) Matchers.anyObject(),
                (ServiceErrors)Matchers.anyObject())).thenReturn(broker);
        Mockito.when(brokerIntegrationService.getBrokersForJob(Mockito.any(UserProfile.class), Mockito.any(ServiceErrors.class))).thenReturn(brokerList);
        Mockito.when(policyIntegrationService.getFNumbers(Matchers.anyString(), (ServiceErrors) Matchers.anyObject())).thenReturn(insurancesTrackings);
        Mockito.when(policyUtility.getAdviserPpId(Matchers.anyString(), (ServiceErrors)Matchers.anyObject())).thenReturn("123456");

        UserProfile userProfile = Mockito.mock(UserProfile.class);
        Mockito.when(userProfileService.getActiveProfile()).thenReturn(userProfile);
    }

    @Test
    public void testSearch() {
        List<ApiSearchCriteria> apiSearchCriterias = new ArrayList<>();
        apiSearchCriterias.add(new ApiSearchCriteria(Attribute.BROKER_ID, "A930F541FD7A98A7"));
        apiSearchCriterias.add(new ApiSearchCriteria(Attribute.INSURANCE_FNUMBER, "F0123174"));
        List<PolicyTrackingIdentifier> trackingDtos = policyTrackingDtoService.search(apiSearchCriterias, new ServiceErrorsImpl());
        Assert.assertNotNull(trackingDtos);
        Assert.assertTrue(trackingDtos.size()==2);

        ApplicationTrackingDto trackingDto1 = (ApplicationTrackingDto)trackingDtos.get(0);
        Assert.assertEquals("Under assessment", trackingDto1.getApplicationStatus());
        Assert.assertEquals("KADN", trackingDto1.getInsuredPersonGivenName());
        Assert.assertEquals((new DateTime(2016, 5, 13, 0, 0)).toString(), trackingDto1.getApplicationReceivedDate());
        Assert.assertEquals(new BigDecimal("823.56"),trackingDto1.getTotalPremium());

        ApplicationTrackingDto trackingDto2 = (ApplicationTrackingDto)trackingDtos.get(1);
        Assert.assertEquals("Assessment complete", trackingDto2.getApplicationStatus());
        Assert.assertEquals("STACIE", trackingDto2.getInsuredPersonGivenName());
        Assert.assertEquals((new DateTime(2016, 5, 9, 0, 0)).toString(), trackingDto2.getApplicationReceivedDate());
        Assert.assertEquals(new BigDecimal("1140.90"),trackingDto2.getTotalPremium());
    }

    @Test
    public void testUnderwritingSearch() {
        PolicyUnderwritingImpl policyUnderwriting = new PolicyUnderwritingImpl();

        List<PolicyUnderWritingNotesImpl> policyUnderWritingNotes = new ArrayList<>();
        PolicyUnderWritingNotesImpl policyUnderWritingNotes1 = new PolicyUnderWritingNotesImpl();
        PolicyUnderWritingNotesImpl policyUnderWritingNotes2 = new PolicyUnderWritingNotesImpl();
        PolicyUnderWritingNotesImpl policyUnderWritingNotes3 = new PolicyUnderWritingNotesImpl();
        PolicyUnderWritingNotesImpl policyUnderWritingNotes4 = new PolicyUnderWritingNotesImpl();

        policyUnderWritingNotes1.setCodeDescription("Follow up");
        policyUnderWritingNotes1.setUnderwritingDetails("A suspense review has been sent to the planner");
        policyUnderWritingNotes1.setDateRequested(new DateTime());
        policyUnderWritingNotes1.setSignOffDate(new DateTime());

        policyUnderWritingNotes2.setCodeDescription("Follow up");
        policyUnderWritingNotes2.setUnderwritingDetails("A suspense review has been sent to the planner");
        policyUnderWritingNotes2.setDateRequested(new DateTime());
        policyUnderWritingNotes2.setSignOffDate(new DateTime());

        policyUnderWritingNotes3.setCodeDescription("Follow up");
        policyUnderWritingNotes3.setUnderwritingDetails("A suspense review has been sent to the planner");
        policyUnderWritingNotes3.setDateRequested(new DateTime());

        policyUnderWritingNotes4.setCodeDescription("Follow up");
        policyUnderWritingNotes4.setUnderwritingDetails("A suspense review has been sent to the planner");
        policyUnderWritingNotes4.setDateRequested(new DateTime());
        policyUnderWritingNotes4.setSignOffDate(new DateTime("1800-01-01"));

        policyUnderWritingNotes.add(policyUnderWritingNotes1);
        policyUnderWritingNotes.add(policyUnderWritingNotes2);
        policyUnderWritingNotes.add(policyUnderWritingNotes3);
        policyUnderWritingNotes.add(policyUnderWritingNotes4);

        List<PolicyTrackingImpl> policyDetails = new ArrayList<>();
        PolicyTrackingImpl policyTracking = new PolicyTrackingImpl();
        policyTracking.setPolicyNumber("CF436983");
        policyTracking.setPolicyStatus(PolicyStatusCode.IN_FORCE);
        policyTracking.setAccountNumber("2365987415");
        policyTracking.setPolicyType(PolicyType.INCOME_PROTECTION);
        policyDetails.add(policyTracking);

        policyUnderwriting.setPolicyDetails(policyDetails);
        policyUnderwriting.setUnderWritingNotes(policyUnderWritingNotes);

        List<PolicyTracking> policyTrackings = new ArrayList<>();
        PolicyTrackingImpl policyTracking1 = new PolicyTrackingImpl();
        policyTracking1.setPolicyNumber("CF436983");
        policyTracking1.setAccountNumber("2365987415");
        policyTrackings.add(policyTracking1);

        AccountKey accountKey = AccountKey.valueOf("23659");
        WrapAccountImpl wrapAccount = new WrapAccountImpl();
        wrapAccount.setAccountKey(accountKey);
        wrapAccount.setAccountNumber("2365987415");
        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();
        accountMap.put(accountKey, wrapAccount);

        BrokerUserImpl brokerUser = new BrokerUserImpl(UserKey.valueOf("201602207"));

        Mockito.when(policyIntegrationService.getUnderwritingNotes(Matchers.anyString(), (ServiceErrors)Matchers.anyObject())).thenReturn(policyUnderwriting);
        Mockito.when(policyIntegrationService.getPolicyByCustomerNumber(Matchers.anyList(), Matchers.anyString(), (ServiceErrors)Matchers.anyObject())).thenReturn(policyTrackings);
        Mockito.when(accountIntegrationService.loadWrapAccountWithoutContainers((ServiceErrors)Matchers.anyObject())).thenReturn(accountMap);
        Mockito.when(brokerIntegrationService.getAdviserBrokerUser((BrokerKey) Matchers.anyObject() ,(ServiceErrors)Matchers.anyObject())).thenReturn(brokerUser);

        CustomerKey customerKey = new CustomerKey("8D6C83FDEB7B299C16478B7848E9AD7779BA7A5263CD3926");
        List<ApiSearchCriteria> apiSearchCriterias = new ArrayList<>();
        apiSearchCriterias.add(new ApiSearchCriteria(Attribute.BROKER_ID, "A930F541FD7A98A7"));
        apiSearchCriterias.add(new ApiSearchCriteria(Attribute.INSURANCE_FNUMBER, "F0123174"));

        List<PolicyTrackingIdentifier> underWritingNotes = policyTrackingDtoService.search(customerKey, apiSearchCriterias, new ServiceErrorsImpl());
        Assert.assertNotNull(underWritingNotes);

        PolicyUnderwritingDto underwritingDto = (PolicyUnderwritingDto)underWritingNotes.get(0);

        Assert.assertTrue(underwritingDto.getUnderwritingNotesList().size()==2);
        PolicyUnderwritingNotesDto underwritingNotesDto1 = underwritingDto.getUnderwritingNotesList().get(0);
        Assert.assertEquals("COMPLETED", underwritingNotesDto1.getStatus());
        Assert.assertTrue(underwritingNotesDto1.getNotes().size()==2);
        PolicyUnderwritingNotesDetailsDto detailsDto1 = underwritingNotesDto1.getNotes().get(0);
        Assert.assertEquals("Follow up", detailsDto1.getAction());
        Assert.assertNotNull(detailsDto1.getDateCompleted());

        PolicyUnderwritingNotesDto underwritingNotesDto2 = underwritingDto.getUnderwritingNotesList().get(1);
        Assert.assertEquals("IN_PROGRESS", underwritingNotesDto2.getStatus());
        Assert.assertTrue(underwritingNotesDto2.getNotes().size()==2);
        PolicyUnderwritingNotesDetailsDto detailsDto2 = underwritingNotesDto2.getNotes().get(0);
        Assert.assertEquals("Follow up", detailsDto2.getAction());
        Assert.assertNull(detailsDto2.getDateCompleted());

        List<PolicyDetailsDto> policyDetailsDtos = underwritingDto.getPolicyDetails();
        Assert.assertTrue(policyDetailsDtos.size()>0);
        PolicyDetailsDto policyDetailsDto = policyDetailsDtos.get(0);
        Assert.assertEquals("CF436983", policyDetailsDto.getPolicyNumber());
        Assert.assertEquals("23659", EncodedString.toPlainText("4B09AB9CE52B58C80B94E09F27D5CEF2E3B70924F8529628"));
    }
}
