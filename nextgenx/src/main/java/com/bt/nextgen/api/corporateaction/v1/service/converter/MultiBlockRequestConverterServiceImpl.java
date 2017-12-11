package com.bt.nextgen.api.corporateaction.v1.service.converter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSelectedOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSelectedOptionsDto;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionElectionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionPortfolioModelDto;
import com.bt.nextgen.api.corporateaction.v1.service.stereotype.CorporateActionRequestConverter;
import com.bt.nextgen.service.avaloq.corporateaction.CorporateActionDecisionImpl;
import com.bt.nextgen.service.avaloq.corporateaction.CorporateActionElectionGroupImpl;
import com.bt.nextgen.service.avaloq.corporateaction.CorporateActionPositionImpl;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDecisionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionElectionGroup;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOption;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionPosition;

import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.core.IsEqual.equalTo;


@CorporateActionRequestConverter("CA_MULTI_BLOCK_REQUEST")
public class MultiBlockRequestConverterServiceImpl extends AbstractCorporateActionRequestConverterServiceImpl {
    @Override
    protected CorporateActionElectionGroup createElectionGroupCommon(CorporateActionContext context,
                                                                     CorporateActionSelectedOptionsDto electionsDto,
                                                                     List<CorporateActionPosition> positions) {
        CorporateActionSelectedOptionDto electionDto = electionsDto.getPrimarySelectedOption();
        CorporateActionElectionGroup electionGroup =
                new CorporateActionElectionGroupImpl(context.getCorporateActionDetails().getOrderNumber(), electionDto,
                        positions, new ArrayList<CorporateActionOption>());

        boolean quantityElection = isTakeUpByUnits(context);

        for (int i = 1; i <= getMaxOptions(); i++) {
            electionGroup.getOptions().add(createQuantityDecision(electionDto, i, quantityElection));
            electionGroup.getOptions().add(createPercentDecision(context, electionDto, i, quantityElection));
        }

        // Always required
        electionGroup.getOptions().add(new CorporateActionDecisionImpl(CorporateActionDecisionKey.BLOCK_ON_DECISION.getCode(), "Y"));

        return electionGroup;
    }

    @Override
    public Collection<CorporateActionElectionGroup> createElectionGroupsForIm(CorporateActionContext context,
                                                                              ImCorporateActionElectionDetailsDto electionDetailsDto) {
        boolean quantityElection = isTakeUpByUnits(context);

        // Use super's implementation if not quantity election
        if (!quantityElection) {
            return super.createElectionGroupsForIm(context, electionDetailsDto);
        }

        List<CorporateActionElectionGroup> electionGroups = new ArrayList<>();

        for (ImCorporateActionPortfolioModelDto portfolioModel : electionDetailsDto.getPortfolioModels()) {
            List<CorporateActionAccount> ipsAccounts =
                    filter(having(on(CorporateActionAccount.class).getIpsId(), equalTo(portfolioModel.getIpsId())),
                            context.getCorporateActionAccountList());

            // Create a new election group per account due to each account having different number of units when take up is by units
            for (CorporateActionAccount ipsAccount : ipsAccounts) {
                electionGroups.add(createElectionGroupForImAccount(context, ipsAccount, portfolioModel.getIpsId(),
                        portfolioModel.getSelectedElections()));
            }
        }

        return electionGroups;
    }

    private CorporateActionElectionGroup createElectionGroupForImAccount(CorporateActionContext context, CorporateActionAccount ipsAccount,
                                                                         String ipsId,
                                                                         CorporateActionSelectedOptionsDto selectedElections) {
        CorporateActionSelectedOptionDto electionDto = selectedElections.getPrimarySelectedOption();

        CorporateActionPosition position = new CorporateActionPositionImpl(ipsAccount.getPositionId(), ipsAccount.getContainerType());

        CorporateActionElectionGroup electionGroup =
                new CorporateActionElectionGroupImpl(context.getCorporateActionDetails().getOrderNumber(), electionDto,
                        Arrays.asList(position), new ArrayList<CorporateActionOption>());

        BigDecimal units = ipsAccount.getAvailableQuantity();

        if (context.getCorporateActionDetails().getTakeoverLimit() != null &&
                context.getCorporateActionDetails().getTakeoverLimit().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal takeOverPercent = context.getCorporateActionDetails().getTakeoverLimit().multiply(BigDecimal.valueOf(0.01));
            units = takeOverPercent.multiply(ipsAccount.getAvailableQuantity());
        }

        units = units.setScale(0, BigDecimal.ROUND_DOWN);

        for (int i = 1; i <= getMaxOptions(); i++) {
            electionGroup.getOptions().add(createQuantityDecision(electionDto, units, i));
            electionGroup.getOptions().add(new CorporateActionDecisionImpl(CorporateActionDecisionKey.PERCENT.getCode(i), ""));
        }

        // Always required
        electionGroup.getOptions().add(new CorporateActionDecisionImpl(CorporateActionDecisionKey.BLOCK_ON_DECISION.getCode(), "Y"));

        return electionGroup;
    }

    private CorporateActionOption createQuantityDecision(CorporateActionSelectedOptionDto electionDto, BigDecimal units, int idx) {
        if (electionDto.getOptionId() == idx) {
            return new CorporateActionDecisionImpl(CorporateActionDecisionKey.QUANTITY.getCode(idx), units.toPlainString());
        }

        return new CorporateActionDecisionImpl(CorporateActionDecisionKey.QUANTITY.getCode(idx), "");
    }

    private CorporateActionOption createQuantityDecision(CorporateActionSelectedOptionDto electionDto, int idx, boolean quantityElection) {
        if (electionDto.getOptionId() == idx) {
            String units = quantityElection && electionDto.getUnits() != null ? electionDto.getUnits().toPlainString() : "";

            return new CorporateActionDecisionImpl(CorporateActionDecisionKey.QUANTITY.getCode(idx), units);
        }

        return new CorporateActionDecisionImpl(CorporateActionDecisionKey.QUANTITY.getCode(idx), "");
    }

    private CorporateActionOption createPercentDecision(CorporateActionContext context, CorporateActionSelectedOptionDto electionDto,
                                                        int idx, boolean quantityElection) {
        if (electionDto.getOptionId() == idx) {
            String percent = !quantityElection ? getTakeUpPercent(context, electionDto) : "";

            return new CorporateActionDecisionImpl(CorporateActionDecisionKey.PERCENT.getCode(idx), percent);
        }

        return new CorporateActionDecisionImpl(CorporateActionDecisionKey.PERCENT.getCode(idx), "");
    }

    protected String getTakeUpPercent(CorporateActionContext context, CorporateActionSelectedOptionDto electionDto) {
        return electionDto.getPercent() != null ? electionDto.getPercent().toPlainString() :
               CorporateActionConverterConstants.DECIMAL_ONE_HUNDRED.toPlainString();
    }

    protected boolean isTakeUpByUnits(CorporateActionContext context) {
        return false;
    }
}
