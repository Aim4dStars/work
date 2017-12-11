package com.bt.nextgen.api.modelportfolio.v2.service.rebalance;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.ExclusionStatus;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.ModelPortfolioRebalanceAccountDto;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.ModelPortfolioRebalanceDetailDto;
import com.bt.nextgen.api.modelportfolio.v2.util.common.ModelPortfolioHelper;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementIntegrationService;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementInterface;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.ModelPortfolioRebalance;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.ModelPortfolioRebalanceIntegrationService;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.ModelPortfolioRebalanceTrigger;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.ModelPortfolioRebalanceTriggerDetails;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.RebalanceAccount;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.userprofile.JobProfileIdentifier;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.broker.BrokerType;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class ModelPortfolioRebalanceDetailDtoServiceTest {

    @InjectMocks
    private ModelPortfolioRebalanceDetailDtoServiceImpl dtoServiceImpl;

    @Mock
    private StaticIntegrationService staticIntegrationService;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private ProductIntegrationService productIntegrationService;

    @Mock
    private ModelPortfolioRebalanceIntegrationService rebalanceIntegrationService;

    @Mock
    private InvestmentPolicyStatementIntegrationService ipsIntegrationService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private ModelPortfolioHelper helper;

    private ModelPortfolioRebalance rebalSummary;

    @Before
    public void setup() throws Exception {
        UserProfile profile = Mockito.mock(UserProfile.class);
        Mockito.when(userProfileService.getActiveProfile()).thenReturn(profile);

        InvestmentPolicyStatementInterface ips = Mockito.mock(InvestmentPolicyStatementInterface.class);
        Mockito.when(ips.getIpsKey()).thenReturn(IpsKey.valueOf("1234"));

        Map<IpsKey, InvestmentPolicyStatementInterface> ipsMap = Collections.singletonMap(ips.getIpsKey(), ips);

        Mockito.when(
                ipsIntegrationService.getInvestmentPolicyStatements(Mockito.anyListOf(IpsKey.class),
                        Mockito.any(ServiceErrors.class)))
                .thenReturn(ipsMap);

        rebalSummary = Mockito.mock(ModelPortfolioRebalance.class);
        Mockito.when(rebalSummary.getIpsKey()).thenReturn(IpsKey.valueOf("1234"));
    }

    @Test
    public void testConversion_whenAccountListIsReturned_ItIsTurnedIntoADto() {

        BrokerUser user = Mockito.mock(BrokerUser.class);
        Mockito.when(user.getFirstName()).thenReturn("fname");
        Mockito.when(user.getLastName()).thenReturn("lname");
        Mockito.when(user.getMiddleName()).thenReturn("mname");

        Broker broker = Mockito.mock(Broker.class);
        Mockito.when(broker.getKey()).thenReturn(BrokerKey.valueOf("brokerKey"));
        Mockito.when(broker.getBrokerType()).thenReturn(BrokerType.DEALER);

        Mockito.when(brokerIntegrationService.getBroker(Mockito.any(BrokerKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(broker);

        Mockito.when(brokerIntegrationService.getBrokersForJob(Mockito.any(JobProfileIdentifier.class),
                Mockito.any(ServiceErrors.class))).thenReturn(Collections.singletonList(broker));

        Mockito.when(
                brokerIntegrationService.getAdviserBrokerUser(Mockito.any(BrokerKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(user);

        Mockito.when(userProfileService.getInvestmentManager(Mockito.any(ServiceErrors.class))).thenReturn(broker);

        Product product = Mockito.mock(Product.class);
        Mockito.when(product.getProductName()).thenReturn("ProductName");
        Mockito.when(productIntegrationService.getProductDetail(Mockito.any(ProductKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(product);

        WrapAccount account = Mockito.mock(WrapAccount.class);
        Mockito.when(account.getAccountKey()).thenReturn(AccountKey.valueOf("accountKey"));
        Mockito.when(account.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
        Mockito.when(account.getProductKey()).thenReturn(ProductKey.valueOf("key"));

        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();
        accountMap.put(account.getAccountKey(), account);
        Mockito.when(accountIntegrationService.loadWrapAccountWithoutContainers(Mockito.any(ServiceErrors.class)))
                .thenReturn(accountMap);

        RebalanceAccount rebal = Mockito.mock(RebalanceAccount.class);
        Mockito.when(rebal.getAccount()).thenReturn(AccountKey.valueOf("accountKey"));
        Mockito.when(rebal.getAdviser()).thenReturn(BrokerKey.valueOf("brokerKey"));
        Mockito.when(rebal.getEstimatedBuys()).thenReturn(Integer.valueOf(1));
        Mockito.when(rebal.getEstimatedSells()).thenReturn(Integer.valueOf(2));
        Mockito.when(rebal.getAssetClassBreach()).thenReturn(Integer.valueOf(3));
        Mockito.when(rebal.getToleranceBreach()).thenReturn(Integer.valueOf(4));
        Mockito.when(rebal.getUserExclusionReason()).thenReturn("exclusion reason");
        Mockito.when(rebal.getRebalDocId()).thenReturn("docid");
        Mockito.when(rebal.getValue()).thenReturn(BigDecimal.valueOf(5));
        
        Mockito.when(
                rebalanceIntegrationService.loadModelPortfolioRebalanceAccounts(Mockito.any(IpsKey.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(Collections.singletonList(rebal));

        ModelPortfolioRebalanceTriggerDetails triggerDetails = Mockito.mock(ModelPortfolioRebalanceTriggerDetails.class);
        // Mockito.when(triggerDetails.getDocId()).thenReturn("docid");

        ModelPortfolioRebalanceTrigger trigger = Mockito.mock(ModelPortfolioRebalanceTrigger.class);
        Mockito.when(trigger.getRebalanceTriggerDetails()).thenReturn(Collections.singletonList(triggerDetails));

        Mockito.when(rebalSummary.getRebalanceTriggers()).thenReturn(Collections.singletonList(trigger));

        Mockito.when(
                rebalanceIntegrationService.loadModelPortfolioRebalances(Mockito.any(BrokerKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn(Collections.singletonList(rebalSummary));

        ModelPortfolioKey key = new ModelPortfolioKey("1234");
        List<ModelPortfolioRebalanceAccountDto> rebalances = dtoServiceImpl.find(key, new FailFastErrorsImpl())
                .getRebalanceAccounts();

        Assert.assertEquals(1, rebalances.size());
        ModelPortfolioRebalanceAccountDto rebalance = rebalances.get(0);
        Assert.assertEquals(rebal.getEstimatedBuys(), rebalance.getBuys());
        Assert.assertEquals(rebal.getEstimatedSells(), rebalance.getSells());
        Assert.assertEquals(rebal.getToleranceBreach(), rebalance.getToleranceBreach());
        Assert.assertEquals(account.getAccountName(), rebalance.getAccountName());
        Assert.assertEquals(account.getAccountNumber(), rebalance.getAccountNumber());
        Assert.assertEquals("fname mname lname", rebalance.getAdviserName());
        Assert.assertEquals(rebal.getUserExclusionReason(), rebalance.getExclusionReason());
        Assert.assertEquals("accountKey", EncodedString.toPlainText(rebalance.getKey().getAccountId()));
        Assert.assertEquals(null, rebalance.getLastRebalance());
        Assert.assertEquals(rebal.getValue(), rebalance.getModelValue());
        Assert.assertEquals(AccountStructureType.Individual.name(), rebalance.getAccountType());
        Assert.assertEquals("ProductName", rebalance.getProductName());
    }

    @Test
    public void testConversion_whenAccountListIsReturnedWithEmptyValues_ItIsTurnedIntoADto() {

        Code code = Mockito.mock(Code.class);
        Mockito.when(code.getName()).thenReturn("codeName");
        Mockito.when(
                staticIntegrationService.loadCode(Mockito.any(CodeCategory.class), Mockito.anyString(),
                        Mockito.any(ServiceErrors.class))).thenReturn(code);

        BrokerUser user = Mockito.mock(BrokerUser.class);

        Broker broker = Mockito.mock(Broker.class);
        Mockito.when(broker.getKey()).thenReturn(BrokerKey.valueOf("brokerKey"));
        Mockito.when(broker.getBrokerType()).thenReturn(BrokerType.DEALER);

        Mockito.when(brokerIntegrationService.getBroker(Mockito.any(BrokerKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(broker);

        Mockito.when(
                brokerIntegrationService.getBrokersForJob(Mockito.any(JobProfileIdentifier.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(Collections.singletonList(broker));

        Mockito.when(
                brokerIntegrationService.getAdviserBrokerUser(Mockito.any(BrokerKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(user);

        Mockito.when(userProfileService.getInvestmentManager(Mockito.any(ServiceErrors.class))).thenReturn(broker);

        Mockito.when(productIntegrationService.getProductDetail(Mockito.any(ProductKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(null);

        WrapAccount account = Mockito.mock(WrapAccount.class);
        Mockito.when(account.getAccountKey()).thenReturn(AccountKey.valueOf("accountKey"));
        Mockito.when(account.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
        Mockito.when(account.getProductKey()).thenReturn(ProductKey.valueOf("key"));

        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();
        accountMap.put(account.getAccountKey(), account);
        Mockito.when(accountIntegrationService.loadWrapAccountWithoutContainers(Mockito.any(ServiceErrors.class))).thenReturn(
                accountMap);

        RebalanceAccount rebal = Mockito.mock(RebalanceAccount.class);
        Mockito.when(rebal.getAccount()).thenReturn(AccountKey.valueOf("accountKey"));
        Mockito.when(rebal.getAdviser()).thenReturn(BrokerKey.valueOf("brokerKey"));
        Mockito.when(rebal.getEstimatedBuys()).thenReturn(Integer.valueOf(1));
        Mockito.when(rebal.getEstimatedSells()).thenReturn(Integer.valueOf(2));
        Mockito.when(rebal.getAssetClassBreach()).thenReturn(Integer.valueOf(3));
        Mockito.when(rebal.getToleranceBreach()).thenReturn(Integer.valueOf(4));
        Mockito.when(rebal.getUserExclusionReason()).thenReturn("exclusion reason");
        Mockito.when(rebal.getRebalDocId()).thenReturn("docid");
        Mockito.when(rebal.getValue()).thenReturn(BigDecimal.valueOf(5));

        Mockito.when(
                rebalanceIntegrationService.loadModelPortfolioRebalanceAccounts(Mockito.any(IpsKey.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(Collections.singletonList(rebal));

        ModelPortfolioRebalanceTriggerDetails triggerDetails = Mockito.mock(ModelPortfolioRebalanceTriggerDetails.class);

        ModelPortfolioRebalanceTrigger trigger = Mockito.mock(ModelPortfolioRebalanceTrigger.class);
        Mockito.when(trigger.getRebalanceTriggerDetails()).thenReturn(Collections.singletonList(triggerDetails));

        Mockito.when(rebalSummary.getRebalanceTriggers()).thenReturn(Collections.singletonList(trigger));

        Mockito.when(
                rebalanceIntegrationService.loadModelPortfolioRebalances(Mockito.any(BrokerKey.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(Collections.singletonList(rebalSummary));

        ModelPortfolioKey key = new ModelPortfolioKey("1234");
        List<ModelPortfolioRebalanceAccountDto> rebalances = dtoServiceImpl.find(key, new FailFastErrorsImpl())
                .getRebalanceAccounts();

        Assert.assertEquals(1, rebalances.size());
        ModelPortfolioRebalanceAccountDto rebalance = rebalances.get(0);
        Assert.assertEquals(rebal.getEstimatedBuys(), rebalance.getBuys());
        Assert.assertEquals(rebal.getEstimatedSells(), rebalance.getSells());
        Assert.assertEquals(rebal.getToleranceBreach(), rebalance.getToleranceBreach());
        Assert.assertEquals(account.getAccountName(), rebalance.getAccountName());
        Assert.assertEquals(account.getAccountNumber(), rebalance.getAccountNumber());
        Assert.assertEquals("", rebalance.getAdviserName());
        Assert.assertEquals(ExclusionStatus.SYSTEM_EXCLUDED, rebalance.getExclusionStatus());
        Assert.assertEquals("codeName", rebalance.getExclusionReason());
        Assert.assertEquals("accountKey", EncodedString.toPlainText(rebalance.getKey().getAccountId()));
        Assert.assertEquals(null, rebalance.getLastRebalance());
        Assert.assertEquals(rebal.getValue(), rebalance.getModelValue());
        Assert.assertEquals(AccountStructureType.Individual.name(), rebalance.getAccountType());
        Assert.assertEquals("", rebalance.getProductName());
    }

    @Test
    public void testConversion_whenNoRebalanceFound_thenRelevantFieldsEmptyInDto() {

        Code code = Mockito.mock(Code.class);
        Mockito.when(code.getName()).thenReturn("codeName");
        Mockito.when(
                staticIntegrationService.loadCode(Mockito.any(CodeCategory.class), Mockito.anyString(),
                        Mockito.any(ServiceErrors.class))).thenReturn(code);

        BrokerUser user = Mockito.mock(BrokerUser.class);

        Broker broker = Mockito.mock(Broker.class);
        Mockito.when(broker.getKey()).thenReturn(BrokerKey.valueOf("brokerKey"));
        Mockito.when(broker.getBrokerType()).thenReturn(BrokerType.DEALER);

        Mockito.when(brokerIntegrationService.getBroker(Mockito.any(BrokerKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(broker);

        Mockito.when(
                brokerIntegrationService.getBrokersForJob(Mockito.any(JobProfileIdentifier.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(Collections.singletonList(broker));

        Mockito.when(
                brokerIntegrationService.getAdviserBrokerUser(Mockito.any(BrokerKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(user);

        Mockito.when(userProfileService.getInvestmentManager(Mockito.any(ServiceErrors.class))).thenReturn(broker);

        Mockito.when(productIntegrationService.getProductDetail(Mockito.any(ProductKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(null);

        WrapAccount account = Mockito.mock(WrapAccount.class);
        Mockito.when(account.getAccountKey()).thenReturn(AccountKey.valueOf("accountKey"));
        Mockito.when(account.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
        Mockito.when(account.getProductKey()).thenReturn(ProductKey.valueOf("key"));

        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();
        accountMap.put(account.getAccountKey(), account);
        Mockito.when(accountIntegrationService.loadWrapAccountWithoutContainers(Mockito.any(ServiceErrors.class))).thenReturn(
                accountMap);

        RebalanceAccount rebal = Mockito.mock(RebalanceAccount.class);
        Mockito.when(rebal.getAccount()).thenReturn(AccountKey.valueOf("accountKey"));
        Mockito.when(rebal.getAdviser()).thenReturn(BrokerKey.valueOf("brokerKey"));
        Mockito.when(rebal.getEstimatedBuys()).thenReturn(Integer.valueOf(1));
        Mockito.when(rebal.getEstimatedSells()).thenReturn(Integer.valueOf(2));
        Mockito.when(rebal.getAssetClassBreach()).thenReturn(Integer.valueOf(3));
        Mockito.when(rebal.getToleranceBreach()).thenReturn(Integer.valueOf(4));
        Mockito.when(rebal.getUserExclusionReason()).thenReturn("exclusion reason");
        Mockito.when(rebal.getRebalDocId()).thenReturn("docid");
        Mockito.when(rebal.getValue()).thenReturn(BigDecimal.valueOf(5));

        Mockito.when(
                rebalanceIntegrationService.loadModelPortfolioRebalanceAccounts(Mockito.any(IpsKey.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(Collections.singletonList(rebal));

        Mockito.when(
                rebalanceIntegrationService.loadModelPortfolioRebalances(Mockito.any(BrokerKey.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(Collections.<ModelPortfolioRebalance> emptyList());

        ModelPortfolioKey key = new ModelPortfolioKey("1234");
        ModelPortfolioRebalanceDetailDto dto = dtoServiceImpl.find(key, new FailFastErrorsImpl());

        Assert.assertNotNull(dto);
        Assert.assertNull(dto.getTotalAccountsCount());
        Assert.assertEquals(1, dto.getRebalanceAccounts().size());

    }

}
