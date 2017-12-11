package com.bt.nextgen.api.client.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.account.v2.model.BankAccountDto;
import com.bt.nextgen.api.client.model.AddressDto;
import com.bt.nextgen.api.client.model.EmailDto;
import com.bt.nextgen.api.client.model.PhoneDto;
import com.bt.nextgen.api.client.model.RegisteredStateDto;
import com.bt.nextgen.api.client.model.TaxResidenceCountriesDto;
import com.bt.nextgen.api.client.util.ClientDetailDtoConverter;
import com.bt.nextgen.service.group.customer.groupesb.phone.CustomerPhone;
import com.bt.nextgen.service.group.customer.groupesb.phone.PhoneAction;
import com.bt.nextgen.service.integration.domain.*;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerData;
import com.bt.nextgen.service.group.customer.groupesb.TaxResidenceCountry;
import com.bt.nextgen.service.group.customer.groupesb.address.AddressAdapter;
import com.bt.nextgen.service.group.customer.groupesb.email.CustomerEmail;
import com.bt.nextgen.service.group.customer.groupesb.phone.PhoneAdapter;
import com.bt.nextgen.service.group.customer.groupesb.state.CustomerRegisteredState;
import com.btfin.panorama.service.integration.account.BankAccount;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.Field;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.userinformation.ClientType;
import com.btfin.panorama.core.conversion.CodeCategory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.on;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.hamcrest.Matchers.equalTo;

/**
 * Converts response retrieved from GCM into CustomerData
 */
@SuppressWarnings({"squid:S1200", "squid:MethodCyclomaticComplexity"})
public final class CustomerDataDtoConverter {

    private static final String ZERO_STR = "0";
    private static final String MASKED_VALUE = "####";
    private static final int ZERO = 0;
    private static final int TWO = 2;
    private static final int THREE = 3;
    private static final int SEVEN = 7;
    private static final int TEN = 10;
    private static final String EXTL_FLD_NAME_TIN_EXEMPT = "btfg$ucm_code";
    private static final String EXTL_FLD_NAME_COUNTRY = "btfg$im_code";
    private static final String AU_COUNTRY_CODE = "61";
    private static final List<String> australianMobileAreaCodes = Arrays.asList("4", "5");

    private CustomerDataDtoConverter() {
    }

    /**
     * * Build email list for a GCM contact details update request
     *
     * @param emailDtos
     * @return
     */
    public static List<Email> getEmailListForGcmUpdate(Collection<EmailDto> emailDtos) {
        final List<Email> emails = new ArrayList<>(emailDtos.size());
        for (EmailDto emailDto : emailDtos) {
            final CustomerEmail customerEmail = new CustomerEmail();
            customerEmail.setEmail(emailDto.getEmail());
            customerEmail.setOldAddress(emailDto.getOldAddress());
            customerEmail.setType(AddressMedium.EMAIL_PRIMARY.getAddressType().equals(emailDto.getEmailType()) ? AddressMedium.EMAIL_PRIMARY : AddressMedium.EMAIL_ADDRESS_SECONDARY);
            customerEmail.setCategory(AddressType.ELECTRONIC);
            customerEmail.setModificationSeq(emailDto.getModificationSeq());
            customerEmail.setPreferred(emailDto.isPreferred());
            if (emailDto.getEmailKey() != null) {
                customerEmail.setEmailKey(com.bt.nextgen.service.integration.domain.AddressKey.valueOf(new EncodedString(emailDto.getEmailKey().getAddressId()).plainText()));
            }
            if (isNotBlank(emailDto.getEmailActionCode())) {
                customerEmail.setAction(CustomerEmail.EmailAction.fromString(emailDto.getEmailActionCode()));
            }
            emails.add(customerEmail);
        }
        return emails;
    }

