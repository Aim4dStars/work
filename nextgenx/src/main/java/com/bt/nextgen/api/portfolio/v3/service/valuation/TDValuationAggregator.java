package com.bt.nextgen.api.portfolio.v3.service.valuation;

import com.bt.nextgen.api.portfolio.v3.model.valuation.InvestmentValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.TermDepositValuationDto;
import com.bt.nextgen.api.portfolio.v3.service.TermDepositPresentationService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.TermDepositMaturityInstruction;
import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.options.service.OptionsService;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.TermDepositHolding;
import com.bt.nextgen.service.wrap.integration.portfolio.WrapTermDepositHoldingImpl;
import com.btfin.panorama.core.conversion.CodeCategory;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.btfin.panorama.core.security.avaloq.Constants.HYPHEN;

@Component("TDValuationAggregatorV3")
public class TDValuationAggregator extends AbstractValuationAggregator {

    private static final String CATEGORY_TERM_DEPOSIT = "Term deposits";
    @Autowired
    private StaticIntegrationService staticIntegrationService;
    @Autowired
    private OptionsService optionsService;
    @Autowired
    private TermDepositPresentationService termDepositPresentationService;

    public String getSubAccountCategory() {
        return CATEGORY_TERM_DEPOSIT;
    }

    public List<InvestmentValuationDto> getTermDepositValuationDtos(AccountKey accountKey,
                                                                    List<AccountHolding> subAccountHoldings, BigDecimal accountBalance, ServiceErrors serviceErrors) {
        List<InvestmentValuationDto> valuationList = new ArrayList<>();

        for (AccountHolding tdHolding : subAccountHoldings) {
            InvestmentValuationDto valuationDto = buildTermDepositValuationDto(accountKey, (TermDepositHolding) tdHolding,
                    accountBalance, serviceErrors);
            valuationList.add(valuationDto);
        }

        return valuationList;
    }

    private TermDepositValuationDto buildTermDepositValuationDto(AccountKey accountKey, TermDepositHolding termDepositHolding,
                                                                 BigDecimal accountBalance, ServiceErrors serviceErrors) {

        String maturityInstruction = null;
        if (StringUtils.isNotBlank(termDepositHolding.getMaturityInstruction())) {
            Code code = staticIntegrationService.loadCode(CodeCategory.TD_RENEW_MODE,
                    termDepositHolding.getMaturityInstruction(), serviceErrors);
            if (code != null) {
                maturityInstruction = code.getIntlId();
            }
        }

        TermDepositMaturityInstruction termDepositMaturityInstruction = TermDepositMaturityInstruction
                .fromCode(maturityInstruction);
        TermDepositValuationDto termDepositValuationDto = null;
        if (termDepositHolding instanceof WrapTermDepositHoldingImpl) {
            termDepositValuationDto = new TermDepositValuationDto(new TermDepositPresentation(), termDepositHolding, accountBalance,
                    HYPHEN, termDepositHolding.getExternal());
        }
        else {
            termDepositValuationDto = new TermDepositValuationDto(termDepositPresentationService.getTermDepositPresentation(accountKey,
                    termDepositHolding.getAsset().getAssetId(), serviceErrors), termDepositHolding, accountBalance,
                    termDepositMaturityInstruction.getDisplayDescription(), termDepositHolding.getExternal());
        }
        return termDepositValuationDto;
    }
}
