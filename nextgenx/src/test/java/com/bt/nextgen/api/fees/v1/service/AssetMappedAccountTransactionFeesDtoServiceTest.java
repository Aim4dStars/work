package com.bt.nextgen.api.fees.v1.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.fees.v1.model.AssetMappedAccountTransactionFeesDto;
import com.bt.nextgen.api.fees.v1.model.TransactionFeeDto;
import com.bt.nextgen.api.trading.v1.util.TradableAssetsDtoServiceHelper;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.transactionfee.AvaloqTransactionFee;
import com.bt.nextgen.service.avaloq.transactionfee.AvaloqTransactionFeeImpl;
import com.bt.nextgen.service.avaloq.transactionfee.CacheManagedAvaloqTransactionFeeIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.transactionfee.ContainerType;
import com.bt.nextgen.service.integration.transactionfee.ExecutionType;
import com.btfin.panorama.service.avaloq.product.FeeType;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * @deprecated Use V2
 */
@Deprecated
@RunWith(MockitoJUnitRunner.class)
public class AssetMappedAccountTransactionFeesDtoServiceTest {

    @InjectMocks
    AssetMappedAccountTransactionFeesDtoServiceImpl transactionFeeDtoService;

    @Mock
    CacheManagedAvaloqTransactionFeeIntegrationService transactionFeeService;

    @Mock
    private TradableAssetsDtoServiceHelper tradableAssetsDtoServiceHelper;

    List<AvaloqTransactionFee> directTransactionFees;
    List<AvaloqTransactionFee> ihinTransactionFees;
    AvaloqTransactionFeeImpl shareTransactionFee;
    AvaloqTransactionFeeImpl ihinTransactionFee;
    AvaloqTransactionFeeImpl adminFee;
    AvaloqTransactionFeeImpl unknownFee;
    AvaloqTransactionFeeImpl managedFundTransactionFee;

