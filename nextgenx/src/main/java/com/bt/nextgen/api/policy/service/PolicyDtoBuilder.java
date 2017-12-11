package com.bt.nextgen.api.policy.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.policy.model.BenefitOptionDto;
import com.bt.nextgen.api.policy.model.BenefitsDto;
import com.bt.nextgen.api.policy.model.BenefitsTypeDto;
import com.bt.nextgen.api.policy.model.LifeInsureDto;
import com.bt.nextgen.api.policy.model.Person;
import com.bt.nextgen.api.policy.model.PolicyDocumentDto;
import com.bt.nextgen.api.policy.model.PolicyDto;
import com.bt.nextgen.api.policy.model.PolicyTrackingDto;
import com.bt.nextgen.api.policy.util.PolicyUtil;
import com.bt.nextgen.core.type.ConsistentEncodedString;
import com.bt.nextgen.service.avaloq.basil.DocumentProperties;
import com.bt.nextgen.service.avaloq.basil.DocumentType;
import com.bt.nextgen.service.avaloq.basil.ImageDetails;
import com.bt.nextgen.service.avaloq.insurance.model.BenefitOptionType;
import com.bt.nextgen.service.avaloq.insurance.model.BenefitOptionsImpl;
import com.bt.nextgen.service.avaloq.insurance.model.BenefitType;
import com.bt.nextgen.service.avaloq.insurance.model.BenefitsImpl;
import com.bt.nextgen.service.avaloq.insurance.model.Policy;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyLifeImpl;
import com.bt.nextgen.service.avaloq.insurance.model.PolicySubType;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyTracking;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyType;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.CollectionUtils;
import org.hamcrest.core.IsEqual;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Class to create and populate policy dto various fields and classes
 */
public class PolicyDtoBuilder {

    /**
     * private constructor to hide the implicit public one - sonar check
     */
    private PolicyDtoBuilder() {
    }

