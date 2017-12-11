package com.bt.nextgen.reports.insurance;

import com.bt.nextgen.api.policy.model.PolicySummaryDto;
import com.bt.nextgen.api.policy.model.PolicyTrackingDto;
import com.bt.nextgen.api.policy.service.PolicyDtoConverter;
import com.bt.nextgen.api.policy.service.PolicySummaryDtoConverter;
import com.bt.nextgen.api.policy.service.PolicyUtility;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.bt.nextgen.core.reporting.BaseReportV2;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.type.ConsistentEncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyTracking;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyTrackingImpl;
import com.bt.nextgen.service.avaloq.insurance.service.PolicyIntegrationService;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.btfin.panorama.core.concurrent.AbstractConcurrentComplete;
import com.btfin.panorama.core.concurrent.Concurrent;
import com.btfin.panorama.core.concurrent.ConcurrentCallable;
import com.btfin.panorama.core.concurrent.ConcurrentComplete;
import com.btfin.panorama.core.concurrent.ConcurrentResult;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Class for Insurance account list csv data creation
 */
@SuppressWarnings({"squid:S1172"})
@Report("InsuranceAccountList")
@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
public class InsuranceAccountListCsvReport extends BaseReportV2 {

    private static final String DISCLAIMER_CONTENT = "DS-IP-0154";
    private static final String REPORT_NAME = "Panorama Insurance Account List";
    private static final String DEFAULT_VALUE = "-";

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    @Autowired
    private PolicyIntegrationService policyIntegrationService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private ContentDtoService contentService;

    @Autowired
    private ProductIntegrationService productIntegrationService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;

    @Autowired
    private PolicyUtility policyUtility;


