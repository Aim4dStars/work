package com.bt.nextgen.api.policy.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.policy.model.ApplicationTrackingDto;
import com.bt.nextgen.api.policy.model.CustomerKey;
import com.bt.nextgen.api.policy.model.PolicyDetailsDto;
import com.bt.nextgen.api.policy.model.PolicyTrackingIdentifier;
import com.bt.nextgen.api.policy.model.PolicyUnderwritingDto;
import com.bt.nextgen.api.policy.model.PolicyUnderwritingNotesDetailsDto;
import com.bt.nextgen.api.policy.model.PolicyUnderwritingNotesDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyApplications;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyStatusCode;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyTracking;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyUnderWritingNotesImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import org.apache.commons.collections.CollectionUtils;
import org.hamcrest.core.IsEqual;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.on;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.IsEqual.equalTo;

public class PolicyTrackingDtoConverter {

    private static final String STATUS_ASSESSMENT_COMPLETE = "Assessment complete";
    private static final String STATUS_UNDER_ASSESSMENT = "Under assessment";
    private static final String STATUS_UNDERWRITING_NOTES_IN_PROGRESS = "IN_PROGRESS";
    private static final String STATUS_UNDERWRITING_NOTES_COMPLETED = "COMPLETED";
    private static final String LOW_DATE = "1800-01-01";

