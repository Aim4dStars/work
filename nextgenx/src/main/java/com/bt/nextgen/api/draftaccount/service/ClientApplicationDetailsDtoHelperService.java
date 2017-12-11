package com.bt.nextgen.api.draftaccount.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.client.model.IndividualDto;
import com.bt.nextgen.api.client.model.InvestorDto;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.model.form.IExtendedPersonDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IPlaceOfBirth;
import com.bt.nextgen.core.repository.OnboardingAccount;
import com.bt.nextgen.core.repository.OnboardingAccountRepository;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.draftaccount.repository.ClientApplicationRepository;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.OBApplicationTypeType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by l079353 on 19/08/2016.
 */
@Service
public class ClientApplicationDetailsDtoHelperService {

    @Autowired
    private ClientApplicationRepository clientApplicationRepository;

    @Autowired
    private OnboardingAccountRepository onboardingAccountRepository;

    @Autowired
    private StaticIntegrationService staticService;

    private static final Logger logger = LoggerFactory.getLogger(ClientApplicationDetailsDtoHelperService.class);

    //@Todo This code need to be moved to  StaticIntegrationService
    public String getCodeNameByIntlId(CodeCategory codeCategory, final String intlId, ServiceErrors serviceErrors) {
        Collection<Code> codes = staticService.loadCodes(codeCategory, serviceErrors);
        Code code = Lambda.selectFirst(codes, new LambdaMatcher<Code>() {
            @Override
            protected boolean matchesSafely(Code code) {
                return code.getIntlId().equals(intlId);
            }
        });
        return code.getName();
    }

    public void setPlaceOfBirthDetails(IExtendedPersonDetailsForm investor, InvestorDto investorDto, ServiceErrors serviceErrors) {
        IPlaceOfBirth placeOfBirth = investor.getPlaceOfBirth();
        if (StringUtils.isBlank(((IndividualDto) investorDto).getPlaceOfBirthCountry()) && null != placeOfBirth) {
            Code country = staticService.loadCodeByUserId(CodeCategory.COUNTRY, placeOfBirth.getCountryOfBirth(), serviceErrors);
            ((IndividualDto) investorDto).setPlaceOfBirthCountry(country.getName());
            ((IndividualDto) investorDto).setPlaceOfBirthState(placeOfBirth.getStateOfBirth());
            ((IndividualDto) investorDto).setPlaceOfBirthSuburb(placeOfBirth.getCityOfBirth());
        }
    }


    public String eligibilityCriteria(String eligibilityCriteria, ServiceErrors serviceErrors) {
        if (eligibilityCriteria != null) {
            Code code = staticService.loadCodeByUserId(CodeCategory.PENSION_ELIGIBILITY_CRITERIA, eligibilityCriteria, serviceErrors);
            return code.getName();
        }
        return null;
    }

    public String conditionOfRelease(String conditionRelease, ServiceErrors serviceErrors) {
        if (conditionRelease != null) {
            Code code = staticService.loadCodeByUserId(CodeCategory.PENSION_CONDITION_RELEASE, conditionRelease, serviceErrors);
            return code.getName();
        }
        return null;
    }


    public Map<String,Boolean> getExistingPersonsByCISKey(String accountNumber, Collection<BrokerIdentifier> adviserIds, ServiceErrors serviceErrors){
        ClientApplication clientApplication = null;
        final OnboardingAccount onboardingAccount = onboardingAccountRepository.findByAccountNumber(accountNumber);

        if (onboardingAccount != null && onboardingAccount.getOnboardingApplicationKey() != null) {
            clientApplication = clientApplicationRepository.findByOnboardingApplicationKey(onboardingAccount.getOnboardingApplicationKey(),adviserIds);
            IClientApplicationForm form = clientApplication.getClientApplicationForm();
            IClientApplicationForm.AccountType accountType = form.getAccountType();
            switch(accountType){

                case COMPANY:
                case CORPORATE_SMSF:
                case CORPORATE_TRUST:
                case NEW_CORPORATE_SMSF:
                    logger.info("Fetching CIS Keys for Accounttype :: {}", accountType.name());
                    return getPersonDetailsByCISKey(form.getDirectors());

                case INDIVIDUAL_SMSF:
                case NEW_INDIVIDUAL_SMSF:
                case INDIVIDUAL_TRUST:
                    logger.info("Fetching CIS Keys for Accounttype :: {}", accountType.name());
                     return getPersonDetailsByCISKey(form.getTrustees());

                case INDIVIDUAL:
                case JOINT:
                case SUPER_ACCUMULATION:
                case SUPER_PENSION:
                    logger.info("Fetching CIS Keys for Accounttype ::{}", accountType.name());
                    return getPersonDetailsByCISKey(form.getInvestors());

                default:
                    throw new UnsupportedOperationException("Account type not supported");

            }
        }
        return null;
    }

    private Map<String,Boolean> getPersonDetailsByCISKey(List<IExtendedPersonDetailsForm> personDetails){
        Map<String,Boolean> cisKeysToTaxDetailsMap  = new HashMap();
        List<IExtendedPersonDetailsForm> personDetailsForms = new ArrayList<>();
        personDetailsForms = Lambda.select(personDetails, new LambdaMatcher<IExtendedPersonDetailsForm>() {
            @Override
            protected boolean matchesSafely(IExtendedPersonDetailsForm personDetailsForm) {
                return StringUtils.isNotBlank(personDetailsForm.getCisId());
            }
        });
        if(CollectionUtils.isNotEmpty(personDetailsForms)){
            for(IExtendedPersonDetailsForm personDetailsForm : personDetailsForms){
                logger.info("Populating overseas data for CIS Key :: {}", personDetailsForm.getCisId());
                cisKeysToTaxDetailsMap.put(personDetailsForm.getCisId(),personDetailsForm.getIsOverseasTaxRes());
            }
        }

        return cisKeysToTaxDetailsMap;

    }
}
