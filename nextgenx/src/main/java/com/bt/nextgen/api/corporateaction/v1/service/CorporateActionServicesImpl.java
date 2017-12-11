package com.bt.nextgen.api.corporateaction.v1.service;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListResult;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.corporateaction.CorporateAction;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetailsResponse;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.ImCorporateActionIntegrationService;
import com.bt.nextgen.service.integration.trustee.TrusteeApprovalStatus;
import com.btfin.panorama.core.concurrent.AbstractConcurrentComplete;
import com.btfin.panorama.core.concurrent.Concurrent;
import com.btfin.panorama.core.concurrent.ConcurrentCallable;
import com.btfin.panorama.core.concurrent.ConcurrentComplete;
import com.btfin.panorama.core.concurrent.ConcurrentResult;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.*;


@Service
public class CorporateActionServicesImpl implements CorporateActionServices {
    @Autowired
    private CorporateActionIntegrationService corporateActionIntegrationService;

    @Autowired
    private ImCorporateActionIntegrationService imCorporateActionIntegrationService;

    /**
     * {@inheritDoc}
     */
    @Override
    public CorporateActionListResult loadVoluntaryCorporateActions(DateTime startDate, DateTime endDate, List<String> accountIds,
                                                                   ServiceErrors serviceErrors) {
        CorporateActionListResult corporateActionListResult = new CorporateActionListResult();
        DateTime toDate = endDate.plusDays(1);

        Concurrent.when(loadVoluntaryCorporateActionForInvestment(startDate, toDate, accountIds, serviceErrors),
                loadVoluntaryCorporateActionForSuper(startDate, toDate, accountIds, serviceErrors)).done(
                processCorporateActionListResult(corporateActionListResult, true)).execute();

        return corporateActionListResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CorporateActionListResult loadVoluntaryCorporateActionsForIm(String imId, DateTime startDate, DateTime endDate,
                                                                        String portfolioModelId, ServiceErrors serviceErrors) {
        CorporateActionListResult corporateActionListResult = new CorporateActionListResult();

        corporateActionListResult.setCorporateActions(imCorporateActionIntegrationService
                .loadVoluntaryCorporateActions(imId, startDate, endDate.plusDays(1), portfolioModelId, serviceErrors));

        return corporateActionListResult;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public CorporateActionListResult loadMandatoryCorporateActions(DateTime startDate, DateTime endDate, List<String> accountIds,
                                                                   ServiceErrors serviceErrors) {
        CorporateActionListResult corporateActionListResult = new CorporateActionListResult();

        Concurrent.when(loadMandatoryCorporateActionForInvestment(startDate, endDate, accountIds, serviceErrors),
                loadMandatoryCorporateActionForSuper(startDate, endDate, accountIds, serviceErrors))
                  .done(processCorporateActionListResult(corporateActionListResult, false)).execute();

        return corporateActionListResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CorporateActionListResult loadMandatoryCorporateActionsForIm(String imId, DateTime startDate, DateTime endDate,
                                                                        String portfolioModelId, ServiceErrors serviceErrors) {
        CorporateActionListResult corporateActionListResult = new CorporateActionListResult();

        corporateActionListResult.setCorporateActions(imCorporateActionIntegrationService
                .loadMandatoryCorporateActions(imId, startDate, endDate, portfolioModelId, serviceErrors));

        return corporateActionListResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CorporateActionListResult loadVoluntaryCorporateActionsForApproval(DateTime startDate, DateTime endDate,
                                                                              ServiceErrors serviceErrors) {
        CorporateActionListResult corporateActionListResult = new CorporateActionListResult();
        DateTime toDate = endDate.plusDays(1);

        corporateActionListResult.setCorporateActions(corporateActionIntegrationService.loadVoluntaryCorporateActionsForApproval(startDate,
                toDate, serviceErrors));

        return corporateActionListResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CorporateActionContext loadCorporateActionDetailsContext(String orderNumber, Boolean summaryOnly,
                                                                    ServiceErrors serviceErrors) {
        CorporateActionContext context = new CorporateActionContext();

        if (summaryOnly != null && summaryOnly) {
            CorporateActionDetailsResponse response =
                    corporateActionIntegrationService.loadCorporateActionDetails(orderNumber, serviceErrors);

            if (response != null && response.getCorporateActionDetailsList() != null &&
                    !response.getCorporateActionDetailsList().isEmpty()) {
                context.setCorporateActionDetails(response.getCorporateActionDetailsList().get(0));
            }

        } else {
            Concurrent.when(loadCorporateActionDetails(orderNumber, serviceErrors), loadCorporateActionAccounts(orderNumber, serviceErrors))
                      .done(processCorporateActionDetailsContext(context)).execute();
        }

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CorporateActionContext loadCorporateActionDetailsContextForIm(String imId, String orderNumber,
                                                                         ServiceErrors serviceErrors) {
        CorporateActionContext context = new CorporateActionContext();

        Concurrent.when(loadCorporateActionDetails(orderNumber, serviceErrors),
                loadCorporateActionAccountsForIm(imId, orderNumber, serviceErrors))
                  .done(processCorporateActionDetailsContext(context)).execute();

        return context;
    }

    private ConcurrentComplete processCorporateActionDetailsContext(final CorporateActionContext context) {
        return new AbstractConcurrentComplete() {
            @Override
            public void run() {
                List<? extends ConcurrentResult<?>> r = this.getResults();

                context.setCorporateActionDetails((CorporateActionDetails) r.get(0).getResult());
                context.setCorporateActionAccountList((List<CorporateActionAccount>) r.get(1).getResult());
            }
        };
    }

    private ConcurrentComplete processCorporateActionListResult(final CorporateActionListResult corporateActionListResult,
                                                                final boolean filterByTrusteeStatus) {
        return new AbstractConcurrentComplete() {
            @Override
            public void run() {
                List<? extends ConcurrentResult<?>> r = this.getResults();
                mergeCorporateActions((List<CorporateAction>) r.get(0).getResult(), (List<CorporateAction>) r.get(1).getResult(),
                        filterByTrusteeStatus, corporateActionListResult);
            }
        };
    }

    private void mergeCorporateActions(List<CorporateAction> investmentCaList, List<CorporateAction> superCaList,
                                       boolean filterByTrusteeStatus, CorporateActionListResult corporateActionListResult) {
        List<CorporateAction> corporateActions = new ArrayList<>();
        corporateActionListResult.setCorporateActions(corporateActions);

        if (investmentCaList != null) {
            corporateActions.addAll(investmentCaList);
        }

        if (superCaList != null && !superCaList.isEmpty()) {
            List<CorporateAction> approvedList =
                    filterByTrusteeStatus ? select(superCaList, having(on(CorporateAction.class).getTrusteeApprovalStatus(),
                            Matchers.anyOf(Matchers.equalTo(TrusteeApprovalStatus.APPROVED),
                                    Matchers.equalTo(TrusteeApprovalStatus.DECLINED),
                                    Matchers.equalTo(TrusteeApprovalStatus.PENDING)))) : superCaList;

            if (!approvedList.isEmpty()) {
                corporateActionListResult.setHasSuperPension(Boolean.TRUE);
                mergeApprovedList(approvedList, investmentCaList, corporateActions);
            }

            if (filterByTrusteeStatus) {
                List<CorporateAction> declinedList = select(superCaList, having(on(CorporateAction.class).getTrusteeApprovalStatus(),
                        Matchers.equalTo(TrusteeApprovalStatus.DECLINED)));

                if (!declinedList.isEmpty()) {
                    // Add declined CA so it still appears in the announcement screen
                    addDeclinedList(declinedList, corporateActions);
                }
            }
        }
    }

    private void mergeApprovedList(List<CorporateAction> approvedList, List<CorporateAction> investmentCaList,
                                   List<CorporateAction> corporateActions) {
        for (CorporateAction superCa : approvedList) {
            CorporateAction invCa =
                    selectFirst(investmentCaList, having(on(CorporateAction.class).getOrderNumber(), Matchers.equalTo(
                            superCa.getOrderNumber())));

            if (invCa != null) {
                int invUnconfirmed = invCa.getUnconfirmed() != null ? invCa.getUnconfirmed() : 0;
                int supUnconfirmed = superCa.getUnconfirmed() != null ? superCa.getUnconfirmed() : 0;

                invCa.setEligible(invCa.getEligible() + superCa.getEligible());
                invCa.setUnconfirmed(invUnconfirmed + supUnconfirmed);
            } else {
                corporateActions.add(superCa);
            }
        }
    }

    private void addDeclinedList(List<CorporateAction> declinedList, List<CorporateAction> corporateActions) {
        for (CorporateAction superCa : declinedList) {
            CorporateAction invCa = selectFirst(corporateActions,
                    having(on(CorporateAction.class).getOrderNumber(), Matchers.equalTo(superCa.getOrderNumber())));

            if (invCa == null) {
                superCa.setEligible(0);
                superCa.setUnconfirmed(0);
                corporateActions.add(superCa);
            }
        }
    }

    private ConcurrentCallable<?> loadVoluntaryCorporateActionForInvestment(final DateTime startDate, final DateTime endDate,
                                                                            final List<String> accountIds,
                                                                            final ServiceErrors serviceErrors) {
        return new ConcurrentCallable<List<CorporateAction>>() {
            @Override
            public List<CorporateAction> call() {
                return corporateActionIntegrationService.loadVoluntaryCorporateActions(startDate, endDate, accountIds, serviceErrors);
            }
        };
    }

    private ConcurrentCallable<?> loadVoluntaryCorporateActionForSuper(final DateTime startDate, final DateTime endDate,
                                                                       final List<String> accountIds,
                                                                       final ServiceErrors serviceErrors) {
        return new ConcurrentCallable<List<CorporateAction>>() {
            @Override
            public List<CorporateAction> call() {
                return corporateActionIntegrationService.loadVoluntaryCorporateActionsForSuper(startDate, endDate, accountIds,
                        serviceErrors);
            }
        };
    }

    private ConcurrentCallable<?> loadMandatoryCorporateActionForInvestment(final DateTime startDate, final DateTime endDate,
                                                                            final List<String> accountIds,
                                                                            final ServiceErrors serviceErrors) {
        return new ConcurrentCallable<List<CorporateAction>>() {
            @Override
            public List<CorporateAction> call() {
                return corporateActionIntegrationService.loadMandatoryCorporateActions(startDate, endDate, accountIds, serviceErrors);
            }
        };
    }

    private ConcurrentCallable<?> loadMandatoryCorporateActionForSuper(final DateTime startDate, final DateTime endDate,
                                                                       final List<String> accountIds,
                                                                       final ServiceErrors serviceErrors) {
        return new ConcurrentCallable<List<CorporateAction>>() {
            @Override
            public List<CorporateAction> call() {
                return corporateActionIntegrationService.loadMandatoryCorporateActionsForSuper(startDate, endDate, accountIds,
                        serviceErrors);
            }
        };
    }

    private ConcurrentCallable<?> loadCorporateActionDetails(final String orderNumber,
                                                             final ServiceErrors serviceErrors) {
        return new ConcurrentCallable<CorporateActionDetails>() {
            @Override
            public CorporateActionDetails call() {
                CorporateActionDetailsResponse response =
                        corporateActionIntegrationService.loadCorporateActionDetails(orderNumber, serviceErrors);

                return response != null && response.getCorporateActionDetailsList() != null &&
                               !response.getCorporateActionDetailsList().isEmpty() ? response.getCorporateActionDetailsList().get(0) : null;
            }
        };
    }

    private ConcurrentCallable<?> loadCorporateActionAccounts(final String orderNumber,
                                                              final ServiceErrors serviceErrors) {
        return new ConcurrentCallable<List<CorporateActionAccount>>() {
            @Override
            public List<CorporateActionAccount> call() {
                return corporateActionIntegrationService.loadCorporateActionAccountsDetails(orderNumber, serviceErrors);
            }
        };
    }

    private ConcurrentCallable<?> loadCorporateActionAccountsForIm(final String imId, final String orderNumber,
                                                                   final ServiceErrors serviceErrors) {
        return new ConcurrentCallable<List<CorporateActionAccount>>() {
            @Override
            public List<CorporateActionAccount> call() {
                return corporateActionIntegrationService.loadCorporateActionAccountsDetailsForIm(imId, orderNumber, serviceErrors);
            }
        };
    }
}
