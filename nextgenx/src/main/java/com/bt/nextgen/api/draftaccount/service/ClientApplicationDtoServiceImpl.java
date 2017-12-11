package com.bt.nextgen.api.draftaccount.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.client.model.AddressDto;
import com.bt.nextgen.api.client.model.ClientKey;
import com.bt.nextgen.api.client.model.ClientUpdateKey;
import com.bt.nextgen.api.client.model.EmailDto;
import com.bt.nextgen.api.client.model.IndividualDto;
import com.bt.nextgen.api.client.model.PhoneDto;
import com.bt.nextgen.api.client.model.TaxResidenceCountriesDto;
import com.bt.nextgen.api.client.service.CustomerDataDto;
import com.bt.nextgen.api.client.service.DirectInvestorDataDtoService;
import com.bt.nextgen.api.client.service.GlobalCustomerDtoService;
import com.bt.nextgen.api.draftaccount.LoggingConstants;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDto;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationKey;
import com.bt.nextgen.api.draftaccount.model.form.AddressFormFactory;
import com.bt.nextgen.api.draftaccount.model.form.ClientApplicationFormFactory;
import com.bt.nextgen.api.draftaccount.model.form.IAddressForm;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.model.form.IContactValue;
import com.bt.nextgen.api.draftaccount.model.form.IExtendedPersonDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IOverseasTaxDetailsForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.Address;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.OverseasTaxDetails;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.StringValue;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.TinOption;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.TinOptionTypeEnum;
import com.bt.nextgen.api.draftaccount.schemas.v1.customer.Email;
import com.bt.nextgen.api.draftaccount.schemas.v1.customer.Phone;
import com.bt.nextgen.core.api.exception.NotFoundException;
import com.bt.nextgen.core.api.exception.ServiceException;
import com.bt.nextgen.core.repository.OnBoardingApplication;
import com.bt.nextgen.core.repository.OnboardingApplicationStatus;
import com.bt.nextgen.core.service.DateTimeService;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.draftaccount.repository.ClientApplicationStatus;
import com.bt.nextgen.draftaccount.repository.PermittedClientApplicationRepository;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.accountactivation.ApplicationIdentifierImpl;
import com.bt.nextgen.service.integration.accountactivation.AccActivationIntegrationService;
import com.bt.nextgen.service.integration.accountactivation.ApplicationDocument;
import com.bt.nextgen.service.integration.accountactivation.ApplicationIdentifier;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.core.security.avaloq.accountactivation.ApplicationStatus;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.NoResultException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.bt.nextgen.api.client.service.GlobalCustomerDtoServiceImpl.FOREIGN;
import static org.springframework.util.StringUtils.hasText;

