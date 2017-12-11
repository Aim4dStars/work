package com.bt.nextgen.api.account.v1.service.transitions;
import com.bt.nextgen.api.account.v1.model.transitions.TransitionAccountDto;
import com.bt.nextgen.api.account.v1.service.TransitionClientDtoServiceImpl;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountBalanceImpl;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.product.ProductImpl;
import com.bt.nextgen.service.integration.account.*;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.account.WrapAccount;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by L069552 on 21/09/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class TransitionClientDtoServiceImplTest {

    @InjectMocks
    TransitionClientDtoServiceImpl transitionClientDtoService;

    @Mock
    private AccountIntegrationService accountService;

    @Mock
    private ProductIntegrationService productIntegrationService;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    @Mock
    TransitionAccountIntegrationService transitionAccountIntegrationService;


    private  WrapAccountImpl account1;
    private WrapAccountImpl account2;
    private WrapAccountImpl account3;
    private AccountKey accountKey1;
    private AccountKey accountKey2;
    private AccountKey accountKey3;
    private Map<AccountKey, WrapAccount> accountMap;
    private ProductImpl product;
    private AccountBalanceImpl accountBalance1;
    private AccountBalanceImpl accountBalance2;
    private AccountBalanceImpl accountBalance3;
    private List<AccountBalance> accountBalanceList;
    private List<TransitionAccountDetail> listTransitions;
    private TransitionAccountDetailImpl transitionAccountDetail;
    private TransitionAccountBPDetailImpl transitionAccountBPDetail1;
    private TransitionAccountBPDetailImpl transitionAccountBPDetail2;
    private TransitionAccountBPDetailImpl transitionAccountBPDetail3;
    private TransitionAccountDetailHolder transitionAccountDetailHolder;
    private List<TransitionAccountBPDetail> bpListTransaction;
    private Map<ProductKey,Product >mapProducts;
    private List<ApiSearchCriteria> criteriaList;

    @Before
    public void setUp()
    {
        accountBalanceList = new ArrayList<AccountBalance>();
        accountBalance1 = new AccountBalanceImpl();
        accountBalance2 = new AccountBalanceImpl();
        accountBalance3 = new AccountBalanceImpl();
        accountMap = new HashMap<>();
        account1 = new WrapAccountImpl();
        account2 = new WrapAccountImpl();
        account3 = new WrapAccountImpl();
        product = new ProductImpl();
        accountKey1 = AccountKey.valueOf("34523654");
        accountKey2 = AccountKey.valueOf("64433");
        accountKey3 = AccountKey.valueOf("56734");
        account1.setAccountKey(accountKey1);
        account1.setProductKey(ProductKey.valueOf("1234"));
        account1.setAccountName("Tom Peters");
        account1.setAccountNumber("120011366");
        account1.setAdviserPositionId(BrokerKey.valueOf("66773"));
        account1.setAccountStructureType(AccountStructureType.Individual);
        account1.setAccountStatus(AccountStatus.ACTIVE);
        account3.setAccountKey(accountKey2);
        account3.setAdviserPositionId(BrokerKey.valueOf("66773"));
        account3.setAccountName("John Cooper");
        account3.setAccountNumber("120011366");
        account3.setProductKey(ProductKey.valueOf("1234"));
        account3.setAccountStructureType(AccountStructureType.Individual);
        account3.setAccountStatus(AccountStatus.ACTIVE);
        account2.setAccountKey(accountKey3);
        account2.setAccountName("Oniston Pty Limited - 01");
        account2.setAdviserPersonId(ClientKey.valueOf("1234"));
        account2.setAdviserPositionId(BrokerKey.valueOf("66773"));
        account2.setAccountNumber("120000005");
        account2.setProductKey(ProductKey.valueOf("1234"));
        account2.setAccountStructureType(AccountStructureType.Individual);
        account2.setAccountStatus(AccountStatus.ACTIVE);
        accountMap.put(accountKey1, account1);
        accountMap.put(accountKey2, account2);
        accountMap.put(accountKey3, account3);
        product.setProductKey(ProductKey.valueOf("1234"));
        product.setProductName("productName");
        accountBalance1.setAvailableCash(new BigDecimal(5000));
        accountBalance1.setPortfolioValue(new BigDecimal(7000));
        accountBalance1.setKey(AccountKey.valueOf("34523654"));
        accountBalance2.setAvailableCash(new BigDecimal(5000));
        accountBalance2.setPortfolioValue(new BigDecimal(7000));
        accountBalance2.setKey(AccountKey.valueOf("64433"));
        accountBalance3.setAvailableCash(new BigDecimal(5000));
        accountBalance3.setPortfolioValue(new BigDecimal(7000));
        accountBalance3.setKey(AccountKey.valueOf("56734"));
        accountBalanceList.add(accountBalance1);
        accountBalanceList.add(accountBalance2);
        accountBalanceList.add(accountBalance3);
        listTransitions = new ArrayList<>();
        transitionAccountDetail = new TransitionAccountDetailImpl();
        transitionAccountBPDetail1=new TransitionAccountBPDetailImpl();
        transitionAccountBPDetail1.setExpectedAssetAmount(new BigDecimal(1000));
        transitionAccountBPDetail1.setExpectedCashAmount(new BigDecimal(1000));
        transitionAccountBPDetail1.setTransitionStatus(TransitionStatus.AWAITING_APPROVAL);
        transitionAccountBPDetail1.setAccountId("34523654");
        transitionAccountBPDetail1.setTransferType("Transitions");
        transitionAccountDetail.setBrokerName("OE Darryl  Gunther");
        transitionAccountDetail.setBrokerId("100747");
        bpListTransaction = new ArrayList<>();
        bpListTransaction.add(transitionAccountBPDetail1);
        transitionAccountDetail.setTransitionAccountBPDetailList(bpListTransaction);
        transitionAccountBPDetail2=new TransitionAccountBPDetailImpl();
        transitionAccountBPDetail2.setExpectedAssetAmount(new BigDecimal(1000));
        transitionAccountBPDetail2.setExpectedCashAmount(new BigDecimal(1000));
        transitionAccountBPDetail2.setTransitionStatus(TransitionStatus.AWAITING_APPROVAL);
        transitionAccountBPDetail2.setAccountId("56734");
        transitionAccountBPDetail2.setTransferType("Transitions");
        bpListTransaction.add(transitionAccountBPDetail2);
        transitionAccountDetail.setTransitionAccountBPDetailList(bpListTransaction);
        transitionAccountBPDetail3=new TransitionAccountBPDetailImpl();
        transitionAccountBPDetail3.setExpectedAssetAmount(new BigDecimal(1000));
        transitionAccountBPDetail3.setExpectedCashAmount(new BigDecimal(1000));
        transitionAccountBPDetail3.setTransitionStatus(TransitionStatus.COMPLETE);
        transitionAccountBPDetail3.setAccountId("64433");
        transitionAccountBPDetail3.setTransferType("Transitions");
        bpListTransaction.add(transitionAccountBPDetail3);
        transitionAccountDetail.setTransitionAccountBPDetailList(bpListTransaction);
        listTransitions.add(transitionAccountDetail);
        transitionAccountDetailHolder = new TransitionAccountDetailHolderImpl();
        transitionAccountDetailHolder.setTransitionAccountDetailList(listTransitions);
        com.bt.nextgen.api.broker.model.BrokerKey brokerKey = new com.bt.nextgen.api.broker.model.BrokerKey("12345");
        mapProducts = new HashMap<ProductKey,Product >();
        mapProducts.put(ProductKey.valueOf("1234"),product);
        String accountSearchQueryString = "[{\"prop\":\"accountStatus\",\"op\":\"=\",\"val\":\"Active\",\"type\":\"string\"}]";
        criteriaList = ApiSearchCriteria.parseQueryString(ApiVersion.CURRENT_VERSION, accountSearchQueryString);
    }

    @Test
    public void testSearchAccounts(){
        when(productIntegrationService.loadProductsMap(any(ServiceErrors.class))).thenReturn(mapProducts);
        when(accountService.loadWrapAccountWithoutContainers(any(ServiceErrors.class))).thenReturn(accountMap);
        when(accountService.loadAccountBalances(any(ServiceErrors.class))).thenReturn(accountBalanceList);
        when(transitionAccountIntegrationService.getAllTransitionAccounts(any(ServiceErrors.class))).thenReturn(transitionAccountDetailHolder);
        List<TransitionAccountDto> transitionAccounts = transitionClientDtoService.search(criteriaList, new ServiceErrorsImpl());
        Assert.assertNotNull(transitionAccounts);
        Assert.assertEquals(transitionAccounts.size(),3);


    }
}
