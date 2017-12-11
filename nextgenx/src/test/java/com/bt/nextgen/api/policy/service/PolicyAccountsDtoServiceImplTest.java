package com.bt.nextgen.api.policy.service;

import com.bt.nextgen.api.policy.model.AccountPolicyDto;
import com.bt.nextgen.api.policy.model.PolicyDto;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.client.ClientImpl;
import com.bt.nextgen.service.avaloq.domain.IndividualImpl;
import com.bt.nextgen.service.avaloq.insurance.model.Policy;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyImpl;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyStatusCode;
import com.bt.nextgen.service.avaloq.insurance.model.PolicySubType;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyType;
import com.bt.nextgen.service.avaloq.insurance.model.PremiumFrequencyType;
import com.bt.nextgen.service.avaloq.insurance.service.PolicyIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

@RunWith(MockitoJUnitRunner.class)
public class PolicyAccountsDtoServiceImplTest {

    @InjectMocks
    private PolicyAccountsDtoServiceImpl policyAccountsDtoService;

    @Mock
    private PolicyUtility policyUtility;

    @Mock
    private PolicyIntegrationService policyIntegrationService;

    @Mock
    private ClientIntegrationService clientIntegrationService;

    @Before
    public void setup() {
        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();

        AccountKey accountKey1 = AccountKey.valueOf("23659");
        AccountKey accountKey2 = AccountKey.valueOf("23658");
        AccountKey accountKey3 = AccountKey.valueOf("23657");

        ClientKey clientKey1 = ClientKey.valueOf("23659");
        ClientKey clientKey2 = ClientKey.valueOf("23859");
        ClientKey clientKey3 = ClientKey.valueOf("23759");

        WrapAccountImpl wrapAccount1 = new WrapAccountImpl();
        WrapAccountImpl wrapAccount2 = new WrapAccountImpl();
        WrapAccountImpl wrapAccount3 = new WrapAccountImpl();

        wrapAccount1.setAccountKey(accountKey1);
        wrapAccount1.setAccountNumber("254698789");
        wrapAccount1.setAccountName("Super acct");
        wrapAccount1.setAccountStructureType(AccountStructureType.SUPER);
        List<ClientKey> owners1 = new ArrayList<>();
        owners1.add(clientKey1);
        wrapAccount1.setAccountOwners(owners1);

        wrapAccount2.setAccountKey(accountKey2);
        wrapAccount2.setAccountName("SMSF acct");
        wrapAccount2.setAccountNumber("254686786");
        wrapAccount2.setAccountStructureType(AccountStructureType.SMSF);
        List<ClientKey> owners2 = new ArrayList<>();
        owners2.add(clientKey1);
        owners2.add(clientKey2);
        wrapAccount2.setAccountOwners(owners2);

        wrapAccount3.setAccountKey(accountKey3);
        wrapAccount3.setAccountName("company acct");
        wrapAccount3.setAccountNumber("254686787");
        wrapAccount3.setAccountStructureType(AccountStructureType.Company);
        List<ClientKey> owners3 = new ArrayList<>();
        owners3.add(clientKey2);
        owners3.add(clientKey3);
        wrapAccount3.setAccountOwners(owners3);

        accountMap.put(accountKey1, wrapAccount1);
        accountMap.put(accountKey2, wrapAccount2);
        accountMap.put(accountKey3, wrapAccount3);

        PolicyDtoConverter policyDtoConverter = new PolicyDtoConverter(accountMap);
        Mockito.when(clientIntegrationService.loadClientMap((ServiceErrors)Matchers.anyObject())).thenReturn(getClientMap());
        Mockito.when(policyUtility.getPolicyDtoConverter(anyString(), any(ServiceErrors.class))).thenReturn(policyDtoConverter);
    }

