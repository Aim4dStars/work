package com.bt.nextgen.api.beneficiary.builder;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.beneficiary.model.Beneficiary;
import com.bt.nextgen.api.beneficiary.model.BeneficiaryDto;
import com.bt.nextgen.api.beneficiary.model.RelationshipTypeDto;
import com.bt.nextgen.api.beneficiary.model.SuperNominationTypeDto;
import com.bt.nextgen.core.type.ConsistentEncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.beneficiary.AccountBeneficiaryDetailsResponseImpl;
import com.bt.nextgen.service.avaloq.beneficiary.BeneficiaryDetails;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.Field;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.on;

/**
 * This converter class massages the avaloq object into the UI Dto object which is sent to UI in JSON response.
 * Created by M035995 on 11/07/2016.
 */
public class BeneficiaryDtoConverter {

    private static final Logger logger = LoggerFactory.getLogger(BeneficiaryDtoConverter.class);

    private static final String NOMINATION_TYPE_EXTL_FLD_NAME_PERCENT = "btfg$ui_pct";
    private static final String NOMINATION_TYPE_EXTL_FLD_VALUE_100 = "100";
    private static final Integer PERCENTAGE_SCALE = 2;
    private static final String DATE_FORMAT = "dd MMM yyyy";
    private static final BigDecimal ONE_HUNDRED_PERCENT = new BigDecimal("100.00");


