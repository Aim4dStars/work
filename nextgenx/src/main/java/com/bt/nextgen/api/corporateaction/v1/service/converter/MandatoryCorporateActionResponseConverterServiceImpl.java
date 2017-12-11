package com.bt.nextgen.api.corporateaction.v1.service.converter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDtoParams;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountElectionsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDetailsDtoParams;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSavedDetails;
import com.bt.nextgen.api.corporateaction.v1.service.stereotype.CorporateActionResponseConverter;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.corporateaction.CorporateActionSecurityExchangeTypeConverter;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOption;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOptionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionSecurityExchangeType;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectFirst;
import static org.hamcrest.core.IsEqual.equalTo;


@CorporateActionResponseConverter("CA_MANDATORY_RESPONSE")
public class MandatoryCorporateActionResponseConverterServiceImpl implements CorporateActionResponseConverterService {
    private static final Pattern patternMergeFields = Pattern.compile("(\\{.*?\\})");

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetIntegrationService;

    @Autowired
    private CorporateActionSecurityExchangeTypeConverter corporateActionSecurityExchangeConverter;

    /**
     * Convert options to summary text by replacing bind variables with appropriate value.  See defined templates above.
     *
     * @param context       the corporate action context
     * @param serviceErrors the service errors
     * @return summary text string
     */
    @Override
    public List<String> toSummaryList(CorporateActionContext context, ServiceErrors serviceErrors) {
        final List<String> result = new ArrayList<>(1);

        if (context.getCorporateActionDetails().getOptions() != null && !context.getCorporateActionDetails().getOptions().isEmpty() &&
                context.getCorporateActionDetails().getCorporateActionType().getSummaryTemplate() != null) {
            final Map<String, String> mergeValues = toMergeValues(context.getCorporateActionDetails(), serviceErrors);

            String summaryTemplate = context.getCorporateActionDetails().getCorporateActionType().getSummaryTemplate().getTemplate();

            Matcher matcher = patternMergeFields.matcher(summaryTemplate);

            while (matcher.find()) {
                String bindKey = matcher.group(1);
                String key = bindKey.substring(1, matcher.group(1).lastIndexOf('}'));

                if (mergeValues.containsKey(key)) {
                    summaryTemplate = summaryTemplate.replace(bindKey, mergeValues.get(key));
                }
            }

            result.add(summaryTemplate);
        }

        return result;
    }

    @Override
    public CorporateActionDetailsDtoParams setCorporateActionDetailsDtoParams(CorporateActionContext context,
                                                                              CorporateActionDetailsDtoParams params) {

        CorporateActionOption option = selectFirst(context.getCorporateActionDetails().getOptions(),
                having(on(CorporateActionOption.class).getKey(), equalTo(CorporateActionOptionKey.SECURITY_EXCHANGE.getCode())));

        if (option != null) {
            CorporateActionSecurityExchangeType securityExchangeType = corporateActionSecurityExchangeConverter.convert(option.getValue());

            if (securityExchangeType != null) {
                params.setCorporateActionType(securityExchangeType.getCorporateActionType().getCode());
            }
        }

        return params;
    }

    @Override
    public CorporateActionAccountDetailsDtoParams setCorporateActionAccountDetailsDtoParams(CorporateActionContext context,
                                                                                            CorporateActionAccount account,
                                                                                            CorporateActionAccountDetailsDtoParams params) {
        return params;
    }

    private Map<String, String> toMergeValues(CorporateActionDetails details, ServiceErrors serviceErrors) {
        Map<String, String> map = new HashMap<>();

        for (CorporateActionOption option : details.getOptions()) {
            map.put(option.getKey(), option.getValue());
        }

        map.put("ca_type", details.getCorporateActionType().getDescription());

        addAssetNames(map, details, serviceErrors);
        addDollarAmounts(map);
        addSecurityExchangeType(map);

        return map;
    }

    private void addAssetNames(Map<String, String> map, CorporateActionDetails details, ServiceErrors serviceErrors) {
        final Asset asset =
                assetIntegrationService.loadAsset(details.getAssetId(), serviceErrors);
        map.put("asset_code", asset.getAssetCode());
        map.put("asset_name", asset.getAssetName());

        if (map.containsKey(CorporateActionOptionKey.ASSET_ID.getCode())) {
            final Asset newAsset =
                    assetIntegrationService.loadAsset(map.get(CorporateActionOptionKey.ASSET_ID.getCode()), serviceErrors);
            map.put("new_asset_code", newAsset.getAssetCode());
            map.put("new_asset_name", newAsset.getAssetName());
        }
    }

    private void addDollarAmounts(Map<String, String> map) {
        if (map.containsKey(CorporateActionOptionKey.OFFERED_PRICE.getCode())) {
            map.put("pay_amount", formatCurrency(map.get(CorporateActionOptionKey.OFFERED_PRICE.getCode())));
        }

        if (map.containsKey(CorporateActionOptionKey.PRICE.getCode())) {
            map.put("price_amount", formatCurrency(map.get(CorporateActionOptionKey.PRICE.getCode())));
        }

        if (map.containsKey(CorporateActionOptionKey.REVENUE_PER_PRICE.getCode())) {
            map.put("rpp_amount", formatCurrency(map.get(CorporateActionOptionKey.REVENUE_PER_PRICE.getCode())));
        }

        if (map.containsKey(CorporateActionOptionKey.PRICE_FACTOR.getCode())) {
            map.put("pf_amount", formatCurrency(map.get(CorporateActionOptionKey.PRICE_FACTOR.getCode())));
        }

        if (map.containsKey(CorporateActionOptionKey.REDEMPTION_PRICE.getCode())) {
            map.put("rdmt_amount", formatCurrency(map.get(CorporateActionOptionKey.REDEMPTION_PRICE.getCode())));
        }

        if (map.containsKey(CorporateActionOptionKey.FINAL_BUY_BACK_PRICE.getCode())) {
            map.put("final_buy_back_price", formatCurrency(map.get(CorporateActionOptionKey.FINAL_BUY_BACK_PRICE.getCode())));
        }
    }

    private void addSecurityExchangeType(Map<String, String> map) {
        if (map.containsKey(CorporateActionOptionKey.SECURITY_EXCHANGE.getCode())) {
            CorporateActionSecurityExchangeType exchangeType = corporateActionSecurityExchangeConverter.convert(
                    map.get(CorporateActionOptionKey.SECURITY_EXCHANGE.getCode()));
            if (exchangeType != null) {
                map.put("sec_type", exchangeType.getDescription());
            }
        }
    }

    protected String formatCurrency(String value) {
        final DecimalFormat df = (DecimalFormat) NumberFormat.getCurrencyInstance();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        df.setNegativePrefix("-" + df.getCurrency().getSymbol());
        df.setNegativeSuffix("");

        return df.format(new BigDecimal(value));
    }

    @Override
    public List<CorporateActionOptionDto> toElectionOptionDtos(CorporateActionContext context, ServiceErrors serviceErrors) {
        return new ArrayList<>();
    }

    @Override
    public CorporateActionAccountElectionsDto toSubmittedAccountElectionsDto(CorporateActionContext context,
                                                                             CorporateActionAccount account) {
        return null;
    }

    @Override
    public CorporateActionAccountElectionsDto toSavedAccountElectionsDto(CorporateActionContext context, String accountId,
                                                                         CorporateActionSavedDetails savedDetails) {
        return null;
    }
}
