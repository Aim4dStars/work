package com.bt.nextgen.api.corporateaction.v1.service.converter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountElectionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountElectionsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionBuyBackAccountElectionDtoImpl;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionBuyBackAccountElectionsDtoImpl;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionPriceOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSavedDetails;
import com.bt.nextgen.core.repository.CorporateActionSavedAccount;
import com.bt.nextgen.core.repository.CorporateActionSavedAccountElection;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDecisionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOption;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOptionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOptions;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectFirst;
import static org.hamcrest.core.IsEqual.equalTo;

public class BuyBackResponseConverterServiceBaseImpl extends AbstractCorporateActionResponseConverterServiceImpl {
    private static final CorporateActionOptionKey[] applicableOptionKeys = {CorporateActionOptionKey.FINAL_TENDER_PRICE,
                                                                            CorporateActionOptionKey.OPTION,
                                                                            CorporateActionOptionKey.OPTION_MINIMUM_PRICE,
                                                                            CorporateActionOptionKey.PRICE_AS_PERCENT,};

    @Override
    protected CorporateActionOptionKey[] getApplicableOptionFields() {
        return applicableOptionKeys;
    }

    @Override
    public CorporateActionAccountElectionsDto toSubmittedAccountElectionsDto(CorporateActionContext contexts,
                                                                             CorporateActionAccount account) {
        List<CorporateActionAccountElectionDto> electionDtos = new ArrayList<>();
        Integer minPriceId = null;

        if (account.getDecisions() != null) {
            CorporateActionOption finalTenderQuantityOption = selectFirst(account.getDecisions(), having(
                    on(CorporateActionOption.class).getKey(), equalTo(CorporateActionDecisionKey.FINAL_TENDER_QUANTITY.getCode())));

            if (finalTenderQuantityOption != null && finalTenderQuantityOption.hasValue()) {
                electionDtos.add(new CorporateActionBuyBackAccountElectionDtoImpl(
                        CorporateActionConverterConstants.OPTION_FINAL_TENDER_PRICE_ID, finalTenderQuantityOption.getBigDecimalValue()));
            }

            for (int idx = 1; idx <= getMaxOptions(); idx++) {
                final String qtyKey = CorporateActionDecisionKey.OPTION_QUANTITY.getCode(idx);

                for (CorporateActionOption option : account.getDecisions()) {
                    if (option.hasValue() && qtyKey.equals(option.getKey()) && BigDecimal.ZERO.compareTo(option.getBigDecimalValue()) < 0) {
                        electionDtos.add(new CorporateActionBuyBackAccountElectionDtoImpl(idx, option.getBigDecimalValue()));
                    }
                }
            }

            minPriceId = getMinimumPriceIndex(account.getDecisions());
        }

        return electionDtos.isEmpty() ? null : new CorporateActionBuyBackAccountElectionsDtoImpl(electionDtos, minPriceId);
    }

    @Override
    public CorporateActionAccountElectionsDto toSavedAccountElectionsDto(CorporateActionContext context, String accountId,
                                                                         CorporateActionSavedDetails savedDetails) {
        CorporateActionAccountElectionsDto electionsDto = null;

        if (savedDetails.getSavedParticipation() != null) {
            CorporateActionSavedAccount savedAccount = selectFirst(savedDetails.getSavedParticipation().getAccounts(),
                    having(on(CorporateActionSavedAccount.class).getKey().getAccountNumber(), equalTo(accountId)));

            if (savedAccount != null && !savedAccount.getAccountElections().isEmpty()) {
                List<CorporateActionAccountElectionDto> electionDtos = new ArrayList<>(savedAccount.getAccountElections().size());

                for (CorporateActionSavedAccountElection savedAccountElection : savedAccount.getAccountElections()) {
                    electionDtos.add(new CorporateActionBuyBackAccountElectionDtoImpl(savedAccountElection.getKey().getOptionId(),
                            savedAccountElection.getUnits()));
                }

                electionsDto = new CorporateActionBuyBackAccountElectionsDtoImpl(electionDtos, savedAccount.getMinimumPriceId());
            }
        }

        return electionsDto;
    }

    protected int getMinimumPriceIndex(List<CorporateActionOption> options) {
        CorporateActionOption minPriceOption = selectFirst(options,
                having(on(CorporateActionOption.class).getKey(),
                        equalTo(CorporateActionDecisionKey.MINIMUM_PRICE_DECISION.getCode())));

        if (minPriceOption != null && minPriceOption.hasValue()) {
            return minPriceOption.getBigDecimalValue().intValue();
        }

        return CorporateActionConverterConstants.NONE_MINIMUM_PRICE_ID;
    }

    public List<CorporateActionPriceOptionDto> toCorporateActionPriceOptionDtos(CorporateActionDetails details, int selectedIndex) {
        final Map<String, CorporateActionOption> optionsMap = toOptionsMap(details.getOptions());

        List<CorporateActionPriceOptionDto> priceOptionDtos = new ArrayList<>();
        priceOptionDtos.add(new CorporateActionPriceOptionDto(CorporateActionConverterConstants.NONE_MINIMUM_PRICE_ID, "None",
                selectedIndex == CorporateActionConverterConstants.NONE_MINIMUM_PRICE_ID));

        for (int idx = 1; idx <= getMaxOptions(); idx++) {
            CorporateActionOption minPriceOption = optionsMap.get(CorporateActionOptionKey.OPTION_MINIMUM_PRICE.getCode(idx));

            if (minPriceOption != null && minPriceOption.hasValue()) {
                int id = idx + 1;
                priceOptionDtos
                        .add(new CorporateActionPriceOptionDto(id, formatCurrency(minPriceOption.getBigDecimalValue()),
                                selectedIndex == id));
            }
        }

        return priceOptionDtos;
    }

