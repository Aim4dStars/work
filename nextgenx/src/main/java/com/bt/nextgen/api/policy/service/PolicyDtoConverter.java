package com.bt.nextgen.api.policy.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.lambdaj.Lambda;
import com.btfin.panorama.core.util.StringUtil;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;

import com.bt.nextgen.api.account.v3.model.AccountDto;
import com.bt.nextgen.api.account.v3.util.AccountDtoUtil;
import com.bt.nextgen.api.beneficiary.model.Beneficiary;
import com.bt.nextgen.api.beneficiary.service.BeneficiaryDtoService;
import com.bt.nextgen.api.client.util.ClientAccountUtil;
import com.bt.nextgen.api.policy.model.AccountPolicyDto;
import com.bt.nextgen.api.policy.model.BeneficiaryDto;
import com.bt.nextgen.api.policy.model.CommissionDto;
import com.bt.nextgen.api.policy.model.OwnerDto;
import com.bt.nextgen.api.policy.model.Person;
import com.bt.nextgen.api.policy.model.PolicyDto;
import com.bt.nextgen.api.policy.model.PolicySummaryDto;
import com.bt.nextgen.api.policy.model.PolicyTrackingDto;
import com.bt.nextgen.api.policy.util.PolicyUtil;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.account.PensionAccountDetailImpl;
import com.bt.nextgen.service.avaloq.beneficiary.RelationshipType;
import com.bt.nextgen.service.avaloq.insurance.model.PersonImpl;
import com.bt.nextgen.service.avaloq.insurance.model.Policy;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyStatusCode;
import com.bt.nextgen.service.avaloq.insurance.model.PolicySubType;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyTracking;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyType;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.service.integration.userinformation.ClientKey;

import static ch.lambdaj.Lambda.selectFirst;

public final class PolicyDtoConverter {

    private static final String PAYMENT_METHOD_SUPER = "Panorama Super";
    private static final String PAYMENT_METHOD_INVESTMENT = "Panorama Investments";
    private static final String BENEFICIARY_CACHE_KEY = "CACHE";
    // overriding given names returned by cloas policy server -- due to size limitiation in cloas
    private static final Map<String, String> givenNameMap = new HashMap<>();

    static {
        givenNameMap.put("westpac securities administrat", "Westpac Securities Administration Limited");
    }

    private Map<AccountKey, WrapAccount> accountMap;
    private Map<ProductKey, Product> productMap = null;

    public PolicyDtoConverter(Map<AccountKey, WrapAccount> accountMap) {
        this.accountMap = accountMap;
    }

    public PolicyDtoConverter(Map<AccountKey, WrapAccount> accountMap, Map<ProductKey, Product> productMap) {
        this.accountMap = accountMap;
        this.productMap = productMap;
    }


    public List<AccountDto> getAccountNumbersWithLinkedAccounts(String accountId, Map<ClientKey, Client> clientMap) {
        ClientAccountUtil clientAccountUtil = new ClientAccountUtil(clientMap, accountMap);
        List<AccountDto> linkedAccounts = new ArrayList<>();
        Collection<WrapAccount> allLinkedAccounts = clientAccountUtil.getLinkedAccountsForAccount(accountId);
        for (WrapAccount wrapAccount : allLinkedAccounts) {
            String ownersAccountId = wrapAccount.getAccountKey().getId();
            if (!accountId.equalsIgnoreCase(ownersAccountId)) {
                AccountDto accountDto = new AccountDto(PolicyUtil.getAccountKey(ownersAccountId));
                accountDto.setAccountName(wrapAccount.getAccountName());
                accountDto.setAccountNumber(wrapAccount.getAccountNumber());
                AccountDtoUtil.setAccountTypeAndDescription(accountDto, wrapAccount);
                linkedAccounts.add(accountDto);
            }
        }
        return linkedAccounts;
    }