    /**
     * Build phone list for a GCM contact details update request
     *
     * @param phoneList
     * @return
     */
    public static List<Phone> getPhoneListForGcmUpdate(List<PhoneDto> phoneList) {
        List<Phone> phones = new ArrayList<>();
        // Transform the full telephone number to country code, area code and number to be sent to gcm
        ClientDetailDtoConverter.parseFullNumberListToGCMNumberFormat(phoneList);
        for (PhoneDto phone : phoneList) {
            if (isNotBlank(phone.getRequestedAction())) {
                CustomerPhone customerPhone = new CustomerPhone();
                customerPhone.setAreaCode(phone.getAreaCode());
                customerPhone.setCountryCode(phone.getCountryCode());
                customerPhone.setNumber(phone.getNumber());
                customerPhone.setModificationSeq(phone.getModificationSeq());
                customerPhone.setType(AddressMedium.getAddressMediumByAddressType(phone.getPhoneType()));
                customerPhone.setGcm(true);
                customerPhone.setAction(PhoneAction.fromString(phone.getRequestedAction()));
                phones.add(customerPhone);
            }
        }
        return phones;
    }


    /**
     * Retrieves registration details from GCM retrieve response
     *
     * @param data
     *
     * @return
     */
    public static RegisteredStateDto getRegisteredStateDto(CustomerData data) {
        final CustomerRegisteredState customerRegisteredState = data.getRegisteredState();
        final RegisteredStateDto customerRegisteredSateDto = new RegisteredStateDto();
        customerRegisteredSateDto.setRegistrationNumber(customerRegisteredState.getRegistrationNumber());
        customerRegisteredSateDto.setRegistrationStateCode(customerRegisteredState.getRegistrationStateCode());
        customerRegisteredSateDto.setRegistrationState(customerRegisteredState.getRegistrationState());
        customerRegisteredSateDto.setCountry(customerRegisteredState.getCountry());

        return customerRegisteredSateDto;
    }

