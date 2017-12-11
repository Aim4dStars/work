package com.bt.nextgen.smsf.service;

import com.bt.nextgen.api.smsf.model.AssetHoldings;
import com.bt.nextgen.api.smsf.model.ExternalAssetClassValuationDto;
import com.bt.nextgen.api.smsf.model.ExternalAssetDto;
import com.bt.nextgen.api.smsf.model.ExternalAssetHoldingsValuationDto;
import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.externalasset.builder.ExternalAssetHoldingsConverter;
import com.bt.nextgen.service.integration.externalasset.service.ExternalAssetIntegrationService;
import org.joda.time.DateTime;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


/**
 * To test percentage rounding logic
 */
public class ViewExternalHoldingsDtoImplTest extends BaseSecureIntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(ViewExternalHoldingsDtoImplTest.class);

    @Autowired
    private ExternalAssetIntegrationService externalAssetIntegrationService;


    @Test
    public void percentageRoundingAssetTest()
    {
        AccountKey accountKey = AccountKey.valueOf("87877");
        List<AccountKey> accountKeys = new ArrayList<>();
        accountKeys.add(accountKey);

        AssetHoldings assetHoldings = externalAssetIntegrationService.getExternalAssets(accountKeys, new DateTime());
        ExternalAssetHoldingsValuationDto externalAssetHoldingsValDto = ExternalAssetHoldingsConverter.toExternalAssetHoldingsValuationDto(assetHoldings);

        BigDecimal totalPercent = BigDecimal.ZERO;
        for (ExternalAssetClassValuationDto assetClassValDto : externalAssetHoldingsValDto.getValuationByAssetClass()) {
            for (ExternalAssetDto externalAssetDto:assetClassValDto.getAssetList()) {
                totalPercent = totalPercent.add(new BigDecimal(externalAssetDto.getPercentageTotal()));
            }
        }
        assertEquals(totalPercent, new BigDecimal("1.0000"));
    }

}