    @Before
    public void setUp() {
        BrokerKey brokerKey = BrokerKey.valueOf("63456");
        ProductKey productKey = ProductKey.valueOf("63456");

        Mockito.when(
                tradableAssetsDtoServiceHelper.loadBroker(Mockito.any(WrapAccountDetail.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(brokerKey);
        Mockito.when(tradableAssetsDtoServiceHelper.loadDirectProductKey(Mockito.any(WrapAccountDetail.class)))
                .thenReturn(productKey);

        shareTransactionFee = new AvaloqTransactionFeeImpl();
        shareTransactionFee.setFeeType(FeeType.BROKERAGE);
        shareTransactionFee.setFactor(new BigDecimal("0.0034"));
        shareTransactionFee.setMaximum(new BigDecimal("500000"));
        shareTransactionFee.setFixedAmount(new BigDecimal("-250"));
        shareTransactionFee.setAdviserId("23455");
        shareTransactionFee.setContainerType(ContainerType.DIRECT);
        shareTransactionFee.setDealerGroupId("634511");
        shareTransactionFee.setExecutionType(ExecutionType.DIRECT_MARKET_ACCESS);
        shareTransactionFee.setPriority(2);
        shareTransactionFee.setProductId("1234");
        shareTransactionFee.setTransactionFeeId("523454");

        managedFundTransactionFee = new AvaloqTransactionFeeImpl();
        managedFundTransactionFee.setFeeType(FeeType.MANAGED_FUND_TRANSACTION_FEE);
        managedFundTransactionFee.setFactor(new BigDecimal("0.0022"));
        managedFundTransactionFee.setMinimum(new BigDecimal("2000"));
        managedFundTransactionFee.setPriority(1);

        adminFee = new AvaloqTransactionFeeImpl();
        adminFee.setFeeType(FeeType.ADMINISTRATION_FEE);
        adminFee.setFactor(new BigDecimal("0.0068"));
        adminFee.setTransactionFeeId("7894");

        unknownFee = new AvaloqTransactionFeeImpl();
        unknownFee.setTransactionFeeId("999999");

        directTransactionFees = new ArrayList<>();
        directTransactionFees.add(shareTransactionFee);
        directTransactionFees.add(managedFundTransactionFee);
        directTransactionFees.add(adminFee);
        directTransactionFees.add(unknownFee);

        ihinTransactionFee = new AvaloqTransactionFeeImpl();
        ihinTransactionFee.setFeeType(FeeType.BROKERAGE);
        ihinTransactionFee.setFactor(new BigDecimal("0.0068"));
        ihinTransactionFee.setMaximum(new BigDecimal("500000"));
        ihinTransactionFee.setFixedAmount(new BigDecimal("-500"));
        ihinTransactionFee.setAdviserId("23455");
        ihinTransactionFee.setContainerType(ContainerType.IHIN);
        ihinTransactionFee.setDealerGroupId("634511");
        ihinTransactionFee.setExecutionType(ExecutionType.DIRECT_MARKET_ACCESS);
        ihinTransactionFee.setPriority(2);
        ihinTransactionFee.setProductId("1234");
        ihinTransactionFee.setTransactionFeeId("548245");

        ihinTransactionFees = new ArrayList<>();
        ihinTransactionFees.add(ihinTransactionFee);
        ihinTransactionFees.add(adminFee);
        ihinTransactionFees.add(unknownFee);

        Mockito.when(transactionFeeService.loadDirectTransactionFees(Mockito.any(String.class), Mockito.any(String.class),
                Mockito.any(String.class), Mockito.any(ServiceErrors.class))).thenReturn(directTransactionFees);

        Mockito.when(transactionFeeService.loadTransactionFees(Mockito.any(ExecutionType.class), Mockito.any(ContainerType.class),
                Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class),
                Mockito.any(ServiceErrors.class))).thenReturn(ihinTransactionFees);
    }

    @Test
    public void testFindDirectTransactionFee() {
        AccountKey accountKey = new AccountKey("23455");
        WrapAccountDetailImpl account = new WrapAccountDetailImpl();
        account.setAdviserKey(BrokerKey.valueOf("123444"));

        Mockito.when(tradableAssetsDtoServiceHelper.loadAccount(Mockito.any(String.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(account);

        AssetMappedAccountTransactionFeesDto assetMappedTransactionFees = transactionFeeDtoService.find(accountKey,
                new FailFastErrorsImpl());

        assertThat(assetMappedTransactionFees.getAssetTransactionFees(), notNullValue());
        assertThat(assetMappedTransactionFees.getAssetTransactionFees().size(), equalTo(2));
        assertThat(assetMappedTransactionFees.getKey(), equalTo(accountKey));

        TransactionFeeDto shareFeeDto = assetMappedTransactionFees.getAssetTransactionFees()
                .get(AssetType.SHARE.getDisplayName());
        TransactionFeeDto managedFundFeeDto = assetMappedTransactionFees.getAssetTransactionFees()
                .get(AssetType.MANAGED_FUND.getDisplayName());

        testMatches(shareTransactionFee, shareFeeDto);
        testMatches(managedFundTransactionFee, managedFundFeeDto);

    }

    @Test
    public void testFindIhinTransactionFee() {
        AccountKey accountKey = new AccountKey("23455");
        WrapAccountDetailImpl account = new WrapAccountDetailImpl();
        account.setAdviserKey(BrokerKey.valueOf("123444"));
        account.setIhin("2332762219");

        Mockito.when(tradableAssetsDtoServiceHelper.loadAccount(Mockito.any(String.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(account);

        AssetMappedAccountTransactionFeesDto assetMappedTransactionFees = transactionFeeDtoService.find(accountKey,
                new FailFastErrorsImpl());

        assertThat(assetMappedTransactionFees.getAssetTransactionFees(), notNullValue());
        assertThat(assetMappedTransactionFees.getAssetTransactionFees().size(), equalTo(2));
        assertThat(assetMappedTransactionFees.getKey(), equalTo(accountKey));

        TransactionFeeDto shareFeeDto = assetMappedTransactionFees.getAssetTransactionFees()
                .get(AssetType.SHARE.getDisplayName());

        testMatches(ihinTransactionFee, shareFeeDto);
    }

    private void testMatches(AvaloqTransactionFee expectedFee, TransactionFeeDto actualFee) {
        assertThat(expectedFee.getFactor(), equalTo(actualFee.getFactor()));
        assertThat(expectedFee.getMinimum(), equalTo(actualFee.getMinimum()));
        assertThat(expectedFee.getMaximum(), equalTo(actualFee.getMaximum()));
        assertThat(expectedFee.getFixedAmount() == null ? null : expectedFee.getFixedAmount().negate(),
                equalTo(actualFee.getFixedAmount()));
    }

}
