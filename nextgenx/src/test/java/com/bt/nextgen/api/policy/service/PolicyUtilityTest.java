package com.bt.nextgen.api.policy.service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.broker.BrokerAnnotationHolder;
import com.bt.nextgen.service.avaloq.broker.BrokerUserImpl;
import com.bt.nextgen.service.avaloq.client.AbstractGenericClientImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userprofile.JobProfileIdentifier;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.exception.ServiceException;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class PolicyUtilityTest {

    @InjectMocks
    private PolicyUtility policyUtility;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    @Mock
    private ClientIntegrationService clientIntegrationService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private WrapAccount wrapAccount;

    @Test
    public void testGetAdviserPpId() {
        Collection<BrokerIdentifier> brokerIdentifiers = new ArrayList<>();
        BrokerAnnotationHolder brokerAnnotationHolder = new BrokerAnnotationHolder();
        brokerAnnotationHolder.setBrokerId("23654");
        brokerIdentifiers.add(brokerAnnotationHolder);

        BrokerUserImpl brokerUser = new BrokerUserImpl(UserKey.valueOf("11111"));
        brokerUser.setClientKey(ClientKey.valueOf("36598"));

        AbstractGenericClientImpl abstractGenericClient = new AbstractGenericClientImpl() {
            @Override
            public String getPpId() {
                return "236598";
            }
        };

        when(brokerIntegrationService.getAdvisersForUser((JobProfileIdentifier) Matchers.anyObject(),
                (ServiceErrors)Matchers.anyObject())).thenReturn(brokerIdentifiers);
        when(brokerIntegrationService.getAdviserBrokerUser((BrokerKey) Matchers.anyObject(),
                (ServiceErrors)Matchers.anyObject())).thenReturn(brokerUser);
        when(clientIntegrationService.loadGenericClientDetails((ClientKey)Matchers.anyObject(),
                (ServiceErrors)Matchers.anyObject())).thenReturn(abstractGenericClient);

        when(userProfileService.isAdviser()).thenReturn(false);
        String ppId = policyUtility.getAdviserPpId(null, new ServiceErrorsImpl());
        Assert.assertNotNull(ppId);
        Assert.assertEquals("236598", ppId);

        when(userProfileService.getPpId()).thenReturn("569845");
        ppId = policyUtility.getAdviserPpId("122CCEC4B0FB19C0", new ServiceErrorsImpl());
        Assert.assertNotNull(ppId);
        Assert.assertEquals("236598", ppId);

        brokerIdentifiers = new ArrayList<>();
        when(brokerIntegrationService.getAdvisersForUser((JobProfileIdentifier) Matchers.anyObject(),
                (ServiceErrors)Matchers.anyObject())).thenReturn(brokerIdentifiers);
        ppId = policyUtility.getAdviserPpId(null, new ServiceErrorsImpl());
        Assert.assertNull(ppId);

        when(userProfileService.isAdviser()).thenReturn(true);
        ppId = policyUtility.getAdviserPpId(null, new ServiceErrorsImpl());
        Assert.assertNotNull(ppId);
        Assert.assertEquals("569845", ppId);
    }

    @Test(expected = ServiceException.class)
    public void testGetPolicyConverterForNull() {
        policyUtility.getPolicyDtoConverter(null, new ServiceErrorsImpl());
    }

    @Test(expected = ServiceException.class)
    public void testGetPolicyConverterForEmptyAccount() {
        policyUtility.getPolicyDtoConverter(" ", new ServiceErrorsImpl());
    }

    @Test
    public void testGetPolicyConverter() {
        PolicyDtoConverter converter = policyUtility.getPolicyDtoConverter("123125", new ServiceErrorsImpl());
        assertThat(converter, is(not(nullValue())));
    }
}