    public static void setBenefits(Policy policy, PolicyDto policyDto, AccountStructureType accountStructureType) {
        List<Person> lifeInsureDtos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(policy.getPolicyLifes())) {
            for (PolicyLifeImpl policyLife : policy.getPolicyLifes()) {
                LifeInsureDto lifeInsureDto = new LifeInsureDto();
                lifeInsureDto.setDateOfBirth(PolicyUtil.getDefaultIfNull(policyLife.getDateOfBirth()));
                lifeInsureDto.setGivenName(policyLife.getGivenName());
                lifeInsureDto.setLastName(policyLife.getLastName());
                lifeInsureDto.setSmokingStatus(PolicyUtil.getDefaultIfNull(policyLife.isSmokingStatus()));
                lifeInsureDto.setAddresses(policyLife.getAddresses());
                lifeInsureDto.setPhones(policyLife.getContactNumbers());
                lifeInsureDto.setCity(policyLife.getCity());
                lifeInsureDto.setCountryCode(policyLife.getCountryCode());
                lifeInsureDto.setState(policyLife.getState());
                lifeInsureDto.setPostCode(policyLife.getPostCode());
                lifeInsureDto.setBenefits(getAllBenefits(policyLife, policy, accountStructureType));
                lifeInsureDtos.add(lifeInsureDto);
            }
        }
        policyDto.setPersonBenefitDetails(PolicyUtil.getSortedWithNames(lifeInsureDtos));
    }

    /**
     * Retrieve all benefits for this policy
     *
     * @param policyLife
     * @param policy
     * @param accountStructureType
     *
     * @return
     */
    private static List<BenefitsDto> getAllBenefits(PolicyLifeImpl policyLife, Policy policy, AccountStructureType accountStructureType) {
        final List<BenefitsDto> benefitDtos = new ArrayList<>();

        for (BenefitType benefitType : BenefitType.values()) {
            List<BenefitsImpl> benefitsOfType = Lambda.filter(
                    Lambda.having(Lambda.on(BenefitsImpl.class).getBenefitType(),
                            IsEqual.equalTo(benefitType)), policyLife.getBenefits());

            if (!CollectionUtils.isEmpty(benefitsOfType)) {
                BenefitsDto benefit = new BenefitsDto();
                if (PolicySubType.BUSINESS_OVERHEAD.equals(policy.getPolicySubType())) {
                    benefit.setBenefitType(BenefitType.BUSINESS_OVERHEAD);
                }
                else if (PolicyType.INCOME_PROTECTION_PLUS.equals(policy.getPolicyType())) {
                    benefit.setBenefitType(BenefitType.INCOME_PROTECTION_PLUS);
                }
                else if (PolicyType.KEY_PERSON_INCOME.equals(policy.getPolicyType())) {
                    benefit.setBenefitType(BenefitType.KEY_PERSON_INCOME);
                }
                else {
                    benefit.setBenefitType(benefitType);
                }
                if (BenefitType.INCOME_PROTECTION.equals(benefitType) || BenefitType.SUPER_PLUS_INCOME_PROTECTION.equals(benefitType)) {
                    benefit.setPolicySubType(policy.getPolicySubType());
                    benefit.setBenefitPeriodFactor(PolicyUtil.getDefaultIfNull(policy.getBenefitPeriodFactor()));
                    benefit.setBenefitPeriodTerm(PolicyUtil.getDefaultIfNull(policy.getBenefitPeriodTerm()));
                    benefit.setWaitingPeriod(PolicyUtil.getDefaultIfNull(policy.getWaitingPeriod()));
                }

                benefit.setBenefits(getBenefitDetails(benefitsOfType));
                benefit.setBenefitOptions(getBenefitOptions(benefitType.getOptions(policy, accountStructureType), policyLife.getBenefitOptions()));
                benefitDtos.add(benefit);
            }
        }
        Collections.sort(benefitDtos, new BeanComparator("benefitType"));
        return benefitDtos;
    }

    private static List<BenefitsTypeDto> getBenefitDetails(final List<BenefitsImpl> benefits) {
        final List<BenefitsTypeDto> benefitDtos = new ArrayList<>();
        for (BenefitsImpl benefit : benefits) {
            BenefitsTypeDto typeDto = new BenefitsTypeDto();
            typeDto.setSumInsured(benefit.getSumInsured());
            typeDto.setCommencementDate(PolicyUtil.getDefaultIfNull(benefit.getCommencementDate()));
            typeDto.setTpdDefinition(benefit.getTpdDefinition());
            typeDto.setOccupationClass(PolicyUtil.getDefaultIfNull(benefit.getOccupationClass() != null ? benefit.getOccupationClass().getValue() : null));
            typeDto.setPremiumStructure(benefit.getPremiumType());
            typeDto.setProposedSumInsured(benefit.getProposedSumInsured());
            benefitDtos.add(typeDto);
        }
        Collections.sort(benefitDtos, new BeanComparator("tpdDefinition"));
        return benefitDtos;
    }

    private static List<BenefitOptionDto> getBenefitOptions(final Collection<BenefitOptionType> benefitOptions, final List<BenefitOptionsImpl> existBenefitOptions) {
        List<BenefitOptionDto> benefitOptionDtos = new ArrayList<>();
        for (BenefitOptionType benefitOption : benefitOptions) {
            BenefitOptionDto optionDto = new BenefitOptionDto();
            optionDto.setBenefitOptionType(PolicyUtil.getDefaultIfNull(benefitOption.name()));
            optionDto.setHelpId(benefitOption.getHelpId());
            optionDto.setStatus(benefitOption.isStatus()); //sets the status for those benefits which doesn't comes from service(supercontribution)
            if (existBenefitOptions != null) {
                for (BenefitOptionsImpl existBenefitOption : existBenefitOptions) {
                    if (benefitOption.equals(existBenefitOption.getBenefitOptions())) {
                        optionDto.setStatus(existBenefitOption.getBenefitOptionStatus().isStatus());
                        break;
                    }
                }
            }
            benefitOptionDtos.add(optionDto);
        }
        return benefitOptionDtos;
    }

    public static PolicyTrackingDto policyTrackingDto(final List<PolicyTracking> policyTracking) {
        PolicyTrackingDto trackingDto = new PolicyTrackingDto();
        List<String> fNumbers = Lambda.collect(policyTracking, Lambda.on(PolicyTracking.class).getFNumber());
        Collections.sort(fNumbers);
        trackingDto.setFNumberList(fNumbers);
        return trackingDto;
    }

    public static List<PolicyDocumentDto> getPolicyDocumentDtos(List<ImageDetails> imageDetails) {
        List<PolicyDocumentDto> policyDocumentDtos = new ArrayList<>();
        for (ImageDetails imageDetail : imageDetails) {
            PolicyDocumentDto policyDocumentDto = new PolicyDocumentDto();
            policyDocumentDto.setDocumentId(ConsistentEncodedString.fromPlainText(imageDetail.getDocumentId()).toString());
            policyDocumentDto.setMimeType(imageDetail.getMimeType());
            for (DocumentProperties documentProperty : imageDetail.getDocumentPropertiesList()) {
                if (documentProperty.getDocumentPropertyName() != null) {
                    switch (documentProperty.getDocumentPropertyName()) {
                        case DOCUMENTTYPE:
                            DocumentType documentType = DocumentType.findByCode(documentProperty.getDocumentPropertyStringValue());
                            if (documentType != null) {
                                policyDocumentDto.setDocumentType(documentType.toString());
                            }
                            break;
                        case SPOLICYID:
                            if (!DocumentType.WELCOME_LETTER.toString().equals(policyDocumentDto.getDocumentType()) &&
                                    !DocumentType.RENEWAL_LETTER.toString().equals(policyDocumentDto.getDocumentType())) {
                                policyDocumentDto.setPolicyOrPortfolioId(documentProperty.getDocumentPropertyStringValue());
                            }
                            break;
                        case SPORTFOLIONUMBER:
                            policyDocumentDto.setPolicyOrPortfolioId
                                    (PolicyUtil.getFormattedPortfolioNumber(documentProperty.getDocumentPropertyStringValue()));
                            break;
                        case EFFECTIVEDATE:
                            policyDocumentDto.setEffectiveDate(documentProperty.getDocumentPropertyDateValue().toString());
                            break;
                    }
                }
            }
            policyDocumentDtos.add(policyDocumentDto);
        }
        return policyDocumentDtos;
    }
}