    public List<AccountPolicyDto> populatePoliciesForAllAccounts(List<AccountDto> accountDtoList, List<Policy> policyList, BeneficiaryDtoService beneficiaryDtoService) {
        List<AccountPolicyDto> accountPolicyDtoList = new ArrayList<>();
        for (AccountDto accountDto : accountDtoList) {
            final String accountId = EncodedString.toPlainText(accountDto.getKey().getAccountId());
            final List<Policy> policyListForAccount = Lambda.select(policyList,
                    Lambda.having(Lambda.on(Policy.class).getAccountId(), Matchers.equalTo(accountId)));
            final List<PolicyDto> policyDtoList = this.toPolicyDto(accountId, policyListForAccount, beneficiaryDtoService);
            if (CollectionUtils.isNotEmpty(policyDtoList)) {
                // Sort the policyDtoList before setting into the accountPolicyDto object
                Collections.sort(policyDtoList, new Comparator<PolicyDto>() {
                    @Override
                    public int compare(PolicyDto o1, PolicyDto o2) {
                        if (o1 == null && o2 == null) {
                            return 0;
                        }
                        if (o1 == null || o2 == null) {
                            return (o1 == null) ? -1 : 1;
                        }

                        int i = o1.getStatus().compareTo(o2.getStatus());
                        if (i == 0) {
                            i = o1.getPolicyType().compareTo(o2.getPolicyType());
                        }
                        return i;
                    }
                });
                final AccountPolicyDto accountPolicyDto = new AccountPolicyDto();
                final com.bt.nextgen.api.account.v3.model.AccountKey accountKey = new com.bt.nextgen.api.account.v3.model.AccountKey(accountDto.getKey().getAccountId());
                accountPolicyDto.setKey(accountKey);
                accountPolicyDto.setAccountName(accountDto.getAccountName());
                accountPolicyDto.setAccountNumber(accountDto.getAccountNumber());
                accountPolicyDto.setAccountType(accountDto.getAccountTypeDescription());
                accountPolicyDto.setPolicyList(policyDtoList);
                accountPolicyDtoList.add(accountPolicyDto);
            }
        }
        return accountPolicyDtoList;
    }


    /**
     * @param unencodedAccountId
     * @param insurancesDetails
     * @param beneficiaryDtoService
     *
     * @return
     */
    public List<PolicyDto> toPolicyDto(String unencodedAccountId, List<Policy> insurancesDetails, BeneficiaryDtoService beneficiaryDtoService) {
        List<PolicyDto> policyDtos = new ArrayList<>();
        final AccountStructureType accountStructureType = getAccountStructureType(unencodedAccountId);
        com.bt.nextgen.api.beneficiary.model.BeneficiaryDto beneficiaryDto = null;
        if (AccountStructureType.SUPER.equals(accountStructureType)) {
            beneficiaryDto = getBeneficiariesDetails(unencodedAccountId, beneficiaryDtoService);
        }
        for (Policy policy : insurancesDetails) {
            if (getAccount(policy.getAccountNumber()) != null) {
                PolicyDto policyDto = new PolicyDto();
                setPolicyDetails(policy, policyDto);
                setCommissionDto(policy, policyDto);
                setPolicyOwnerDetails(policy.getOwners(), policyDto);
                if (PolicyType.TERM_LIFE.equals(policy.getPolicyType()) || PolicyType.TERM_LIFE_AS_SUPER.equals(policy.getPolicyType())) {
                    setPolicyBeneficiaryDetails(accountStructureType, policy.getBeneficiaries(), policyDto, beneficiaryDto);
                }
                PolicyDtoBuilder.setBenefits(policy, policyDto, getAccountStructureType(unencodedAccountId));
                policyDtos.add(policyDto);
            }
        }

        return policyDtos;
    }

    /**
     * Retrieves the beneficiary details for an account
     * service returns a list of object
     *
     * @param unencodedAccountId
     * @param beneficiaryDtoService
     *
     * @return com.bt.nextgen.api.beneficiary.model.BeneficiaryDto
     */
    private com.bt.nextgen.api.beneficiary.model.BeneficiaryDto getBeneficiariesDetails(String unencodedAccountId, BeneficiaryDtoService beneficiaryDtoService) {
        List<com.bt.nextgen.api.beneficiary.model.BeneficiaryDto> beneficiaryDtoList = beneficiaryDtoService != null ?
                beneficiaryDtoService.getBeneficiaryDetails(new com.bt.nextgen.api.account.v3.model.AccountKey(unencodedAccountId),
                        new ServiceErrorsImpl(), BENEFICIARY_CACHE_KEY) : null;
        com.bt.nextgen.api.beneficiary.model.BeneficiaryDto beneficiaryDetails = null;
        if (CollectionUtils.isNotEmpty(beneficiaryDtoList)) {
            beneficiaryDetails = beneficiaryDtoList.get(0);
        }
        return beneficiaryDetails;
    }

