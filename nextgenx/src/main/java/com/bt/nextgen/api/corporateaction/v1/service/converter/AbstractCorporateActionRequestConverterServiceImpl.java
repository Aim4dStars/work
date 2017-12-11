package com.bt.nextgen.api.corporateaction.v1.service.converter;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSelectedOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSelectedOptionsDto;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionElectionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionPortfolioModelDto;
import com.bt.nextgen.service.avaloq.corporateaction.CorporateActionDecisionImpl;
import com.bt.nextgen.service.avaloq.corporateaction.CorporateActionElectionGroupImpl;
import com.bt.nextgen.service.avaloq.corporateaction.CorporateActionPositionImpl;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDecisionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionElectionGroup;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOption;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;

public abstract class AbstractCorporateActionRequestConverterServiceImpl implements CorporateActionRequestConverterService {
    @Override
    public Collection<CorporateActionElectionGroup> createElectionGroups(CorporateActionContext context,
                                                                         CorporateActionElectionDetailsDto electionDetailsDto) {
        Map<Object, CorporateActionElectionGroup> electionGroups = new HashMap<>();

        for (CorporateActionAccountDetailsDto account : electionDetailsDto.getAccounts()) {
            Object key = getElectionGroupKey(account.getSelectedElections());

            CorporateActionElectionGroup electionGroup = electionGroups.get(key);

            if (electionGroup == null) {
                electionGroup = createElectionGroup(context, account.getSelectedElections());
                electionGroups.put(key, electionGroup);
            }

            electionGroup.getPositions().add(new CorporateActionPositionImpl(account.getPositionId()));
        }

        return electionGroups.values();
    }

    @Override
    public Collection<CorporateActionElectionGroup> createElectionGroupsForIm(CorporateActionContext context,
                                                                              ImCorporateActionElectionDetailsDto electionDetailsDto) {
        List<CorporateActionElectionGroup> electionGroups = new ArrayList<>();

        // Not really an ideal solution to group by portfolio model - would be more optimal to submit based on elections but this will
        // complicate error association and reporting
        for (ImCorporateActionPortfolioModelDto portfolioModel : electionDetailsDto.getPortfolioModels()) {
            List<CorporateActionAccount> ipsAccounts =
                    filter(having(on(CorporateActionAccount.class).getIpsId(), equalTo(portfolioModel.getIpsId())),
                            context.getCorporateActionAccountList());

            CorporateActionElectionGroup electionGroup =
                    createElectionGroupForIm(context, getAccountPositions(ipsAccounts), portfolioModel.getSelectedElections());
            electionGroup.setIpsId(portfolioModel.getIpsId());

            electionGroups.add(electionGroup);
        }

        return electionGroups;
    }

    @Override
    public Collection<CorporateActionElectionGroup> createElectionGroupsForDg(CorporateActionContext context,
                                                                              ImCorporateActionElectionDetailsDto electionDetailsDto) {
        // Filter account list to those within the single IPS we are submitting for
        String ipsId = electionDetailsDto.getPortfolioModels().get(0).getIpsId();
        List<CorporateActionAccount> accounts = filter(having(on(CorporateActionAccount.class).getIpsId(), equalTo(ipsId)),
                context.getCorporateActionAccountList());

        List<CorporateActionAccount> ipsAccounts = filter(
                having(on(CorporateActionAccount.class).getContainerType(), not(ContainerType.MANAGED_PORTFOLIO)), accounts);

        List<CorporateActionAccount> mpAccounts = filter(
                having(on(CorporateActionAccount.class).getContainerType(), equalTo(ContainerType.MANAGED_PORTFOLIO)), accounts);

        Map<Object, CorporateActionElectionGroup> electionGroups = new HashMap<>();

        groupElection(context, ipsAccounts, electionDetailsDto, false, electionGroups);
        groupElection(context, mpAccounts, electionDetailsDto, true, electionGroups);

        return electionGroups.values();
    }

