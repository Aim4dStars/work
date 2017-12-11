package com.bt.nextgen.api.client.util;

import com.bt.nextgen.api.account.v1.model.AccountDto;
import com.bt.nextgen.api.account.v1.model.AccountKey;
import com.bt.nextgen.api.account.v1.model.AccountPaymentPermission;
import com.bt.nextgen.core.type.ConsistentEncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.*;
import com.bt.nextgen.service.avaloq.broker.BrokerImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerUserImpl;
import com.bt.nextgen.service.avaloq.product.ProductDetailImpl;
import com.bt.nextgen.service.integration.account.AccountBalance;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerWrapper;
import com.bt.nextgen.service.integration.broker.BrokerWrapperImpl;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.account.WrapAccount;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountDtoConverterTest {

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    private BrokerUserImpl brokerUserImpl;

    private Map<ProductKey, Product> productKeyProductMap;
    private Map<com.bt.nextgen.service.integration.account.AccountKey, WrapAccount> accountMap;
    private Map<com.bt.nextgen.service.integration.account.AccountKey, AccountBalance> accountBalanceMap;

    private AccountDtoConverter dtoConverter;

    private Map<BrokerKey, BrokerWrapper> brokerWrapperMap;
    BrokerImpl broker;

    @Before
    public void setUp() throws Exception {
        brokerUserImpl = new BrokerUserImpl(UserKey.valueOf("11111"));
        brokerUserImpl.setFirstName("AdviserFName");
        brokerUserImpl.setLastName("AdviserLName");
        
        broker = Mockito.mock(BrokerImpl.class);
        Mockito.when(broker.isDirectInvestment()).thenReturn(true);

        BrokerKey brokerKey1 = BrokerKey.valueOf("12341");
        BrokerKey brokerKey2 = BrokerKey.valueOf("12342");

        BrokerWrapper brokerWrapper1 = new BrokerWrapperImpl(brokerKey1, brokerUserImpl, true, "");
        BrokerWrapper brokerWrapper2 = new BrokerWrapperImpl(brokerKey2, brokerUserImpl, true, "");

        brokerWrapperMap = new HashMap<>();
        brokerWrapperMap.put(brokerKey1, brokerWrapper1);
        brokerWrapperMap.put(brokerKey2, brokerWrapper2);

        productKeyProductMap = new HashMap<>();
        ProductDetailImpl product = new ProductDetailImpl();
        product.setProductKey(ProductKey.valueOf("55555"));
        product.setProductId("55555");
        product.setProductName("Product 1");
        productKeyProductMap.put(ProductKey.valueOf("55555"), product);

        product = new ProductDetailImpl();
        product.setProductKey(ProductKey.valueOf("55556"));
        product.setProductId("55556");
        product.setProductName("Product 2");
        productKeyProductMap.put(ProductKey.valueOf("55556"), product);

        accountBalanceMap = new HashMap<>();
        AccountBalanceImpl accountBalance = new AccountBalanceImpl();
        accountBalance.setAvailableCash(new BigDecimal(10000));
        accountBalance.setPortfolioValue(new BigDecimal(20000));
        accountBalance.setAccountKey("22222");
        accountBalance.setKey(com.bt.nextgen.service.integration.account.AccountKey.valueOf("22222"));
        accountBalanceMap.put(com.bt.nextgen.service.integration.account.AccountKey.valueOf("22222"), accountBalance);

        accountBalance = new AccountBalanceImpl();
        accountBalance.setAvailableCash(new BigDecimal(30000));
        accountBalance.setPortfolioValue(new BigDecimal(40000));
        accountBalance.setAccountKey("22223");
        accountBalance.setKey(com.bt.nextgen.service.integration.account.AccountKey.valueOf("22223"));
        accountBalanceMap.put(com.bt.nextgen.service.integration.account.AccountKey.valueOf("22223"), accountBalance);

        accountBalance = new AccountBalanceImpl();
        accountBalance.setAvailableCash(new BigDecimal(30000));
        accountBalance.setPortfolioValue(new BigDecimal(40000));
        accountBalance.setAccountKey("22224");
        accountBalance.setKey(com.bt.nextgen.service.integration.account.AccountKey.valueOf("22224"));
        accountBalanceMap.put(com.bt.nextgen.service.integration.account.AccountKey.valueOf("22224"), accountBalance);

        accountBalance = new AccountBalanceImpl();
        accountBalance.setAvailableCash(new BigDecimal(30000));
        accountBalance.setPortfolioValue(new BigDecimal(40000));
        accountBalance.setAccountKey("22225");
        accountBalance.setKey(com.bt.nextgen.service.integration.account.AccountKey.valueOf("22225"));
        accountBalanceMap.put(com.bt.nextgen.service.integration.account.AccountKey.valueOf("22225"), accountBalance);

        accountMap = new HashMap<>();
        WrapAccountImpl wrapAccount = new WrapAccountImpl();
        wrapAccount.setAccountKey(com.bt.nextgen.service.integration.account.AccountKey.valueOf("22222"));
        wrapAccount.setAccountName("Company account");
        wrapAccount.setAccountNumber("111");
        wrapAccount.setAccountStructureType(AccountStructureType.Company);
        wrapAccount.setAccountStatus(AccountStatus.ACTIVE);
        wrapAccount.setProductKey(ProductKey.valueOf("55555"));
        wrapAccount.setAdviserPersonId(ClientKey.valueOf("12345"));
        wrapAccount.setAdviserPositionId(BrokerKey.valueOf("12341"));
        wrapAccount.setAdviserPermissions(Collections.singleton(TransactionPermission.Payments_Deposits));
        accountMap.put(com.bt.nextgen.service.integration.account.AccountKey.valueOf("22222"), wrapAccount);

        wrapAccount = new WrapAccountImpl();
        wrapAccount.setAccountKey(com.bt.nextgen.service.integration.account.AccountKey.valueOf("22223"));
        wrapAccount.setAccountName("Individual account");
        wrapAccount.setAccountNumber("111222");
        wrapAccount.setAccountStructureType(AccountStructureType.Individual);
        wrapAccount.setAccountStatus(AccountStatus.ACTIVE);
        wrapAccount.setProductKey(ProductKey.valueOf("55556"));
        wrapAccount.setAdviserPersonId(ClientKey.valueOf("12345"));
        wrapAccount.setAdviserPositionId(BrokerKey.valueOf("12342"));
        wrapAccount.setAdviserPermissions(Collections.singleton(TransactionPermission.Payments_Deposits));
        accountMap.put(com.bt.nextgen.service.integration.account.AccountKey.valueOf("22223"), wrapAccount);

        PensionAccountDetailImpl pensionAccount = new PensionAccountDetailImpl();
        pensionAccount.setAccountKey(com.bt.nextgen.service.integration.account.AccountKey.valueOf("22224"));
        pensionAccount.setAccountName("Accumulation account");
        pensionAccount.setAccountNumber("111333");
        pensionAccount.setAccountStructureType(AccountStructureType.SUPER);
        pensionAccount.setAccountStatus(AccountStatus.ACTIVE);
        pensionAccount.setProductKey(ProductKey.valueOf("55556"));
        pensionAccount.setAdviserPersonId(ClientKey.valueOf("12345"));
        pensionAccount.setAdviserPositionId(BrokerKey.valueOf("12342"));
        pensionAccount.setAdviserPermissions(Collections.singleton(TransactionPermission.Payments_Deposits));
        accountMap.put(com.bt.nextgen.service.integration.account.AccountKey.valueOf("22224"), pensionAccount);

        pensionAccount = new PensionAccountDetailImpl();
        pensionAccount.setAccountKey(com.bt.nextgen.service.integration.account.AccountKey.valueOf("22225"));
        pensionAccount.setAccountName("Pension (TTR) account");
        pensionAccount.setAccountNumber("111444");
        pensionAccount.setAccountStructureType(AccountStructureType.SUPER);
        pensionAccount.setPensionType(PensionType.TTR);
        pensionAccount.setAccountStatus(AccountStatus.ACTIVE);
        pensionAccount.setProductKey(ProductKey.valueOf("55556"));
        pensionAccount.setAdviserPersonId(ClientKey.valueOf("12345"));
        pensionAccount.setAdviserPositionId(BrokerKey.valueOf("12342"));
        pensionAccount.setAdviserPermissions(Collections.singleton(TransactionPermission.Payments_Deposits));
        accountMap.put(com.bt.nextgen.service.integration.account.AccountKey.valueOf("22225"), pensionAccount);

        pensionAccount = new PensionAccountDetailImpl();
        pensionAccount.setAccountKey(com.bt.nextgen.service.integration.account.AccountKey.valueOf("22226"));
        pensionAccount.setAccountName("Pension (TTR) account");
        pensionAccount.setAccountNumber("111444");
        pensionAccount.setAccountStructureType(AccountStructureType.SUPER);
        pensionAccount.setPensionType(PensionType.TTR_RETIR_PHASE);
        pensionAccount.setAccountStatus(AccountStatus.ACTIVE);
        pensionAccount.setProductKey(ProductKey.valueOf("55556"));
        pensionAccount.setAdviserPersonId(ClientKey.valueOf("12345"));
        pensionAccount.setAdviserPositionId(BrokerKey.valueOf("12342"));
        pensionAccount.setAdviserPermissions(Collections.singleton(TransactionPermission.Payments_Deposits));
        accountMap.put(com.bt.nextgen.service.integration.account.AccountKey.valueOf("22226"), pensionAccount);

        dtoConverter = new AccountDtoConverter(accountMap, accountBalanceMap, productKeyProductMap, brokerIntegrationService);

    }

    @Test
    public void testConvert() throws Exception {
        when(brokerIntegrationService.getAdviserBrokerUser(any(BrokerKey.class), any(ServiceErrors.class)))
                .thenReturn(brokerUserImpl);
        when(brokerIntegrationService.getAdviserBrokerUser(anyListOf(BrokerKey.class), any(ServiceErrors.class)))
                .thenReturn(brokerWrapperMap);
        Map<com.bt.nextgen.service.integration.account.AccountKey, AccountDto> accountDtoMap = dtoConverter
                .convert(new ServiceErrorsImpl());
        AccountDto accountDto = accountDtoMap.get(com.bt.nextgen.service.integration.account.AccountKey.valueOf("22223"));
        Assert.assertThat(accountDto.getAccountNumber(), CoreMatchers.is("111222"));

        Assert.assertThat(accountDto.getAccountName(), CoreMatchers.is("Individual account"));
        Assert.assertThat(accountDto.getProduct(), CoreMatchers.is("Product 2"));
        Assert.assertThat(accountDto.getAccountType(), CoreMatchers.is(AccountStructureType.Individual.name()));
        Assert.assertThat(accountDto.getAccountStatus(), CoreMatchers.is(AccountStatus.ACTIVE.getStatusDescription()));
        Assert.assertThat(accountDto.getAvailableCash(), CoreMatchers.is(new BigDecimal(30000)));
        Assert.assertThat(accountDto.getPortfolioValue(), CoreMatchers.is(new BigDecimal(40000)));
        Assert.assertThat(new ConsistentEncodedString(accountDto.getProductId()).plainText(), CoreMatchers.is("55556"));
        Assert.assertThat(new EncodedString(accountDto.getKey().getAccountId()).plainText(), CoreMatchers.is("22223"));

        Assert.assertThat(new EncodedString(accountDto.getAdviserId()).plainText(), CoreMatchers.is("12342"));
        Assert.assertThat(accountDto.getAdviserName(), CoreMatchers.is("AdviserLName, AdviserFName"));
        Assert.assertThat(accountDto.getAdviserPermission(),
                CoreMatchers.is(AccountPaymentPermission.PAYMENTS_DEPOSITS_TO_ALL.getAdviserPermissionDesc()));

    }

    @Test
    public void testToAccountDto() throws Exception {
        when(brokerIntegrationService.getAdviserBrokerUser(any(BrokerKey.class), any(ServiceErrors.class)))
                .thenReturn(brokerUserImpl);

        WrapAccount wrapAccount = accountMap.get(com.bt.nextgen.service.integration.account.AccountKey.valueOf("22222"));
        AccountBalance accountBalance = accountBalanceMap
                .get(com.bt.nextgen.service.integration.account.AccountKey.valueOf("22222"));
        Product product = productKeyProductMap.get(ProductKey.valueOf("55555"));
        AccountDto accountDto = dtoConverter.toAccountDto(wrapAccount, accountBalance, product, brokerUserImpl, new ServiceErrorsImpl());
        Assert.assertThat(accountDto.getAccountNumber(), CoreMatchers.is("111"));

        Assert.assertThat(accountDto.getAccountName(), CoreMatchers.is("Company account"));
        Assert.assertThat(accountDto.getProduct(), CoreMatchers.is("Product 1"));
        Assert.assertThat(accountDto.getAccountType(), CoreMatchers.is(AccountStructureType.Company.name()));
        Assert.assertThat(accountDto.getAccountStatus(), CoreMatchers.is(AccountStatus.ACTIVE.getStatusDescription()));
        Assert.assertThat(accountDto.getAvailableCash(), CoreMatchers.is(new BigDecimal(10000)));
        Assert.assertThat(accountDto.getPortfolioValue(), CoreMatchers.is(new BigDecimal(20000)));
        Assert.assertThat(new ConsistentEncodedString(accountDto.getProductId()).plainText(), CoreMatchers.is("55555"));
        Assert.assertThat(new EncodedString(accountDto.getKey().getAccountId()).plainText(), CoreMatchers.is("22222"));

        Assert.assertThat(new EncodedString(accountDto.getAdviserId()).plainText(), CoreMatchers.is("12341"));
        Assert.assertThat(accountDto.getAdviserName(), CoreMatchers.is("AdviserLName, AdviserFName"));
        Assert.assertThat(accountDto.getAdviserPermission(),
                CoreMatchers.is(AccountPaymentPermission.PAYMENTS_DEPOSITS_TO_ALL.getAdviserPermissionDesc()));

    }

    @Test
    public void testSetAdviserDetails() {
        AccountDtoConverter accountDtoConverter = new AccountDtoConverter(null, null, null, brokerIntegrationService);
        WrapAccountImpl wrapAccount = new WrapAccountImpl();
        wrapAccount.setAdviserPersonId(ClientKey.valueOf("12341"));
        wrapAccount.setAdviserPositionId(BrokerKey.valueOf("12345"));
        wrapAccount.setAdviserPermissions(Collections.singleton(TransactionPermission.Payments_Deposits));
        when(brokerIntegrationService.getAdviserBrokerUser(any(BrokerKey.class), any(ServiceErrors.class)))
                .thenReturn(brokerUserImpl);
        AccountDto accountDto = new AccountDto(new AccountKey("Acc1"));
        accountDtoConverter.setAdviserDetails(accountDto, wrapAccount, brokerUserImpl, new ServiceErrorsImpl());
        Assert.assertThat(new EncodedString(accountDto.getAdviserId()).plainText(), CoreMatchers.is("12345"));
        Assert.assertThat(accountDto.getAdviserName(), CoreMatchers.is("AdviserLName, AdviserFName"));
        Assert.assertThat(accountDto.getAdviserPermission(),
                CoreMatchers.is(AccountPaymentPermission.PAYMENTS_DEPOSITS_TO_ALL.getAdviserPermissionDesc()));
    }

    @Test
    public void testToAccountDto_whenProvidedAnAccumulationAccount_thentheAccountTypeDescriptionIsSuper() throws Exception {
        when(brokerIntegrationService.getAdviserBrokerUser(any(BrokerKey.class), any(ServiceErrors.class)))
                .thenReturn(brokerUserImpl);

        WrapAccount wrapAccount = accountMap.get(com.bt.nextgen.service.integration.account.AccountKey.valueOf("22224"));
        AccountBalance accountBalance = accountBalanceMap
                .get(com.bt.nextgen.service.integration.account.AccountKey.valueOf("22224"));
        Product product = productKeyProductMap.get(ProductKey.valueOf("55555"));
        AccountDto accountDto = dtoConverter.toAccountDto(wrapAccount, accountBalance, product, brokerUserImpl, new ServiceErrorsImpl());
        Assert.assertThat(accountDto.getAccountTypeDescription(), CoreMatchers.is("Super"));

    }

    @Test
    public void testToAccountDto_whenProvidedAPensionTTRAccount_thentheAccountTypeDescriptionIsPensionTTRTaxed() throws Exception {
        when(brokerIntegrationService.getAdviserBrokerUser(any(BrokerKey.class), any(ServiceErrors.class)))
                .thenReturn(brokerUserImpl);

        WrapAccount wrapAccount = accountMap.get(com.bt.nextgen.service.integration.account.AccountKey.valueOf("22225"));
        AccountBalance accountBalance = accountBalanceMap
                .get(com.bt.nextgen.service.integration.account.AccountKey.valueOf("22225"));
        Product product = productKeyProductMap.get(ProductKey.valueOf("55555"));
        AccountDto accountDto = dtoConverter.toAccountDto(wrapAccount, accountBalance, product, brokerUserImpl, new ServiceErrorsImpl());
        Assert.assertThat(accountDto.getAccountTypeDescription(), CoreMatchers.is(PensionType.TTR.getLabel()));

    }

    @Test
    public void testToAccountDto_whenProvidedAPensionTTRAccount_thentheAccountTypeDescriptionIsPensionTTRRetirement() throws Exception {
        when(brokerIntegrationService.getAdviserBrokerUser(any(BrokerKey.class), any(ServiceErrors.class)))
                .thenReturn(brokerUserImpl);

        WrapAccount wrapAccount = accountMap.get(com.bt.nextgen.service.integration.account.AccountKey.valueOf("22226"));
        AccountBalance accountBalance = accountBalanceMap
                .get(com.bt.nextgen.service.integration.account.AccountKey.valueOf("22225"));
        Product product = productKeyProductMap.get(ProductKey.valueOf("55555"));
        AccountDto accountDto = dtoConverter.toAccountDto(wrapAccount, accountBalance, product, brokerUserImpl, new ServiceErrorsImpl());
        Assert.assertThat(accountDto.getAccountTypeDescription(), CoreMatchers.is(PensionType.TTR_RETIR_PHASE.getLabel()));

    }
}