    private void setPolicyDetails(Policy policy, PolicyDto policyDto) {
        policyDto.setPolicyNumber(PolicyUtil.getDefaultIfNull(policy.getPolicyNumber()));
        if (PolicySubType.BUSINESS_OVERHEAD.equals(policy.getPolicySubType())) {
            policyDto.setPolicyType(PolicyType.valueOf(policy.getPolicySubType().name()));
            policyDto.setPolicyName(PolicyType.valueOf(policy.getPolicySubType().name()).getDisplayName());
        }
        else {
            policyDto.setPolicyType(policy.getPolicyType());
            policyDto.setPolicyName(policy.getPolicyType().getDisplayName());
        }
        policyDto.setAccountNumber(policy.getAccountNumber());
        policyDto.setPaymentMethod(getPaymentMethod(policy.getAccountNumber()));
        policyDto.setStatus(policy.getStatus());
        policyDto.setPolicyFrequency(PolicyUtil.getDefaultIfNull(policy.getPolicyFrequency()));
        if (PolicyStatusCode.IN_SUSPENSE.equals(policy.getStatus())) {
            policyDto.setInforcePremium(PolicyUtil.getDefaultIfNull(policy.getPremium()));
        }
        policyDto.setPremium(PolicyUtil.setPolicyPremiumValue(policy.getPremium(), policy.getProposedPremium()));
        policyDto.setPortfolioNumber(PolicyUtil.getDefaultIfNull(PolicyUtil.getFormattedPortfolioNumber(policy.getPortfolioNumber())));
        policyDto.setCommencementDate(PolicyUtil.getDefaultIfNull(policy.getCommencementDate()));
        policyDto.setRenewalDate(PolicyUtil.setNextRenewalDate(policy.getCommencementDate(), policy.getRenewalCalendarDay()));
        policyDto.setPaidToDate(PolicyUtil.getDefaultIfNull(policy.getPaidToDate()));
        policyDto.setParentPolicyNumber(policy.getParentPolicyNumber());
    }

    private void setCommissionDto(Policy policy, PolicyDto policyDto) {
        CommissionDto commissionDto = new CommissionDto();
        commissionDto.setCommissionType(PolicyUtil.getDefaultIfNull(policy.getCommissionStructure()));
        commissionDto.setCommissionState(PolicyUtil.getDefaultIfNull(policy.getCommissionState()));
        commissionDto.setDialDown(PolicyUtil.getDefaultIfNull(policy.getDialDown()));
        commissionDto.setRenewalPercent(PolicyUtil.getDefaultIfNull(policy.getRenewalPercent()));
        commissionDto.setCommissionSplit(PolicyUtil.getDefaultIfNull(policy.isSharedPolicy()));
        policyDto.setCommission(commissionDto);
    }

    private void setPolicyOwnerDetails(List<PersonImpl> ownerDetails, PolicyDto policyDto) {
        List<Person> owners = new ArrayList<>();
        for (PersonImpl owner : ownerDetails) {
            if (StringUtil.isNotNullorEmpty(owner.getGivenName())) {
                OwnerDto ownerDto = new OwnerDto();

                String givenName = owner.getGivenName();

                if (givenNameMap.get(StringUtils.lowerCase(givenName)) != null) {
                    givenName = givenNameMap.get(StringUtils.lowerCase(givenName));
                }

                ownerDto.setGivenName(givenName);

                if (!PAYMENT_METHOD_SUPER.equals(policyDto.getPaymentMethod())) {
                    ownerDto.setLastName(owner.getLastName());
                }
                owners.add(ownerDto);
            }
        }

        policyDto.setOwners(PolicyUtil.getSortedWithNames(owners));
    }

