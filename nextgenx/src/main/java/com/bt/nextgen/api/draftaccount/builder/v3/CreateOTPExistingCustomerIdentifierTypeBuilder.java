package com.bt.nextgen.api.draftaccount.builder.v3;

import ns.btfin_com.party.v3_0.CustomerNoAllIssuerType;
import ns.btfin_com.party.v3_0.CustomerNumberIdentifier;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.CreateOTPExistingCustomerIdentifierType;
import org.springframework.stereotype.Component;

public class CreateOTPExistingCustomerIdentifierTypeBuilder {

	private String id;	
	private CustomerNoAllIssuerType type;

	public void setId(String id) {
		this.id = id;
	}

	public void setType(CustomerNoAllIssuerType type) {
		this.type = type;
	}

	public CreateOTPExistingCustomerIdentifierType build() {
		CreateOTPExistingCustomerIdentifierType createOTPExistingCustomerIdentifierType = new CreateOTPExistingCustomerIdentifierType();
        CustomerNumberIdentifier customerNumberIdentifier = new CustomerNumberIdentifier();
        customerNumberIdentifier.setCustomerNumber(this.id);
        customerNumberIdentifier.setCustomerNumberIssuer(this.type);
        createOTPExistingCustomerIdentifierType.setCustomerNumberIdentifier(customerNumberIdentifier);
        return createOTPExistingCustomerIdentifierType;
	}
}
