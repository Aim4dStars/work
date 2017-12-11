package com.bt.nextgen.api.beneficiary.service;

import com.bt.nextgen.api.beneficiary.builder.BeneficiariesDetailsConverter;
import com.bt.nextgen.api.beneficiary.model.Beneficiary;
import com.bt.nextgen.api.beneficiary.model.BeneficiaryTrxnDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceError;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.beneficiary.BeneficiaryDetailsIntegrationService;
import com.bt.nextgen.service.avaloq.beneficiary.NominationType;
import com.bt.nextgen.service.avaloq.beneficiary.RelationshipType;
import com.bt.nextgen.service.integration.TransactionStatus;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Super beneficiaries - save/update service implementation
 */
@Service
public class SaveBeneficiariesDtoServiceImpl implements SaveBeneficiariesDtoService{

    private static final BigDecimal MAX_PERCENTAGE = new BigDecimal(100);
    @Autowired
    @Qualifier("BeneficiaryDetailsIntegrationServiceImpl")
    private BeneficiaryDetailsIntegrationService beneficiariesDetailsIntegrationService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    /**
     * Submit - save super beneficiaries by invoking save service call
     * @param beneficiaryTrxnDto
     * @param serviceErrors
     * @return
     */
    @Override
    public BeneficiaryTrxnDto submit(BeneficiaryTrxnDto beneficiaryTrxnDto, ServiceErrors serviceErrors) {

        if (validateBeneficiaries(beneficiaryTrxnDto, serviceErrors)) {
            AccountKey accountKey = null;
            BeneficiariesDetailsConverter converter = new BeneficiariesDetailsConverter();
            accountKey = AccountKey.valueOf(EncodedString.toPlainText(beneficiaryTrxnDto.getKey().getAccountId()));
            final WrapAccountDetail account = accountService.loadWrapAccountDetail(accountKey, serviceErrors);
            TransactionStatus transactionStatus = beneficiariesDetailsIntegrationService.saveOrUpdate(converter.toBeneficiaryDetails(beneficiaryTrxnDto, account));

            BeneficiaryTrxnDto response = converter.toBeneficiariesDetailsResponseDto(transactionStatus, beneficiaryTrxnDto);
            return response;
        }
        else{
            return beneficiaryTrxnDto;
        }
    }
        /**
        * validate beneficiaries for errors
                * @param beneficiaryTrxnDto
                * @param serviceErrors
                * @return
        */
        private boolean validateBeneficiaries(BeneficiaryTrxnDto beneficiaryTrxnDto, ServiceErrors serviceErrors) {
            boolean result = true;
            List<ServiceError> errors = new ArrayList<>();
            //AccountId check
            blankStringCheck("accountId", beneficiaryTrxnDto.getKey().getAccountId(), errors);
            BigDecimal totalPercent = BigDecimal.ZERO;

            if(!beneficiaryTrxnDto.getBeneficiaries().isEmpty()){
                for(Beneficiary dto : beneficiaryTrxnDto.getBeneficiaries()) {
                    totalPercent = totalPercent.add(new BigDecimal(dto.getAllocationPercent().replaceAll(",", "")));

                    //blank String check for nominationtype, relationshipType
                    blankStringCheck("nominationType", dto.getNominationType(), errors);
                    blankStringCheck("relationshipType", dto.getRelationshipType(), errors);
                    checkNominationTypeIsValid(dto.getNominationType(), errors);
                    checkRelationshipTypeIsValid(dto.getRelationshipType(), errors);

                    if(!(dto.getRelationshipType().equals(RelationshipType.LPR.getAvaloqInternalId()))) {
                        //blank String check for firstName, dateOfBirth, gender
                        blankStringCheck("firstName", dto.getFirstName(), errors);
                        blankStringCheck("dateOfBirth", dto.getDateOfBirth(), errors);
                        blankStringCheck("gender", dto.getGender(), errors);
                    }
                }
                //Check if total percentage add up to 100
                if(totalPercent.compareTo(MAX_PERCENTAGE) !=0){
                    ServiceErrorImpl serviceError = new ServiceErrorImpl();
                    serviceError.setMessage("Total Percentage is less than 100");
                    serviceError.setId("allocationPercent");
                    errors.add(serviceError);
                }
            }

            if (CollectionUtils.isNotEmpty(errors)) {
                serviceErrors.addErrors(errors);
                result = false;
            }
            return result;
        }

    /**
     * Validation - String null check validation
     * @param fieldname
     * @param input
     * @param errors
     */

    private void blankStringCheck(String fieldname, String input, List <ServiceError> errors) {

        if (!StringUtils.isNotBlank(input)) {
            ServiceErrorImpl serviceError = new ServiceErrorImpl();
            serviceError.setMessage("Invalid " + fieldname);
            serviceError.setId(fieldname);
            errors.add(serviceError);
        }
    }

    private void checkNominationTypeIsValid(String nominationType, List <ServiceError> errors)
    {
        if (!StringUtils.isEmpty(nominationType))
        {
            if (NominationType.findByAvaloqId(nominationType) == null)
            {
                ServiceErrorImpl serviceError = new ServiceErrorImpl();
                serviceError.setMessage("Invalid " + nominationType);
                serviceError.setId(nominationType);
                errors.add(serviceError);
            }
        }

    }

    private void checkRelationshipTypeIsValid(String relationshipType, List <ServiceError> errors)
    {
        if (!StringUtils.isEmpty(relationshipType))
        {
            if (RelationshipType.findByAvaloqId(relationshipType) == null)
            {
                ServiceErrorImpl serviceError = new ServiceErrorImpl();
                serviceError.setMessage("Invalid " + relationshipType);
                serviceError.setId(relationshipType);
                errors.add(serviceError);
            }
        }
    }

}