    @Override
    public List<CorporateActionOptionDto> toElectionOptionDtos(CorporateActionContext context, ServiceErrors serviceErrors) {
        final List<CorporateActionOptionDto> options = new ArrayList<>();
        final Map<String, CorporateActionOption> optionsMap = toOptionsMap(context.getCorporateActionDetails().getOptions());
        final int selectedOptionIdx = getSelectedOptionIndex(context.getCorporateActionDetails().getDecisions());

        int titleIdx = 0;
        int idx = 1;

        CorporateActionOption finalTenderPriceOption = optionsMap.get(CorporateActionOptionKey.FINAL_TENDER_PRICE.getCode());

        if (finalTenderPriceOption != null &&
                CorporateActionConverterConstants.OPTION_VALUE_YES.equals(finalTenderPriceOption.getValue())) {
            options.add(new CorporateActionOptionDto(CorporateActionConverterConstants.OPTION_FINAL_TENDER_PRICE_ID,
                    getOptionFieldHeader(titleIdx), "As a Final Price Tender",
                    selectedOptionIdx == CorporateActionConverterConstants.OPTION_FINAL_TENDER_PRICE_ID));
            titleIdx++;
        }

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

        CorporateActionOptionDto lastOptionDto = generateLastCorporateActionOptionDto(idx, titleIdx, null,
                selectedOptionIdx == CorporateActionConverterConstants.OPTION_TAKE_NO_ACTION_ID, serviceErrors);

        options.add(lastOptionDto);

        return options;
    }

    /**
     * Generate the summary line for each corporate action option.  Deciding whether this logic should be in the front-end or not.
     *
     * @param referenceIndex   the fixed index, which will be used as the option ID
     * @param currentIndex     the current iteration index, which will be used to calculate alphabet letter
     * @param options          the corporate action options map
     * @param isSelectedOption set to true if this option is to be flagged as default
     * @param serviceErrors    the service errors object
     * @return populated CorporateActionOptionDto with ID, title and summary line.  Returns null if no translation available for the entry.
     */
    @Override
    protected CorporateActionOptionDto generateCorporateActionOptionDto(int referenceIndex, int currentIndex,
                                                                        CorporateActionOptions options,
                                                                        boolean isSelectedOption,
                                                                        ServiceErrors serviceErrors) {
        CorporateActionOptionDto dto = null;

        if (options.hasValue(CorporateActionOptionKey.OPTION)) {
            dto = new CorporateActionOptionDto(referenceIndex, getOptionFieldHeader(currentIndex),
                    "Tender at " + formatPercent(options.getBigDecimal(CorporateActionOptionKey.OPTION)) + " discount", isSelectedOption);
        }

        return dto;
    }

    @Override
    protected CorporateActionOptionDto generateLastCorporateActionOptionDto(int referenceIndex, int currentIndex,
                                                                            List<CorporateActionOptionDto> options,
                                                                            boolean isSelectedOption,
                                                                            ServiceErrors serviceErrors) {
        return new CorporateActionOptionDto(CorporateActionConverterConstants.OPTION_TAKE_NO_ACTION_ID,
                getOptionFieldHeader(currentIndex), "Take no action", Boolean.TRUE, isSelectedOption);
    }

    /**
     * Determine the index of default option, which is mapped to par_list.
     *
     * @param decisions list of Avaloq "default" options.
     * @return the index, starting from 1, where the default option is located.  Returns CorporateActionConverterConstants
     * .OPTION_TAKE_NO_ACTION_ID if not found.
     */
    @Override
    protected int getSelectedOptionIndex(List<CorporateActionOption> decisions) {
        if (decisions != null) {
            if (hasFinalTenderValue(decisions)) {
                return CorporateActionConverterConstants.OPTION_FINAL_TENDER_PRICE_ID;
            }

            for (int idx = 1; idx <= getMaxOptions(); idx++) {
                final String pctKey = CorporateActionDecisionKey.OPTION_PERCENT.getCode(idx);

                for (CorporateActionOption option : decisions) {
                    if (option.hasValue() && pctKey.equals(option.getKey()) &&
                            CorporateActionConverterConstants.DECIMAL_ONE_HUNDRED.compareTo(option.getBigDecimalValue()) == 0) {
                        return idx;
                    }
                }
            }
        }

        return CorporateActionConverterConstants.OPTION_TAKE_NO_ACTION_ID;
    }

    private boolean hasFinalTenderValue(List<CorporateActionOption> decisions) {
        CorporateActionOption finalTenderOption = selectFirst(decisions, having(on(CorporateActionOption.class).getKey(),
                equalTo(CorporateActionDecisionKey.FINAL_TENDER_PERCENT.getCode())));

        if (finalTenderOption != null && finalTenderOption.hasValue() &&
                CorporateActionConverterConstants.DECIMAL_ONE_HUNDRED.compareTo(finalTenderOption.getBigDecimalValue()) == 0) {
            return true;
        }

        finalTenderOption = selectFirst(decisions, having(on(CorporateActionOption.class).getKey(),
                equalTo(CorporateActionDecisionKey.FINAL_TENDER_QUANTITY.getCode())));

        if (finalTenderOption != null && finalTenderOption.hasValue() &&
                BigDecimal.ZERO.compareTo(finalTenderOption.getBigDecimalValue()) < 0) {
            return true;
        }

        return false;
    }

    @Override
    protected int getMaxOptions() {
        return CorporateActionConverterConstants.MAX_BUY_BACK_OPTIONS;
    }
}