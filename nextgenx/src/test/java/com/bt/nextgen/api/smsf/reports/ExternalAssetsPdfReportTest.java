package com.bt.nextgen.api.smsf.reports;

/**
 * Created by L067218 on 29/03/2017.
 */

import com.bt.nextgen.api.smsf.constants.AssetClass;
import com.bt.nextgen.api.smsf.constants.AssetType;
import com.bt.nextgen.api.smsf.model.*;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.PositionIdentifierImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.externalasset.model.ExternalAsset;
import com.bt.nextgen.service.integration.externalasset.model.OnPlatformExternalAssetImpl;
import com.bt.nextgen.service.integration.externalasset.service.ExternalAssetIntegrationService;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class ExternalAssetsPdfReportTest {
    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    @InjectMocks
    private ExternalAssetsPdfReport pdfReport;

    @Mock
    private ExternalAssetIntegrationService externalAssetIntegrationService;

    @Mock
    private ContentDtoService contentService;

    private Map<String, String> params;


    @Before
    public void init() {
        params = new HashMap<>();
    }

    @Test
    public void getStartDate() {
        assertThat("startDate", pdfReport.getStartDate(params), notNullValue());
    }


    @Test
    public void getReportName() {
        assertThat("report name", pdfReport.getReportName(params), equalTo("External assets valuation"));
    }

    @Test
    public void getDisclaimer() {
        getDisclaimer(true, "");
        getDisclaimer(false, "");
        getDisclaimer(false, "my disclaimer text");
    }

    @Test
    public void getExternalAssetsHoldingValuation() {
        final AccountKey accountKey = AccountKey.valueOf("87877");
        final List<AccountKey> accountKeys = new ArrayList<>();
        final Map<String, ExternalAssetHoldingsValuationDto> holdingValuations;
        ExternalAssetHoldingsValuationDto holdingValuation;
        ExternalAssetClassValuationDto assetClassValuation;
        ExternalAssetDto asset;

        accountKeys.add(accountKey);
        params.put("account-id", EncodedString.fromPlainText(accountKey.getId()).toString());

        when(externalAssetIntegrationService.getExternalAssets(anyListOf(AccountKey.class), any(DateTime.class))).thenReturn(getAssetHoldings());

        holdingValuations = pdfReport.getExternalAssetsHoldingValuation(params);
        verify(externalAssetIntegrationService).getExternalAssets(anyListOf(AccountKey.class), any(DateTime.class));

        assertThat(holdingValuations, notNullValue());
        assertThat("valuations size", holdingValuations.size(), equalTo(3));


        // TD
        holdingValuation = holdingValuations.get("cashtd");
        assertThat("Holding - cashtd exists", holdingValuation, notNullValue());
        assertThat("Holding - percentageOfPortfolio", holdingValuation.getPercentageOfPortfolio().setScale(SCALE, ROUNDING_MODE),
                equalTo((new BigDecimal("1")).setScale(SCALE, ROUNDING_MODE)));
        assertThat("Holding - totalMarketValue", holdingValuation.getTotalMarketValue().setScale(SCALE, ROUNDING_MODE),
                equalTo((new BigDecimal("64000")).setScale(SCALE, ROUNDING_MODE)));
        assertThat("Holding - assetClasses", holdingValuation.getValuationByAssetClass().size(), equalTo(1));

        assetClassValuation = holdingValuation.getValuationByAssetClass().get(0);
        assertThat("assetClass", assetClassValuation.getAssetClass(), equalTo(AssetClass.CASH.getDescription()));
        assertThat("AssetClass - percentageOfPortfolio", assetClassValuation.getPercentageOfPortfolio().setScale(SCALE, ROUNDING_MODE),
                equalTo(new BigDecimal("25").setScale(SCALE, ROUNDING_MODE)));
        assertThat("AssetClass - totalMarketValue", assetClassValuation.getTotalMarketValue().setScale(SCALE, ROUNDING_MODE),
                equalTo((new BigDecimal("16000")).setScale(SCALE, ROUNDING_MODE)));
        assertThat("AssetClass - assets", assetClassValuation.getAssetList().size(), equalTo(1));

        asset = assetClassValuation.getAssetList().get(0);
        assertThat("Asset - assetClass", asset.getAssetClass(), equalTo(AssetClass.CASH.getCode()));
        assertThat("Asset - assetType", asset.getAssetClass(), equalTo(AssetType.CASH.getCode()));
        assertThat("Asset - positionId", asset.getPositionId(), equalTo("9876"));
        assertThat("Asset - positionCode", asset.getPositionCode(), equalTo("TD123"));


        ////// OTHER CASH
        holdingValuation = holdingValuations.get("othercash");
        assertThat("Holding - othercash exists", holdingValuation, notNullValue());
        assertThat("Holding - percentageOfPortfolio", holdingValuation.getPercentageOfPortfolio().setScale(SCALE, ROUNDING_MODE),
                equalTo((new BigDecimal("1")).setScale(SCALE, ROUNDING_MODE)));
        assertThat("Holding - totalMarketValue", holdingValuation.getTotalMarketValue().setScale(SCALE, ROUNDING_MODE),
                equalTo((new BigDecimal("64000")).setScale(SCALE, ROUNDING_MODE)));
        assertThat("Holding - assetClasses", holdingValuation.getValuationByAssetClass().size(), equalTo(1));

        assetClassValuation = holdingValuation.getValuationByAssetClass().get(0);
        assertThat("assetClass", assetClassValuation.getAssetClass(), equalTo(AssetClass.CASH.getDescription()));
        assertThat("AssetClass - percentageOfPortfolio", assetClassValuation.getPercentageOfPortfolio().setScale(SCALE, ROUNDING_MODE),
                equalTo(new BigDecimal("25").setScale(SCALE, ROUNDING_MODE)));
        assertThat("AssetClass - totalMarketValue", assetClassValuation.getTotalMarketValue().setScale(SCALE, ROUNDING_MODE),
                equalTo((new BigDecimal("16000")).setScale(SCALE, ROUNDING_MODE)));
        assertThat("AssetClass - assets", assetClassValuation.getAssetList().size(), equalTo(0));


        ////// ALL
        holdingValuation = holdingValuations.get("all");
        assertThat("Holding - all exists", holdingValuation, notNullValue());
        assertThat("Holding - percentageOfPortfolio", holdingValuation.getPercentageOfPortfolio().setScale(SCALE, ROUNDING_MODE),
                equalTo((new BigDecimal("1")).setScale(SCALE, ROUNDING_MODE)));
        assertThat("Holding - totalMarketValue", holdingValuation.getTotalMarketValue().setScale(SCALE, ROUNDING_MODE),
                equalTo((new BigDecimal("64000")).setScale(SCALE, ROUNDING_MODE)));
        assertThat("Holding - assetClasses", holdingValuation.getValuationByAssetClass().size(), equalTo(2));

        assetClassValuation = holdingValuation.getValuationByAssetClass().get(0);
        assertThat("assetClass", assetClassValuation.getAssetClass(), equalTo(AssetClass.CASH.getDescription()));
        assertThat("AssetClass - percentageOfPortfolio", assetClassValuation.getPercentageOfPortfolio().setScale(SCALE, ROUNDING_MODE),
                equalTo(new BigDecimal("25").setScale(SCALE, ROUNDING_MODE)));
        assertThat("AssetClass - totalMarketValue", assetClassValuation.getTotalMarketValue().setScale(SCALE, ROUNDING_MODE),
                equalTo((new BigDecimal("16000")).setScale(SCALE, ROUNDING_MODE)));
        assertThat("AssetClass - assets", assetClassValuation.getAssetList().size(), equalTo(0));

        assetClassValuation = holdingValuation.getValuationByAssetClass().get(1);
        assertThat("assetClass", assetClassValuation.getAssetClass(),
                equalTo(capitalize(lowerCase(AssetClass.AUSTRALIAN_LISTED_SECURITIES.getDescription()))));
        assertThat("AssetClass - percentageOfPortfolio", assetClassValuation.getPercentageOfPortfolio().setScale(SCALE, ROUNDING_MODE),
                equalTo(new BigDecimal("75").setScale(SCALE, ROUNDING_MODE)));
        assertThat("AssetClass - totalMarketValue", assetClassValuation.getTotalMarketValue().setScale(SCALE, ROUNDING_MODE),
                equalTo((new BigDecimal("48000")).setScale(SCALE, ROUNDING_MODE)));
        assertThat("AssetClass - assets", assetClassValuation.getAssetList().size(), equalTo(1));

        asset = assetClassValuation.getAssetList().get(0);
        assertThat("Asset - assetClass", asset.getAssetClass(), equalTo(AssetClass.CASH.getCode()));
        assertThat("Asset - assetType", asset.getAssetClass(), equalTo(AssetType.CASH.getCode()));
        assertThat("Asset - positionId", asset.getPositionId(), equalTo("12345"));
        assertThat("Asset - positionCode", asset.getPositionCode(), equalTo("BHP"));
        assertThat("Asset - quantity", asset.getQuantity(), equalTo("150"));
    }

    private void getDisclaimer(boolean nullContent, String disclaimerText) {
        final ContentDto contentDto = nullContent ? null : new ContentDto("DS-IP-0062", disclaimerText);

        reset(contentService);
        when(contentService.find(any(ContentKey.class), any(ServiceErrors.class))).thenReturn(contentDto);

        assertThat("disclaimerText: nullContent = " + nullContent + "disclaimerText = " + disclaimerText,
                pdfReport.getDisclaimer(null), equalTo(disclaimerText));
        verify(contentService).find(any(ContentKey.class), any(ServiceErrors.class));
    }

    private AssetHoldings getAssetHoldings() {
        ExternalAsset listedSecurityAsset = new OnPlatformExternalAssetImpl();
        listedSecurityAsset.setAssetName("BHP");
        listedSecurityAsset.setPositionCode("BHP");
        listedSecurityAsset.setAssetClass(AssetClass.AUSTRALIAN_LISTED_SECURITIES);
        listedSecurityAsset.setSource("ETRADE");
        listedSecurityAsset.setMarketValue(new BigDecimal(48000));
        listedSecurityAsset.setValueDate(new DateTime());
        listedSecurityAsset.setPositionIdentifier(new PositionIdentifierImpl("12345"));
        listedSecurityAsset.setQuantity(new BigDecimal(150));
        listedSecurityAsset.setAssetType(AssetType.AUSTRALIAN_LISTED_SECURITIES);
        listedSecurityAsset.setAssetClass(AssetClass.CASH);

        ExternalAsset cashAsset = new OnPlatformExternalAssetImpl();
        cashAsset.setSource("ETRADE");
        cashAsset.setPositionCode("TD123");
        cashAsset.setMarketValue(new BigDecimal(16000));
        cashAsset.setValueDate(new DateTime());
        cashAsset.setPositionIdentifier(new PositionIdentifierImpl("9876"));
        cashAsset.setAssetType(AssetType.CASH);

        AssetClassValuation valuation1 = new AssetClassValuationImpl(AssetClass.AUSTRALIAN_LISTED_SECURITIES);
        AssetClassValuation valuation2 = new AssetClassValuationImpl(AssetClass.CASH);

        valuation1.setAssets(Arrays.asList(listedSecurityAsset));
        valuation2.setAssets(Arrays.asList(cashAsset));

        AssetHoldings holdings = new AssetHoldingsImpl();
        holdings.setAssetClassValuations(Arrays.asList(valuation1, valuation2));

        return holdings;
    }

}