@Service
@Transactional("springJpaTransactionManager")
@SuppressWarnings({"squid:ClassCyclomaticComplexity"})
public class ClientApplicationDtoServiceImpl implements ClientApplicationDtoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientApplicationDtoServiceImpl.class);
    @Autowired
    private PermittedClientApplicationRepository clientApplicationRepository;
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private DateTimeService dateTimeService;
    @Autowired
    private BrokerIntegrationService brokerIntegrationService;
    @Autowired
    private AccActivationIntegrationService accActivationIntegrationService;
    @Autowired
    private ClientApplicationDtoConverterService clientApplicationDtoConverterService;
    @Autowired
    private ProductIntegrationService productIntegrationService;
    @Autowired
    private ClientApplicationDtoHelperService clientApplicationDtoHelperService;
    @Autowired
    private GlobalCustomerDtoService globalCustomerDtoService;
    @Autowired
    private StaticIntegrationService staticIntegrationService;
    @Autowired
    private DirectInvestorDataDtoService directInvestorDataDtoService;
    @Autowired
    private FeatureTogglesService featureTogglesService;

    @Autowired
    @Qualifier("jsonObjectMapper")
    private ObjectMapper objectMapper;

    private boolean isRetrieveCrsEnabled() {
        return featureTogglesService.findOne(new FailFastErrorsImpl()).getFeatureToggle("retrieveCrsEnabled");
    }

    @Override
    public ClientApplicationDto create(ClientApplicationDto dto, ServiceErrors serviceErrors) {
        ClientApplication application = createClientApplication(dto, serviceErrors);

        clientApplicationRepository.save(application);

        ClientApplicationDto convertedDto = clientApplicationDtoConverterService.convertToDto(application, serviceErrors);
        clientApplicationDtoHelperService.setBrokerNames(application, convertedDto, serviceErrors);

        return convertedDto;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public List findAll(ServiceErrors serviceErrors) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ClientApplicationDto update(ClientApplicationDto dto, ServiceErrors serviceErrors) {
        try {
            LOGGER.info(LoggingConstants.ONBOARDING_UPDATE + "begin");
            Long clientApplicationKey = dto.getKey().getClientApplicationKey();
            ClientApplication draftAccount = clientApplicationRepository.find(clientApplicationKey);

            String clientApplicationKeyString = "clientApplicationKey=" + clientApplicationKey;
            LOGGER.info(LoggingConstants.ONBOARDING_UPDATE + clientApplicationKeyString);


            clientApplicationDtoHelperService.checkProductIdAndAdviserIdAreAllowedForLoggedInUser(draftAccount.getAdviserPositionId(), draftAccount.getProductId());
            clientApplicationDtoHelperService.checkProductIdAndAdviserIdAreAllowedForLoggedInUser(EncodedString.toPlainText(dto.getAdviserId()),
                    EncodedString.toPlainText(dto.getProductId()));

            if (dto.getStatus() == ClientApplicationStatus.docuploaded) {
                draftAccount.assertCanBeModifiedOffline();
                draftAccount.updateDocUploadedStatus();
            } else {
                draftAccount.assertCanBeModified();
            }

            updateLastModified(draftAccount, serviceErrors);
            draftAccount.setFormData(dto.getFormData());
            final ClientApplicationDto convertedDto = clientApplicationDtoConverterService.convertToDto(draftAccount, serviceErrors);
            clientApplicationDtoHelperService.setBrokerNames(draftAccount, convertedDto, serviceErrors);
            return convertedDto;
        } catch (NoResultException ex) {
            // The contract of this method appears to be to return null so that the wrapping Update operation will
            // return NotFoundException. Questionable.
            LOGGER.info(LoggingConstants.ONBOARDING_UPDATE, ex);
            return null;
        } finally {
            LOGGER.info(LoggingConstants.ONBOARDING_UPDATE + "end");
        }
    }

    private ClientApplication createClientApplication(ClientApplicationDto clientApplicationDto, ServiceErrors serviceErrors) {
        LOGGER.info(LoggingConstants.ONBOARDING_CREATE + "begin");
        ClientApplication clientApplication = new ClientApplication();
        clientApplication.setFormData(clientApplicationDto.getFormData());

        String adviserIdString = EncodedString.toPlainText(clientApplicationDto.getAdviserId());
        String productIdString = EncodedString.toPlainText(clientApplicationDto.getProductId());
        clientApplicationDtoHelperService.checkProductIdAndAdviserIdAreAllowedForLoggedInUser(adviserIdString, productIdString);

        clientApplication.setAdviserPositionId(adviserIdString);
        clientApplication.setProductId(productIdString);

        LOGGER.info(LoggingConstants.ONBOARDING_CREATE + "adviserIdString=" + adviserIdString + ",productIdString=" + productIdString);
        updateLastModified(clientApplication, serviceErrors);
        LOGGER.info(LoggingConstants.ONBOARDING_CREATE + "end");
        return clientApplication;
    }


    @Override
    public void delete(ClientApplicationKey key, ServiceErrors serviceErrors) {
        try {
            LOGGER.info(LoggingConstants.ONBOARDING_DELETE + "begin");
            Long clientApplicationKey = key.getClientApplicationKey();
            LOGGER.info(LoggingConstants.ONBOARDING_DELETE + "clientApplicationKey=" + clientApplicationKey);
            ClientApplication clientApplication = clientApplicationRepository.find(clientApplicationKey);
            assertCanApplicationBeDeleted(clientApplication, serviceErrors);
            clientApplication.markDeleted();
            updateLastModified(clientApplication, serviceErrors);
        } catch (NoResultException ex) {
            // Sadly we do not know the version of the API
            LOGGER.info(LoggingConstants.ONBOARDING_DELETE, ex);
            throw new NotFoundException("unknown");
        } finally {
            LOGGER.info(LoggingConstants.ONBOARDING_DELETE + "end");
        }
    }
    @Override
    public ClientApplicationDto find(ClientApplicationKey key, ServiceErrors serviceErrors) {
        try {
            ClientApplication draftAccount = clientApplicationRepository.find(key.getClientApplicationKey());
            ClientApplicationDto clientApplicationDto = clientApplicationDtoConverterService.convertToDto(draftAccount, serviceErrors);
            IClientApplicationForm clientApplicationForm = ClientApplicationFormFactory.getNewClientApplicationForm(clientApplicationDto.getFormData());
            final List<? extends IExtendedPersonDetailsForm> investorsList = getInvestors(clientApplicationForm);

            for(IExtendedPersonDetailsForm form : investorsList){
                updateInvestorWithGCMData(form, serviceErrors);
            }

            return clientApplicationDto;
        } catch (NoResultException ex) {
            return null;
        }
    }


    @SuppressWarnings({ "squid:MethodCyclomaticComplexity", "squid:S1142" })
    private  List<IExtendedPersonDetailsForm> getInvestors(IClientApplicationForm form) {
        switch (form.getAccountType()) {
            case INDIVIDUAL:
            case SUPER_ACCUMULATION:
            case SUPER_PENSION:
            case JOINT:
                return form.getInvestors();
            case CORPORATE_SMSF:
            case NEW_CORPORATE_SMSF:
            case CORPORATE_TRUST:
                return form.getDirectors();
            case INDIVIDUAL_SMSF:
            case NEW_INDIVIDUAL_SMSF:
            case INDIVIDUAL_TRUST:
                return form.getTrustees();
            case COMPANY:
                return form.getDirectorsSecretariesSignatories();
            default:
                throw new UnsupportedOperationException("Don't know how to get investors type for : " +form.getAccountType());
        }
    }

    private void updateInvestorWithGCMData(IExtendedPersonDetailsForm form, ServiceErrors serviceErrors){
        if(hasText(form.getCisId())) {
            IndividualDto individualDto = (IndividualDto) globalCustomerDtoService.find(new ClientKey(form.getCisId()), serviceErrors);;
            if (null!=individualDto && hasText(form.getCisId()) && !hasText(form.getPanoramaNumber())) {
                //update UI form in database with latest GCM data
                if (individualDto.isRegistered()) {
                    updateContactDetatilsFromAvaloq(individualDto, form, serviceErrors);
                    if (isRetrieveCrsEnabled()) {
                        setOverseasTaxDetailsFromGCM(form, individualDto.getCisId(), serviceErrors);
                        updatePersonDetails(individualDto, form);
                        updateAddressFromAvaloq(individualDto, form);
                    }
                } else {
                    updateForm(individualDto, form, serviceErrors);
                }
            } else if (null!=individualDto && isRetrieveCrsEnabled() && hasText(form.getPanoramaNumber())) {
                updateContactDetatilsFromAvaloq(individualDto, form, serviceErrors);
                updatePersonDetails(individualDto, form);
                updateAddressFromAvaloq(individualDto, form);
                setOverseasTaxDetailsFromGCM(form, form.getCisId(), serviceErrors);
            }
        }

    }

    private void updatePersonDetails(IndividualDto individualDto,IExtendedPersonDetailsForm form) {
        form.setFirstName(individualDto.getFirstName());
        form.setLastName(individualDto.getLastName());
        form.setPreferredName(individualDto.getPreferredName());
        form.setMiddleName(individualDto.getMiddleName());
        form.setDateOfBirth(individualDto.getDateOfBirth());
        form.setTitle(individualDto.getTitle());
        form.setGender(individualDto.getGender());
    }

    private void updateTitle(IExtendedPersonDetailsForm form, String newTitle) {
        if ((form.getTitle() != null && !form.getTitle().equalsIgnoreCase(newTitle)) || (form.getTitle() == null && newTitle != null)) {
            onChangeGCMRecord(form, true);
            form.setTitle(newTitle);
        }
    }

    private void updateFirstName(IExtendedPersonDetailsForm form, String newFirstName) {
        if ((form.getFirstName() != null && !form.getFirstName().equalsIgnoreCase(newFirstName)) || (form.getFirstName() == null && newFirstName != null)) {
            onChangeGCMRecord(form, true);
            form.setFirstName(newFirstName);
        }
    }

    private void updateMiddleName(IExtendedPersonDetailsForm form, String newMiddleName) {
        if ((form.getMiddleName() != null && !form.getMiddleName().equalsIgnoreCase(newMiddleName)) || (form.getMiddleName() == null && newMiddleName != null)) {
            onChangeGCMRecord(form, true);
            form.setMiddleName(newMiddleName);
        }
    }

    private void updateLastName(IExtendedPersonDetailsForm form, String newLastName) {
        if ((form.getLastName() != null && !form.getLastName().equalsIgnoreCase(newLastName)) || (form.getLastName() == null && newLastName != null)) {
            onChangeGCMRecord(form, true);
            form.setLastName(newLastName);
        }
    }

    private void updatePrefferedName(IExtendedPersonDetailsForm form, String newPrefferedName) {
        if ((form.getPreferredName() != null && !form.getPreferredName().equalsIgnoreCase(newPrefferedName)) || (form.getPreferredName() == null && newPrefferedName != null)) {
            onChangeGCMRecord(form, true);
            form.setPreferredName(newPrefferedName);
        }
    }

    private void updateDateOfBirth(IExtendedPersonDetailsForm form, String newDateOfBirth) {
        if ((form.getDateOfBirth() != null && !form.getDateOfBirth().equalsIgnoreCase(newDateOfBirth)) || (form.getDateOfBirth() == null && newDateOfBirth != null)) {
            onChangeGCMRecord(form, true);
            form.setDateOfBirth(newDateOfBirth);
        }
    }


    private void updateIsForeignRegistered(IExtendedPersonDetailsForm form, String isForeignRegistered) {
        if (form.getIsForeignRegistered() != null && !form.getIsForeignRegistered().equals(isForeignRegistered)) {
            onChangeGCMRecord(form, true);
            form.setIsForeignRegistered(isForeignRegistered);
        }
    }

    private void updateContactDetatilsFromAvaloq(IndividualDto individualDto, IExtendedPersonDetailsForm form, ServiceErrors serviceErrors) {

        try {
            LOGGER.info(LoggingConstants.ONBOARDING_UPDATE + "Existing Investor GCM ID" + individualDto.getGcmId());
            form.setClientKey(individualDto.getKey().getClientId());
            form.setRegistered(individualDto.isRegistered());
            form.setPanoramanumber(individualDto.getGcmId());
            if (form.hasJsonSchema()) {
                form.setPhones(convertDtoListToListPhone(individualDto.getPhones()));
                form.setEmails(convertDtoListToListEmail(individualDto.getEmails()));
            } else {
                form.setPhones(convertDtoListToListMap(individualDto.getPhones()));
                form.setEmails(convertDtoListToListMap(individualDto.getEmails()));
            }

        } catch (IOException ex) {
            LOGGER.error(LoggingConstants.ONBOARDING + "::Error updating person details from Avaloq ", ex);
            throw new ServiceException("unknown", serviceErrors, ex);
        }

    }

    /**
     * This method updates tax details from GCM to the form.
     * @param form
     * @param cisId
     * @param serviceErrors
     */
    private  void setOverseasTaxDetailsFromGCM(IExtendedPersonDetailsForm form, String cisId, ServiceErrors serviceErrors){
        ClientUpdateKey clientUpdateKey = new ClientUpdateKey("","tax_details", cisId,"INDIVIDUAL");
        CustomerDataDto customerDataDto = directInvestorDataDtoService.find(clientUpdateKey,serviceErrors);
        setForeignRegistered(form, customerDataDto.getTaxResidenceCountries());
        setTaxDetails(form, customerDataDto.getTaxResidenceCountries(),serviceErrors);
    }

    private void setForeignRegistered(IExtendedPersonDetailsForm form, List<TaxResidenceCountriesDto> taxResidenceCountriesDtos) {
        for(TaxResidenceCountriesDto taxResidenceCountriesDto : taxResidenceCountriesDtos) {
            //For "Overseas resident for tax purposes" , there will be row created  with taxResidencyCountry as 'Foreign' with tin value 'Y' as registered and 'N' as not registered.
            if (FOREIGN.equalsIgnoreCase(taxResidenceCountriesDto.getTaxResidenceCountry())) {
                if("Y".equalsIgnoreCase(taxResidenceCountriesDto.getTin())){
                    form.setIsForeignRegistered("Y");
                    form.setIsOverseasTaxRes(true);
                }
                else if("N".equalsIgnoreCase(taxResidenceCountriesDto.getTin())){
                    form.setIsForeignRegistered("N");
                    form.setIsOverseasTaxRes(false);
                }
            }
        }
    }

    private void updateForm(IndividualDto individualDto, IExtendedPersonDetailsForm form, ServiceErrors serviceErrors) {
        updateTitle(form, individualDto.getTitle());
        updateFirstName(form, individualDto.getFirstName());
        updateMiddleName(form, individualDto.getMiddleName());
        updateLastName(form, individualDto.getLastName());
        updatePrefferedName(form, individualDto.getPreferredName());
        updateDateOfBirth(form, individualDto.getDateOfBirth());

        updateCrsTaxDetails(individualDto, form, serviceErrors);

        if (form.hasIdVerified() && (form.isIdVerified() != individualDto.isIdVerified())) {
            onChangeGCMRecord(form, true);
            form.setIdVerified(individualDto.isIdVerified());
        } else if (!form.hasIdVerified()) {
            form.setIdVerified(individualDto.isIdVerified());
        }

        if ((form.getGenderAsString() != null && !form.getGenderAsString().equalsIgnoreCase(individualDto.getGender()))
                || (form.getGenderAsString() == null && individualDto.getGender() != null)){
            onChangeGCMRecord(form, true);
            form.setGender(individualDto.getGender());
        }
        try {
            setAddresses(individualDto, form);
            setPhones(individualDto, form);
            setEmails(individualDto, form);
            setPreferredContact(individualDto, form);
        } catch (IOException ex){
            LOGGER.error(LoggingConstants.ONBOARDING + "::Error updating person details ", ex);
            throw new ServiceException("unknown", serviceErrors, ex);
        }
    }

    private void updateCrsTaxDetails(IndividualDto individualDto, IExtendedPersonDetailsForm form, ServiceErrors serviceErrors) {
        if(form.hasCrsTaxDetails()) {
            updateIsForeignRegistered(form, individualDto.getIsForeignRegistered());
            updateTaxResidenceCountries(form, individualDto.getTaxResidenceCountries(), serviceErrors);
        }
    }

    private void updateTaxResidenceCountries(IExtendedPersonDetailsForm form, final List<TaxResidenceCountriesDto> taxResidenceCountries, ServiceErrors serviceErrors) {
        List<IOverseasTaxDetailsForm> overseasTaxDetailsForm = form.getOverseasTaxDetails();
        if(taxResidenceCountries != null && hasTaxDetailsChanged(taxResidenceCountries, overseasTaxDetailsForm, serviceErrors)){
            onChangeGCMRecord(form, true);
            setTaxDetails(form, taxResidenceCountries, serviceErrors);
            updateSelectedOverseasCountry(form);
        } else if(taxResidenceCountries == null){
            form.setOverseasTaxDetails(new ArrayList<OverseasTaxDetails>());
            updateSelectedOverseasCountry(form);
        }
    }

    private void updateSelectedOverseasCountry(IExtendedPersonDetailsForm form) {
        if(form.hasOverseasTaxCountry()) {
            final String overseasCountry = form.getOverseasTaxCountry();

            List<IOverseasTaxDetailsForm> overseasTaxDetailsList = form.getOverseasTaxDetails();

            if(CollectionUtils.isNotEmpty(overseasTaxDetailsList)) {
                Object overseasTaxDetails = Lambda.selectFirst(overseasTaxDetailsList, new LambdaMatcher<IOverseasTaxDetailsForm>() {
                    @Override
                    protected boolean matchesSafely(IOverseasTaxDetailsForm iOverseasTaxDetailsForm) {
                        return iOverseasTaxDetailsForm.getOverseasTaxCountry().equals(overseasCountry);
                    }
                });

                if (overseasTaxDetails == null) {
                    form.setOverseasTaxCountry(null);
                }
            }
        }
    }

    private void setTaxDetails(IExtendedPersonDetailsForm form, List<TaxResidenceCountriesDto> taxResidenceCountries, ServiceErrors serviceErrors) {
        List<OverseasTaxDetails> overseasTaxDetailsList = new ArrayList<>();
        for(TaxResidenceCountriesDto dto: taxResidenceCountries){
            //If TaxResidenceCountries are retreived using DirectInvestorDtoServiceImpl, it can have a country with "Foreign"
            if (!FOREIGN.equalsIgnoreCase(dto.getTaxResidenceCountry())) {
                OverseasTaxDetails overseasTaxDetails = new OverseasTaxDetails();
                TinOptionTypeEnum tinOptionTypeEnum = null;

                Code code = staticIntegrationService.loadCodeByAvaloqId(CodeCategory.COUNTRY, dto.getTaxResidenceCountry(), serviceErrors);
                overseasTaxDetails.setOverseasTaxCountry(getStringValue(code != null ? code.getUserId() : dto.getTaxResidenceCountry()));

                if (!StringUtils.isEmpty(dto.getTaxExemptionReason())) {
                    overseasTaxDetails.setTinExemptionReason(getStringValue(dto.getTaxExemptionReason()));
                    tinOptionTypeEnum = TinOptionTypeEnum.EXEMPTION_REASON_PROVIDED;
                } else if (!StringUtils.isEmpty(dto.getTin())) {
                    overseasTaxDetails.setTin(getStringValue(dto.getTin()));
                    tinOptionTypeEnum = TinOptionTypeEnum.TIN_PROVIDED;
                }

                TinOption tinOption = new TinOption();
                tinOption.setValue(tinOptionTypeEnum);
                overseasTaxDetails.setTinOption(tinOption);
                overseasTaxDetailsList.add(overseasTaxDetails);
            }
        }
        form.setOverseasTaxDetails(overseasTaxDetailsList);
    }

    private StringValue getStringValue(String value) {
        StringValue stringValue = new StringValue();
        stringValue.setValue(value);
        stringValue.setValid(true);
        return stringValue;
    }

    private boolean hasTaxDetailsChanged(List<TaxResidenceCountriesDto> taxResidenceCountries, List<IOverseasTaxDetailsForm> overseasTaxDetailsForm, ServiceErrors serviceErrors) {
        if (overseasTaxDetailsForm != null && taxResidenceCountries != null) {
            if (overseasTaxDetailsForm.size() != taxResidenceCountries.size()) {
                return true;
            } else {
                for (IOverseasTaxDetailsForm detailsForm : overseasTaxDetailsForm) {
                    if (!listHasOverseasTaxCountries(taxResidenceCountries, detailsForm, serviceErrors)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean listHasOverseasTaxCountries(List<TaxResidenceCountriesDto> taxResidenceCountries, IOverseasTaxDetailsForm detailsForm, ServiceErrors serviceErrors) {
        boolean taxCountryDetailsFound = false;
        for (TaxResidenceCountriesDto dto : taxResidenceCountries) {
            if (isTaxExemptionReasonEqual(detailsForm, dto)
                    && isTinEqual(detailsForm, dto)
                    && isTaxCountryEqual(detailsForm, dto, serviceErrors) ) {
                taxCountryDetailsFound = true;
                break;
            }
        }
        return taxCountryDetailsFound;
    }

    private boolean isTinEqual(IOverseasTaxDetailsForm detailsForm, TaxResidenceCountriesDto dto) {
        String dtoTin = dto.getTin();
        String detailsFormTIN = detailsForm.getTIN();
        if(dtoTin != null && detailsFormTIN != null && dtoTin.equals(detailsFormTIN)) {
            return true;
        }
        return dtoTin == null && detailsFormTIN == null;
    }

    private boolean isTaxExemptionReasonEqual(IOverseasTaxDetailsForm detailsForm, TaxResidenceCountriesDto dto) {
        String dtoTaxExemptionReason = dto.getTaxExemptionReason();
        String formTaxExemptionReason = detailsForm.getTINExemptionReason();
        if(formTaxExemptionReason != null && dtoTaxExemptionReason != null && formTaxExemptionReason.equals(dtoTaxExemptionReason)){
            return true;
        }
        return formTaxExemptionReason == null && dtoTaxExemptionReason == null;
    }

    // Need to do this cause during GCM retrieve we get static code intlId and while saving and submitting we user userId
    private boolean isTaxCountryEqual(IOverseasTaxDetailsForm detailsForm, TaxResidenceCountriesDto dto, ServiceErrors serviceErrors) {
        Code code = staticIntegrationService.loadCodeByUserId(CodeCategory.COUNTRY, detailsForm.getOverseasTaxCountry(), serviceErrors);
        return code != null && dto.getTaxResidenceCountry().equals(code.getIntlId());
    }

    private void setPreferredContact(IndividualDto individualDto, IExtendedPersonDetailsForm form) {
        //only if we have previously selected a preferred contact
        if (!StringUtils.isEmpty(form.getPreferredContact())) {
            final IContactValue prefContactValue = form.getContactValue(form.getPreferredContact());
            if (prefContactValue.isNull()) {
                form.removePreferredContact();
                form.removeContactDetails();
            } else {
                //check if preferred contact value exists in new list of phones & emails
                final boolean emailFound = listHasEmail(individualDto.getEmails(), prefContactValue);
                final boolean phoneFound = listHasPhoneNumber(individualDto.getPhones(), prefContactValue);
                if (!(emailFound || phoneFound)) {
                    //must remove the preferred contact and set updated flag to true
                    form.removeContactValue(form.getPreferredContact());
                    form.removePreferredContact();
                    form.removeContactDetails();
                    onChangeGCMRecord(form, true);
                }
            }
        }
    }

    private void setEmails(IndividualDto individualDto, IExtendedPersonDetailsForm form) throws IOException {
        if (CollectionUtils.isNotEmpty(individualDto.getEmails()) && null!=form.getEmail() && !form.getEmail().isNull()) {
            final boolean hasEmail = listHasEmail(individualDto.getEmails(), form.getEmail());
            form.setGcmUpdated(!hasEmail);
            if (!hasEmail) {
                form.removeEmail();
            }
        }
        if (form.hasJsonSchema()) {
            form.setEmails(convertDtoListToListEmail(individualDto.getEmails()));
        } else {
            form.setEmails(convertDtoListToListMap(individualDto.getEmails()));
        }
    }

    private boolean listHasEmail(List<EmailDto> list, IContactValue email) {
        for(EmailDto emailDto: list) {
            if (emailDto.getEmail().equalsIgnoreCase(email.getValue())) {
                return true;
            }
        }
        return false;
    }

    private void setPhones(IndividualDto individualDto, IExtendedPersonDetailsForm form) throws IOException {
        //check if mobile number has changed
        if (CollectionUtils.isNotEmpty(individualDto.getPhones()) && null!=form.getMobile() && !form.getMobile().isNull()) {
            final boolean hasPhone = listHasPhoneNumber(individualDto.getPhones(), form.getMobile());
            form.setGcmUpdated(!hasPhone);
            if (!hasPhone) {
                form.removeMobile();
            }
        }
        if (form.hasJsonSchema()) {
            form.setPhones(convertDtoListToListPhone(individualDto.getPhones()));
        } else {
            form.setPhones(convertDtoListToListMap(individualDto.getPhones()));
        }
    }


    private boolean samePhoneNumber(IContactValue contact, PhoneDto phoneDto) {
        return contact.getCountryCode().equals(phoneDto.getCountryCode()) &&
                contact.getAreaCode().equals(phoneDto.getAreaCode()) && contact.getValue().equals(phoneDto.getNumber());
    }

    private boolean sameMobileNumber(IContactValue contact, PhoneDto phoneDto) {
        String areaCode=StringUtils.isEmpty(phoneDto.getAreaCode())? "" :phoneDto.getAreaCode() ;
        String phoneNumber=StringUtils.isEmpty(phoneDto.getNumber())?  "" : phoneDto.getNumber();
        String countryCode=StringUtils.isEmpty(phoneDto.getCountryCode())? "" : phoneDto.getCountryCode();
        return contact.getValue().equals(areaCode.concat(phoneNumber))
                || contact.getValue().equals(countryCode.concat(areaCode).concat(phoneNumber));
    }

    private boolean listHasPhoneNumber(List<PhoneDto> list, IContactValue contact) {
        for(PhoneDto phoneDto: list) {
            //TODO: what can be done about 'mobile' field so that it comes with 'countryCode' and 'areaCode' and not as a concatenated String value only
            if (contact.getCountryCode() != null && contact.getAreaCode() != null) {
                if (samePhoneNumber(contact, phoneDto)) {
                    return true;
                }
            } else {
                if (sameMobileNumber(contact, phoneDto)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void onChangeGCMRecord(IExtendedPersonDetailsForm form, Object obj) {
        if (!form.isGcmUpdated() && obj != null) {
            if (obj instanceof Collection) {
                form.setGcmUpdated(CollectionUtils.isNotEmpty((Collection) obj));
            } else {
                form.setGcmUpdated(true);
            }
        }
    }

    private boolean hasSameValue(IAddressForm resAddress, AddressDto dtoAddress, String propertyName) {
        PropertyAccessor resAddressAccessor = PropertyAccessorFactory.forBeanPropertyAccess(resAddress);
        PropertyAccessor dtoAddressAccessor = PropertyAccessorFactory.forBeanPropertyAccess(dtoAddress);
        String resAddressPropertyValue = (String) resAddressAccessor.getPropertyValue(propertyName);
        if (resAddressPropertyValue != null) {
            return resAddressPropertyValue.equalsIgnoreCase((String) dtoAddressAccessor.getPropertyValue(propertyName));
        } else {
            return dtoAddressAccessor.getPropertyValue(propertyName) == null;
        }
    }

    private boolean hasSameValues(IAddressForm resAddress, AddressDto dtoAddress) {
        final boolean v1 = hasSameValue(resAddress, dtoAddress, "country")
                && hasSameValue(resAddress, dtoAddress, "city")
                && hasSameValue(resAddress, dtoAddress, "state");
        final boolean v2 = hasSameValue(resAddress, dtoAddress, "streetName")
                && hasSameValue(resAddress, dtoAddress, "streetNumber")
                && hasSameValue(resAddress, dtoAddress, "streetType");
        final boolean v3 = hasSameValue(resAddress, dtoAddress, "floor")
                && hasSameValue(resAddress, dtoAddress, "addressLine1")
                && hasSameValue(resAddress, dtoAddress, "addressLine2");
        final boolean v4 = v1 && v2 && v3;
        return v4 && hasSameValue(resAddress, dtoAddress, "postcode");
    }

    private boolean sameAddress(IAddressForm resAddress, AddressDto dtoAddress) {
        if (resAddress != null && dtoAddress != null) {
            return hasSameValues(resAddress, dtoAddress);
        } else if (resAddress == null && dtoAddress == null) {
            return true;
        }
        return false;
    }

    private void setAddresses(IndividualDto individualDto, IExtendedPersonDetailsForm form) throws IOException {
        if (CollectionUtils.isNotEmpty(individualDto.getAddresses())) {
            // NOTE: GCM sends us a single address only
            final AddressDto address = individualDto.getAddresses().get(0);
            final IAddressForm resAddress;
            if (form.hasJsonSchema()) { //using JSON schemas for validation
                resAddress = form.getResidentialAddress();
            } else { //pre schema old crap
                resAddress = AddressFormFactory.getNewAddressForm(form.getAddresses().get(0));
            }
            if (!form.isGcmUpdated()) {
                form.setGcmUpdated(!sameAddress(resAddress, address));
            }
            if (form.hasJsonSchema()) {
                if (form.isGcmUpdated()) {
                    form.updateResidentialAddress(address);//update residential address when using JSON schema supported forms
                    form.updatePostalAddress(address);
                }
            } else { // support pre-json crap
                form.setAddresses(convertDtoListToListMap(individualDto.getAddresses()));
            }
        } else {
            onChangeGCMRecord(form, form.getResidentialAddress());
        }
    }

    private void updateAddressFromAvaloq(IndividualDto individualDto, IExtendedPersonDetailsForm form) {
        for (AddressDto addressDto : individualDto.getAddresses()) {
            if(addressDto.isDomicile()){
                form.updateResidentialAddress(addressDto);
            } else if(addressDto.isMailingAddress()) {
                form.updatePostalAddress(addressDto);
            }
        }
    }

    private <T> List<Map<String, Object>> convertDtoListToListMap(List<T> list) throws IOException {
        List<Map<String, Object>> newList = new ArrayList<Map<String, Object>>();
        for (T obj : list) {
            final String jsonString = objectMapper.writeValueAsString(obj);
            final Map<String, Object> formData = objectMapper.readValue(jsonString, new TypeReference<LinkedHashMap<String, Object>>() {
            });
            newList.add(formData);
        }
        return newList;
    }

    private List<Email> convertDtoListToListEmail(List<EmailDto> list) throws IOException {
        List<Email> newList = new ArrayList<>();
        Email newEmail;
        for (EmailDto dto : list) {
            newEmail = new Email();

            newEmail.setPreferred(dto.isPreferred());
            newEmail.setEmail(dto.getEmail());
            newEmail.setEmailType(dto.getEmailType());
            newList.add(newEmail);
        }
        return newList;
    }

    private List<Phone> convertDtoListToListPhone(List<PhoneDto> list) throws IOException {
        List<Phone> newList = new ArrayList<>();
        Phone newPhone;
        for (PhoneDto dto : list) {
            newPhone = new Phone();
            newPhone.setType(dto.getType());
            newPhone.setNumber(dto.getNumber());

            newPhone.setPhoneType(dto.getPhoneType());
            newPhone.setPreferred(dto.isPreferred());
            newPhone.setAreaCode(dto.getAreaCode());
            newPhone.setCountryCode(dto.getCountryCode());

            newList.add(newPhone);
        }
        return newList;
    }

    @SuppressWarnings({"unused", "squid:UnusedPrivateMethod"})
    private List<Address> convertDtoListToListAddress(List<AddressDto> list) throws IOException {
        List<Address> newList = new ArrayList<>();
        Address newAdr = null;
        for (AddressDto dto : list) {
            newAdr = new Address();
            newAdr.setCity(dto.getCity());

            newAdr.setAddressLine1(dto.getAddressLine1());
            newAdr.setAddressLine2(dto.getAddressLine2());
            newAdr.setAddressType(dto.getAddressType());
            newAdr.setBuilding(dto.getBuilding());
            newAdr.setCountry(dto.getCountry());
            newAdr.setCountryCode(dto.getCountryCode());
            newAdr.setStateAbbr(dto.getStateAbbr());
            newAdr.setDomicile(dto.isDomicile());
            newAdr.setFloor(dto.getFloor());
            newAdr.setStandardAddressFormat(dto.isStandardAddressFormat());
            newAdr.setGcmStreetType(dto.getGcmStreetType());
            newAdr.setInternationalAddress(dto.isInternationalAddress());
            newAdr.setStreetName(dto.getStreetName());
            newAdr.setStreetNumber(dto.getStreetNumber());
            newAdr.setUnitNumber(dto.getUnitNumber());
            newAdr.setPostcode(dto.getPostcode());
            newAdr.setAddressType(dto.getAddressType());
            newList.add(newAdr);
        }
        return newList;
    }

    @Override
    public ClientApplicationDto simulateDraftAccount(ClientApplicationKey clientApplicationKey, ServiceErrors serviceErrors) {

        ClientApplicationDto oldClientApplicationDto = find(clientApplicationKey, serviceErrors);
        if (EncodedString.toPlainText(oldClientApplicationDto.getAdviserId()).equals(userProfileService.getPositionId())) {
            return create(oldClientApplicationDto, serviceErrors);
        }

        return null;
    }

    public void assertCanApplicationBeDeleted(ClientApplication clientApplication, ServiceErrors serviceErrors) {
        if (!ClientApplicationStatus.draft.equals(clientApplication.getStatus())) {
            OnBoardingApplication onboardingApplication = clientApplication.getOnboardingApplication();
            if (!OnboardingApplicationStatus.ApplicationCreationFailed.equals(onboardingApplication.getStatus())) {
                ApplicationIdentifier applicationIdentifier = new ApplicationIdentifierImpl();
                applicationIdentifier.setDocId(onboardingApplication.getAvaloqOrderId());
                UserProfile activeProfile = userProfileService.getActiveProfile();
                List<ApplicationDocument> applicationDocuments = accActivationIntegrationService.loadAccApplicationForApplicationId(Arrays.asList(applicationIdentifier),activeProfile.getJobRole(),activeProfile.getClientKey(), serviceErrors);

                if (CollectionUtils.isNotEmpty(applicationDocuments)) {
                    ApplicationDocument applicationDocument = applicationDocuments.get(0);

                    if (!applicationDocument.getAppState().equals(ApplicationStatus.DISCARDED)) {
                        throw new IllegalStateException("Cannot update Client Application (id=" + clientApplication.getId() + ") in state '" + clientApplication.getStatus() + "'");
                    }
                }
            }
        }
    }

    private void updateLastModified(ClientApplication draftAccount, ServiceErrors serviceErrors) {
        draftAccount.setLastModifiedAt(dateTimeService.getCurrentDateTime());
        draftAccount.setLastModifiedId(userProfileService.getGcmId());
        LOGGER.info(LoggingConstants.ONBOARDING_UPDATE + " userProfileService.getPanoramaNumber() " + userProfileService.getGcmId());
    }

    void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

}
