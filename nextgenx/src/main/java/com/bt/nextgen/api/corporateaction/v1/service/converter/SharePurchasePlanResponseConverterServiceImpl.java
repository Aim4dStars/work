package com.bt.nextgen.api.corporateaction.v1.service.converter;

import java.util.List;

import com.btfin.panorama.core.conversion.CodeCategory;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.core.IsEqual;
import org.springframework.beans.factory.annotation.Autowired;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDtoParams;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountElectionsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountElectionsDtoImpl;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDetailsDtoParams;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSavedDetails;
import com.bt.nextgen.api.corporateaction.v1.service.stereotype.CorporateActionResponseConverter;
import com.bt.nextgen.core.repository.CorporateActionSavedAccount;
import com.bt.nextgen.core.repository.CorporateActionSavedAccountElection;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccountParticipationStatus;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDecisionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOption;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOptionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOptions;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectFirst;
import static org.hamcrest.core.IsEqual.equalTo;


@CorporateActionResponseConverter("CA_SHARE_PURCHASE_PLAN_RESPONSE")
public class SharePurchasePlanResponseConverterServiceImpl extends AbstractCorporateActionResponseConverterServiceImpl {

    private static final CorporateActionOptionKey[] applicableOptionKeys = {CorporateActionOptionKey.TITLE,
                                                                            CorporateActionOptionKey.ASSET_ID,
                                                                            CorporateActionOptionKey.SUBSCRIPTION_AMOUNT,
                                                                            CorporateActionOptionKey.SUBSCRIPTION_QUANTITY,};

    @Autowired
    private StaticIntegrationService staticCodeService;

    @Override
    protected CorporateActionOptionKey[] getApplicableOptionFields() {
        return applicableOptionKeys;
    }

    @Override
    public CorporateActionAccountElectionsDto toSubmittedAccountElectionsDto(CorporateActionContext context,
                                                                             CorporateActionAccount account) {
        return CorporateActionAccountElectionsDtoImpl.createSingleAccountElection(getSelectedOptionIndex(account.getDecisions()));
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
                electionsDto =
                        CorporateActionAccountElectionsDtoImpl.createSingleAccountElection(savedAccountElection.getKey().getOptionId());
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

    @Override
    public CorporateActionAccountDetailsDtoParams setCorporateActionAccountDetailsDtoParams(CorporateActionContext context,
                                                                                            CorporateActionAccount account,
                                                                                            CorporateActionAccountDetailsDtoParams params) {
        // Modify available balance to the balance prior to election so not enough cash warning will not be shown
        if (CorporateActionAccountParticipationStatus.SUBMITTED.equals(account.getElectionStatus())) {
            final int selectedOptionIdx = getSelectedOptionIndex(account.getDecisions());

            final CorporateActionOption priceOption = selectFirst(context.getCorporateActionDetails().getOptions(),
                    having(on(CorporateActionOption.class).getKey(),
                            equalTo(CorporateActionOptionKey.SUBSCRIPTION_AMOUNT.getCode(selectedOptionIdx))));

            if (priceOption != null) {
                params.setCash(params.getCash().add(priceOption.getBigDecimalValue()));
            }
        }

        return params;
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

        if (options.hasValue(CorporateActionOptionKey.SUBSCRIPTION_AMOUNT)) {
            // IPMR2_AC_003269 Apply for $2500.00
            dto = new CorporateActionOptionDto(referenceIndex, getOptionFieldHeader(currentIndex),
                    "Apply for " + formatCurrency(options.getBigDecimal(CorporateActionOptionKey.SUBSCRIPTION_AMOUNT)), isSelectedOption);
        }

        return dto;
    }

    @Override
    protected CorporateActionOptionDto generateLastCorporateActionOptionDto(int referenceIndex, int currentIndex,
                                                                            List<CorporateActionOptionDto> options,
                                                                            boolean isSelectedOption,
                                                                            ServiceErrors serviceErrors) {
        return new CorporateActionOptionDto(CorporateActionConverterConstants.OPTION_TAKE_NO_ACTION_ID, getOptionFieldHeader(currentIndex),
                "Take no action", Boolean.TRUE, isSelectedOption);
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
            CorporateActionOption option = selectFirst(decisions,
                    having(on(CorporateActionOption.class).getKey(),
                            IsEqual.equalTo(CorporateActionDecisionKey.SUBSCRIBED_OPTION.getCode())));

            if (option != null && StringUtils.isNotEmpty(option.getValue())) {
                return toOptionIndex(option.getValue());
            }
        }

        return CorporateActionConverterConstants.OPTION_TAKE_NO_ACTION_ID;
    }

    private int toOptionIndex(String value) {
        final Code code = staticCodeService.loadCode(CodeCategory.CA_SHARE_PURCHASE_PLAN_OPTION, value, null);

        if (code != null && code.getUserId() != null) {
            int idx = code.getUserId().indexOf(CorporateActionConverterConstants.OPTION_PREFIX);

            // This is not really an ideal solution.
            // Ideally we use the "val" column but this is not available in static code list
            if (idx > -1) {
                return Integer.parseInt(code.getUserId().substring(CorporateActionConverterConstants.OPTION_PREFIX.length() + idx));
            }
        }

        return CorporateActionConverterConstants.OPTION_TAKE_NO_ACTION_ID;
    }

    @Override
    protected int getMaxOptions() {
        return CorporateActionConverterConstants.MAX_SHARE_PURCHASE_PLAN_OPTIONS;
    }
}