package com.bt.nextgen.reports.account;

import com.bt.nextgen.api.account.v3.model.AccountDto;
import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.account.v3.model.WrapAccountDetailDto;
import com.bt.nextgen.api.account.v3.service.WrapAccountDetailDtoService;
import com.bt.nextgen.api.broker.model.BrokerDto;
import com.bt.nextgen.api.broker.model.BrokerKey;
import com.bt.nextgen.api.client.model.PhoneDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.integration.broker.Broker;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class AccountReportTest {

    @InjectMocks
    private AccountReport accountReport = new AccountReport() {
    };

    @Mock
    private WrapAccountDetailDtoService accountDetailDtoService;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    @Test
    public void testGetAccountDto_whenAdviserNull_thenEmptyValuesReturned() {
        WrapAccountDetailDto wrapAccount = Mockito.mock(WrapAccountDetailDto.class);
        Mockito.when(wrapAccount.getKey()).thenReturn(new AccountKey("accountId"));
        Mockito.when(wrapAccount.getAccountName()).thenReturn("accountName");
        Mockito.when(wrapAccount.getAccountNumber()).thenReturn("accountNumber");
        Mockito.when(wrapAccount.getAccountType()).thenReturn("accountType");
        Mockito.when(wrapAccount.getAdviser()).thenReturn(null);

        Mockito.when(accountDetailDtoService.search(Mockito.anyListOf(ApiSearchCriteria.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(wrapAccount);
        
        Map<String, String> params = new HashMap<>();
        params.put("account-id", "accountId");
        
        Collection<AccountDto> accountDtos = accountReport.getAccount(params);
        Assert.assertEquals(1, accountDtos.size());

        AccountDto accountDto = (AccountDto) accountDtos.toArray()[0];
        Assert.assertNotNull(accountDto);
        Assert.assertEquals("accountName", accountDto.getAccountName());
        Assert.assertEquals("accountNumber", accountDto.getAccountNumber());
        Assert.assertEquals("accountType", accountDto.getAccountType());
        Assert.assertEquals("accountId", accountDto.getKey().getAccountId());

        Assert.assertEquals("", accountDto.getAdviserName());
        Assert.assertEquals("", accountDto.getAdviserDealerGroup());
        Assert.assertEquals("", accountDto.getAdviserMobileNumber());
    }

    @Test
    public void testGetAccountDto_whenAdviserPresent_thenAdviserValuesReturned() {
        BrokerDto broker = new BrokerDto();
        broker.setFirstName("firstName");
        broker.setLastName("lastName");
        broker.setCorporateName("");
        broker.setBrokerParentKey(new BrokerKey(EncodedString.fromPlainText("brokerId").toString()));
        broker.setPhone(Collections.singletonList(Mockito.mock(PhoneDto.class)));

        WrapAccountDetailDto wrapAccount = Mockito.mock(WrapAccountDetailDto.class);
        Mockito.when(wrapAccount.getKey()).thenReturn(new AccountKey("accountId"));
        Mockito.when(wrapAccount.getAccountName()).thenReturn("accountName");
        Mockito.when(wrapAccount.getAccountNumber()).thenReturn("accountNumber");
        Mockito.when(wrapAccount.getAccountType()).thenReturn("accountType");
        Mockito.when(wrapAccount.getAdviser()).thenReturn(broker);

        Mockito.when(accountDetailDtoService.search(Mockito.anyListOf(ApiSearchCriteria.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(wrapAccount);

        Broker parentBroker = Mockito.mock(Broker.class);
        Mockito.when(parentBroker.getPositionName()).thenReturn("adviserDealerGroup");

        Mockito.when(
                brokerIntegrationService.getBroker(Mockito.any(com.bt.nextgen.service.integration.broker.BrokerKey.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(parentBroker);

        Map<String, String> params = new HashMap<>();
        params.put("account-id", "accountId");

        Collection<AccountDto> accountDtos = accountReport.getAccount(params);
        Assert.assertEquals(1, accountDtos.size());

        AccountDto accountDto = (AccountDto) accountDtos.toArray()[0];
        Assert.assertNotNull(accountDto);
        Assert.assertEquals("accountName", accountDto.getAccountName());
        Assert.assertEquals("accountNumber", accountDto.getAccountNumber());
        Assert.assertEquals("accountType", accountDto.getAccountType());
        Assert.assertEquals("accountId", accountDto.getKey().getAccountId());

        Assert.assertEquals("firstName lastName", accountDto.getAdviserName());
        Assert.assertEquals("adviserDealerGroup", accountDto.getAdviserDealerGroup());
    }

    @Test
    public void testGetAccountDto_whenAdviserWithCorporateNamePresent_thenAdviserValuesReturned() {
        BrokerDto broker = new BrokerDto();
        broker.setFirstName("firstName");
        broker.setLastName("lastName");
        broker.setCorporateName("corporateName");
        broker.setBrokerParentKey(new BrokerKey(EncodedString.fromPlainText("brokerId").toString()));
        broker.setPhone(Collections.singletonList(Mockito.mock(PhoneDto.class)));

        WrapAccountDetailDto wrapAccount = Mockito.mock(WrapAccountDetailDto.class);
        Mockito.when(wrapAccount.getKey()).thenReturn(new AccountKey("accountId"));
        Mockito.when(wrapAccount.getAccountName()).thenReturn("accountName");
        Mockito.when(wrapAccount.getAccountNumber()).thenReturn("accountNumber");
        Mockito.when(wrapAccount.getAccountType()).thenReturn("accountType");
        Mockito.when(wrapAccount.getAdviser()).thenReturn(broker);

        Mockito.when(accountDetailDtoService.search(Mockito.anyListOf(ApiSearchCriteria.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(wrapAccount);

        Broker parentBroker = Mockito.mock(Broker.class);
        Mockito.when(parentBroker.getPositionName()).thenReturn("adviserDealerGroup");

        Mockito.when(
                brokerIntegrationService.getBroker(Mockito.any(com.bt.nextgen.service.integration.broker.BrokerKey.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(parentBroker);

        Map<String, String> params = new HashMap<>();
        params.put("account-id", "accountId");

        Collection<AccountDto> accountDtos = accountReport.getAccount(params);
        Assert.assertEquals(1, accountDtos.size());

        AccountDto accountDto = (AccountDto) accountDtos.toArray()[0];
        Assert.assertNotNull(accountDto);
        Assert.assertEquals("accountName", accountDto.getAccountName());
        Assert.assertEquals("accountNumber", accountDto.getAccountNumber());
        Assert.assertEquals("accountType", accountDto.getAccountType());
        Assert.assertEquals("accountId", accountDto.getKey().getAccountId());

        Assert.assertEquals("corporateName", accountDto.getAdviserName());
        Assert.assertEquals("adviserDealerGroup", accountDto.getAdviserDealerGroup());
    }
}
