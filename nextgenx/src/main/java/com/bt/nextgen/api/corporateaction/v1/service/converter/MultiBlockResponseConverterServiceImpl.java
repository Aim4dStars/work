package com.bt.nextgen.api.corporateaction.v1.service.converter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountElectionsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDetailsDtoParams;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionMultiBlockAccountElectionsDtoImpl;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSavedDetails;
import com.bt.nextgen.api.corporateaction.v1.service.stereotype.CorporateActionResponseConverter;
import com.bt.nextgen.core.repository.CorporateActionSavedAccount;
import com.bt.nextgen.core.repository.CorporateActionSavedAccountElection;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDecisionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOption;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOptionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOptions;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectFirst;
import static org.hamcrest.core.IsEqual.equalTo;


@CorporateActionResponseConverter("CA_MULTI_BLOCK_RESPONSE")
public class MultiBlockResponseConverterServiceImpl extends AbstractCorporateActionResponseConverterServiceImpl {
    protected static final String OPTION_TEXT_NO_ACTION = "Do not participate";

    private static final CorporateActionOptionKey[] applicableOptionKeys = {CorporateActionOptionKey.TITLE,
                                                                            CorporateActionOptionKey.ASSET_ID,
                                                                            CorporateActionOptionKey.OFFERED_PRICE,
                                                                            CorporateActionOptionKey.OLD_STOCK_HELD,
                                                                            CorporateActionOptionKey.NEW_STOCK_ALLOCATED,};
    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetIntegrationService;