    @Test
    public void testSearch() {
        List<Policy> policies = new ArrayList<>();

        PolicyImpl policy1 = new PolicyImpl();
        policy1.setPolicyNumber("CF506182");
        policy1.setPolicyType(PolicyType.INCOME_PROTECTION);
        policy1.setPolicySubType(PolicySubType.BUSINESS_OVERHEAD);
        policy1.setAccountNumber("254686786");
        policy1.setAccountId("23658");
        policy1.setPolicyFrequency(PremiumFrequencyType.YEARLY);
        policy1.setStatus(PolicyStatusCode.IN_FORCE);

        PolicyImpl policy2 = new PolicyImpl();
        policy2.setPolicyNumber("CL898278");
        policy2.setPolicyType(PolicyType.TERM_LIFE);
        policy2.setAccountNumber("254686789");
        policy2.setAccountId("23659");
        policy2.setPolicyFrequency(PremiumFrequencyType.MONTHLY);
        policy2.setStatus(PolicyStatusCode.IN_FORCE);

        PolicyImpl policy3 = new PolicyImpl();
        policy3.setPolicyNumber("CM287468");
        policy3.setPolicyType(PolicyType.STAND_ALONE_TPD);
        policy3.setAccountNumber("254686786");
        policy3.setAccountId("23658");
        policy3.setPolicyFrequency(PremiumFrequencyType.MONTHLY);
        policy3.setStatus(PolicyStatusCode.IN_FORCE);

        policies.add(policy1);
        policies.add(policy2);
        policies.add(policy3);

        Mockito.when(policyIntegrationService.retrievePoliciesByAccountNumber(Matchers.anyString(), (ServiceErrors) Matchers.anyObject())).thenReturn(policies);

        com.bt.nextgen.api.account.v3.model.AccountKey accountKey = new com.bt.nextgen.api.account.v3.model.AccountKey("C0979F31BBF343CEAAD23D64496159F0C36B9C3BB9185F89");
        List<AccountPolicyDto> accountPolicyDtos = policyAccountsDtoService.search(accountKey, new ServiceErrorsImpl());
        Assert.assertNotNull(accountPolicyDtos);
        Assert.assertTrue(accountPolicyDtos.size()==1);
        AccountPolicyDto accountPolicyDto = accountPolicyDtos.get(0);
        Assert.assertEquals("254686786", accountPolicyDto.getAccountNumber());
        Assert.assertEquals("SMSF acct", accountPolicyDto.getAccountName());

        Assert.assertNotNull(accountPolicyDto.getPolicyList());
        Assert.assertTrue(accountPolicyDto.getPolicyList().size()==2);

        PolicyDto policyDto1 = accountPolicyDto.getPolicyList().get(0);
        Assert.assertEquals("CM287468", policyDto1.getPolicyNumber());
        Assert.assertEquals(PolicyStatusCode.IN_FORCE, policyDto1.getStatus());

        PolicyDto policyDto2 = accountPolicyDto.getPolicyList().get(1);
        Assert.assertEquals("CF506182", policyDto2.getPolicyNumber());
        Assert.assertEquals(PolicyStatusCode.IN_FORCE, policyDto2.getStatus());
    }

    private Map<ClientKey, Client> getClientMap() {
        final ClientKey clientKey1 = ClientKey.valueOf("23659");
        final ClientKey clientKey2 = ClientKey.valueOf("23859");
        final ClientKey clientKey3 = ClientKey.valueOf("23759");
        final ClientKey clientKey4 = ClientKey.valueOf("23749");

        final List<ClientKey> associatedClients1 = new ArrayList<>();
        associatedClients1.add(clientKey2);
        associatedClients1.add(clientKey4);
        ClientImpl client1 = new IndividualImpl();
        client1.setClientKey(clientKey1);
        client1.setAssociatedPersonKeys(associatedClients1);

        final List<ClientKey> associatedClients2 = new ArrayList<>();
        ClientImpl client2 = new IndividualImpl();
        client2.setClientKey(clientKey2);
        client2.setAssociatedPersonKeys(associatedClients2);

        final List<ClientKey> associatedClients3 = new ArrayList<>();
        associatedClients3.add(clientKey2);
        ClientImpl client3 = new IndividualImpl();
        client3.setClientKey(clientKey3);
        client3.setAssociatedPersonKeys(associatedClients3);

        final List<ClientKey> associatedClients4 = new ArrayList<>();
        ClientImpl client4 = new IndividualImpl();
        client4.setClientKey(clientKey4);
        client4.setAssociatedPersonKeys(associatedClients4);

        Map<ClientKey, Client> clientMap = new HashMap<>();
        clientMap.put(clientKey1, client1);
        clientMap.put(clientKey2, client2);
        clientMap.put(clientKey3, client3);
        clientMap.put(clientKey4, client4);

        return clientMap;
    }
}
