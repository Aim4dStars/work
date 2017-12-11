package com.bt.nextgen.api.corporateaction.v1.service.converter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.core.IsEqual;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountElectionsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDetailsDtoParams;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionMinMax;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionNonProRataPriorityOfferAccountElectionsDtoImpl;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSavedDetails;
import com.bt.nextgen.api.corporateaction.v1.service.stereotype.CorporateActionResponseConverter;
import com.bt.nextgen.core.repository.CorporateActionSavedAccount;
import com.bt.nextgen.core.repository.CorporateActionSavedAccountElection;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDecisionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionNonProRataPriorityOfferType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOption;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOptionKey;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectFirst;
import static org.hamcrest.core.IsEqual.equalTo;

@CorporateActionResponseConverter("CA_NON_PRO_RATA_PRIORITY_OFFER_RESPONSE")
public class NonProRataPriorityOfferResponseConverterServiceImpl extends AbstractCorporateActionResponseConverterServiceImpl {
    @Override
    public CorporateActionAccountElectionsDto toSubmittedAccountElectionsDto(CorporateActionContext context,
                                                                             CorporateActionAccount account) {
        int idx = getSelectedOptionIndex(account.getDecisions());

        BigDecimal units = null;

        if (idx == CorporateActionNonProRataPriorityOfferType.TAKE_UP.getId()) {
            final CorporateActionOption subscriptionQuantityOption =
                    selectFirst(account.getDecisions(), having(on(CorporateActionOption.class).getKey(),
                            equalTo(CorporateActionOptionKey.SUBSCRIPTION_QUANTITY.getCode())));

            units = subscriptionQuantityOption.getBigDecimalValue();
        }

        return CorporateActionNonProRataPriorityOfferAccountElectionsDtoImpl.createSingleAccountElection(idx, units);
    }

    @Override
    public CorporateActionAccountElectionsDto toSavedAccountElectionsDto(CorporateActionContext context, String accountId,
                                                                         CorporateActionSavedDetails savedElections) {
        CorporateActionAccountElectionsDto electionsDto = null;

        if (savedElections.getSavedParticipation() != null) {
            CorporateActionSavedAccount savedAccount =
                    selectFirst(savedElections.getSavedParticipation().getAccounts(),
                            having(on(CorporateActionSavedAccount.class).getKey().getAccountNumber(), equalTo(accountId)));

            if (savedAccount != null && !savedAccount.getAccountElections().isEmpty()) {
                // Only one will ever exist for this CA
                CorporateActionSavedAccountElection savedAccountElection = savedAccount.getAccountElections().iterator().next();
                electionsDto = CorporateActionNonProRataPriorityOfferAccountElectionsDtoImpl
                        .createSingleAccountElection(savedAccountElection.getKey().getOptionId(), savedAccountElection.getUnits());
            }
        }

        return electionsDto;
    }

    @Override
    public CorporateActionDetailsDtoParams setCorporateActionDetailsDtoParams(CorporateActionContext context,
                                                                              CorporateActionDetailsDtoParams params) {
        final CorporateActionOption minQtyOption =
                selectFirst(context.getCorporateActionDetails().getOptions(),
                        having(on(CorporateActionOption.class).getKey(), equalTo(CorporateActionOptionKey.MINIMUM_QUANTITY.getCode())));

        final CorporateActionOption maxQtyOption =
                selectFirst(context.getCorporateActionDetails().getOptions(),
                        having(on(CorporateActionOption.class).getKey(), equalTo(CorporateActionOptionKey.MAXIMUM_QUANTITY.getCode())));

        final CorporateActionOption stepOption =
                selectFirst(context.getCorporateActionDetails().getOptions(),
                        having(on(CorporateActionOption.class).getKey(), equalTo(CorporateActionOptionKey.STEP.getCode())));

        BigDecimal minQty = null;
        BigDecimal maxQty = null;
        BigDecimal step = null;

        if (minQtyOption != null) {
            minQty = minQtyOption.getBigDecimalValue();
        }

        if (maxQtyOption != null) {
            maxQty = maxQtyOption.getBigDecimalValue();
        }

        if (stepOption != null) {
            step = stepOption.getBigDecimalValue();
        }

        params.setElectionMinMax(new CorporateActionElectionMinMax(minQty, maxQty, step));

        final CorporateActionOption priceOption =
                selectFirst(context.getCorporateActionDetails().getOptions(),
                        having(on(CorporateActionOption.class).getKey(), equalTo(CorporateActionOptionKey.PRICE.getCode())));

        if (priceOption != null) {
            params.setCorporateActionPrice(priceOption.getBigDecimalValue());
        }

        return params;
    }

    @Override
    public List<CorporateActionOptionDto> toElectionOptionDtos(CorporateActionContext context, ServiceErrors serviceErrors) {
        List<CorporateActionOptionDto> options = new ArrayList<>(2);
        final int selectedOptionIdx = getSelectedOptionIndex(context.getCorporateActionDetails().getDecisions());

        options.add(new CorporateActionOptionDto(0, getOptionFieldHeader(0), "Take up",
                selectedOptionIdx == CorporateActionNonProRataPriorityOfferType.TAKE_UP.getId()));

        options.add(new CorporateActionOptionDto(CorporateActionNonProRataPriorityOfferType.LAPSE.getId(), getOptionFieldHeader(1), "Lapse",
                Boolean.TRUE, selectedOptionIdx == CorporateActionNonProRataPriorityOfferType.LAPSE.getId()));

        return options;
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
            CorporateActionOption option = selectFirst(decisions, having(on(CorporateActionOption.class).getKey(), IsEqual.equalTo(
                    CorporateActionDecisionKey.SUBSCRIBED_QUANTITY.getCode())));

            if (option != null && option.hasValue() && option.getBigDecimalValue().compareTo(BigDecimal.ZERO) != 0) {
                return CorporateActionNonProRataPriorityOfferType.TAKE_UP.getId();
            }
        }

        return CorporateActionNonProRataPriorityOfferType.LAPSE.getId();
    }
}