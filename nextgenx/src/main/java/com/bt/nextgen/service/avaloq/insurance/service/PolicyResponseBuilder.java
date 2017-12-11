package com.bt.nextgen.service.avaloq.insurance.service;

import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.insurance.model.BenefitOptionStatus;
import com.bt.nextgen.service.avaloq.insurance.model.BenefitOptionsImpl;
import com.bt.nextgen.service.avaloq.insurance.model.BenefitsImpl;
import com.bt.nextgen.service.avaloq.insurance.model.CommissionState;
import com.bt.nextgen.service.avaloq.insurance.model.CommissionStructureType;
import com.bt.nextgen.service.avaloq.insurance.model.OccupationClass;
import com.bt.nextgen.service.avaloq.insurance.model.PersonImpl;
import com.bt.nextgen.service.avaloq.insurance.model.Policy;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyImpl;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyLifeImpl;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyStatusCode;
import com.bt.nextgen.service.avaloq.insurance.model.PolicySubType;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyType;
import com.bt.nextgen.service.avaloq.insurance.model.PremiumFrequencyType;
import com.bt.nextgen.service.avaloq.insurance.model.PremiumType;
import com.bt.nextgen.service.avaloq.insurance.model.TPDBenefitDefinitionCode;
import ns.btfin_com.product.insurance.lifeinsurance.client.v1_2.LIBeneficiaryType;
import ns.btfin_com.product.insurance.lifeinsurance.client.v1_2.LIFullOwnerType;
import ns.btfin_com.product.insurance.lifeinsurance.policy.policyservice.policyreply.v4_2.NoUWPremiumPolicyType;
import ns.btfin_com.product.insurance.lifeinsurance.policy.policyservice.policyreply.v4_2.SearchPolicyByPaymentAccountResponseDetailType;
import ns.btfin_com.product.insurance.lifeinsurance.policy.policyservice.policyreply.v4_2.SearchPolicyByPaymentAccountResponseMsgType;
import ns.btfin_com.product.insurance.lifeinsurance.policy.policyservice.policyreply.v4_2.SearchPolicyByPaymentAccountSuccessResponseType;
import ns.btfin_com.product.insurance.lifeinsuranceservice.policy.v4_2.BenefitOptionType;
import ns.btfin_com.product.insurance.lifeinsuranceservice.policy.v4_2.BenefitPeriodFactorCode;
import ns.btfin_com.product.insurance.lifeinsuranceservice.policy.v4_2.BenefitType;
import ns.btfin_com.product.insurance.lifeinsuranceservice.policy.v4_2.LevelCommissionType;
import ns.btfin_com.product.insurance.lifeinsuranceservice.policy.v4_2.PolicyBasicType;
import ns.btfin_com.product.insurance.lifeinsuranceservice.policy.v4_2.PolicyLifeDetailsType;
import ns.btfin_com.sharedservices.common.address.v2_4.AddressType;
import ns.btfin_com.sharedservices.common.contact.v1_1.ContactType;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

/**
 * Created by M035995 on 20/10/2016.
 */
public class PolicyResponseBuilder {

    private static final String STATUS_ERROR = "Error";

    public List<Policy> getAllPoliciesForAccount(final String accountId, final SearchPolicyByPaymentAccountResponseMsgType policyResponse,
                                                 final ServiceErrors serviceErrors) {
        List<Policy> policyList = null;
        SearchPolicyByPaymentAccountResponseDetailType responseDetails = null;

        if (policyResponse != null && policyResponse.getResponseDetails() != null && isNotEmpty(policyResponse.
                getResponseDetails().getResponseDetail())) {
            responseDetails = policyResponse.getResponseDetails().getResponseDetail().get(0);
        }

        if (responseDetails == null || STATUS_ERROR.equals(policyResponse.getStatus().value())) {
            final ServiceErrorImpl error = new ServiceErrorImpl();
            error.setReason(policyResponse != null ? responseDetails.getErrorResponse().getDescription()
                    : "Error retrieving policy details for an account");
            serviceErrors.addError(error);
            return new ArrayList<Policy>();
        }
        else {
            policyList = populatePolicyDetails(accountId, responseDetails.getSuccessResponse());
        }

        return policyList;
    }

