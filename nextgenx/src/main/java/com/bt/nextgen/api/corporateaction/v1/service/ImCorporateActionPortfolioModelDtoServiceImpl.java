package com.bt.nextgen.api.corporateaction.v1.service;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountElectionsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSavedDetails;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionPortfolioModelDto;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionPortfolioModelDtoParams;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccountParticipationStatus;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionGroup;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionStatus;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionTransactionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionTransactionStatus;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementIntegrationService;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementInterface;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.modelportfolio.common.ModelType;
import com.bt.nextgen.service.integration.modelportfolio.detail.ModelPortfolioDetail;
import org.hamcrest.core.IsEqual;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static ch.lambdaj.Lambda.selectFirst;
import static org.hamcrest.core.IsEqual.equalTo;


@Service
public class ImCorporateActionPortfolioModelDtoServiceImpl implements ImCorporateActionPortfolioModelDtoService {

    @Autowired
    private InvestmentPolicyStatementIntegrationService ipsIntegrationService;

    @Autowired
    private CorporateActionAccountHelper corporateActionAccountHelper;

    @Autowired
    private CorporateActionTransactionDetailsConverter transactionDetailsConverter;

    @Override
    public List<ImCorporateActionPortfolioModelDto> toCorporateActionPortfolioModelDto(CorporateActionContext context,
                                                                                       List<CorporateActionAccount> accounts,
                                                                                       CorporateActionSavedDetails savedDetails,
                                                                                       ServiceErrors serviceErrors) {
        if (accounts != null) {
            Map<IpsKey, InvestmentPolicyStatementInterface> ipsMap = ipsIntegrationService.getInvestmentPolicyStatements(serviceErrors);

            List<IpsKey> ipsKeys = getIpsIdList(accounts);

            Map<IpsKey, ModelPortfolioDetail> modelPortfolioDetailsMap = ipsIntegrationService.getModelDetails(ipsKeys, serviceErrors);

            final List<ImCorporateActionPortfolioModelDto> result = new ArrayList<>();

            List<CorporateActionTransactionDetails> transactionDetailsList =
                    CorporateActionStatus.CLOSED.equals(context.getCorporateActionDetails().getCorporateActionStatus()) ?
                    loadTransactionDetailsList(context, serviceErrors) : null;

            for (IpsKey ipsKey : ipsKeys) {
                InvestmentPolicyStatementInterface ips = ipsMap.get(ipsKey);

                if (ips != null) {
                    List<CorporateActionAccount> ipsAccounts =
                            filter(having(on(CorporateActionAccount.class).getIpsId(), equalTo(ipsKey.getId())), accounts);

                    List<CorporateActionAccount> ipsMpAccounts = select(ipsAccounts,
                            having(on(CorporateActionAccount.class).getContainerType(), equalTo(ContainerType.MANAGED_PORTFOLIO)));

                    if (!ipsMpAccounts.isEmpty()) {
                        ImCorporateActionPortfolioModelDtoParams params = new ImCorporateActionPortfolioModelDtoParams();
                        params.setIpsId(ipsKey.getId());
                        params.setPortfolioModelId(ips.getApirCode() != null ? ips.getApirCode() : ips.getCode());
                        params.setPortfolioCode(ips.getCode());
                        params.setPortfolioName(ips.getInvestmentName());
                        params.setInvestors(ipsMpAccounts.size());
                        params.setInvestorElectionsSubmitted(getInvestorElectionsSubmitted(ipsMpAccounts, context));
                        params.setEligibleHolding(sumAvailableQuantity(ipsMpAccounts));
                        params.setSavedElections(corporateActionAccountHelper.getSavedElections(context, ipsKey.getId(), savedDetails));
                        updateElection(context, params, ipsAccounts, ipsMpAccounts);
                        if (transactionDetailsList != null) {
                            setTransactionDetails(context, ipsKey.getId(), transactionDetailsList, accounts, params);
                        }

                        params.setTrusteeApproval(getTrusteeApproval(ipsKey, modelPortfolioDetailsMap));

                        result.add(new ImCorporateActionPortfolioModelDto(params));
                    }
                }
            }

            return result;
        }

        return Collections.emptyList();
    }

    private Integer sumAvailableQuantity(List<CorporateActionAccount> accounts) {
        BigDecimal result = BigDecimal.ZERO;

        for (CorporateActionAccount account : accounts) {
            result = result.add(account.getAvailableQuantity());
        }

        return result.setScale(0, BigDecimal.ROUND_HALF_DOWN).intValue();
    }

    private boolean getTrusteeApproval(IpsKey ipsKey, Map<IpsKey, ModelPortfolioDetail> modelPortfolioDetailsMap) {
        ModelPortfolioDetail modelPortfolioDetail = modelPortfolioDetailsMap.get(ipsKey);

        return modelPortfolioDetail != null && ModelType.SUPERANNUATION == ModelType
                .forId(modelPortfolioDetail.getAccountType());
    }

    private List<IpsKey> getIpsIdList(List<CorporateActionAccount> allAccounts) {
        List<IpsKey> ipsIds = new ArrayList<>();

        for (CorporateActionAccount account : allAccounts) {
            if (!ipsIds.contains(IpsKey.valueOf(account.getIpsId()))) {
                ipsIds.add(IpsKey.valueOf(account.getIpsId()));
            }
        }

        return ipsIds;
    }