    /**
     * Generate the summary line for each corporate action option.  Deciding whether this logic should be in the front-end or not.
     *
     * @param referenceIndex   the fixed index, which will be used as the option ID
     * @param currentIndex     the current iteration index, which will be used to calculate alphabet letter
     * @param options          the corporate action option entry map
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
        OptionResult optionResult = options.hasValue(CorporateActionOptionKey.OFFERED_PRICE) ? getOfferedPriceOptionResult(options,
                serviceErrors) : getStockOptionResult(options, serviceErrors);

        if (optionResult != null) {
            dto = new CorporateActionOptionDto(referenceIndex, getOptionFieldHeader(currentIndex), optionResult.getOptionText(),
                    optionResult.isNoAction(), isSelectedOption);
        }

        return dto;
    }

    private OptionResult getOfferedPriceOptionResult(CorporateActionOptions options, ServiceErrors serviceErrors) {
        OptionResult optionResult = null;

        if (options.hasValue(CorporateActionOptionKey.ASSET_ID)) {
            if (options.hasValue(CorporateActionOptionKey.OLD_STOCK_HELD) &&
                    options.hasValue(CorporateActionOptionKey.NEW_STOCK_ALLOCATED)) {
                // IPMR_AC_001123 e.g "$0.40 per share & 1:2 RIOX shares"
                final Asset asset = assetIntegrationService.loadAsset(options.getString(CorporateActionOptionKey.ASSET_ID), serviceErrors);
                optionResult =
                        new OptionResult(formatCurrency(options.getBigDecimal(CorporateActionOptionKey.OFFERED_PRICE)) + " per share & " +
                                options.getString(CorporateActionOptionKey.OLD_STOCK_HELD) + ":" +
                                options.getString(CorporateActionOptionKey.NEW_STOCK_ALLOCATED) + " " + asset.getAssetCode() +
                                " shares", false);
            }
        } else {
            // IPMR2_AC_001121 e.g "$0.40 per share"
            optionResult =
                    new OptionResult(formatCurrency(options.getBigDecimal(CorporateActionOptionKey.OFFERED_PRICE)) + " per share", false);
        }

        return optionResult;
    }

    private OptionResult getStockOptionResult(CorporateActionOptions options, ServiceErrors serviceErrors) {
        OptionResult optionResult = null;

        if (options.hasValue(CorporateActionOptionKey.ASSET_ID)) {
            if (options.hasValue(CorporateActionOptionKey.OLD_STOCK_HELD) &&
                    options.hasValue(CorporateActionOptionKey.NEW_STOCK_ALLOCATED)) {
                // IPMR2_AC_001122 e.g "1:2 RIOX shares"
                final Asset asset = assetIntegrationService.loadAsset(options.getString(CorporateActionOptionKey.ASSET_ID), serviceErrors);
                optionResult = new OptionResult(options.getString(CorporateActionOptionKey.OLD_STOCK_HELD) + ":" +
                        options.getString(CorporateActionOptionKey.NEW_STOCK_ALLOCATED) + " " + asset.getAssetCode() + " shares", false);
            }
        } else if (!options.hasValue(CorporateActionOptionKey.OLD_STOCK_HELD) &&
                !options.hasValue(CorporateActionOptionKey.NEW_STOCK_ALLOCATED)) {
            // TODO: use swift message to determine which is "Do not participate" - not yet available in avaloq
            optionResult = new OptionResult(OPTION_TEXT_NO_ACTION, true);
        }

        return optionResult;
    }

    @Override
    protected CorporateActionOptionKey[] getApplicableOptionFields() {
        return applicableOptionKeys;
    }

    @Override
    public CorporateActionAccountElectionsDto toSubmittedAccountElectionsDto(CorporateActionContext context,
                                                                             CorporateActionAccount account) {
        boolean takeUpByUnits = isTakeUpByUnits(context);

        CorporateActionAccountElectionsDto electionsDto = null;

        int idx = getSelectedOptionIndex(account.getDecisions());

        if (idx > -1) {
            Map<String, CorporateActionOption> optionsMap = toOptionsMap(account.getDecisions());
            CorporateActionOption percentOption = optionsMap.get(CorporateActionDecisionKey.PERCENT.getCode(idx));
            CorporateActionOption quantityOption = optionsMap.get(CorporateActionDecisionKey.QUANTITY.getCode(idx));

            if (takeUpByUnits) {
                // This could happen when CA is changed to take over by units
                if (quantityOption != null) {
                    electionsDto = CorporateActionMultiBlockAccountElectionsDtoImpl
                            .createSingleAccountElection(idx, quantityOption.getBigDecimalValue(), null);
                } else {
                    // Use submitted percentage to calculate the units.  Percent exists if we get to this point
                    BigDecimal quantity = percentOption.getBigDecimalValue().multiply(BigDecimal.valueOf(0.01))
                                                       .multiply(account.getAvailableQuantity());

                    electionsDto = CorporateActionMultiBlockAccountElectionsDtoImpl
                            .createSingleAccountElection(idx, quantity.setScale(0, BigDecimal.ROUND_FLOOR), null);
                }
            } else {
                if (percentOption != null) {
                    electionsDto = CorporateActionMultiBlockAccountElectionsDtoImpl
                            .createSingleAccountElection(idx, null, percentOption.getBigDecimalValue());
                } else {
                    // Use submitted units to calculate the percentage.  Units exists if we get to this point
                    BigDecimal percent =
                            quantityOption.getBigDecimalValue().divide(account.getAvailableQuantity(), 4, BigDecimal.ROUND_HALF_DOWN)
                                          .multiply(BigDecimal.valueOf(100.0)).setScale(0, BigDecimal.ROUND_FLOOR);

                    electionsDto = CorporateActionMultiBlockAccountElectionsDtoImpl.createSingleAccountElection(idx, null, percent);
                }
            }
        }

        return electionsDto;
    }

    @Override
    public CorporateActionAccountElectionsDto toSavedAccountElectionsDto(CorporateActionContext context, String accountId,
                                                                         CorporateActionSavedDetails savedDetails) {
        CorporateActionAccountElectionsDto electionsDto = null;

        if (savedDetails.getSavedParticipation() != null) {
            CorporateActionSavedAccount savedAccount = selectFirst(savedDetails.getSavedParticipation().getAccounts(),
                    having(on(CorporateActionSavedAccount.class).getKey().getAccountNumber(), equalTo(accountId)));

            if (savedAccount != null && !savedAccount.getAccountElections().isEmpty()) {
                // Only one will ever exist for this CA
                CorporateActionSavedAccountElection savedAccountElection = savedAccount.getAccountElections().iterator().next();
                electionsDto = CorporateActionMultiBlockAccountElectionsDtoImpl
                        .createSingleAccountElection(savedAccountElection.getKey().getOptionId(), savedAccountElection.getUnits(),
                                savedAccountElection.getPercent());
            }
        }

        return electionsDto;
    }

    @Override
    public CorporateActionDetailsDtoParams setCorporateActionDetailsDtoParams(CorporateActionContext context,
                                                                              CorporateActionDetailsDtoParams params) {
        CorporateActionOptionDto defaultOption = selectFirst(params.getOptions(),
                having(on(CorporateActionOptionDto.class).getIsDefault(), equalTo(Boolean.TRUE)));

        if (defaultOption == null) {
            params.setErrorMessage("No default option set in Avaloq");
        }

        return params;
    }

    /**
     * Determine the index of default option, which is mapped to par_list.
     *
     * @param decisions list of Avaloq "default" options.
     * @return the index, starting from 1, where the default option is located.  Returns -1 if not found.
     */
    @Override
    protected int getSelectedOptionIndex(List<CorporateActionOption> decisions) {
        if (decisions != null) {
            for (int idx = 1; idx <= getMaxOptions(); idx++) {
                final String pctKey = CorporateActionDecisionKey.PERCENT.getCode(idx);
                final String qtyKey = CorporateActionDecisionKey.QUANTITY.getCode(idx);

                for (CorporateActionOption option : decisions) {
                    if (option.hasValue() && (pctKey.equals(option.getKey()) || qtyKey.equals(option.getKey())) && BigDecimal.ZERO
                            .compareTo(option.getBigDecimalValue()) < 0) {
                        return idx;
                    }
                }
            }
        }

        return -1;
    }

    protected boolean isTakeUpByUnits(CorporateActionContext context) {
        return false;
    }

    private class OptionResult {
        private String optionText;
        private boolean noAction;

        public OptionResult(String optionText, boolean noAction) {
            this.optionText = optionText;
            this.noAction = noAction;
        }

        public String getOptionText() {
            return optionText;
        }

        public boolean isNoAction() {
            return noAction;
        }
    }
}