    /**
     * This method massages the avaloq beneficiary details object into the UI dto object.
     *
     * @param accountBeneficiaryDetail - beneficiary details of an account
     *
     * @return List of {@link com.bt.nextgen.api.beneficiary.model.Beneficiary}
     */
    public BeneficiaryDto getBeneficiaryDetails(final AccountBeneficiaryDetailsResponseImpl accountBeneficiaryDetail) {
        BeneficiaryDto beneficiaryDto = new BeneficiaryDto();
        if (accountBeneficiaryDetail != null) {
            AccountKey key = new AccountKey(ConsistentEncodedString.fromPlainText(accountBeneficiaryDetail.getAccountKey().getId()).toString());
            beneficiaryDto.setKey(key);
            beneficiaryDto.setBeneficiariesLastUpdatedTime(accountBeneficiaryDetail.getLastUpdatedDate());
            beneficiaryDto.setAutoReversionaryActivationDate(accountBeneficiaryDetail.getAutoReversionaryActivationDate());
            List<BeneficiaryDetails> beneficiaryDetailsList = accountBeneficiaryDetail.getBeneficiaryDetails();
            final List<Beneficiary> beneficiaryList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(beneficiaryDetailsList)) {
                BigDecimal totalAllocationPercent = BigDecimal.ZERO;
                for (BeneficiaryDetails beneficiaryDetails : beneficiaryDetailsList) {

                    Beneficiary beneficiary = setBeneficiaryObject(beneficiaryDetails);
                    totalAllocationPercent = setAllocationPercent(totalAllocationPercent, beneficiary, beneficiaryDetails);
                    beneficiaryList.add(beneficiary);
                }
                beneficiaryDto.setTotalAllocationPercent(totalAllocationPercent.setScale(PERCENTAGE_SCALE).toString());
            }
            beneficiaryDto.setBeneficiaries(beneficiaryList);
            final int beneficiariesCount = beneficiaryList.size();
            beneficiaryDto.setTotalBeneficiaries(beneficiariesCount == 0 ? null : String.valueOf(beneficiariesCount)); //as per UI code
        }
        return beneficiaryDto;
    }

    private BigDecimal setAllocationPercent(BigDecimal totalAllocationPercent, Beneficiary beneficiary, final BeneficiaryDetails beneficiaryDetails) {
        final BigDecimal allocationPercent = getAllocationPercent(beneficiaryDetails.getNominationType(),
                beneficiaryDetails.getAllocationPercent());
        if (allocationPercent != null) {
            beneficiary.setAllocationPercent(allocationPercent.setScale(PERCENTAGE_SCALE).toString());
            totalAllocationPercent = totalAllocationPercent.add(allocationPercent);
        }
        return totalAllocationPercent;
    }

    private Beneficiary setBeneficiaryObject(final BeneficiaryDetails beneficiaryDetails) {

        final Beneficiary beneficiary = new Beneficiary();
        // The only object which will never be null is Nomination Type
        final Code nominationType = beneficiaryDetails.getNominationType();

        beneficiary.setNominationType(nominationType.getIntlId());
        // Check for nulls for the remaining objects
        beneficiary.setPhoneNumber(beneficiaryDetails.getPhoneNumber());
        beneficiary.setEmail(beneficiaryDetails.getEmail());
        beneficiary.setFirstName(beneficiaryDetails.getFirstName());
        beneficiary.setLastName(beneficiaryDetails.getLastName());
        beneficiary.setGender(beneficiaryDetails.getGender() != null ? beneficiaryDetails.getGender().toString() : null);
        beneficiary.setDateOfBirth(beneficiaryDetails.getDateOfBirth() != null ? beneficiaryDetails.getDateOfBirth().
                toString(DateTimeFormat.forPattern(DATE_FORMAT)) : null);
        beneficiary.setRelationshipType(beneficiaryDetails.getRelationshipType() != null ? beneficiaryDetails.getRelationshipType().getAvaloqInternalId() : null);

        return beneficiary;
    }

    private BigDecimal getAllocationPercent(final Code nominationType, final BigDecimal allocationPercent) {
        final Field percentField = nominationType.getField(NOMINATION_TYPE_EXTL_FLD_NAME_PERCENT);

        // temporary workaround for auto reversionary, which does not have a percentage value in Avaloq
        // This is expected to be modified once the requirement for auto reversionary & non-auto reversionary
        // has been defined (Feb 17 release).
        if (percentField != null && NOMINATION_TYPE_EXTL_FLD_VALUE_100.equals(percentField.getValue())) {
            return ONE_HUNDRED_PERCENT;
        }

        return allocationPercent;
    }

    /**
     * converts the beneficiary domain object list to a list of BeneficiaryDto
     *
     * @param accountBeneficiaryDetails
     * @param serviceErrors
     *
     * @return List<BeneficiaryDto>
     */
    public List<BeneficiaryDto> getBeneficiaryDetails(List<AccountBeneficiaryDetailsResponseImpl> accountBeneficiaryDetails, final StaticIntegrationService staticIntegrationService, ServiceErrors serviceErrors) {

        logger.info("BeneficiaryDtoConverter::getBeneficiaryDetails: method invoked");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        final Map<String, SuperNominationTypeDto> nominationTypeMap = getAllNominationTypes(staticIntegrationService, serviceErrors);
        final Map<String, RelationshipTypeDto> relationshipMap = getAllRelationshipTypes(staticIntegrationService, serviceErrors);
        final List<BeneficiaryDto> beneficiaryDtoList = Lambda.convert(accountBeneficiaryDetails, new Converter<AccountBeneficiaryDetailsResponseImpl, BeneficiaryDto>() {
            public BeneficiaryDto convert(AccountBeneficiaryDetailsResponseImpl accountBeneficiaryDetail) {
                BeneficiaryDto beneficiaryDto = getBeneficiaryDetails(accountBeneficiaryDetail);
                List<Beneficiary> beneficiaries = beneficiaryDto.getBeneficiaries();
                if (CollectionUtils.isNotEmpty(beneficiaries)) {
                    for (Beneficiary beneficiary : beneficiaries) {
                        if (relationshipMap.containsKey(beneficiary.getRelationshipType())) {
                            beneficiary.setRelationshipType(relationshipMap.get(beneficiary.getRelationshipType()).getLabel());
                        }
                        beneficiary.setGender(getGender(staticIntegrationService, beneficiary));
                        if (nominationTypeMap.containsKey(beneficiary.getNominationType())) {
                            beneficiary.setNominationType(nominationTypeMap.get(beneficiary.getNominationType()).getLabel());
                        }
                    }
                }
                return beneficiaryDto;
            }
        });

        stopWatch.stop();
        logger.info("BeneficiaryDtoConverter::getBeneficiaryDetails: complete time taken = {} ms", stopWatch.getTime());
        return beneficiaryDtoList;
    }

    /**
     * Gets the nominationType for all accountsubtype - pension and super
     * returns the map of nomination objects with key intlId
     *
     * @param serviceErrors
     *
     * @return Map<String, SuperNominationTypeDto>
     */
    private Map<String, SuperNominationTypeDto> getAllNominationTypes(StaticIntegrationService staticIntegrationService, ServiceErrors serviceErrors) {
        final SuperNominationTypeDtoConverter dtoConverter = new SuperNominationTypeDtoConverter();
        final List<SuperNominationTypeDto> nominationList = dtoConverter.createNominationTypeList(staticIntegrationService.loadCodes(CodeCategory.SUPER_NOMINATION_TYPE, serviceErrors), null, "false");
        Map<String, SuperNominationTypeDto> nominationTypeMap = new HashMap<>();
        if (nominationList != null && CollectionUtils.isNotEmpty(nominationList)) {
            nominationTypeMap = Lambda.index(nominationList, on(SuperNominationTypeDto.class).getIntlId());
        }
        return nominationTypeMap;
    }

    /**
     * Retrieves a map of all the avaloq relationship (Static code)
     *          To get the label of relationshipType based on its intlId
     *      returns the map of relationshipDto objects with key intlId
     *
     * @param serviceErrors
     * @return Map<String, RelationshipTypeDto>
     */
    private Map<String, RelationshipTypeDto> getAllRelationshipTypes(StaticIntegrationService staticIntegrationService, ServiceErrors serviceErrors) {
        final RelationshipDtoConverter dtoConverter = new RelationshipDtoConverter();
        final List<RelationshipTypeDto> relationshipList = dtoConverter.getRelationshipList(staticIntegrationService.loadCodes(CodeCategory.SUPER_RELATIONSHIP_TYPE, serviceErrors));
        Map<String, RelationshipTypeDto> relationshipMap = new HashMap<>();
        if (relationshipList != null && CollectionUtils.isNotEmpty(relationshipList)) {
            relationshipMap = Lambda.index(relationshipList, on(RelationshipTypeDto.class).getIntlId());
        }
        return relationshipMap;
    }

    /**

    /**
     * Retrieve gender from code using staticservice
     *
     * @param beneficiary
     *
     * @return String
     */
    private String getGender(StaticIntegrationService staticIntegrationService, Beneficiary beneficiary) {
        String gender = null;
        if (beneficiary.getGender() != null) {
            Code code = staticIntegrationService.loadCodeByAvaloqId(CodeCategory.GENDER, beneficiary.getGender(), new ServiceErrorsImpl());
            gender = code != null ? code.getName() : null;
        }
        return gender;
    }
}
