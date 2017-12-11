package com.bt.nextgen.api.corporateaction.v1.service.converter;

import java.math.BigDecimal;
import java.util.List;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDtoParams;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionOptionDto;
import com.bt.nextgen.api.corporateaction.v1.service.stereotype.CorporateActionResponseConverter;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccountParticipationStatus;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOption;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOptionKey;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectFirst;
import static org.hamcrest.core.IsEqual.equalTo;

@CorporateActionResponseConverter("CA_MULTI_BLOCK_CAPITAL_CALL_RESPONSE")
public class MultiBlockCapitalCallResponseConverterServiceImpl extends MultiBlockResponseConverterServiceImpl {
    @Override
    protected CorporateActionOptionDto generateLastCorporateActionOptionDto(int referenceIndex, int currentIndex,
                                                                            List<CorporateActionOptionDto> options,
                                                                            boolean isSelectedOption, ServiceErrors serviceErrors) {
        if (selectFirst(options, having(on(CorporateActionOptionDto.class).getIsNoAction(), equalTo(Boolean.TRUE))) == null) {
            return new CorporateActionOptionDto(CorporateActionConverterConstants.OPTION_TAKE_NO_ACTION_ID,
                    getOptionFieldHeader(currentIndex), OPTION_TEXT_NO_ACTION, Boolean.TRUE, isSelectedOption);
        }

        return null;
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
                            equalTo(CorporateActionOptionKey.OFFERED_PRICE.getCode(selectedOptionIdx))));

            if (priceOption != null) {
                BigDecimal price = priceOption.getBigDecimalValue();
                params.setCash(params.getCash().add(price.multiply(new BigDecimal(params.getHolding()))));
            }
        }

        return params;
    }

    @Override
    protected int getSelectedOptionIndex(List<CorporateActionOption> decisions) {
        int selectedOptionIndex = super.getSelectedOptionIndex(decisions);

        return selectedOptionIndex > -1 ? selectedOptionIndex : CorporateActionConverterConstants.OPTION_TAKE_NO_ACTION_ID;
    }
}