    private List<Policy> populatePolicyDetails(final String accountId,
                                               final SearchPolicyByPaymentAccountSuccessResponseType successResponseType) {

        final List<Policy> policyList = new ArrayList<>();
        final List<NoUWPremiumPolicyType> noUWPremiumPolicyTypeList = successResponseType.getPolicy();
        for (NoUWPremiumPolicyType noUWPremiumPolicyType : noUWPremiumPolicyTypeList) {
            final PolicyBasicType policyBasicType = noUWPremiumPolicyType.getPolicyBasic();

            if (policyBasicType != null) {
                final PolicyImpl policy = new PolicyImpl();
                policy.setAccountId(accountId);
                if (policyBasicType.getPolicyType() != null) {
                    policy.setPolicyType(PolicyType.forValue(policyBasicType.getPolicyType().value()));
                }
                if (policyBasicType.getSubPolicyType() != null) {
                    policy.setPolicySubType(PolicySubType.forValue(policyBasicType.getSubPolicyType().getValue().value()));
                }
                // If policy number is null, system should throw an exception
                policy.setPolicyNumber(policyBasicType.getPolicyNumber().getValue());

                if (policyBasicType.getPremiumFrequency() != null) {
                    policy.setPolicyFrequency(PremiumFrequencyType.forValue(policyBasicType.getPremiumFrequency().value()));
                }

                if (policyBasicType.getPaymentMethod().getWorkingCashAccount().getAccountNumber() != null) {
                    policy.setAccountNumber(policyBasicType.getPaymentMethod().getWorkingCashAccount().getAccountNumber().getValue());
                }

                policy.setPremium(policyBasicType.getTotalPremiumInstalment());
                policy.setProposedPremium(policyBasicType.getProposedTotalPremiumInstalment());

                if (policyBasicType.getPolicyStatus() != null) {
                    policy.setStatus(PolicyStatusCode.forStatus(policyBasicType.getPolicyStatus().value()));
                }

                policy.setOwners(getOwners(noUWPremiumPolicyType.getOwner()));
                policy.setBeneficiaries(getBeneficiaries(noUWPremiumPolicyType.getBeneficiary()));
                if (policyBasicType.getCommissionStructure() != null) {
                    policy.setCommissionStructure(CommissionStructureType.forCode(policyBasicType.getCommissionStructure().value()));
                }

                if (policyBasicType.getPortfolioNumber() != null) {
                    policy.setPortfolioNumber(policyBasicType.getPortfolioNumber().getValue());
                }

                if (policyBasicType.getParentPolicyNumber() != null) {
                    policy.setParentPolicyNumber(policyBasicType.getParentPolicyNumber().getValue());
                }
                policy.setSharedPolicy(policyBasicType.isSharedPolicy());
                policy.setCommencementDate(convertToDateTime(policyBasicType.getRiskCommenceDate()));
                policy.setRenewalCalendarDay(setRenewalDate(policyBasicType.getRenewalCalendarDay()));
                policy.setPaidToDate(convertToDateTime(noUWPremiumPolicyType.getPolicyExtended().getDatePaidTo()));
                // Setting Renewal Percentage
                if (policyBasicType.getLevelCommissions() != null) {
                    final LevelCommissionType commissionType = policyBasicType.getLevelCommissions().getLevelCommission().get(0);
                    policy.setRenewalPercent(commissionType != null ? commissionType.getLevelCommissionPercentage().getRenewalCommission() : null);
                }

                if (policyBasicType.getCommissionState() != null) {
                    policy.setCommissionState(CommissionState.forCode(policyBasicType.getCommissionState().value()));
                }

                policy.setDialDown(noUWPremiumPolicyType.getPolicyExtended().getPolicyDialDown());
                final JAXBElement<BenefitPeriodFactorCode> factorCode = noUWPremiumPolicyType.getPolicyExtended().getBenefitPeriodFactor();
                policy.setBenefitPeriodFactor(factorCode != null ? factorCode.getValue().value() : null);
                policy.setBenefitPeriodTerm(noUWPremiumPolicyType.getPolicyExtended().getBenefitPeriodTerm().toString());
                policy.setWaitingPeriod(noUWPremiumPolicyType.getPolicyExtended().getWaitingPeriodInDays());
                policy.setPolicyLifes(this.getPolicyLifes(noUWPremiumPolicyType.getPolicyLifeDetails()));
                policy.setIPIncomeRatioPercent(policyBasicType.getIPIncomeRatioPercent());

                policyList.add(policy);
            }

        }
        return policyList;

    }

    private DateTime convertToDateTime(final XMLGregorianCalendar date) {
        if (date != null) {
            return new DateTime(date.toGregorianCalendar().getTime());
        }
        return null;
    }

    private DateTime setRenewalDate(final XMLGregorianCalendar date) {
        if (date != null) {
            return new RenewalDateConverter().convert(date.toString());
        }
        return null;
    }

    private List<PersonImpl> getBeneficiaries(final List<LIBeneficiaryType> liBeneficiaryTypeList) {
        final List<PersonImpl> personList = new ArrayList<>();
        for (LIBeneficiaryType liBeneficiaryType : liBeneficiaryTypeList) {
            final PersonImpl person = new PersonImpl();
            person.setGivenName(liBeneficiaryType.getBeneficiary().getPartyDetails().getGivenName());
            person.setLastName(liBeneficiaryType.getBeneficiary().getPartyDetails().getLastName());
            person.setBeneficiaryContribution(liBeneficiaryType.getBeneficiaryPercent());
            personList.add(person);
        }
        return personList;
    }

