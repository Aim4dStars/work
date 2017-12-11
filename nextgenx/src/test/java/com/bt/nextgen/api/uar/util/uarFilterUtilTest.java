package com.bt.nextgen.api.uar.util;

import com.bt.nextgen.api.uar.model.UarDetailsDto;
import com.bt.nextgen.service.avaloq.broker.BrokerUserImpl;
import com.bt.nextgen.service.integration.broker.JobAuthorizationRole;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import com.bt.nextgen.service.integration.userprofile.JobProfileIdentifier;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.broker.BrokerAnnotationHolder;
import com.bt.nextgen.service.avaloq.broker.JobProfileAnnotatedHolder;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.JobProfileUser;
import com.bt.nextgen.service.integration.uar.*;
import com.btfin.panorama.service.client.broker.dto.BrokerClientImpl;
import com.btfin.panorama.service.client.broker.dto.BrokerRoleClientImpl;
import com.btfin.panorama.service.client.broker.dto.BrokerUserClientImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.broker.BrokerType;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by l082026 on 24/01/2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class uarFilterUtilTest {

    @InjectMocks
    private UarFilterUtil uarFilterUtil;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    @Mock
    private UarIntegrationService uarIntegrationService;

    @Mock
    private UserProfileService userProfileService;

    @Test
    public void getUarClientsTest() {

        Broker broker = createBroker("123456");
        UarDoc uardoc = createUarDoc();
        UarResponse uarResponse = createUarResponse();
        BrokerAnnotationHolder brokerAnnotationHolder = createBrokerAnnotationHolder();
        BrokerUserImpl brokerUser = createBrokerUserImpl();
        Mockito.when(brokerIntegrationService.getBrokersForJob(Mockito.any(UserProfile.class), Mockito.any(ServiceErrors.class))).thenReturn(Arrays.asList(broker));
        Mockito.when(uarIntegrationService.getUarOrderId(Mockito.any(List.class), Mockito.any(ServiceErrors.class))).thenReturn(uardoc);
        Mockito.when(uarIntegrationService.getUarAccounts(Mockito.any(UarRequest.class), Mockito.any(ServiceErrors.class))).thenReturn(uarResponse);
        Mockito.when(brokerIntegrationService.getBroker(Mockito.any(BrokerKey.class), Mockito.any(ServiceErrors.class))).thenReturn(brokerAnnotationHolder);
        Mockito.when(brokerIntegrationService.getBrokerUser(Mockito.any(JobProfileIdentifier.class), Mockito.any(ServiceErrors.class))).thenReturn(brokerUser);
        List<UarDetailsDto> uarDetailsDto = uarFilterUtil.getUarClients(new ServiceErrorsImpl());
        Assert.assertEquals(BigDecimal.valueOf(132456), uarDetailsDto.get(0).getDocId());
        Assert.assertEquals("Green, Rachel", uarDetailsDto.get(0).getUarComponent().get(0).getUserName());
        Assert.assertEquals("Adviser (Investor managed)", uarDetailsDto.get(0).getUarComponent().get(0).getUserRole());
    }


    @Test
    public void getUarClientsTest_OffThread() {

        Broker broker = createBroker("123456");
        UarDoc uardoc = createUarDoc();
        UarResponse uarResponse = createUarResponse();
        BrokerAnnotationHolder brokerAnnotationHolder = createBrokerAnnotationHolder();
        BrokerUserClientImpl brokerUser = createBrokerUserClientImpl();
        Mockito.when(brokerIntegrationService.getBrokersForJob(Mockito.any(UserProfile.class), Mockito.any(ServiceErrors.class))).thenReturn(Arrays.asList(broker));
        Mockito.when(uarIntegrationService.getUarOrderId(Mockito.any(List.class), Mockito.any(ServiceErrors.class))).thenReturn(uardoc);
        Mockito.when(uarIntegrationService.getUarAccounts(Mockito.any(UarRequest.class), Mockito.any(ServiceErrors.class))).thenReturn(uarResponse);
        Mockito.when(brokerIntegrationService.getBroker(Mockito.any(BrokerKey.class), Mockito.any(ServiceErrors.class))).thenReturn(brokerAnnotationHolder);
        Mockito.when(brokerIntegrationService.getBrokerUser(Mockito.any(JobProfileIdentifier.class), Mockito.any(ServiceErrors.class))).thenReturn(brokerUser);
        List<UarDetailsDto> uarDetailsDto = uarFilterUtil.getUarClients(new ServiceErrorsImpl());
        Assert.assertEquals(BigDecimal.valueOf(132456), uarDetailsDto.get(0).getDocId());
        Assert.assertEquals("Green, Rachel", uarDetailsDto.get(0).getUarComponent().get(0).getUserName());
        Assert.assertEquals("Adviser (Investor managed)", uarDetailsDto.get(0).getUarComponent().get(0).getUserRole());
    }

    private JobProfileUser createJobProfileUser() {
        JobProfileAnnotatedHolder jobProfileAnnotatedHolder = new JobProfileAnnotatedHolder();
        jobProfileAnnotatedHolder.setJobId("772226");
        jobProfileAnnotatedHolder.setJobRole(JobRole.ADVISER);
        jobProfileAnnotatedHolder.setFirstName("Rachel");
        jobProfileAnnotatedHolder.setLastName("Green");
        return (JobProfileUser) jobProfileAnnotatedHolder;
     }

    private BrokerAnnotationHolder createBrokerAnnotationHolder() {
        BrokerAnnotationHolder brokerAnnotationHolder = new BrokerAnnotationHolder();
        brokerAnnotationHolder.setUserExperience(UserExperience.ASIM);
        List<JobProfileUser> jobProfileUserList = new ArrayList<>();
        jobProfileUserList.add(createJobProfileUser());
        brokerAnnotationHolder.setJobList(jobProfileUserList);
        return brokerAnnotationHolder;
    }

    private BrokerUserImpl createBrokerUserImpl() {
        BrokerUserImpl brokerUser = new BrokerUserImpl(UserKey.valueOf("99884"), JobKey.valueOf("772226"));
        brokerUser.addBroker(JobRole.ADVISER, BrokerKey.valueOf("123456"), JobAuthorizationRole.Supervisor_Transact);
        brokerUser.setFirstName("Rachel");
        brokerUser.setLastName("Green");
        return brokerUser;
    }

    private BrokerUserClientImpl createBrokerUserClientImpl() {
        BrokerUserClientImpl brokerUser = new BrokerUserClientImpl();
        brokerUser.setJob(JobKey.valueOf("772226"));
        BrokerRoleClientImpl brokerRoleClient = new BrokerRoleClientImpl();
        brokerRoleClient.setKey(BrokerKey.valueOf("123456"));
        brokerRoleClient.setRole(JobRole.ADVISER);
        brokerRoleClient.setAuthorizationRole(JobAuthorizationRole.Supervisor_Transact);
        Collection<BrokerRoleClientImpl> roles = Arrays.asList(brokerRoleClient);
        brokerUser.setRoles(roles);
        brokerUser.setFirstName("Rachel");
        brokerUser.setLastName("Green");
        return brokerUser;
    }

    private Broker createBroker(String myBrokerId) {
        Broker broker = mock(Broker.class);
        when(broker.getBrokerType()).thenReturn(BrokerType.ADVISER);
        when(broker.getKey()).thenReturn(BrokerKey.valueOf(myBrokerId));
        when(broker.getDealerKey()).thenReturn(BrokerKey.valueOf("DEALER_KEY"));
        when(broker.getUserExperience()).thenReturn(UserExperience.ASIM);
        when(broker.getPositionName()).thenReturn("Adviser");
        return broker;
    }

    private UarDoc createUarDoc() {
        UarDoc uarDoc = mock(UarDoc.class);
        when(uarDoc.getDocId()).thenReturn(BigDecimal.valueOf(123456));
        return uarDoc;
    }

    private UarResponse createUarResponse() {
        UarResponse uarResponse = new UarResponseImpl();
        uarResponse.setDocId(BigDecimal.valueOf(132456));
        uarResponse.setBrokerId("654321");
        uarResponse.setUarDate(new DateTime());
        List<UarRecords> uarRecordsList = new ArrayList<>();
        uarRecordsList.add(createUarRecords());
        uarResponse.setUarRecords(uarRecordsList);
        return uarResponse;
    }

    private UarRecords createUarRecords() {
        UarRecords uarRecords = new UarRecords();
        uarRecords.setBrokerId("654321");
        uarRecords.setIsFrozen(false);
        uarRecords.setIsInvalid(false);
        uarRecords.setPersonName("Green Rachel");
        uarRecords.setPersonId("44705");
        uarRecords.setJobId("772226");
        uarRecords.setRecordType("DIR");
        uarRecords.setRecordIndex(BigDecimal.ONE);
        uarRecords.setBrokerName("Evolution Advisory Pty Ltd (Dealer Group)");
        return uarRecords;
    }
}