    private void groupElection(CorporateActionContext context, List<CorporateActionAccount> accounts,
                               ImCorporateActionElectionDetailsDto electionDetailsDto, boolean overwriteModelElection,
                               Map<Object, CorporateActionElectionGroup> electionGroups) {
        ImCorporateActionPortfolioModelDto portfolioModel = electionDetailsDto.getPortfolioModels().get(0);

        if (accounts != null) {
            for (CorporateActionAccount account : accounts) {
                CorporateActionSelectedOptionsDto elections;
                CorporateActionAccountDetailsDto accountDetails = Lambda.selectFirst(electionDetailsDto.getAccounts(),
                        having(on(CorporateActionAccountDetailsDto.class).getPositionId(), equalTo(account.getPositionId())));

                if (accountDetails == null && overwriteModelElection) {
                    // This prevents accounts waiting for trustee approval from being overwritten by a model election
                    continue;
                } else if (accountDetails != null) {
                    elections = accountDetails.getSelectedElections();
                } else {
                    elections = portfolioModel.getSelectedElections();
                }

                Object key = getElectionGroupKey(elections);
                CorporateActionElectionGroup electionGroup = electionGroups.get(key);

                if (electionGroup == null) {
                    electionGroup = createElectionGroup(context, elections);
                    electionGroup.setIpsId(portfolioModel.getIpsId());
                    electionGroups.put(key, electionGroup);
                }

                electionGroup.getPositions().add(new CorporateActionPositionImpl(account.getPositionId()));
            }
        }
    }

    protected List<CorporateActionPosition> getAccountPositions(List<CorporateActionAccount> accounts) {
        List<CorporateActionPosition> positions = new ArrayList<>(accounts.size());

        for (CorporateActionAccount account : accounts) {
            positions.add(new CorporateActionPositionImpl(account.getPositionId(), account.getContainerType()));
        }

        return positions;
    }

    protected Object getElectionGroupKey(CorporateActionSelectedOptionsDto accountElectionsDto) {
        // For most CA's there should only be one election on the screen, unlike buy back which will override this method
        return accountElectionsDto.getPrimarySelectedOption();
    }

    protected CorporateActionElectionGroup createElectionGroup(CorporateActionContext context,
                                                               CorporateActionSelectedOptionsDto electionsDto) {
        return createElectionGroupCommon(context, electionsDto, new ArrayList<CorporateActionPosition>());
    }

    protected CorporateActionElectionGroup createElectionGroupForIm(CorporateActionContext context,
                                                                    List<CorporateActionPosition> positions,
                                                                    CorporateActionSelectedOptionsDto electionsDto) {
        return createElectionGroupCommon(context, electionsDto, positions);
    }

    protected CorporateActionElectionGroup createElectionGroupCommon(CorporateActionContext context,
                                                                     CorporateActionSelectedOptionsDto electionsDto,
                                                                     List<CorporateActionPosition> positions) {
        CorporateActionSelectedOptionDto electionDto = electionsDto.getPrimarySelectedOption();
        CorporateActionElectionGroup electionGroup =
                new CorporateActionElectionGroupImpl(context.getCorporateActionDetails().getOrderNumber(), electionDto,
                        positions, new ArrayList<CorporateActionOption>());

        for (int i = 1; i <= getMaxOptions(); i++) {
            electionGroup.getOptions().add(new CorporateActionDecisionImpl(CorporateActionDecisionKey.QUANTITY.getCode(i), ""));
            electionGroup.getOptions().add(new CorporateActionDecisionImpl(CorporateActionDecisionKey.PERCENT.getCode(i),
                    electionDto.getOptionId() == i ? "100" : ""));
        }

        // Always required
        electionGroup.getOptions()
                     .add(new CorporateActionDecisionImpl(CorporateActionDecisionKey.BLOCK_ON_DECISION.getCode(), "Y"));

        return electionGroup;
    }

    protected int getMaxOptions() {
        return CorporateActionConverterConstants.MAX_OPTIONS;
    }
}
