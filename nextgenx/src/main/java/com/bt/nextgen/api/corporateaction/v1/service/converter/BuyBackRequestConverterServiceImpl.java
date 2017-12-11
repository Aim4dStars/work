package com.bt.nextgen.api.corporateaction.v1.service.converter;

import java.util.ArrayList;
import java.util.List;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionBuyBackOptionKey;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSelectedOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSelectedOptionsDto;
import com.bt.nextgen.api.corporateaction.v1.service.stereotype.CorporateActionRequestConverter;
import com.bt.nextgen.service.avaloq.corporateaction.CorporateActionDecisionImpl;
import com.bt.nextgen.service.avaloq.corporateaction.CorporateActionElectionGroupImpl;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDecisionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionElectionGroup;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOption;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionPosition;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectFirst;
import static org.hamcrest.core.IsEqual.equalTo;


@CorporateActionRequestConverter("CA_BUY_BACK_REQUEST")
public class BuyBackRequestConverterServiceImpl extends AbstractCorporateActionRequestConverterServiceImpl {
    @Override
    protected CorporateActionElectionGroup createElectionGroup(CorporateActionContext context,
                                                               CorporateActionSelectedOptionsDto optionsDto) {
        List<CorporateActionOption> decisions = createDecisions(optionsDto);
        decisions.add(new CorporateActionDecisionImpl(CorporateActionDecisionKey.BLOCK_ON_DECISION.getCode(),
                getBlockOnDecisionsValue(optionsDto)));

        return new CorporateActionElectionGroupImpl(context.getCorporateActionDetails().getOrderNumber(), null,
                new ArrayList<CorporateActionPosition>(), decisions);
    }

    @Override
    protected CorporateActionElectionGroup createElectionGroupForIm(CorporateActionContext context, List<CorporateActionPosition> positions,
                                                                    CorporateActionSelectedOptionsDto electionsDto) {
        List<CorporateActionOption> decisions = createDecisions(electionsDto);
        decisions.add(new CorporateActionDecisionImpl(CorporateActionDecisionKey.BLOCK_ON_DECISION.getCode(),
                getBlockOnDecisionsValue(electionsDto)));

        return new CorporateActionElectionGroupImpl(context.getCorporateActionDetails().getOrderNumber(), null, positions, decisions);
    }

    @Override
    protected Object getElectionGroupKey(CorporateActionSelectedOptionsDto optionsDto) {
        return new CorporateActionBuyBackOptionKey(optionsDto.getOptions(), optionsDto.getMinimumPriceId());
    }

    private List<CorporateActionOption> createDecisions(CorporateActionSelectedOptionsDto optionsDto) {

        List<CorporateActionOption> decisions = new ArrayList<>();

        CorporateActionSelectedOptionDto finalTenderPriceOptionDto = selectFirst(optionsDto.getOptions(),
                having(on(CorporateActionSelectedOptionDto.class).getOptionId(),
                        equalTo(CorporateActionConverterConstants.OPTION_FINAL_TENDER_PRICE_ID)));

        String finalTenderQuantity = finalTenderPriceOptionDto != null && finalTenderPriceOptionDto.getUnits() != null ?
                                     finalTenderPriceOptionDto.getUnits().toPlainString() : "";
        decisions.add(new CorporateActionDecisionImpl(CorporateActionDecisionKey.FINAL_TENDER_QUANTITY.getCode(), finalTenderQuantity));

        for (int idx = 1; idx <= getMaxOptions(); idx++) {
            CorporateActionSelectedOptionDto optionDto = selectFirst(optionsDto.getOptions(),
                    having(on(CorporateActionSelectedOptionDto.class).getOptionId(), equalTo(idx)));

            String quantity = optionDto != null && optionDto.getUnits() != null ? optionDto.getUnits().toPlainString() : "";
            decisions.add(new CorporateActionDecisionImpl(CorporateActionDecisionKey.OPTION_QUANTITY.getCode(idx), quantity));
        }

        // Minimum price
        String minPriceId = optionsDto.getMinimumPriceId() != null ? optionsDto.getMinimumPriceId().toString() :
                            Integer.toString(CorporateActionConverterConstants.NONE_MINIMUM_PRICE_ID);

        decisions.add(new CorporateActionDecisionImpl(CorporateActionDecisionKey.MINIMUM_PRICE_DECISION.getCode(), minPriceId));

        return decisions;
    }

    private String getBlockOnDecisionsValue(CorporateActionSelectedOptionsDto optionsDto) {
        return (optionsDto.getOptions() != null && optionsDto.getOptions().size() == 1 &&
                optionsDto.getPrimarySelectedOption().getOptionId() == CorporateActionConverterConstants.OPTION_TAKE_NO_ACTION_ID) ?
               CorporateActionConverterConstants.OPTION_VALUE_NO :
               CorporateActionConverterConstants.OPTION_VALUE_YES;
    }

    @Override
    protected int getMaxOptions() {
        return CorporateActionConverterConstants.MAX_BUY_BACK_OPTIONS;
    }
}
