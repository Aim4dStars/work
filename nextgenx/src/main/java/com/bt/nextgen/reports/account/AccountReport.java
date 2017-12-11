package com.bt.nextgen.reports.account;

import com.bt.nextgen.api.account.v3.model.AccountDto;
import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.account.v3.model.PersonRelationDto;
import com.bt.nextgen.api.account.v3.model.WrapAccountDetailDto;
import com.bt.nextgen.api.account.v3.service.WrapAccountDetailDtoService;
import com.bt.nextgen.api.broker.model.BrokerDto;
import com.bt.nextgen.api.client.model.AddressDto;
import com.bt.nextgen.api.client.model.ClientKey;
import com.bt.nextgen.api.client.model.PhoneDto;
import com.bt.nextgen.api.client.v2.model.InvestorDto;
import com.bt.nextgen.api.client.v2.model.RegisteredEntityDto;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.reporting.BaseReport;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.InvestorType;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.integration.broker.Broker;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
public abstract class AccountReport extends BaseReport {
    @Autowired
    private WrapAccountDetailDtoService accountDetailDtoService;

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    private static final String FORWARDSLASH = "/";
    private static final String SPACE = " ";
    private static final String COMMA = ",";

    @ReportBean("accounts")
    public Collection<AccountDto> getAccount(Map<String, String> params) {
        ServiceErrors serviceErrors = new FailFastErrorsImpl();

        final List<ApiSearchCriteria> criteria = new ArrayList<>();
        criteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, ApiSearchCriteria.SearchOperation.EQUALS,
                getAccountKey(params).getAccountId(), ApiSearchCriteria.OperationType.STRING));
        WrapAccountDetailDto wrapAccountDetailDto = accountDetailDtoService.search(criteria, serviceErrors);

        return Collections.singletonList(getAccountDto(wrapAccountDetailDto, serviceErrors));
    }

    protected AccountKey getAccountKey(Map<String, String> params) {
        AccountKey accountKey = new AccountKey(params.get(UriMappingConstants.ACCOUNT_ID_URI_MAPPING));
        return accountKey;
    }

    private AccountDto getAccountDto(WrapAccountDetailDto wrapAccountDetailDto, ServiceErrors serviceErrors) {
        AccountDto account = new AccountDto(wrapAccountDetailDto.getKey());
        account.setAccountName(wrapAccountDetailDto.getAccountName().replace(",", " and"));
        account.setAccountNumber(wrapAccountDetailDto.getAccountNumber());
        account.setAccountType(wrapAccountDetailDto.getAccountType());

        if (wrapAccountDetailDto.getProduct() != null) {
            account.setProduct(wrapAccountDetailDto.getProduct().getProductName());
        }

        InvestorDto investor = getPrimaryContact(wrapAccountDetailDto);
        setPrimaryContactDetails(account, investor);

        if (null != wrapAccountDetailDto.getAdviser()) {
            setAdviserDetails(account, wrapAccountDetailDto.getAdviser(), serviceErrors);
        }
        else {
            account.setAdviserName("");
            account.setAdviserDealerGroup("");
            account.setAdviserMobileNumber("");
        }

        return account;
    }

    private void setAdviserDetails(AccountDto account, BrokerDto broker, ServiceErrors serviceErrors) {
        BrokerKey brokerParentKey = BrokerKey.valueOf(EncodedString.toPlainText(broker.getBrokerParentKey().getBrokerId()));
        Broker parentBroker = brokerIntegrationService.getBroker(brokerParentKey, serviceErrors);

        List<PhoneDto> phoneDtos = broker.getPhone();
        List<PhoneDto> phones = new ArrayList<>();

        String primaryMobile = "";

        setAdviserPhoneDetails(phones, phoneDtos);

        if (!CollectionUtils.isEmpty(phones)) {

            PhoneDto phoneDto = phones.get(0);
            if (null != phoneDto.getNumber()) {
                primaryMobile = phoneDto.getNumber();
            }
        }

        account.setAdviserName(broker.getCorporateName());
        account.setAdviserDealerGroup(parentBroker.getPositionName());
        account.setAdviserMobileNumber(ReportFormatter.formatTelephoneNumber(primaryMobile));

    }

    private void setPrimaryContactDetails(AccountDto account, InvestorDto investor) {
        List<AddressDto> addressDtos = investor.getAddresses();
        StringBuilder primaryAddress = new StringBuilder();

        if (!CollectionUtils.isEmpty(addressDtos)) {
            AddressDto addressDto = addressDtos.get(0);
            primaryAddress = getAddressDetails(addressDto);
        }

        List<PhoneDto> phoneDtos = investor.getPhones();
        String primaryMobile = "";

        if (!CollectionUtils.isEmpty(phoneDtos)) {
            PhoneDto phoneDto = phoneDtos.get(0);
            if (null != phoneDto.getNumber()) {
                primaryMobile = phoneDto.getNumber();
            }
        }

        if (investor.getFirstName() != null && investor.getLastName() != null) {
            account.setPrimaryContactName(investor.getFirstName() + " " + investor.getLastName());
        }
        else {
            account.setPrimaryContactName("");
        }
        account.setPrimaryContactAddress(primaryAddress.toString());
        account.setPrimaryContactNumber(ReportFormatter.formatTelephoneNumber(primaryMobile));

    }

    private StringBuilder getAddressDetails(AddressDto addressDto) {
        StringBuilder primaryAddress = new StringBuilder();
        if (null != addressDto.getUnitNumber()) {
            primaryAddress.append(addressDto.getUnitNumber()).append(FORWARDSLASH).append(SPACE);
        }
        if (null != addressDto.getStreetNumber()) {
            primaryAddress.append(addressDto.getStreetNumber()).append(SPACE);
        }
        if (null != addressDto.getStreetName()) {
            primaryAddress.append(addressDto.getStreetName()).append(SPACE);
        }
        if (null != addressDto.getStreetType()) {
            primaryAddress.append(addressDto.getStreetType()).append(SPACE);
        }
        if (null != addressDto.getFloor()) {
            primaryAddress.append(addressDto.getFloor()).append(SPACE);
        }
        if (null != addressDto.getBuilding()) {
            primaryAddress.append(addressDto.getBuilding()).append(SPACE);
        }
        if (null != addressDto.getSuburb()) {
            if (StringUtils.isNotBlank(primaryAddress)) {
                primaryAddress.setLength(primaryAddress.length() - 1);
                primaryAddress.append(COMMA).append(SPACE).append(addressDto.getSuburb()).append(SPACE);
            }
            else {
                primaryAddress.append(addressDto.getSuburb()).append(SPACE);
            }
        }
        if (null != addressDto.getStateAbbr()) {
            primaryAddress.append(addressDto.getStateAbbr()).append(SPACE);
        }
        if (null != addressDto.getPostcode()) {
            primaryAddress.append(addressDto.getPostcode()).append(SPACE);
        }
        return primaryAddress;
    }

    private InvestorDto getPrimaryContact(WrapAccountDetailDto wrapAccountDetailDto) {
        InvestorDto investorDto = new InvestorDto();

        ClientKey primaryContactKey = null;

        if (!CollectionUtils.isEmpty(wrapAccountDetailDto.getSettings())) {
            for (PersonRelationDto personRelationdto : wrapAccountDetailDto.getSettings()) {
                if (personRelationdto.isPrimaryContactPerson()) {
                    primaryContactKey = personRelationdto.getClientKey();
                    break;
                }
            }
        }

        if (!CollectionUtils.isEmpty(wrapAccountDetailDto.getOwners()) && primaryContactKey != null) {
            for (InvestorDto dto : wrapAccountDetailDto.getOwners()) {
                if (primaryContactKey.equals(dto.getKey())) {
                    investorDto = dto;
                    break;
                }

                if (!dto.getInvestorType().equalsIgnoreCase(InvestorType.INDIVIDUAL.toString())) {

                    List<InvestorDto> linkedClients = ((RegisteredEntityDto) dto).getLinkedClients();
                    if (!CollectionUtils.isEmpty(linkedClients)) {
                        for (InvestorDto linkedClient : linkedClients) {
                            if (primaryContactKey.equals(linkedClient.getKey())) {
                                investorDto = linkedClient;
                                break;
                            }
                        }
                    }
                }
            }
        }

        getPrimaryContactDetails(investorDto);

        return investorDto;
    }

    private void getPrimaryContactDetails(InvestorDto investorDto) {
        List<AddressDto> addresses = new ArrayList<>();
        List<PhoneDto> phones = new ArrayList<>();

        if (!CollectionUtils.isEmpty(investorDto.getAddresses())) {
            for (AddressDto addressDto : investorDto.getAddresses()) {
                if (addressDto.isMailingAddress()) {
                    addresses.add(addressDto);
                    break;
                }
            }
        }

        if (!CollectionUtils.isEmpty(investorDto.getPhones())) {
            setPhoneDetails(phones, investorDto.getPhones());
        }

        investorDto.setAddresses(addresses);
        investorDto.setPhones(phones);
    }

    private void setPhoneDetails(List<PhoneDto> phones, List<PhoneDto> orgPhoneDtos) {
        for (PhoneDto phoneDto : orgPhoneDtos) {
            if (AddressMedium.MOBILE_PHONE_PRIMARY.getAddressType().equals(phoneDto.getPhoneType())) {
                phones.clear();
                phones.add(phoneDto);
                break;
            }
            else if (phoneDto.getNumber() != null) {
                phones.add(phoneDto);
            }
        }
    }

    private void setAdviserPhoneDetails(List<PhoneDto> phones, List<PhoneDto> orgPhoneDtos) {
        for (PhoneDto phoneDto : orgPhoneDtos) {
            if (AddressMedium.BUSINESS_TELEPHONE.getAddressType().equals(phoneDto.getPhoneType())) {
                phones.add(phoneDto);
                break;
            }
        }
    }

    /**
     * For use by spring security annotations. For general code, the method you're looking for is @see getAccountKey()
     * 
     * @return
     */
    public Object getAccountEncodedId(Map<String, Object> params) {
        return params.get(UriMappingConstants.ACCOUNT_ID_URI_MAPPING);
    }
}