    private void setPolicyBeneficiaryDetails(AccountStructureType accountStructureType, List<PersonImpl> beneficiaryDetails,
                                             PolicyDto policyDto, com.bt.nextgen.api.beneficiary.model.BeneficiaryDto superBeneficiaryDto) {
        final List<BeneficiaryDto> beneficiaries = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(beneficiaryDetails) && !AccountStructureType.SUPER.equals(accountStructureType)) {
            for (PersonImpl beneficiary : beneficiaryDetails) {
                BeneficiaryDto beneficiaryDto = new BeneficiaryDto();
                beneficiaryDto.setGivenName(beneficiary.getGivenName());
                beneficiaryDto.setLastName(beneficiary.getLastName());
                beneficiaryDto.setBeneficiaryContribution(beneficiary.getBeneficiaryContribution());
                beneficiaries.add(beneficiaryDto);
            }
        }
        if (superBeneficiaryDto != null) {
            final List<Beneficiary> beneficiaryList = superBeneficiaryDto.getBeneficiaries();
            if (CollectionUtils.isNotEmpty(beneficiaryList)) {
                for (Beneficiary beneficiary : beneficiaryList) {
                    BeneficiaryDto beneficiaryDto = new BeneficiaryDto();
                    //For Legal personal representative firstname and lastname are null, to display 'Legal presonal representative'
                    if (RelationshipType.LPR.getAvaloqInternalId().equalsIgnoreCase(beneficiary.getRelationshipType())) {
                        beneficiaryDto.setGivenName(StringUtil.toProperCase(RelationshipType.LPR.getName()));
                        beneficiaryDto.setLastName(null);
                    }
                    else {
                        beneficiaryDto.setGivenName(beneficiary.getFirstName());
                        beneficiaryDto.setLastName(beneficiary.getLastName());
                    }
                    if (StringUtils.isNotBlank(beneficiary.getAllocationPercent())) {
                        beneficiaryDto.setBeneficiaryContribution(new BigDecimal(beneficiary.getAllocationPercent()));
                    }
                    else {
                        beneficiaryDto.setBeneficiaryContribution(BigDecimal.ZERO);//TODO: verify and confirm if zero is ok or any other value to be set when allocation percent is null or not ?
                    }
                    beneficiaries.add(beneficiaryDto);
                }
            }
        }
        policyDto.setNominatedBenificiaries(PolicyUtil.getSortedWithContribution(beneficiaries));
    }

    private String getPaymentMethod(String accountNumber) {
        WrapAccount wrapAccount = getAccount(accountNumber);
        return wrapAccount != null && isSuper(wrapAccount)
                ? PAYMENT_METHOD_SUPER
                : PAYMENT_METHOD_INVESTMENT;
    }

    private WrapAccount getAccount(String accountNumber) {
        return selectFirst(accountMap.values(),
                Lambda.having(Lambda.on(WrapAccount.class).getAccountNumber(), IsEqual.equalTo(accountNumber)));
    }

    private AccountStructureType getAccountStructureType(String unencodedAccountId) {
        WrapAccount account = accountMap.get(AccountKey.valueOf(unencodedAccountId));
        return (account != null) ? account.getAccountStructureType() : null;
    }

    private boolean isSuper(WrapAccount wrapAccount) {
        return wrapAccount.getAccountStructureType().equals(AccountStructureType.SUPER);
    }

    public List<PolicyTrackingDto> policyTrackingDetailDtos(List<PolicyTracking> policyTracking, boolean displayAccountDetails) {
        List<PolicyTrackingDto> policySummaries = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(policyTracking)) {
            for (PolicyTracking tracking : policyTracking) {
                PolicySummaryDto policySummaryDto = getPolicySummaryDto(tracking, displayAccountDetails);
                // If not insurance tracking page and the account number is not valid
                if (getValidPolicy(policySummaryDto, displayAccountDetails)) {
                    policySummaries.add(policySummaryDto);
                }
            }
        }

        return policySummaries;
    }

    /**
     * @param tracking              -   policies of the adviser returned from service
     * @param displayAccountDetails - true for application tracking screen and false for business report screen
     *                              to display non panorama account details on the application tracking screen
     *                              <p>
     *                              Note:            For business report, only panorama institution name policies are returned from integration layer
     *                              filter logic in InsuranceTrackingResponseHolder class
     *
     * @return
     */
    private PolicySummaryDto getPolicySummaryDto(PolicyTracking tracking, boolean displayAccountDetails) {
        PolicySummaryDto policySummaryDto = new PolicySummaryDto();
        WrapAccount wrapAccount = getAccount(tracking.getAccountNumber());
        setPolicyAttributes(policySummaryDto, tracking, displayAccountDetails);
        if (wrapAccount != null) {
            if (wrapAccount.getAccountStatus().equals(AccountStatus.ACTIVE) || wrapAccount.getAccountStatus().equals(AccountStatus.CLOSE)) {
                policySummaryDto.setEncodedAccountId(EncodedString.fromPlainText(wrapAccount.getAccountKey().getId()).toString());
                policySummaryDto.setAccountName(wrapAccount.getAccountName());
                policySummaryDto.setAccountNumber(wrapAccount.getAccountNumber());
                policySummaryDto.setProductName(getProductName(wrapAccount));
                policySummaryDto.setAccountSubType(isSuper(wrapAccount)
                        ? wrapAccount.getSuperAccountSubType().getAccountType()
                        : wrapAccount.getAccountStructureType().name());
                if (wrapAccount instanceof PensionAccountDetailImpl) {
                    final PensionAccountDetailImpl pensionAccountDetail = (PensionAccountDetailImpl) wrapAccount;
                    policySummaryDto.setPensionType(pensionAccountDetail.getPensionType().getValue());
                }
            }
            else {
                if (displayAccountDetails) {
                    // To display Asgard and Wrap accounts in policy tracking screen, not to be displayed in policy business report(insurance account list)
                    policySummaryDto.setAccountNumber(tracking.getAccountNumber());
                    policySummaryDto.setAccountSubType(StringUtil.toProperCase(tracking.getInstitutionName()));
                }
                else {
                    policySummaryDto.setPolicyNumber(null); //set to null to filter these policies for business report screen
                }
            }
        }
        else if (displayAccountDetails) {
            // To display Asgard and Wrap accounts in policy tracking screen, not to be displayed in policy business report(insurance account list)
            policySummaryDto.setAccountNumber(tracking.getAccountNumber());
            policySummaryDto.setAccountSubType(StringUtil.toProperCase(tracking.getInstitutionName()));
        }
        return policySummaryDto;
    }

    private String getProductName(WrapAccount wrapAccount) {
        String productName = "";
        if (productMap != null && productMap.containsKey(wrapAccount.getProductKey())) {
            productName = productMap.get(wrapAccount.getProductKey()).getProductName();
        }
        return productName;
    }

    private void setPolicyAttributes(PolicySummaryDto policySummaryDto, PolicyTracking tracking, boolean displayAccountDetails) {
        policySummaryDto.setFnumber(tracking.getFNumber()); //Fnumber included for csv
        policySummaryDto.setPolicyNumber(tracking.getPolicyNumber());
        policySummaryDto.setPolicyStatus(tracking.getPolicyStatus().name());
        BigDecimal frequencyMultiplier = new BigDecimal(tracking.getPaymentFrequency().getAnnualFrequency());
        BigDecimal totalPremium = null;

        if (tracking.getPremium() != null && tracking.getProposedPremium() != null) {
            totalPremium = tracking.getPremium().add(tracking.getProposedPremium());
            if (displayAccountDetails) { //total annual premium is displayed for policy tracking screen
                policySummaryDto.setPremium(totalPremium.multiply(frequencyMultiplier));
            }
            else {
                policySummaryDto.setPremium(totalPremium);
            }
        }

        policySummaryDto.setPaymentFrequency(tracking.getPaymentFrequency() != null ? tracking.getPaymentFrequency().name() : null);
        policySummaryDto.setRenewalCommission(tracking.getRenewalCommission());
        policySummaryDto.setRenewalCalenderDay(tracking.getRenewalCalendarDay());
        policySummaryDto.setCommencementDate(tracking.getCommencementDate());
        policySummaryDto.setFundingAccount(tracking.getPaymentType() != null ? tracking.getPaymentType().getValue() : null);

        if (tracking.getPolicySubType() != null &&
                PolicySubType.BUSINESS_OVERHEAD.equals(tracking.getPolicySubType())) {
            policySummaryDto.setPolicyType(PolicyType.valueOf(tracking.getPolicySubType().name()).name());
        }
        else {
            policySummaryDto.setPolicyType(tracking.getPolicyType() != null ? tracking.getPolicyType().name() : null);
        }
    }

    /**
     * Method to filter policies to be displayed/returned based on non availability of critical information
     *
     * @param policySummaryDto      - policy to be validated
     * @param displayAccountDetails - true for application tracking screen and false for business report screen
     *                              to display non panorama account details on the application tracking screen
     *
     * @return
     */
    private boolean getValidPolicy(PolicySummaryDto policySummaryDto, boolean displayAccountDetails) {
        boolean flagValidPolicy = true;

        if (StringUtils.isEmpty(policySummaryDto.getPolicyNumber()) && !displayAccountDetails) {
            flagValidPolicy = false;
        }

        return flagValidPolicy;
    }
}
