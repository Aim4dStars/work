package com.bt.nextgen.api.draftaccount.model;

import com.bt.nextgen.api.client.model.ClientDto;
import com.bt.nextgen.api.client.model.EmailDto;
import com.bt.nextgen.api.client.model.PhoneDto;
import com.bt.nextgen.service.integration.domain.AddressMedium;

public class HoldingApplicationClientDto {
    private final String fullName;
    private final String email;
    private final String phoneNumber;
    private final boolean isApprover;
    private final ApplicationClientStatus status;

    public HoldingApplicationClientDto(ClientDto clientDto, boolean isApprover, ApplicationClientStatus status) {
        this.fullName = getName(clientDto);
        this.email = getPrimaryEmail(clientDto);
        this.phoneNumber = getMobilePhoneNumber(clientDto);
        this.isApprover = isApprover;
        this.status = status;
    }

    private String getMobilePhoneNumber(ClientDto clientDto) {
        for(PhoneDto phoneNumber : clientDto.getPhones()) {
            if(AddressMedium.MOBILE_PHONE_PRIMARY.getAddressType().equals(phoneNumber.getPhoneType())) {
                return phoneNumber.getNumber();
            }
        }
        return "";
    }

    private String getPrimaryEmail(ClientDto clientDto) {
        for(EmailDto email : clientDto.getEmails()) {
            if(AddressMedium.EMAIL_PRIMARY.getAddressType().equals(email.getEmailType())) {
                return email.getEmail();
            }
        }
        return "";
    }

    private String getName(ClientDto clientDto) {
        return String.format("%s, %s", clientDto.getLastName(), clientDto.getFirstName());
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public boolean isApprover() {
        return isApprover;
    }

    public ApplicationClientStatus getStatus() {
        return status;
    }
}
