package com.bt.nextgen.reports.account.fees.schedule;

import com.bt.nextgen.api.fees.model.FeesScheduleTrxnDto;
import com.bt.nextgen.api.fees.model.FeesTypeTrxnDto;
import com.bt.nextgen.api.fees.model.FlatPercentageFeeDto;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.reports.account.common.AccountReportV2;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.options.model.OptionKey;
import com.bt.nextgen.service.integration.options.service.OptionNames;
import com.bt.nextgen.service.integration.options.service.OptionsService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.json.JsonSanitizer;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Report("feeScheduleAuthorisationForm")
public class FeeScheduleAuthorisationForm extends AccountReportV2 {

    private static final String BRANDED_HEADER_TEXT = "DS-IP-0176";
    private static final String BRANDED_AUTHORISATION_TEXT = "DS-IP-0043";
    private static final String AUTHORISATION_TEXT = "DS-IP-0186";
    private static final String ACCOUNT_ID = "account-id";
    private static final String FEE_TYPE_FLAT_PERCENTAGE = "FlatPercentageFee";

    @Autowired
    @Qualifier("jsonObjectMapper")
    private ObjectMapper mapper;

    @Autowired
    private OptionsService optionsService;

    private FeesScheduleTrxnDto buildFeesFromRequest(Map<String, Object> params) throws IOException {
        FeesScheduleTrxnDto feeScheduleTransactionDto = mapper.readValue((String) params.get("feeScheduleTransactionDto"),
                FeesScheduleTrxnDto.class);
        feeScheduleTransactionDto.setContributionFees(getContributionFees(params));
        return feeScheduleTransactionDto;
    }

    @Override
    public List<Object> getData(Map<String, Object> params, Map<String, Object> dataCollections) {
        try {
            FeesScheduleTrxnDto dto = buildFeesFromRequest(params);
            List<Object> result = new ArrayList<>();
            result.add(new FeeScheduleAuthorisationData(dto));
            return result;
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to parse request", e);
        }
    }

    @Override
    public String getReportType(Map<String, Object> params, Map<String, Object> dataCollections) {
        return "Client authorisation - advice fees";
    }

    @ReportBean("authorisationText")
    public String getAuthorisationText(Map<String, Object> params) {
        Boolean brandedCashOption = getBrandedCashOption(params);
        if (brandedCashOption) {
            return getContent(BRANDED_AUTHORISATION_TEXT);
        } else {
            return getContent(AUTHORISATION_TEXT);
        }
    }

    @ReportBean("headerText")
    public String getHeaderDisclaimer(Map<String, Object> params) {
        return getContent(BRANDED_HEADER_TEXT);
    }

    public Boolean getBrandedCashOption(Map<String, Object> params) {
        String accountId = (String) params.get(ACCOUNT_ID);
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(accountId));
        Boolean brandedCashOption = optionsService.hasFeature(OptionKey.valueOf(OptionNames.CASH_BRANDED), accountKey,
                serviceErrors);
        return brandedCashOption;
    }

    public List<FeesTypeTrxnDto> getContributionFees(Map<String, Object> params) throws IOException {
        List<FeesTypeTrxnDto> contributionFees = new ArrayList<>();
        try {
            JsonNode nodes = mapper.readTree(JsonSanitizer.sanitize((String) params.get("feeScheduleTransactionDto")));
            JsonNode contributionFeeNode = nodes.path("contributionFees");
            if (contributionFeeNode.isArray()) {
                for (JsonNode node : contributionFeeNode) {
                    FeesTypeTrxnDto feesTypeTrxnDto = new FeesTypeTrxnDto();
                    String feeType = node.path("type").asText();
                    if (StringUtils.isNotBlank(feeType) && feeType.equals(FEE_TYPE_FLAT_PERCENTAGE)) {
                        FlatPercentageFeeDto flatPercentageFee = new FlatPercentageFeeDto();
                        flatPercentageFee.setRate(node.path("rate").decimalValue());
                        flatPercentageFee.setName(node.path("name").asText());
                        flatPercentageFee.setLabel(node.path("contributionType").asText());
                        feesTypeTrxnDto.setFlatPercentageFee(flatPercentageFee);
                        contributionFees.add(feesTypeTrxnDto);
                    }
                }
            }
        } catch (JsonProcessingException e) {
            throw new IOException("error writing json", e);
        }
        return contributionFees;
    }
}