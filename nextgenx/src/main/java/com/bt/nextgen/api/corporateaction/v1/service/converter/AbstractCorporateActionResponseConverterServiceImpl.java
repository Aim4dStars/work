package com.bt.nextgen.api.corporateaction.v1.service.converter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDtoParams;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDetailsDtoParams;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionOptionDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDecisionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOption;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOptionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOptions;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.index;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectFirst;
import static org.hamcrest.core.IsEqual.equalTo;

public abstract class AbstractCorporateActionResponseConverterServiceImpl implements CorporateActionResponseConverterService {
    protected abstract int getSelectedOptionIndex(List<CorporateActionOption> decisions);

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CorporateActionOptionDto> toElectionOptionDtos(CorporateActionContext context, ServiceErrors serviceErrors) {
        final List<CorporateActionOptionDto> options = new ArrayList<>();
        final Map<String, CorporateActionOption> optionsMap = toOptionsMap(context.getCorporateActionDetails().getOptions());
        final int selectedOptionIdx = getSelectedOptionIndex(context.getCorporateActionDetails().getDecisions());

        int titleIdx = 0;
        int idx = 1;

        for (; idx <= getMaxOptions(); idx++) {
            final CorporateActionOptions applicableOptions = getOptionsByIndex(idx, optionsMap, getApplicableOptionFields());

            if (!applicableOptions.isEmpty()) {
                CorporateActionOptionDto optionDto =
                        generateCorporateActionOptionDto(idx, titleIdx, applicableOptions, idx == selectedOptionIdx, serviceErrors);

                if (optionDto != null) {
                    options.add(optionDto);
                    titleIdx++;
                }
            }
        }

        CorporateActionOptionDto lastOptionDto =
                generateLastCorporateActionOptionDto(idx, titleIdx, options, idx == selectedOptionIdx || selectedOptionIdx == -1 ||
                        selectedOptionIdx == CorporateActionConverterConstants.OPTION_TAKE_NO_ACTION_ID, serviceErrors);

        if (lastOptionDto != null) {
            options.add(lastOptionDto);
        }


        return postProcessElectionOptionDtos(options);
    }

    /**
     * Convert corporate action options to DTO representation
     *
     * @param referenceIndex   the reference index
     * @param currentIndex     the actual index
     * @param options          the options
     * @param isSelectedOption selected option flag
     * @param serviceErrors    service errors
     * @return null CorporateActionOptionDto as default implementation
     */
    protected CorporateActionOptionDto generateCorporateActionOptionDto(int referenceIndex, int currentIndex,
                                                                        CorporateActionOptions options,
                                                                        boolean isSelectedOption,
                                                                        ServiceErrors serviceErrors) {
        return null;
    }

    /**
     * Generate the last corporate action option dto
     *
     * @param referenceIndex   the reference index
     * @param currentIndex     the actual index
     * @param options          the options
     * @param isSelectedOption selected option flag
     * @param serviceErrors    service errors
     * @return null CorporateActionOptionDto as default implementation
     */
    protected CorporateActionOptionDto generateLastCorporateActionOptionDto(int referenceIndex, int currentIndex,
                                                                            List<CorporateActionOptionDto> options,
                                                                            boolean isSelectedOption,
                                                                            ServiceErrors serviceErrors) {
        return null;
    }

    /**
     * Set default option if there is no default option set
     *
     * @param options list of options created by child classes
     * @return processed options if there is no default option set, else original list
     */
    private List<CorporateActionOptionDto> postProcessElectionOptionDtos(List<CorporateActionOptionDto> options) {
        CorporateActionOptionDto defaultOption =
                selectFirst(options, having(on(CorporateActionOptionDto.class).getIsDefault(), equalTo(Boolean.TRUE)));

        if (defaultOption == null) {
            List<CorporateActionOptionDto> newOptions = new ArrayList<>(options.size());

            for (CorporateActionOptionDto option : options) {
                newOptions.add(option.getIsNoAction() ?
                               new CorporateActionOptionDto(option.getId(), option.getTitle(), option.getSummary(), option.getIsNoAction(),
                                       true) :
                               option);
            }

            return newOptions;
        }

        return options;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> toSummaryList(CorporateActionContext context, ServiceErrors serviceErrors) {
        List<String> summaryList = new ArrayList<>();

        if (context.getCorporateActionDetails().getSummary() != null && !context.getCorporateActionDetails().getSummary().isEmpty()) {
            for (String summaryLine : context.getCorporateActionDetails().getSummary().split("\\|")) {
                summaryList.add(summaryLine);
            }
        }

        return summaryList;
    }

    @Override
    public CorporateActionDetailsDtoParams setCorporateActionDetailsDtoParams(CorporateActionContext context,
                                                                              CorporateActionDetailsDtoParams params) {
        return params;
    }

    @Override
    public CorporateActionAccountDetailsDtoParams setCorporateActionAccountDetailsDtoParams(CorporateActionContext context,
                                                                                            CorporateActionAccount account,
                                                                                            CorporateActionAccountDetailsDtoParams params) {
        return params;
    }

    protected String formatCurrency(BigDecimal value) {
        final DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance();
        df.setMaximumFractionDigits(8);
        //df.setMinimumFractionDigits(2);
        df.setMinimumIntegerDigits(1);
        df.setPositivePrefix(NumberFormat.getCurrencyInstance().getCurrency().getSymbol());
        df.setNegativePrefix("-" + NumberFormat.getCurrencyInstance().getCurrency().getSymbol());
        df.setNegativeSuffix("");

        return df.format(value);
    }

    protected String formatPercent(BigDecimal value) {
        final DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance();
        df.setPositiveSuffix("%");
        df.setNegativeSuffix("%");

        return df.format(value);
    }

    protected Map<String, CorporateActionOption> toOptionsMap(List<CorporateActionOption> options) {
        return index(options, on(CorporateActionOption.class).getKey());
    }

    protected CorporateActionOptions getOptionsByIndex(int idx, Map<String, CorporateActionOption> options,
                                                       CorporateActionOptionKey... keys) {

        Map<CorporateActionOptionKey, String> result = new HashMap<>();

        for (CorporateActionOptionKey key : keys) {
            CorporateActionOption option = options.get(key.getCode(idx));

            if (option != null) {
                result.put(key, option.getValue());
            }
        }

        return new CorporateActionOptions(result);
    }

    protected BigDecimal getBigDecimalOptionValue(CorporateActionDecisionKey key, Map<String, CorporateActionOption> optionsMap) {
        CorporateActionOption option = optionsMap.get(key.getCode());

        return option != null ? option.getBigDecimalValue() : null;
    }

    protected String getOptionFieldHeader(int charIdx) {
        return "Option " + Character.toString((char) (charIdx + 65));
    }

    protected CorporateActionOptionKey[] getApplicableOptionFields() {
        return new CorporateActionOptionKey[0];
    }

    /**
     * Override-able method to return the number of options available on Avaloq.  Each CA could have a different number.
     *
     * @return a whole number
     */
    protected int getMaxOptions() {
        return CorporateActionConverterConstants.MAX_OPTIONS;
    }
}
