package com.bt.nextgen.api.smsf.service;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.smsf.constants.AssetClass;
import com.bt.nextgen.api.smsf.constants.AssetType;
import com.bt.nextgen.api.smsf.model.ExternalAssetDto;
import com.bt.nextgen.api.smsf.model.ExternalAssetTrxnDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.TransactionStatusImpl;
import com.bt.nextgen.service.avaloq.account.SubAccountImpl;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.integration.TransactionStatus;
import com.bt.nextgen.service.integration.account.*;
import com.bt.nextgen.service.integration.accountingsoftware.model.SoftwareFeedStatus;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.externalasset.service.ExternalAssetIntegrationService;
import com.btfin.panorama.service.integration.account.SubAccount;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SaveExternalAssetsServiceImplTest {
    private static final String ACCOUNT_ID = "BB8FE88383B20FDD59ED5E2DD0231C347EED094FC7DF2098";

    @Mock
    ExternalAssetIntegrationService externalAssetIntegrationService;

    @Mock
    private AssetIntegrationService assetIntegrationService;

    @Mock
    private AccountIntegrationService accountService;

    @Mock
    private ServiceErrors serviceErrors;

    @InjectMocks
    private SaveExternalAssetsServiceImpl service;

    @Test
    public void submitWithValidData() {
        final String savedStatus = "saved";
        final ExternalAssetTrxnDto result;

        when(externalAssetIntegrationService.saveOrUpdateExternalAssets(
                any((com.bt.nextgen.service.integration.account.AccountKey.class)),
                any(ContainerKey.class), anyList(), any(DateTime.class))).thenReturn(
                makeTransactionStatus(savedStatus));

        when(assetIntegrationService.loadExternalAssets(serviceErrors)).thenReturn(makeExternalAssets());
        when(accountService.loadWrapAccountDetail(any(com.bt.nextgen.service.integration.account.AccountKey.class),
                any(ServiceErrors.class))).thenReturn(makeAccount());

        result = service.submit(makeExternalAssetTrxnDto(ACCOUNT_ID, makeDtos()), serviceErrors);
        assertThat("result", result, notNullValue());
        assertThat("result status", result.getTransactionStatus(), equalTo(savedStatus));

        verify(externalAssetIntegrationService).saveOrUpdateExternalAssets(
                any((com.bt.nextgen.service.integration.account.AccountKey.class)),
                any(ContainerKey.class), anyList(), any(DateTime.class));
        verify(assetIntegrationService).loadExternalAssets(serviceErrors);
        verify(accountService).loadWrapAccountDetail(any(com.bt.nextgen.service.integration.account.AccountKey.class),
                any(ServiceErrors.class));
    }

    @Test
    public void submitWithInvalidData() {
        final ExternalAssetTrxnDto result;

        when(assetIntegrationService.loadExternalAssets(serviceErrors)).thenReturn(makeExternalAssets());

        result = service.submit(makeExternalAssetTrxnDto(ACCOUNT_ID, makeDtosWithInvalidData()), serviceErrors);
        assertThat("result", result, notNullValue());
        assertThat("result status", result.getTransactionStatus(), nullValue());

        verify(externalAssetIntegrationService, never()).saveOrUpdateExternalAssets(
                any((com.bt.nextgen.service.integration.account.AccountKey.class)),
                any(ContainerKey.class), anyList(), any(DateTime.class));
        verify(assetIntegrationService).loadExternalAssets(serviceErrors);
        verify(accountService, never()).loadWrapAccountDetail(any(com.bt.nextgen.service.integration.account.AccountKey.class),
                any(ServiceErrors.class));
    }

    private ExternalAssetTrxnDto makeExternalAssetTrxnDto(String accountId, List<ExternalAssetDto> assetDtos) {
        final ExternalAssetTrxnDto retval = new ExternalAssetTrxnDto();

        retval.setKey(new AccountKey(accountId));
        retval.setContainer("ex_containertype");
        retval.setAssetDtos(assetDtos);

        return retval;
    }

    private TransactionStatus makeTransactionStatus(String status) {
        final TransactionStatus transactionStatus = new TransactionStatusImpl();

        transactionStatus.setStatus(status);

        return transactionStatus;
    }

    private List<ExternalAssetDto> makeDtosWithInvalidData() {
        List<ExternalAssetDto> input = new ArrayList<>();
        ExternalAssetDto asset = null;

        // edit all class on platform

        // edit all class off platform

        // Edit on platform - australian listed securities
        asset = new ExternalAssetDto();
        asset.setAssetId("11");
        asset.setPositionId("1111");
        asset.setAssetType("ls"); // Intl_id of AUSTRALIAN_LISTED_SECURITIES
        asset.setAssetName("Eservglobal Limited");
        asset.setAssetCode("ESV"); // max 6 length
        asset.setAssetClass(AssetClass.AUSTRALIAN_LISTED_SECURITIES.getCode());
        asset.setQuantity("15");
        asset.setMarketValue("150");
        asset.setValueDate("17 Apr 2015");
        input.add(asset);

        // Edit off platform - australian listed securities
        asset = new ExternalAssetDto();
        // asset.setAssetId("11");
        asset.setPositionId("2222");
        // asset.setSource("new panorama asset"); //optional
        asset.setAssetType("ls"); // Intl_id of AUSTRALIAN_LISTED_SECURITIES
        asset.setPositionName("TLSQ");
        asset.setPositionCode("TSTCD"); // max 6 length
        asset.setAssetClass(AssetClass.AUSTRALIAN_LISTED_SECURITIES.getCode());
        asset.setQuantity("20");
        asset.setMarketValue("450");
        asset.setValueDate("17 Apr 2015");
        input.add(asset);

        // Edit on platform - managed fund
        asset = new ExternalAssetDto();
        asset.setAssetId("44");
        asset.setPositionId("3333");
        asset.setQuantity("10"); // required
        asset.setSource("new panorama asset");
        asset.setAssetType("mf"); // Intl_id of MANAGED_FUND
        asset.setAssetName("RIM0038AU Russell Emerging Markets Fund");
        asset.setPositionCode("RIM");
        asset.setAssetClass(AssetClass.INTERNATIONAL_LISTED_SECURITIES.getCode());
        asset.setMarketValue("100");
        asset.setValueDate("17 Apr 2015");
        input.add(asset);

        // Edit off platform - managed fund
        asset = new ExternalAssetDto();
        asset.setPositionId("4444");
        asset.setAssetType("mf"); // Intl_id
        asset.setPositionName("TRT");
        asset.setPositionCode("QRU11AS");
        asset.setAssetClass(AssetClass.INTERNATIONAL_LISTED_SECURITIES.getCode());
        asset.setQuantity("0"); // required
        asset.setMarketValue("550");
        asset.setValueDate("16 Apr 2015");
        input.add(asset);

        // off panorama AssetType.INTERNATIONAL_LISTED_SECURITIES
        asset = new ExternalAssetDto();
        asset.setPositionId("");
        asset.setQuantity("dja"); // required
        asset.setSource("The traditional balance of payments identity does not take into account changes in asset prices and exchange rates. For example, the value of external assets or liabilities can change due to higher or lower stockmarket prices or a default/write-off on debt."); // optional
        asset.setAssetType("ils"); // Intl_id of international listed security
        asset.setPositionName("testils");
        asset.setPositionCode("ESE458"); // max 6 length
        asset.setAssetClass(AssetClass.AUSTRALIAN_FIXED_INTEREST.getCode());
        asset.setMarketValue("250");
        asset.setValueDate("17 Apr 2015");
        input.add(asset);

        // AssetType.TERM_DEPOSIT
        // Add new off platform - Term Deposit
        asset = new ExternalAssetDto();
        asset.setPositionId("6666");
        // asset.setSource(""); //optional
        asset.setAssetType("td"); // Intl_id of Term Deposit
        asset.setPositionName("test term depo");
        // asset.setAssetCode("QRU1111AS");
        asset.setAssetClass(AssetClass.CASH.getCode());
        // asset.setQuantity("15"); //required
        asset.setMarketValue("DGT");
        asset.setValueDate("02 Feb 2015");
        asset.setMaturityDate("30 Apr 2015");
        input.add(asset);

        // AssetType.CASH
        // Add new off platform
        asset = new ExternalAssetDto();
        asset.setPositionId("7777");
        // asset.setSource(""); //optional
        asset.setAssetType("cash"); // Intl_id
        asset.setPositionName("test cash type");
        // asset.setAssetCode("QRU1111AS");
        asset.setAssetClass(AssetClass.CASH.getCode());
        // asset.setQuantity("15"); //required
        asset.setMarketValue("2300");
        asset.setValueDate("20 Apr 2015");
        input.add(asset);

        // AssetType.INTERNATIONAL_LISTED_SECURITIES
        // Add new off platform
        asset = new ExternalAssetDto();
        asset.setPositionId("8888");
        // asset.setSource(""); //optional
        asset.setAssetType("ils"); // Intl_id
        asset.setPositionName("test ils asset");
        // asset.setAssetCode("QRU1111AS");
        asset.setAssetClass(AssetClass.INTERNATIONAL_LISTED_SECURITIES.getCode());
        asset.setQuantity("25"); // required
        asset.setMarketValue("670");
        asset.setValueDate("20 Apr 2015");
        input.add(asset);

        // AssetType.MANAGED_PORTFOLIO
        // Add new off platform
        asset = new ExternalAssetDto();
        asset.setPositionId("9999");
        // asset.setSource(""); //optional
        asset.setAssetType("mp"); // Intl_id
        asset.setPositionName("test mport");
        asset.setPositionCode("EST1121ER");
        asset.setAssetClass(AssetClass.AUSTRALIAN_REAL_ESTATE.getCode());
        // asset.setQuantity("25"); //required
        asset.setMarketValue("250000");
        asset.setValueDate("20 Apr 2015");
        input.add(asset);

        // AssetType.DIRECT_PROPERTY;
        // Add new off platform
        asset = new ExternalAssetDto();
        asset.setPositionId("29999");
        // asset.setSource(""); //optional
        asset.setAssetType("mp"); // Intl_id
        asset.setPositionCode("EST1156ER");
        asset.setPositionName("151 Clarence Street, Sydney, NSW 2000");
        asset.setPropertyType("Commercial");
        asset.setAssetClass(AssetClass.AUSTRALIAN_REAL_ESTATE.getCode());
        // asset.setQuantity("25"); //required
        asset.setMarketValue("250000");
        asset.setValueDate("20 Apr 2015");
        input.add(asset);

        // AssetType.OTHER_ASSET
        // Add new off platform
        asset = new ExternalAssetDto();
        asset.setPositionId("22222");
        // asset.setSource(""); //optional
        asset.setAssetType("oth"); // Intl_id
        asset.setPositionName("test otherasset");
        asset.setPositionCode("OTHASTC1");
        asset.setAssetClass(AssetClass.CASH.getCode());
        asset.setQuantity("35"); // required
        asset.setMarketValue("3500");
        asset.setValueDate("20 Apr 2015");
        input.add(asset);

        return input;
    }

    private List<ExternalAssetDto> makeDtos() {
        List<ExternalAssetDto> input = new ArrayList<>();
        ExternalAssetDto asset = null;

        // Add new on platform - australian listed securities
        asset = new ExternalAssetDto();
        asset.setAssetId("11");
        asset.setQuantity("10"); // required
        asset.setSource("new panorama asset"); // optional
        asset.setAssetType("ls"); // Intl_id of AUSTRALIAN_LISTED_SECURITIES
        asset.setPositionName("Eservglobal Limited");
        asset.setAssetCode("ESV"); // max 6 length
        asset.setAssetClass(AssetClass.AUSTRALIAN_LISTED_SECURITIES.getCode());
        asset.setAssetClassId(AssetClass.AUSTRALIAN_LISTED_SECURITIES.getCode());
        asset.setMarketValue("110");
        asset.setValueDate("17 Apr 2015");
        //asset.setPositionId("12345");
        input.add(asset);

        // Add new off platform - australian listed securities
        asset = new ExternalAssetDto();
        // asset.setAssetId("11");
        asset.setQuantity("20"); // required
        // asset.setSource("new panorama asset"); //optional
        asset.setAssetType("ls"); // Intl_id of AUSTRALIAN_LISTED_SECURITIES
        asset.setPositionName("TLSQ");
        asset.setPositionId("45698");
        asset.setPositionCode("TSTCD1213"); // max 6 length
        asset.setAssetClass(AssetClass.AUSTRALIAN_LISTED_SECURITIES.getCode());
        asset.setAssetClassId(AssetClass.AUSTRALIAN_LISTED_SECURITIES.getCode());
        asset.setMarketValue("450");
        asset.setValueDate("17 Apr 2015");
        input.add(asset);

        // Add new on platform - managed fund
        asset = new ExternalAssetDto();
        asset.setAssetId("44");
        asset.setQuantity("10"); // required
        asset.setSource("new panorama asset"); // optional
        asset.setAssetType("mf"); // Intl_id of MANAGED_FUND
        asset.setPositionName("RIM0038AU Russell Emerging Markets Fund");
        //asset.setPositionId("454");
        asset.setPositionCode("RIM0038AU");
        asset.setAssetClass(AssetClass.INTERNATIONAL_LISTED_SECURITIES.getCode());
        asset.setAssetClassId(AssetClass.INTERNATIONAL_LISTED_SECURITIES.getCode());
        asset.setMarketValue("100");
        asset.setValueDate("17 Apr 2015");
        input.add(asset);

        // Add new off platform - managed fund
        asset = new ExternalAssetDto();
        // asset.setAssetId("44");
        // asset.setSource(""); //optional
        asset.setAssetType("mf"); // Intl_id of MANAGED_FUND
        asset.setPositionId("7898");
        asset.setPositionName("TRT");
        asset.setPositionCode("QRU1111AS");
        asset.setAssetClass(AssetClass.INTERNATIONAL_LISTED_SECURITIES.getCode());
        asset.setAssetClassId(AssetClass.INTERNATIONAL_LISTED_SECURITIES.getCode());
        asset.setQuantity("15"); // required
        asset.setMarketValue("550");
        asset.setValueDate("16 Apr 2015");
        input.add(asset);

        // off panorama AssetType.INTERNATIONAL_LISTED_SECURITIES
        asset = new ExternalAssetDto();
        // asset.setAssetId("");
        asset.setQuantity("5"); // required
        asset.setSource("source one"); // optional
        asset.setAssetType("ils"); // Intl_id of international listed security
        asset.setPositionId("42124");
        asset.setPositionName("testils");
        asset.setPositionCode("ESE485"); // max 6 length
        asset.setAssetClass(AssetClass.AUSTRALIAN_FIXED_INTEREST.getCode());
        asset.setAssetClassId(AssetClass.AUSTRALIAN_FIXED_INTEREST.getCode());
        asset.setMarketValue("250");
        asset.setValueDate("17 Apr 2015");
        input.add(asset);

        // AssetType.TERM_DEPOSIT
        // Add new off platform - Term Deposit
        asset = new ExternalAssetDto();
        // asset.setAssetId("44");
        // asset.setSource(""); //optional
        asset.setAssetType("td"); // Intl_id of Term Deposit
        asset.setPositionId("12154");
        asset.setPositionName("test term depo");
        // asset.setAssetCode("QRU1111AS");
        asset.setAssetClass(AssetClass.CASH.getCode());
        asset.setAssetClassId(AssetClass.CASH.getCode());
        // asset.setQuantity("15"); //required
        asset.setMarketValue("1200");
        asset.setValueDate("02 Feb 2015");
        asset.setMaturityDate("30 Apr 2015");
        input.add(asset);

        // AssetType.CASH
        // Add new off platform
        asset = new ExternalAssetDto();
        // asset.setAssetId("44");
        // asset.setSource(""); //optional
        asset.setAssetType("cash"); // Intl_id
        asset.setPositionId("517");
        asset.setPositionName("test cash type");
        // asset.setAssetCode("QRU1111AS");
        asset.setAssetClass(AssetClass.CASH.getCode());
        asset.setAssetClassId(AssetClass.CASH.getCode());
        // asset.setQuantity("15"); //required
        asset.setMarketValue("2300");
        asset.setValueDate("20 Apr 2015");
        input.add(asset);

        // AssetType.INTERNATIONAL_LISTED_SECURITIES
        // Add new off platform
        asset = new ExternalAssetDto();
        asset.setAssetType("ils"); // Intl_id
        asset.setPositionName("test ils asset");
        asset.setPositionId("5699");
        // asset.setAssetCode("QRU1111AS");
        asset.setAssetClass(AssetClass.INTERNATIONAL_LISTED_SECURITIES.getCode());
        asset.setAssetClassId(AssetClass.INTERNATIONAL_LISTED_SECURITIES.getCode());
        asset.setQuantity("25"); // required
        asset.setMarketValue("670");
        asset.setValueDate("20 Apr 2015");
        input.add(asset);

        // AssetType.MANAGED_PORTFOLIO
        // Add new off platform
        asset = new ExternalAssetDto();
        asset.setAssetType("mp"); // Intl_id
        asset.setPositionName("test mport");
        asset.setPositionId("127458");
        asset.setPositionCode("EST1121ER");
        asset.setAssetClass(AssetClass.AUSTRALIAN_REAL_ESTATE.getCode());
        asset.setAssetClassId(AssetClass.AUSTRALIAN_REAL_ESTATE.getCode());
        // asset.setQuantity("25"); //required
        asset.setMarketValue("250000");
        asset.setValueDate("20 Apr 2015");
        input.add(asset);

        // AssetType.DIRECT_PROPERTY;
        // Add new off platform
        asset = new ExternalAssetDto();
        asset.setAssetType("mp"); // Intl_id
        asset.setPositionName("151 Clarence Street, Sydney, NSW 2000");
        asset.setPositionId("4823");
        asset.setPositionCode("EST1188ER");
        asset.setPropertyType("Commercial");
        asset.setAssetClass(AssetClass.AUSTRALIAN_REAL_ESTATE.getCode());
        asset.setAssetClassId(AssetClass.AUSTRALIAN_REAL_ESTATE.getCode());
        // asset.setQuantity("25"); //required
        asset.setMarketValue("250000");
        asset.setValueDate("20 Apr 2015");
        input.add(asset);

        // AssetType.OTHER_ASSET
        // Add new off platform
        asset = new ExternalAssetDto();
        asset.setAssetType("oth"); // Intl_id
        asset.setPositionName("test otherasset");
        asset.setPositionId("1616");
        asset.setPositionCode("OTHASTC1");
        asset.setAssetClass(AssetClass.CASH.getCode());
        asset.setAssetClassId(AssetClass.CASH.getCode());
        asset.setQuantity("35"); // required
        asset.setMarketValue("3500");
        asset.setValueDate("20 Apr 2015");
        input.add(asset);

        // AssetType.AUSTRALIAN_LISTED_SECURITIES
        // AssetType.MANAGED_FUND
        // AssetType.INTERNATIONAL_LISTED_SECURITIES
        // AssetType.TERM_DEPOSIT
        // AssetType.CASH
        // AssetType.INTERNATIONAL_LISTED_SECURITIES
        // AssetType.MANAGED_PORTFOLIO
        // AssetType.DIRECT_PROPERTY;
        // AssetType.OTHER_ASSET

        // add all class new panorama

        // add all class off platform

        return input;
    }

    private WrapAccountDetail makeAccount() {
        WrapAccountDetail account = new WrapAccountDetailImpl();
        account.setAccountKey(com.bt.nextgen.service.integration.account.AccountKey.valueOf("BB8FE88383B20FDD59ED5E2DD0231C347EED094FC7DF2098"));
        List<SubAccount> accList = new ArrayList<>();
        SubAccountImpl subAccount = new SubAccountImpl();
        subAccount.setSubAccountType(ContainerType.EXTERNAL_ASSET);
        subAccount.setSubAccountId(SubAccountKey.valueOf("BB0FE88383B20FDD59ED5E2DD0231C347EED094FC7DF2098"));
        subAccount.setExternalAssetsFeedState(SoftwareFeedStatus.MANUAL.getValue());
        accList.add(subAccount);
        account.setSubAccounts(accList);
        return account;
    }

    private Map<String, Asset> makeExternalAssets() {
        Map<String, Asset> results = new HashMap<>();

        AssetImpl asset1 = new AssetImpl();
        asset1.setAssetId("11");
        asset1.setAssetClassId(AssetClass.AUSTRALIAN_LISTED_SECURITIES);
        asset1.setAssetCode("ESV");
        asset1.setAssetName("Eservglobal Limited");
        asset1.setCluster(AssetType.AUSTRALIAN_LISTED_SECURITIES);
        results.put(asset1.getAssetId(), asset1);

        asset1 = new AssetImpl();
        asset1.setAssetId("22");
        asset1.setAssetClassId(AssetClass.INTERNATIONAL_LISTED_SECURITIES);
        asset1.setAssetCode("code-ls");
        asset1.setAssetName("codename-LS");
        asset1.setCluster(AssetType.AUSTRALIAN_LISTED_SECURITIES);
        results.put(asset1.getAssetId(), asset1);

        // MANAGED FUNDS - assettype - start

        asset1 = new AssetImpl();
        asset1.setAssetId("33");
        asset1.setAssetClassId(AssetClass.INTERNATIONAL_LISTED_SECURITIES);
        asset1.setAssetCode("RIM0032AU");
        asset1.setAssetName("Russell Global Opportunities Fund");
        asset1.setCluster(AssetType.MANAGED_FUND);
        results.put(asset1.getAssetId(), asset1);

        asset1 = new AssetImpl();
        asset1.setAssetId("44");
        asset1.setAssetClassId(AssetClass.INTERNATIONAL_LISTED_SECURITIES);
        asset1.setAssetCode("RIM0038AU");
        asset1.setAssetName("Russell Emerging Markets Fund");
        asset1.setCluster(AssetType.MANAGED_FUND);
        results.put(asset1.getAssetId(), asset1);

        asset1 = new AssetImpl();
        asset1.setAssetId("55");
        asset1.setAssetClassId(AssetClass.AUSTRALIAN_LISTED_SECURITIES);
        asset1.setAssetCode("IML0005AU");
        asset1.setAssetName("Investors Mutual Equity Income Fund");
        asset1.setCluster(AssetType.MANAGED_FUND);
        results.put(asset1.getAssetId(), asset1);

        asset1 = new AssetImpl();
        asset1.setAssetId("66");
        asset1.setAssetClassId(AssetClass.AUSTRALIAN_LISTED_SECURITIES);
        asset1.setAssetCode("HOW0121AU");
        asset1.setAssetName("Challenger Wholesale Socially Responsive Share Fund");
        asset1.setCluster(AssetType.MANAGED_FUND);
        results.put(asset1.getAssetId(), asset1);

        // MANAGED FUNDS - assettype - Count=4 - END

        return results;
    }

}