    /**
     * Insurance account list for csv - retrieves all the fnumber of the adviser/selected adviser (in case of paraplanner)
     * and retrieves the policies for all the fnumbers
     *
     * @param params - input paramters if any
     *
     * @return policySummaryDtos - Collection of Polices of type PolicySummaryDto
     */
    @ReportBean("insuranceAccountList")
    public Collection<PolicySummaryDto> retrieveInsuranceAccountList(Map<String, Object> params) {
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        String brokerId = (String) params.get("brokerid");
        // Get AdviserPPID from policyUtility generic method
        final String adviserPpId = policyUtility.getAdviserPpId(brokerId, serviceErrors);
        List<PolicyTracking> fnumbers = new ArrayList<>();
        if (StringUtils.isNotEmpty(adviserPpId)) {
            fnumbers = policyIntegrationService.getFNumbers(adviserPpId, serviceErrors);
        }
        List<PolicyTracking> insurancesTrackingsList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(fnumbers)) {
            List<ConcurrentCallable<?>> concurrentCallables = new ArrayList<ConcurrentCallable<?>>();
            int fNumbersAvailableToConcurrent = 1;
            int fNumbersRemainsToConcurrent = fnumbers.size();
            for (PolicyTracking fnumber : fnumbers) {
                concurrentCallables.add(getPoliciesForFnumber(fnumber.getFNumber(), serviceErrors));
                //Restrict to create 2 or less than 2 concurrent object at a time
                if (fNumbersRemainsToConcurrent == 1 || fNumbersAvailableToConcurrent % 2 == 0) {
                    Concurrent.when(concurrentCallables.toArray(new ConcurrentCallable<?>[concurrentCallables.size()]))
                            .done(processResults(insurancesTrackingsList, serviceErrors)).execute();
                    concurrentCallables = new ArrayList<>();
                    fNumbersAvailableToConcurrent = 1;
                }
                else {
                    fNumbersAvailableToConcurrent++;
                }
                fNumbersRemainsToConcurrent--;
            }
        }
        final Map<ProductKey, Product> productMap = productIntegrationService.loadProductsMap(serviceErrors);
        final Map<AccountKey, WrapAccount> accountMap = accountIntegrationService.loadWrapAccountWithoutContainers(serviceErrors);
        PolicyDtoConverter dtoConverter = new PolicyDtoConverter(accountMap, productMap);
        List<PolicyTrackingDto> policyTrackingDtos = dtoConverter.policyTrackingDetailDtos(insurancesTrackingsList, false);
        List<PolicySummaryDto> policySummaryDtos = PolicySummaryDtoConverter.getPolicySummaryCsvDtos(policyTrackingDtos);
        return policySummaryDtos;
    }


    /**
     * Invokes the service call to retrieve the policies of a particular fnumber
     *
     * @param fnumber       - fnumber of an adviser
     * @param serviceErrors
     *
     * @return policyTrackings - list of polices of type PolicyTracking containing Fnumber
     */
    private ConcurrentCallable<?> getPoliciesForFnumber(final String fnumber, final ServiceErrors serviceErrors) {
        return new ConcurrentCallable<List<PolicyTracking>>() {
            @Override
            public List<PolicyTracking> call() {
                List<PolicyTracking> policyTrackings = new ArrayList<>();
                policyTrackings = policyIntegrationService.getPoliciesForAdviser(fnumber, serviceErrors);
                if (CollectionUtils.isNotEmpty(policyTrackings)) {
                    for (PolicyTracking policyTracking : policyTrackings) {
                        ((PolicyTrackingImpl) policyTracking).setFNumber(fnumber); //Fnumber set for each output, fnumber is not returned from service
                    }
                }
                return policyTrackings;
            }
        };
    }

    /**
     * Processess the results of the concurrent service calls
     * - Here its adding up the list of policies of each fnumber to one list
     *
     * @param results       - List of Policy details - summation of list of policies of each fnumbers
     * @param serviceErrors
     */
    private ConcurrentComplete processResults(final List<PolicyTracking> results, final ServiceErrors serviceErrors) {
        return new AbstractConcurrentComplete() {
            @Override
            public void run() {
                List<? extends ConcurrentResult<?>> r = this.getResults();
                List<PolicyTracking> policyList = null;
                for (ConcurrentResult concurrentResult : r) {
                    policyList = (List<PolicyTracking>) concurrentResult.getResult();
                    if (CollectionUtils.isNotEmpty(policyList)) {
                        results.addAll(policyList);
                    }
                }
            }
        };
    }

    /**
     * Retrieves the adviser name of the associated fnumber for which the policies are displayed in csv
     *
     * @param params - input/output value of the report
     *
     * @return
     */
    @ReportBean("adviserName")
    public String retrieveAdviserName(Map<String, Object> params) {
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        String brokerId = (String) params.get("brokerid");
        BrokerUser brokerUser = null;
        if (brokerId != null) {
            String plaintextBrokerId = new ConsistentEncodedString(brokerId).plainText();
            brokerUser = brokerIntegrationService.getAdviserBrokerUser(BrokerKey.valueOf(plaintextBrokerId), serviceErrors);
        }
        else {
            brokerUser = brokerIntegrationService.getBrokerUser(userProfileService.getActiveProfile(), serviceErrors);
        }
        return brokerUser.getFullName();
    }

    /**
     * Retrieves the disclaimer text for the content id.
     *
     * @param params input/output parameters
     *
     * @return disclaimer text
     */
    @ReportBean("disclaimer")
    public String getDisclaimer(Map<String, Object> params) {
        final ServiceErrors serviceErrors = new FailFastErrorsImpl();
        final ContentKey key = new ContentKey(DISCLAIMER_CONTENT);
        final ContentDto content = contentService.find(key, serviceErrors);
        return content != null ? content.getContent() : "";
    }

    @ReportBean("reportType")
    public String getReportType(Map<String, Object> params) {
        return REPORT_NAME;
    }

    @ReportBean("defaultValue")
    public String getDefaultValue(Map<String, Object> params) {
        return DEFAULT_VALUE;
    }
}