    /**
     * Converts the list of phones into a list of PhoneDtos
     *
     * @param phones
     *
     * @return
     */
    public static List<PhoneDto> getPhoneDtos(List<Phone> phones) {
        final List<PhoneDto> phoneDtos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(phones)) {
            for (Phone phone : phones) {
                final PhoneDto phoneDto = new PhoneDto();
                phoneDto.setAreaCode(phone.getAreaCode());
                phoneDto.setCountryCode(phone.getCountryCode());
                phoneDto.setModificationSeq(phone.getModificationSeq());
                phoneDto.setNumber(phone.getNumber());
                phoneDto.setPhoneType(phone.getType().getAddressType());
                phoneDto.setGcmPhone(true);

                if (phone instanceof PhoneAdapter) {
                    phoneDto.setPhoneCategory(((PhoneAdapter) phone).getPhoneCategory().toUpperCase());
                }

                final String fullPhoneNumber = getFullPhoneNumber(phone);
                phoneDto.setFullPhoneNumber(fullPhoneNumber);
                phoneDtos.add(phoneDto);
            }
        }
        return phoneDtos;
    }

    /**
     * Returns a list of Australian mobile phones which are masked in format 041####123
     *
     * @param phones
     *
     * @return
     */
    public static List<PhoneDto> getMaskedAustralianMobileNumbers(List<Phone> phones) {
        final List<PhoneDto> phoneDtos = new ArrayList<>();
        Set<String> phoneList = new HashSet<>();
        if (CollectionUtils.isNotEmpty(phones)) {
            for (Phone phone : phones) {
                if (isAustralianMobileNumber(phone)) {
                    final String fullPhoneNumber = getFullPhoneNumber(phone);
                    //Don't add duplicate phone numbers to the list
                    if (!phoneList.contains(fullPhoneNumber)) {
                        final PhoneDto phoneDto = new PhoneDto();
                        final String formattedMobileNumber = ZERO_STR + fullPhoneNumber.substring(TWO);
                        phoneDto.setNumber(EncodedString.fromPlainText(formattedMobileNumber).toString());
                        phoneDto.setFullPhoneNumber(maskFullPhoneNumber(formattedMobileNumber, MASKED_VALUE));
                        phoneDtos.add(phoneDto);
                        phoneList.add(fullPhoneNumber);
                    }
                }
            }
        }
        return phoneDtos;
    }

    private static boolean isAustralianMobileNumber(Phone phone) {
        return AU_COUNTRY_CODE.equalsIgnoreCase(phone.getCountryCode()) && australianMobileAreaCodes.contains(phone.getAreaCode());
    }

    private static String maskFullPhoneNumber(String fullPhoneNumber, String maskedValue) {
        return fullPhoneNumber.substring(ZERO, THREE) + maskedValue + fullPhoneNumber.substring(SEVEN, TEN);
    }

    private static String getFullPhoneNumber(Phone phone) {
        String fullPhoneNumber = "";
        if (StringUtils.isNotBlank(phone.getCountryCode())) {
            fullPhoneNumber += phone.getCountryCode();
        }
        if (StringUtils.isNotBlank(phone.getAreaCode())) {
            fullPhoneNumber += phone.getAreaCode();
        }
        if (StringUtils.isNotBlank(phone.getNumber())) {
            fullPhoneNumber += phone.getNumber();
        }
        return fullPhoneNumber;
    }

    /**
     * Converts a list of consumer transaction accounts into BankAccountDto
     *
     * @param data
     *
     * @return
     */
    public static List<BankAccountDto> getBankAccountsDto(CustomerData data) {
        final List<BankAccountDto> bankAccounts = new ArrayList<>();
        for (BankAccount account : data.getBankAccounts()) {
            final BankAccountDto bankAccount = new BankAccountDto();
            bankAccount.setName(account.getName());
            bankAccount.setBsb(account.getBsb());
            bankAccount.setAccountNumber(account.getAccountNumber());
            bankAccount.setNickName(account.getNickName());
            bankAccounts.add(bankAccount);
        }
        return bankAccounts;
    }

    public static String setDtoAddressType(ClientType clientType) {
        if (clientType == ClientType.N) {
            return "residential";
        }
        else {
            return "registered";
        }
    }

    /**
     * Converts list of email addresses retrieved from GCM to list of EmailDtos
     *
     * @param data
     *
     * @return
     */
    public static List<EmailDto> getEmailDtos(CustomerData data) {
        final List<EmailDto> emailDtoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(data.getEmails())) {
            for (Email email : data.getEmails()) {
                final EmailDto emailDto = new EmailDto();
                emailDto.setEmail(email.getEmail());
                emailDto.setGcmMastered(Boolean.TRUE);
                emailDto.setOldAddress(email.getEmail());
                emailDtoList.add(emailDto);
            }
        }
        return emailDtoList;
    }

    /**
     * Converts an address retrieved from GCM into AddressDto
     *
     * @param address
     *
     * @return
     */
    public static AddressDto populateAddress(Address address) {
        final AddressDto addrDto = new AddressDto();
        if (address instanceof AddressAdapter) {
            final AddressAdapter addressAdapter = (AddressAdapter) address;
            if (addressAdapter.isStandardAddressFormat()) {
                addrDto.setFloor(address.getFloor());
                addrDto.setUnitNumber(address.getUnit());
                addrDto.setStreetName(address.getStreetName());
                addrDto.setStreetNumber(address.getStreetNumber());
                addrDto.setStreetType(address.getStreetType());
                addrDto.setBuilding(address.getBuilding());
            }
            else {
                //Set the fields for non standard address
                addrDto.setAddressLine1(address.getAddressLine1());
                addrDto.setAddressLine2(address.getAddressLine2());
                addrDto.setAddressLine3(address.getAddressLine3());
            }
            addrDto.setInternationalAddress(address.isInternationalAddress());
            addrDto.setStandardAddressFormat(addressAdapter.isStandardAddressFormat());
            addrDto.setCity(address.getCity());
            addrDto.setSuburb(address.getCity());
            addrDto.setState(address.getState());
            addrDto.setStateAbbr(address.getState());
            addrDto.setPostcode(address.getPostCode());
            addrDto.setCountry(address.getCountry());
            addrDto.setCountryAbbr(address.getCountry());
            addrDto.setDomicile(true);
            addrDto.setMailingAddress(true);
            addrDto.setGcmAddress(true);
        }
        return addrDto;
    }

    public static List<TaxResidenceCountriesDto> convertTaxResidenceCountryDto(List<TaxResidenceCountry> taxResidenceCountries,
                                                                               String countryWithTaxHolding,
                                                                               StaticIntegrationService staticIntegrationService,
                                                                               ServiceErrors serviceErrors) {
        List<TaxResidenceCountriesDto> countriesDtos = new ArrayList<>();
        if (taxResidenceCountries != null && CollectionUtils.isNotEmpty(taxResidenceCountries)) {
            for (TaxResidenceCountry taxResidenceCountry : taxResidenceCountries) {
                final TaxResidenceCountriesDto countriesDto = new TaxResidenceCountriesDto();
                countriesDto.setTin(taxResidenceCountry.getTin());
                if (taxResidenceCountry.getStartDate() != null) {
                    countriesDto.setStartDate(taxResidenceCountry.getStartDate().toString());
                }
                if (taxResidenceCountry.getEndDate() != null) {
                    countriesDto.setEndDate(taxResidenceCountry.getEndDate().toString());
                }
                countriesDto.setVersionNumber(taxResidenceCountry.getVersionNumber());
                if (taxResidenceCountry.getResidenceCountry() != null) {
                    if ("FOREIGN".equals(taxResidenceCountry.getResidenceCountry())) {
                        countriesDto.setTaxResidenceCountry(taxResidenceCountry.getResidenceCountry());
                    }
                    else {
                        countriesDto.setTaxResidenceCountry(getValueFromStatic(staticIntegrationService,
                                CodeCategory.COUNTRY, taxResidenceCountry.getResidenceCountry(), EXTL_FLD_NAME_COUNTRY, serviceErrors));
                        countriesDto.setTaxResidencyCountryCode(staticIntegrationService.loadCodeByAvaloqId(CodeCategory.COUNTRY, taxResidenceCountry.getResidenceCountry(), serviceErrors).getUserId());
                    }
                }
                countriesDto.setTaxExemptionReason(getValueFromStatic(staticIntegrationService, CodeCategory.TIN_EXEMPTION_REASONS,
                        taxResidenceCountry.getExemptionReason(), EXTL_FLD_NAME_TIN_EXEMPT, serviceErrors));
                countriesDto.setTaxExemptionReasonCode(taxResidenceCountry.getExemptionReason());
                countriesDtos.add(countriesDto);
            }
        }

        if (StringUtils.isNotEmpty(countryWithTaxHolding)) {
            final Code code = staticIntegrationService.loadCode(CodeCategory.COUNTRY, countryWithTaxHolding, serviceErrors);
            if (!Lambda.exists(countriesDtos, Lambda.having(on(TaxResidenceCountriesDto.class).getTaxResidenceCountry(),
                    equalTo(code.getIntlId())))) {
                final TaxResidenceCountriesDto countriesDto = new TaxResidenceCountriesDto();
                countriesDto.setTaxResidenceCountry(code.getIntlId());
                countriesDtos.add(countriesDto);
            }
        }

        return countriesDtos;
    }

    private static String getValueFromStatic(StaticIntegrationService staticIntegrationService,
                                             CodeCategory codeCategory,
                                             String compareValue,
                                             String extFieldName, ServiceErrors serviceErrors) {
        if (compareValue != null) {
            final Collection<Code> categoryCodes = staticIntegrationService.loadCodes(codeCategory, serviceErrors);
            for (Code code : categoryCodes) {
                final Field field = code.getField(extFieldName);
                if (field != null && compareValue.equals(field.getValue())) {
                    return code.getIntlId();
                }
            }
        }
        return null;
    }
}
