package com.bt.nextgen.api.adviser.service;

import com.bt.nextgen.api.adviser.model.SingleAdviserForUserDto;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.avaloq.broker.BrokerIdentifierImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerUserImpl;
import com.bt.nextgen.service.integration.broker.*;
import com.bt.nextgen.service.integration.user.UserKey;
import com.btfin.panorama.core.security.integration.userinformation.UserInformation;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;
import com.btfin.panorama.service.integration.broker.BrokerType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SingleAdviserForUserDtoServiceImplTest {
    @InjectMocks
    private SingleAdviserForUserDtoServiceImpl singleAdviserForUserDtoService;

    @Mock
    private BrokerIntegrationService brokerService;

    @Mock
    private UserProfileService userProfileService;

    @Test
    public void findOneShouldReturnSingleUserIfLoggedinUserIsAdviser(){
        List<Broker> list = new ArrayList();
        Broker broker1 = new BrokerImpl( BrokerKey.valueOf("AdviserID"), BrokerType.ADVISER);
        Broker broker2 = new BrokerImpl( BrokerKey.valueOf("SomeID"),BrokerType.DEALER);
        list.add(broker1);
        list.add(broker2);
        BrokerUser brokerUser = createBrokerUser();
        when(brokerService.getBrokersForJob(any(UserInformation.class), any(ServiceErrors.class))).thenReturn(list);
        when(brokerService.getAdviserBrokerUser(any(BrokerKey.class),any(ServiceErrors.class))).thenReturn(brokerUser);
        when(userProfileService.isAdviser()).thenReturn(true);
        when(userProfileService.getActiveProfile()).thenReturn(mock(UserProfile.class));

        SingleAdviserForUserDto result = singleAdviserForUserDtoService.findOne(null);
        assertNotNull(result);
        assertNotNull(result.getAdviserPositionId());
        assertThat(result.getFirstName(),is("Test"));
        assertThat(result.getLastName(),is("Last"));
        assertThat(result.getFullName(),is("Test Last"));
        assertThat(result.isSingleAdviser(), is(true));
        assertThat(EncodedString.toPlainText(result.getAdviserPositionId()), is("AdviserID"));
    }

    @Test
    public void testFindOneShouldReturnSingleUserIfLoggedInUserNotAdviserAndHasOneAdviser(){
        BrokerUser brokerUser = createBrokerUser();
        when(userProfileService.isAdviser()).thenReturn(false);
        when(userProfileService.getActiveProfile()).thenReturn(mock(UserProfile.class));

        List<BrokerIdentifier> list = Arrays.asList(createBrokerIdentifier("adviserId"));
        when(brokerService.getAdvisersForUser(any(UserInformation.class), any(ServiceErrors.class))).thenReturn(list);
        when(brokerService.getAdviserBrokerUser(any(BrokerKey.class),any(ServiceErrors.class))).thenReturn(brokerUser);

        SingleAdviserForUserDto result = singleAdviserForUserDtoService.findOne(null);
        assertNotNull(result.getAdviserPositionId());
        assertThat(result.isSingleAdviser(), is(true));
        assertThat(EncodedString.toPlainText(result.getAdviserPositionId()), is("adviserId"));
    }

    @Test
    public void testFindOneShouldReturnFalseOnFlagIfLoggedInUserNotAdviserAndHasMultipleAdvisers(){
        when(userProfileService.isAdviser()).thenReturn(false);
        when(userProfileService.getActiveProfile()).thenReturn(mock(UserProfile.class));

        List<BrokerIdentifier> list = Arrays.asList(createBrokerIdentifier("adviserId"), createBrokerIdentifier("someId"));
        when(brokerService.getAdvisersForUser(any(UserInformation.class), any(ServiceErrors.class))).thenReturn(list);

        SingleAdviserForUserDto result = singleAdviserForUserDtoService.findOne(null);
        assertThat(result.isSingleAdviser(), is(false));
        assertNull(result.getAdviserPositionId());
    }

    private BrokerIdentifier createBrokerIdentifier(String brokerKey){
        BrokerIdentifierImpl brokerIdentifier = new BrokerIdentifierImpl();
        brokerIdentifier.setKey(BrokerKey.valueOf(brokerKey));
        return brokerIdentifier;
    }

    private BrokerUser createBrokerUser(){
        BrokerUserImpl brokerUserImpl = new BrokerUserImpl(UserKey.valueOf("32154"));
        brokerUserImpl.setFirstName("Test");
        brokerUserImpl.setLastName("Last");
        return brokerUserImpl;
    }
}