    private Integer getInvestorElectionsSubmitted(List<CorporateActionAccount> accounts, CorporateActionContext context) {
        Integer submitted = 0;
        for (CorporateActionAccount account : accounts) {
            CorporateActionAccountElectionsDto election = corporateActionAccountHelper.getSubmittedElections(context, account);
            if (election != null) {
                submitted++;
            }
        }
        return submitted;
    }

    private CorporateActionAccountElectionsDto deriveSubmittedElections(CorporateActionContext context,
                                                                        List<CorporateActionAccount> accounts) {
        CorporateActionAccount account = selectFirst(accounts,
                having(on(CorporateActionAccount.class).getElectionStatus(), equalTo(CorporateActionAccountParticipationStatus.SUBMITTED)));

        return account != null ? corporateActionAccountHelper.getSubmittedElections(context, account) : null;
    }

    /**
     * Retrieve the electionsDto for the shadow-portfolio. This should only by used in the TMP case where the model's status is
     * displayed.
     *
     * @param context
     * @param accounts
     * @return
     */
    private CorporateActionAccountElectionsDto getShadowPortfolioSubmittedElection(CorporateActionContext context,
                                                                                   List<CorporateActionAccount> accounts) {
        CorporateActionAccount account = selectFirst(accounts,
                having(on(CorporateActionAccount.class).getContainerType(), equalTo(ContainerType.SHADOW_MANAGED_PORTFOLIO)));

        return account != null ? corporateActionAccountHelper.getSubmittedElections(context, account) : null;
    }

    private CorporateActionAccountParticipationStatus deriveShadowPortfolioElectionStatus(List<CorporateActionAccount> accounts) {
        CorporateActionAccount account = selectFirst(accounts,
                having(on(CorporateActionAccount.class).getContainerType(), equalTo(ContainerType.SHADOW_MANAGED_PORTFOLIO)));

        return account != null ? account.getElectionStatus() : CorporateActionAccountParticipationStatus.NOT_SUBMITTED;
    }

    private CorporateActionAccountParticipationStatus deriveElectionStatus(List<CorporateActionAccount> accounts) {
        CorporateActionAccount account = selectFirst(accounts,
                having(on(CorporateActionAccount.class).getElectionStatus(), equalTo(CorporateActionAccountParticipationStatus.SUBMITTED)));

        return account != null ? CorporateActionAccountParticipationStatus.SUBMITTED : CorporateActionAccountParticipationStatus.NOT_SUBMITTED;
    }

    private void setTransactionDetails(CorporateActionContext context, String ipsId,
                                       List<CorporateActionTransactionDetails> transactionDetailsList,
                                       List<CorporateActionAccount> accounts,
                                       ImCorporateActionPortfolioModelDtoParams params) {
        params.setTransactionStatus(CorporateActionTransactionStatus.PRE_EX_DATE);

        // Transaction details only available under shadow MP account
        CorporateActionAccount account = selectFirst(accounts, having(on(CorporateActionAccount.class).getIpsId(), equalTo(ipsId))
                .and(having(on(CorporateActionAccount.class).getContainerType(), equalTo(ContainerType.SHADOW_MANAGED_PORTFOLIO))));

        if (account != null) {
            CorporateActionTransactionDetails transactionDetails = selectFirst(transactionDetailsList,
                    having(on(CorporateActionTransactionDetails.class).getPositionId(), IsEqual.equalTo(account.getPositionId())));

            if (transactionDetails != null) {
                params.setTransactionStatus(CorporateActionTransactionStatus.POST_EX_DATE);
                params.setTransactionDescription(transactionDetails.getTransactionDescription());
            }
        }
    }

    private List<CorporateActionTransactionDetails> loadTransactionDetailsList(CorporateActionContext context,
            ServiceErrors serviceErrors) {

        List<CorporateActionTransactionDetails> transactionDetailsList = null;
        boolean isMandatory = CorporateActionGroup.MANDATORY.equals(context.getCorporateActionDetails().getCorporateActionType()
                .getGroup());
        boolean isVoluntary = CorporateActionGroup.VOLUNTARY.equals(context.getCorporateActionDetails().getCorporateActionType()
                .getGroup());
        boolean isPastExDate = context.getCorporateActionDetails().getExDate().isBeforeNow();

        if (isVoluntary || (isMandatory && isPastExDate)) {
            transactionDetailsList = transactionDetailsConverter.loadTransactionDetailsForIm(context, serviceErrors);
        }

        return transactionDetailsList;
    }

    /**
     * Update details election-status and submitted elections. For DG, these information will be based on the shadow-portfolio.
     *
     * @param context
     * @param params
     * @param ipsAccounts
     * @param ipsMpAccounts
     */
    private void updateElection(CorporateActionContext context, ImCorporateActionPortfolioModelDtoParams params,
                                List<CorporateActionAccount> ipsAccounts, List<CorporateActionAccount> ipsMpAccounts) {
        if (context.isDealerGroup()) {
            // For dealer group, retrieve the election-status and details of the shadow-portfolio.
            params.setElectionStatus(deriveShadowPortfolioElectionStatus(ipsAccounts));
            params.setSubmittedElections(getShadowPortfolioSubmittedElection(context, ipsAccounts));
        } else {
            params.setElectionStatus(deriveElectionStatus(ipsAccounts));
            params.setSubmittedElections(CorporateActionAccountParticipationStatus.SUBMITTED.equals(params.getElectionStatus()) ?
                                         deriveSubmittedElections(context, ipsMpAccounts) : null);
        }
    }
}
