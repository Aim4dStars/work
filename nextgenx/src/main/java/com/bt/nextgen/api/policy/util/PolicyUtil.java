package com.bt.nextgen.api.policy.util;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.policy.model.BeneficiaryDto;
import com.bt.nextgen.api.policy.model.Person;
import com.bt.nextgen.api.userpreference.model.UserPreferenceDto;
import com.bt.nextgen.api.userpreference.model.UserPreferenceDtoKey;
import com.bt.nextgen.api.userpreference.model.UserPreferenceEnum;
import com.bt.nextgen.api.userpreference.model.UserTypeEnum;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.avaloq.insurance.model.Policy;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyApplications;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyStatusCode;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyTracking;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyType;
import com.bt.nextgen.service.avaloq.insurance.model.PremiumFrequencyType;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.core.IsEqual;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class PolicyUtil {

    private static final Logger logger = LoggerFactory.getLogger(PolicyUtil.class);
    private static final String DEFAULT_VALUE_NOT_AVAILABLE = "-";

    private PolicyUtil() {
    }

    /**
     * If the commencement date is equal to current date, the nextRenewalDate year should be +1;
     * Otherwise rely on the RenewalDateConverter to set the correct year
     *
     * @param commencementDate Commencement date of policy
     * @param nextRenewalDate  renewal date of policy
     *
     * @return RenewalDate of the policy.
     */
    public static String setNextRenewalDate(DateTime commencementDate, DateTime nextRenewalDate) {
        if (commencementDate != null && nextRenewalDate != null) {
            final DateTime today = new DateTime();
            if (today.toLocalDate().compareTo(commencementDate.toLocalDate()) == 0) {
                nextRenewalDate = nextRenewalDate.withYear(nextRenewalDate.getYear() + 1);
            }
            return nextRenewalDate.toString();
        }
        return DEFAULT_VALUE_NOT_AVAILABLE;
    }


    public static String getDefaultIfNull(Object value) {
        boolean isValid = true;
        if (value != null) {
            if (value instanceof String && StringUtils.isEmpty(value.toString())) {
                isValid = false;
            }
            if (isValid) {
                return value.toString();
            }
        }
        return DEFAULT_VALUE_NOT_AVAILABLE;
    }

    public static String getDefaultIfNull(Enum value) {
        if (value != null) {
            return value.name();
        }
        return DEFAULT_VALUE_NOT_AVAILABLE;
    }

    public static String setPolicyPremiumValue(BigDecimal premium, BigDecimal proposedPremium) {
        if (premium == null && proposedPremium == null) {
            return DEFAULT_VALUE_NOT_AVAILABLE;
        }
        else if (premium == null) {
            return proposedPremium.toString();
        }
        else if (proposedPremium == null) {
            return premium.toString();
        }
        else {
            return (premium.add(proposedPremium)).toString();
        }
    }

    public static AccountKey getAccountKey(String accountId) {
        return new com.bt.nextgen.api.account.v3.model.AccountKey(EncodedString.fromPlainText(accountId).toString());
    }

    public static List<Person> getSortedWithNames(List<Person> names) {
        Collections.sort(names, new Comparator<Person>() {
            @Override
            public int compare(Person o1, Person o2) {
                int c = 0;
                if (o1.getGivenName() != null && o2.getGivenName() != null) {
                    c = o1.getGivenName().toLowerCase().compareTo(o2.getGivenName().toLowerCase());
                }
                if (c == 0 && o1.getLastName() != null && o2.getLastName() != null) {
                    c = o1.getLastName().toLowerCase().compareTo(o2.getLastName().toLowerCase());
                }
                return c;
            }
        });
        return names;
    }

    public static List<BeneficiaryDto> getSortedWithContribution(List<BeneficiaryDto> beneficiaries) {
        Collections.sort(beneficiaries, new Comparator<BeneficiaryDto>() {
            @Override
            public int compare(BeneficiaryDto beneficiary1, BeneficiaryDto beneficiary2) {
                int c = 0;
                if (beneficiary2.getBeneficiaryContribution() != null && beneficiary1.getBeneficiaryContribution() != null) {
                    c = beneficiary2.getBeneficiaryContribution().compareTo(beneficiary1.getBeneficiaryContribution());
                }
                if (c == 0 && beneficiary1.getGivenName() != null && beneficiary2.getGivenName() != null) {
                    c = beneficiary1.getGivenName().toLowerCase().compareTo(beneficiary2.getGivenName().toLowerCase());
                }
                if (c == 0 && beneficiary1.getLastName() != null && beneficiary2.getLastName() != null) {
                    c = beneficiary1.getLastName().toLowerCase().compareTo(beneficiary2.getLastName().toLowerCase());
                }
                return c;
            }
        });
        return beneficiaries;
    }


    public static UserPreferenceDto createDtoToSavePreferences(String valueToSave, UserPreferenceEnum preference) {
        UserPreferenceDtoKey userPreferenceDtoKey = new UserPreferenceDtoKey();
        userPreferenceDtoKey.setUserType(UserTypeEnum.USER.getUserType());
        userPreferenceDtoKey.setPreferenceId(preference.getPreferenceKey());
        UserPreferenceDto userPreferenceDto = new UserPreferenceDto();
        userPreferenceDto.setKey(userPreferenceDtoKey);
        userPreferenceDto.setValue(valueToSave);
        return userPreferenceDto;
    }

    public static UserPreferenceDtoKey getUserPreferenceKey(UserPreferenceEnum preference) {
        UserPreferenceDtoKey userPreferenceDtoKey = new UserPreferenceDtoKey();
        userPreferenceDtoKey.setUserType(UserTypeEnum.USER.getUserType());
        userPreferenceDtoKey.setPreferenceId(preference.getPreferenceKey());
        return userPreferenceDtoKey;
    }

    public static List<PolicyTracking> getCustomerRecentInsurances(String customerNumber,
                                                                   List<PolicyTracking> allCustomerInsurances,
                                                                   List<PolicyApplications> allRecentInsurancesAdviser) {
        List<PolicyTracking> customerRecentInsurances = new ArrayList<>();
        List<PolicyApplications> filteredRecentInsuranceAdviser = Lambda.filter(
                Lambda.having(Lambda.on(PolicyApplications.class).getCustomerNumber(),
                        IsEqual.equalTo(customerNumber)), allRecentInsurancesAdviser);
        for (PolicyApplications customerInsurance : filteredRecentInsuranceAdviser) {
            List<PolicyTracking> customerInsurances = Lambda.filter(
                    Lambda.having(Lambda.on(PolicyTracking.class).getPolicyNumber(),
                            IsEqual.equalTo(customerInsurance.getPolicyNumber())), allCustomerInsurances);
            customerRecentInsurances.addAll(customerInsurances);
        }
        return customerRecentInsurances;
    }

    public static void populatePolicyNumbersAndPortfolioNumbers(List<Policy> polices, List<String> policyNumbers, Set<String> portfolioNumbers) {
        for (Policy policy : polices) {
            policyNumbers.add(policy.getPolicyNumber());
            portfolioNumbers.add(policy.getPortfolioNumber());
        }
    }

    public static List<String> getSortedFNumbers(String fNumber) {
        List<String> fNumbers = new ArrayList<>();
        if (StringUtils.isNotEmpty(fNumber)) {
            fNumbers = Arrays.asList(fNumber.split(","));
            Collections.sort(fNumbers); //Sorting fnumbers used as cache key
            if (fNumbers.size() > 10) {
                throw new IllegalArgumentException("fnumbers cannot be more than 10");
            }
        }
        return fNumbers;
    }

    /**
     * Returns the PolicyType enum equivalent to the string as enum name
     *
     * @param name input string compared with the enum name
     *
     * @return PolicyType enum
     */
    public static PolicyType getPolicyType(String name) {
        PolicyType policyType = null;
        try {
            policyType = PolicyType.valueOf(name);
        }
        catch (IllegalArgumentException e) {
            logger.debug("Policy type enum name not found: {}, errorMessage:{}", name, e);
            policyType = PolicyType.NOT_AVAILABLE;
        }
        return policyType;
    }

    /**
     * Returns the PolicyStatusCode enum equivalent to the string as enum name
     *
     * @param name input string compared with the enum name
     *
     * @return PolicyStatusCode enum
     */
    public static PolicyStatusCode getPolicyStatus(String name) {
        PolicyStatusCode policyStatusCode = null;
        try {
            policyStatusCode = PolicyStatusCode.valueOf(name);
        }
        catch (IllegalArgumentException e) {
            logger.debug("PolicyStatusCode enum name not found: {}, errorMessage:{}", name, e);
            policyStatusCode = PolicyStatusCode.NOT_AVAILABLE;
        }
        return policyStatusCode;
    }

    /**
     * Returns the PremiumFrequencyType enum equivalent to the string as enum name
     *
     * @param name input string compared with the enum name
     *
     * @return PremiumFrequencyType enum
     */
    public static String getPolicyFrequencyLabel(String name) {
        PremiumFrequencyType premiumFrequencyType = null;
        String label = null;
        try {
            premiumFrequencyType = PremiumFrequencyType.valueOf(name);
            label = premiumFrequencyType.getLabel();
        }
        catch (IllegalArgumentException e) {
            logger.debug("PremiumFrequencyType enum name not found: {}, errorMessage:{}", name, e);
            label = DEFAULT_VALUE_NOT_AVAILABLE;
        }
        return label;
    }

    /**
     * Formatting portfolio number as required more details in  Defect# 17560 - adding the hyphen into Portfolio number
     *
     * @param portfolioNumber
     *
     * @return
     */
    public static String getFormattedPortfolioNumber(String portfolioNumber) {
        int portfolioLength = isNotEmpty(portfolioNumber) ? portfolioNumber.length() : 0;
        if (portfolioLength > 1) {
            portfolioNumber = portfolioNumber.substring(0, portfolioLength - 1) + "-" + portfolioNumber.substring(portfolioLength - 1, portfolioLength);
        }
        return portfolioNumber;

    }
}
