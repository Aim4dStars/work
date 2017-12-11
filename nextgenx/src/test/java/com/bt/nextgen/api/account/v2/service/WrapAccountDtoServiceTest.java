/**
 * 
 */
package com.bt.nextgen.api.account.v2.service;

import com.bt.nextgen.api.account.v2.model.AccountDto;
import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.account.TransactionPermission;
import com.bt.nextgen.service.avaloq.client.PersonImpl;
import com.bt.nextgen.service.avaloq.product.ProductImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.account.AccountType;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.userinformation.ClientDetail;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Collections;

/**
 * @author L072463
 * 
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class WrapAccountDtoServiceTest {

    @InjectMocks
    WrapAccountDtoService wrapAccountDtoService = new WrapAccountDtoServiceImpl();

    @Mock
    private AccountIntegrationService accountService;

    @Mock
    private ProductIntegrationService productIntegrationService;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    WrapAccount wrapAccount;
    ClientDetail clientDetail;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        clientDetail = Mockito.mock(ClientDetail.class);
        Mockito.when(clientDetail.getClientKey()).thenReturn(ClientKey.valueOf("clientId"));
        Mockito.when(clientDetail.getAge()).thenReturn(0);
        Mockito.when(clientDetail.isRegistrationOnline()).thenReturn(false);
        Mockito.when(clientDetail.getFullName()).thenReturn("fullName");

        wrapAccount = Mockito.mock(WrapAccount.class);
        Mockito.when(wrapAccount.getAccountType()).thenReturn(AccountType.MANAGED_FUND);
        Mockito.when(wrapAccount.getAccountName()).thenReturn("accountName");
        Mockito.when(wrapAccount.getAccountKey()).thenReturn(com.bt.nextgen.service.integration.account.AccountKey
                .valueOf("80A063E31114DEA03439E0BD2F82ABFCB056BED9910DDAEA"));
        Mockito.when(wrapAccount.isOpen()).thenReturn(false);
        Mockito.when(wrapAccount.getProductKey())
                .thenReturn(com.bt.nextgen.service.integration.product.ProductKey.valueOf("productId"));
        Mockito.when(wrapAccount.getPortfolioValue()).thenReturn(new BigDecimal(100));
        Mockito.when(wrapAccount.getAvailableCash()).thenReturn(new BigDecimal(1000000));
        Mockito.when(wrapAccount.getAdviserPersonId()).thenReturn(ClientKey.valueOf("1234134"));
        Mockito.when(wrapAccount.getAdviserPermissions())
                .thenReturn(Collections.singleton(TransactionPermission.Payments_Deposits));
        Mockito.when(wrapAccount.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
        Mockito.when(wrapAccount.getAccountStatus()).thenReturn(AccountStatus.ACTIVE);
        Mockito.when(wrapAccount.getMinCashAmount()).thenReturn(BigDecimal.valueOf(2000d));
        Mockito.when(wrapAccount.isHasMinCash()).thenReturn(true);
    }

    /**
     * Test method for
     * {@link com.bt.nextgen.api.account.v2.service.WrapAccountDtoServiceImpl#find(com.bt.nextgen.api.account.v2.model.AccountKey, com.bt.nextgen.service.ServiceErrors)}
     * .
     */
    @Test
    public final void testFind() {

        AccountKey accountKey = new AccountKey("80A063E31114DEA03439E0BD2F82ABFCB056BED9910DDAEA");
        Mockito.when(
accountService.loadWrapAccountWithoutContainers(
                Mockito.any(com.bt.nextgen.service.integration.account.AccountKey.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(wrapAccount);

        ProductImpl product = new ProductImpl();
        product.setProductKey(ProductKey.valueOf("productId"));
        product.setProductName("productName");
        PersonImpl person = new PersonImpl();
        person.setFirstName("Abc");
        person.setFullName("Testing 123");
        Mockito.when(productIntegrationService.getProductDetail(Mockito.any(ProductKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(product);
        Mockito.when(
                brokerIntegrationService.getPersonDetailsOfBrokerUser(Mockito.any(ClientKey.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(person);

        AccountDto accountDto = wrapAccountDtoService.find(accountKey, null);

        Assert.assertEquals(accountDto.getAccountId(), wrapAccount.getAccountKey().getId());
        Assert.assertEquals(accountDto.getAccountType(), wrapAccount.getAccountStructureType().name());
        Assert.assertEquals(accountDto.getAccountStatus().toUpperCase(), wrapAccount.getAccountStatus().name());
        Assert.assertEquals(accountDto.getAvailableCash(), wrapAccount.getAvailableCash());
        Assert.assertEquals(accountDto.getPortfolioValue(), wrapAccount.getPortfolioValue());
        Assert.assertEquals(accountDto.getMinCashAmount(), wrapAccount.getMinCashAmount());
        Assert.assertEquals(accountDto.isHasMinCash(), wrapAccount.isHasMinCash());

    }

    /**
     * Test method for
     * {@link com.bt.nextgen.api.account.v2.service.WrapAccountDtoServiceImpl#toAccountDto(com.btfin.panorama.service.integration.account.WrapAccount, com.bt.nextgen.service.ServiceErrors)}
     * .
     */
    @Test
    public final void testToAccountDto() {

    }

}
