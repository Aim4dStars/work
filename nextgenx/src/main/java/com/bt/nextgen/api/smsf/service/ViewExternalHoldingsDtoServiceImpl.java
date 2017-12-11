package com.bt.nextgen.api.smsf.service;


import com.bt.nextgen.api.smsf.model.AssetHoldings;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.externalasset.service.ExternalAssetIntegrationService;
import com.bt.nextgen.service.integration.externalasset.builder.ExternalAssetHoldingsConverter;
import com.bt.nextgen.api.smsf.model.ExternalAssetHoldingsValuationDto;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"findbugs:UCF_USELESS_CONTROL_FLOW"})
@Component
public class ViewExternalHoldingsDtoServiceImpl implements ViewExternalHoldingsDtoService
{
    @Autowired
    private ExternalAssetIntegrationService externalAssetIntegrationService;

    public List<ExternalAssetHoldingsValuationDto> search(List<ApiSearchCriteria> criteria, ServiceErrors serviceErrors)
    {
        String accountId = "";

        for (ApiSearchCriteria searchCriteria : criteria)
        {
            if (("account_id").equalsIgnoreCase(searchCriteria.getProperty()))
            {
                accountId = searchCriteria.getValue();
            }
        }

        if (StringUtils.isEmpty(accountId))
        {
            throw new IllegalArgumentException("Account Id was not supplied");
        }

        AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(accountId));
        List<AccountKey> accountKeys = new ArrayList<>();
        accountKeys.add(accountKey);

        AssetHoldings assetHoldings = externalAssetIntegrationService.getExternalAssets(accountKeys, new DateTime());
        ExternalAssetHoldingsValuationDto externalAssetHoldingsValDto = ExternalAssetHoldingsConverter.toExternalAssetHoldingsValuationDto(assetHoldings);
        List<ExternalAssetHoldingsValuationDto> result = new ArrayList<>();
        result.add(externalAssetHoldingsValDto);

        return result;
    }
}
