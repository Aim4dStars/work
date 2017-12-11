/**
 * 
 */
package com.bt.nextgen.serviceops.model;

import au.com.westpac.gn.locationmanagement.services.locationmanagement.xsd.retrievepostaladdress.v1.svc0454.AddressType;

public class RetrivePostalAddressReqModel {
	private String key;
	private AddressType addressType;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public AddressType getAddressType() {
		return addressType;
	}

	public void setAddressType(AddressType addressType) {
		this.addressType = addressType;
	}
}