    private List<PersonImpl> getOwners(final List<LIFullOwnerType> liFullOwnerTypeList) {
        final List<PersonImpl> personList = new ArrayList<>();
        for (LIFullOwnerType liFullOwnerType : liFullOwnerTypeList) {
            final PersonImpl person = new PersonImpl();
            person.setGivenName(liFullOwnerType.getPartyDetails().getGivenName());
            person.setLastName(liFullOwnerType.getPartyDetails().getLastName());
            personList.add(person);
        }
        return personList;
    }

    private List<PolicyLifeImpl> getPolicyLifes(final List<PolicyLifeDetailsType> policyLifeDetailsTypes) {
        final List<PolicyLifeImpl> policyLifeList = new ArrayList<>();

        for (PolicyLifeDetailsType policyLifeDetailsType : policyLifeDetailsTypes) {
            final PolicyLifeImpl policyLife = new PolicyLifeImpl();
            List<String> contactList = new ArrayList<>();
            policyLife.setGivenName(policyLifeDetailsType.getLifeInsuredDetails().getPartyDetails().getGivenName());
            policyLife.setLastName(policyLifeDetailsType.getLifeInsuredDetails().getPartyDetails().getLastName());
            policyLife.setDateOfBirth(convertToDateTime(policyLifeDetailsType.getDateOfBirth()));
            policyLife.setSmokingStatus(policyLifeDetailsType.isSmoker());
            final AddressType addressType = policyLifeDetailsType.getLifeInsuredDetails().getPostalAddresses().getAddress().get(0);
            if (addressType != null) {
                policyLife.setCity(addressType.getAddressDetail().getStructuredAddressDetail().getCity());
                policyLife.setState(addressType.getAddressDetail().getStructuredAddressDetail().getState());
                policyLife.setPostCode(addressType.getAddressDetail().getStructuredAddressDetail().getPostcode());
                policyLife.setCountryCode(addressType.getAddressDetail().getStructuredAddressDetail().getCountryCode());
                policyLife.setAddresses(addressType.getAddressDetail().getStructuredAddressDetail().getAddressTypeDetail().
                        getNonStandardAddress().getAddressLine());
            }

            List<ContactType> contactTypeList = policyLifeDetailsType.getLifeInsuredDetails().getContacts().getContact();
            for (ContactType contactType : contactTypeList) {
                final String contactNumber = contactType.getContactDetail().getContactNumber().getNonStandardContactNumber();
                if (StringUtils.isNotEmpty(contactNumber)) {
                    contactList.add(contactNumber);
                }
            }
            policyLife.setContactNumbers(contactList);
            policyLife.setBenefits(getBenefits(policyLifeDetailsType.getBenefit()));
            policyLife.setBenefitOptions(getBenefitOptions(policyLifeDetailsType.getBenefitOption()));

            policyLifeList.add(policyLife);
        }
        return policyLifeList;
    }


    private List<BenefitsImpl> getBenefits(final List<BenefitType> benefitTypeList) {
        final List<BenefitsImpl> benefitsList = new ArrayList<>();
        for (BenefitType benefitType : benefitTypeList) {
            final BenefitsImpl benefits = new BenefitsImpl();
            // If BenefitType is null, let the system fail and throw an error
            benefits.setBenefitType(com.bt.nextgen.service.avaloq.insurance.model.BenefitType.forValue(benefitType.
                    getBenefitType().value()));
            benefits.setSumInsured(benefitType.getCurrentSumInsured());
            benefits.setCommencementDate(convertToDateTime(benefitType.getRiskCommenceDate()));
            if (benefitType.getTPDBenefitDefinition() != null) {
                benefits.setTpdDefinition(TPDBenefitDefinitionCode.forValue(benefitType.getTPDBenefitDefinition().
                        getValue().value()));
            }
            benefits.setProposedSumInsured(benefitType.getProposedSumInsured());
            if (benefitType.getOccupationClass() != null) {
                benefits.setOccupationClass(OccupationClass.forValue(benefitType.getOccupationClass().value()));
            }
            if (benefitType.getPremiumType() != null) {
                benefits.setPremiumType(PremiumType.forValue(benefitType.getPremiumType().value()));
            }
            benefitsList.add(benefits);
        }
        return benefitsList;
    }

    private List<BenefitOptionsImpl> getBenefitOptions(final List<BenefitOptionType> benefitOptionTypeList) {
        final List<BenefitOptionsImpl> benefitOptionsList = new ArrayList<>();
        for (BenefitOptionType benefitOptionType : benefitOptionTypeList) {
            final BenefitOptionsImpl benefitOptions = new BenefitOptionsImpl();
            if (benefitOptionType.getBenefitOptionType() != null) {
                benefitOptions.setBenefitOptions(com.bt.nextgen.service.avaloq.insurance.model.BenefitOptionType.
                        forValue(benefitOptionType.getBenefitOptionType().value()));
            }
            if (benefitOptionType.getBenefitOptionStatus() != null) {
                benefitOptions.setBenefitOptionStatus(BenefitOptionStatus.forValue(benefitOptionType.
                        getBenefitOptionStatus().value()));
            }

            benefitOptionsList.add(benefitOptions);
        }
        return benefitOptionsList;
    }

}
