package com.bt.nextgen.api.policy.service;

import com.bt.nextgen.api.policy.model.PolicySummaryDto;
import com.bt.nextgen.api.policy.model.PolicyTrackingDto;
import com.bt.nextgen.api.policy.util.PolicyUtil;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.bt.nextgen.service.avaloq.account.PensionType;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyStatusCode;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyType;
import org.apache.commons.lang3.StringUtils;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for PolicySummaryDto converter - Dto values converted to as required in CSV
 */
public class PolicySummaryDtoConverter {

    private static final String DEFAULT_VALUE = "-";
    private static final String PENDING = "Pending";
    private static final String SPACE = " ";

    private PolicySummaryDtoConverter() {
    }

    /**
     * Format policy fields as required to display in the CSV file like hyphen when null, etc. full details in US17659
     *
     * @param policyTrackingDtos -list of policy details
     *
     * @return policySummaries - List of sort policies of Type PolicySummaryDto
     */
    public static List<PolicySummaryDto> getPolicySummaryCsvDtos(List<PolicyTrackingDto> policyTrackingDtos) {
        List<PolicySummaryDto> policySummaries = new ArrayList<>();
        for (PolicyTrackingDto policyTrackingDto : policyTrackingDtos) {
            PolicySummaryDto policySummaryDto = (PolicySummaryDto) policyTrackingDto;
            PolicyType policyType = PolicyUtil.getPolicyType(policySummaryDto.getPolicyType());
            PolicyStatusCode policyStatusCode = PolicyUtil.getPolicyStatus(policySummaryDto.getPolicyStatus());
            policySummaryDto.setPolicyType(PolicyType.NOT_AVAILABLE.equals(policyType) ? DEFAULT_VALUE : policyType.getLabel());
            policySummaryDto.setPolicyStatus(PolicyStatusCode.NOT_AVAILABLE.equals(policyStatusCode) ? DEFAULT_VALUE : policyStatusCode.getLabel());
            policySummaryDto.setPaymentFrequency(PolicyUtil.getPolicyFrequencyLabel(policySummaryDto.getPaymentFrequency()));

            if (policySummaryDto.getPremium() != null) {
                policySummaryDto.setPremium(policySummaryDto.getPremium().setScale(2, RoundingMode.HALF_UP));
            }
            if (policySummaryDto.getRenewalCommission() != null) {
                policySummaryDto.setRenewalCommission(policySummaryDto.getRenewalCommission().setScale(2, RoundingMode.HALF_UP));
            }
            policySummaryDto.setAccountType(setAccountTypeAttribute(policySummaryDto));
            setDateAttributes(policyStatusCode, policySummaryDto);
            if (PolicyType.ADVICE_SERVICE_FEE.equals(policyType)) {
                policySummaryDto.setRenewalCommission(null);
            }
            policySummaries.add(policySummaryDto);
        }
        return policySummaries;
    }

    /**
     * Renewal Date and commencement date set to defaultvalue(hyphen) and null as per requirement
     * -more details in US story  US17659
     *
     * @param policyStatusCode - policystatus enum
     * @param policySummaryDto - policy detail
     */
    public static void setDateAttributes(PolicyStatusCode policyStatusCode, PolicySummaryDto policySummaryDto) {
        String commenceDate = ReportFormatter.format(ReportFormat.SHORT_DATE, policySummaryDto.getCommencementDate());
        policySummaryDto.setCsvCommencementDate(StringUtils.isNotEmpty(commenceDate) ? commenceDate : DEFAULT_VALUE);
        if (PolicyStatusCode.DECLINED.equals(policyStatusCode) || PolicyStatusCode.PROPOSAL.equals(policyStatusCode)) {
            policySummaryDto.setRenewalCalenderDay(null);
            policySummaryDto.setCsvCommencementDate(DEFAULT_VALUE);
        }
        if (PolicyStatusCode.PROPOSAL.equals(policyStatusCode)) {
            policySummaryDto.setCsvCommencementDate(PENDING);
        }
        if (PolicyStatusCode.CANCELLED.equals(policyStatusCode)) {
            policySummaryDto.setRenewalCalenderDay(null);
        }
    }


    /**
     * Account Type display logic concatenate accounttype and accountsubtype include Pension TTR if available - US17659
     * //BT Panorama Investment or BT Panorama Super
     * //AccountSubType - Joint, SMSF.. etc..
     *
     * @param policySummaryDto - policy detail
     */
    public static String setAccountTypeAttribute(PolicySummaryDto policySummaryDto) {
        String accountType = null;
        String accountPensionType = null;
        if (StringUtils.isNotEmpty(policySummaryDto.getPensionType())) {
            if (policySummaryDto.getPensionType().equalsIgnoreCase(PensionType.TTR.getValue())) {
                accountPensionType = SPACE + "(" + PensionType.TTR.getValue() + ")";
            } else if (policySummaryDto.getPensionType().equalsIgnoreCase(PensionType.TTR_RETIR_PHASE.getValue())) {
                accountPensionType = SPACE + "(" + PensionType.TTR_RETIR_PHASE.getValue() + ")";
            }
        }
        if (StringUtils.isNotEmpty(policySummaryDto.getAccountSubType())) {
            accountType = policySummaryDto.getAccountSubType() + (StringUtils.isNotEmpty(accountPensionType) ? accountPensionType : "");
        }
        return accountType;
    }
}