    public static List<PolicyTrackingIdentifier> policyApplicationTrackingDtos(List<PolicyApplications> policyApplicationses) {
        List<PolicyTrackingIdentifier> policyApplications = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(policyApplicationses)) {
            Map<String, List<PolicyApplications>> applicationMap = groupByCustNumber(policyApplicationses);
            for (String customerNumber : applicationMap.keySet()) {
                List<PolicyApplications> applications = applicationMap.get(customerNumber);
                BigDecimal totalAnnualPremium = new BigDecimal(0);
                String givenName = null;
                String lastName = null;
                List<PolicyDetailsDto> policyDetails = new ArrayList<>();
                for (PolicyApplications policyApplication : applications) {
                    if (!PolicyStatusCode.CANCELLED.equals(policyApplication.getPolicyStatus()) &&
                            !PolicyStatusCode.DECLINED.equals(policyApplication.getPolicyStatus())) {
                        totalAnnualPremium = totalAnnualPremium.add(policyApplication.getTotalPremium());
                    }
                    givenName = policyApplication.getInsuredPersonGivenName();
                    lastName = policyApplication.getInsuredPersonLastName();
                    PolicyDetailsDto policyDetailsDto = new PolicyDetailsDto();
                    policyDetailsDto.setPolicyStatus(policyApplication.getPolicyStatus().name());
                    policyDetailsDto.setTotalPremium(policyApplication.getTotalPremium());
                    policyDetailsDto.setInsuranceAdviserId(policyApplication.getInsuranceAdviserId());
                    policyDetailsDto.setApplicationReceivedDate(policyApplication.getApplicationReceivedDate().toString());
                    policyDetails.add(policyDetailsDto);
                }
                ApplicationTrackingDto applicationTrackingDto = new ApplicationTrackingDto();
                CustomerKey customerKey = new CustomerKey(EncodedString.fromPlainText(customerNumber).toString());
                applicationTrackingDto.setKey(customerKey);
                applicationTrackingDto.setPolicyDetails(policyDetails);
                applicationTrackingDto.setTotalPremium(totalAnnualPremium);
                applicationTrackingDto.setInsuredPersonGivenName(givenName);
                applicationTrackingDto.setInsuredPersonLastName(lastName);
                applicationTrackingDto.setApplicationStatus(getStatus(policyDetails));
                applicationTrackingDto.setApplicationReceivedDate(getLowestDate(policyDetails));
                policyApplications.add(applicationTrackingDto);
            }
        }
        return policyApplications;
    }

    private static String getStatus(List<PolicyDetailsDto> policyDetails) {
        return Lambda.exists(policyDetails, Lambda.having(on(PolicyDetailsDto.class).getPolicyStatus(),
                anyOf(equalTo(PolicyStatusCode.PROPOSAL.name()),
                        equalTo(PolicyStatusCode.IN_SUSPENSE.name()))))
                ? STATUS_UNDER_ASSESSMENT : STATUS_ASSESSMENT_COMPLETE;
    }

    private static String getLowestDate(List<PolicyDetailsDto> policyDetails) {
        PolicyDetailsDto detailsDto = Collections.min(policyDetails, new Comparator<PolicyDetailsDto>() {
            @Override
            public int compare(PolicyDetailsDto o1, PolicyDetailsDto o2) {
                return new DateTime(o1.getApplicationReceivedDate()).compareTo(new DateTime(o2.getApplicationReceivedDate()));
            }
        });
        return detailsDto.getApplicationReceivedDate();
    }

    private static Map<String, List<PolicyApplications>> groupByCustNumber(List<PolicyApplications> policyApplicationses) {
        Map<String, List<PolicyApplications>> groupedApplications = new HashMap<>();
        if (CollectionUtils.isNotEmpty(policyApplicationses)) {
            for (PolicyApplications dto : policyApplicationses) {
                String groupingValue = dto.getCustomerNumber();
                List<PolicyApplications> applicationList = groupedApplications.get(groupingValue);
                if (applicationList == null) {
                    applicationList = new ArrayList<>();
                    groupedApplications.put(groupingValue, applicationList);
                }
                applicationList.add(dto);
            }
        }
        return groupedApplications;
    }

    public static PolicyUnderwritingDto getUnderwritingDetails(List<PolicyTracking> policyDetails,
                                                               List<PolicyUnderWritingNotesImpl> policyUnderWritingNotes,
                                                               Map<String, PolicyTracking> policyTrackings,
                                                               Map<AccountKey, WrapAccount> accounts) {
        PolicyUnderwritingDto underwritingDto = new PolicyUnderwritingDto();
        underwritingDto.setPolicyDetails(getPolicyDetailsForUnderwritingNotes(policyDetails, policyTrackings, accounts));
        underwritingDto.setUnderwritingNotesList(getUnderwritingNotesDetails(policyUnderWritingNotes));
        return underwritingDto;
    }

    private static List<PolicyDetailsDto> getPolicyDetailsForUnderwritingNotes(List<PolicyTracking> policyDetails,
                                                                               Map<String, PolicyTracking> policyTrackings,
                                                                               Map<AccountKey, WrapAccount> accounts) {
        List<PolicyDetailsDto> policyDetailsDtos = new ArrayList<>();
        for (PolicyTracking policyTracking : policyDetails) {
            PolicyDetailsDto policyDetailsDto = new PolicyDetailsDto();
            policyDetailsDto.setPolicyNumber(policyTracking.getPolicyNumber());
            policyDetailsDto.setPolicyStatus(policyTracking.getPolicyStatus().name());
            policyDetailsDto.setPolicyType(policyTracking.getPolicyType().getCode());
            PolicyTracking policyTracking1 = policyTrackings.get(policyTracking.getPolicyNumber());
            WrapAccount wrapAccount = Lambda.selectFirst(accounts.values(),
                    Lambda.having(Lambda.on(WrapAccount.class).getAccountNumber(), IsEqual.equalTo(policyTracking1.getAccountNumber())));
            if (wrapAccount != null &&
                    (AccountStatus.ACTIVE.equals(wrapAccount.getAccountStatus()) ||
                            AccountStatus.CLOSE.equals(wrapAccount.getAccountStatus()))) {
                policyDetailsDto.setEncodedAccountId(EncodedString.fromPlainText(wrapAccount.getAccountKey().getId()).toString());
            }
            policyDetailsDtos.add(policyDetailsDto);
        }
        return policyDetailsDtos;
    }

    private static List<PolicyUnderwritingNotesDto> getUnderwritingNotesDetails(List<PolicyUnderWritingNotesImpl> policyUnderWritingNotes) {
        final List<PolicyUnderwritingNotesDto> underwritingNotes = new ArrayList<>();
        final List<PolicyUnderWritingNotesImpl> completedUnderwritingNotes = new ArrayList<>();
        final List<PolicyUnderWritingNotesImpl> inProgressUnderwritingNotes = new ArrayList<>();
        final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");

        for (PolicyUnderWritingNotesImpl policyUnderWritingNote : policyUnderWritingNotes) {
            if (policyUnderWritingNote.getSignOffDate() == null ||
                    LOW_DATE.equals(dateTimeFormatter.print(policyUnderWritingNote.getSignOffDate()))) {
                inProgressUnderwritingNotes.add(policyUnderWritingNote);
            }
            else {
                completedUnderwritingNotes.add(policyUnderWritingNote);
            }
        }
        underwritingNotes.add(getUnderwritingNotesByStatus(completedUnderwritingNotes, STATUS_UNDERWRITING_NOTES_COMPLETED));
        underwritingNotes.add(getUnderwritingNotesByStatus(inProgressUnderwritingNotes, STATUS_UNDERWRITING_NOTES_IN_PROGRESS));
        return underwritingNotes;
    }

    private static PolicyUnderwritingNotesDto getUnderwritingNotesByStatus(List<PolicyUnderWritingNotesImpl> underWritingNotes, String status) {
        PolicyUnderwritingNotesDto underwritingNotesDto = new PolicyUnderwritingNotesDto();
        underwritingNotesDto.setStatus(status);
        underwritingNotesDto.setNotes(getUnderwritingNotesDtos(underWritingNotes));
        return underwritingNotesDto;
    }

    private static List<PolicyUnderwritingNotesDetailsDto> getUnderwritingNotesDtos(List<PolicyUnderWritingNotesImpl> underWritingNotes) {
        List<PolicyUnderwritingNotesDetailsDto> underwritingNotesDetailsDtos = new ArrayList<>();
        for (PolicyUnderWritingNotesImpl underwritingNote : underWritingNotes) {
            PolicyUnderwritingNotesDetailsDto underwritingNotesDto = new PolicyUnderwritingNotesDetailsDto();
            underwritingNotesDto.setDateRequested(underwritingNote.getDateRequested().toString());
            underwritingNotesDto.setAction(underwritingNote.getCodeDescription());
            if (underwritingNote.getSignOffDate() != null) {
                underwritingNotesDto.setDateCompleted(underwritingNote.getSignOffDate().toString());
            }
            underwritingNotesDto.setDetails(underwritingNote.getUnderwritingDetails());
            underwritingNotesDetailsDtos.add(underwritingNotesDto);
        }
        return underwritingNotesDetailsDtos;
    }
}
