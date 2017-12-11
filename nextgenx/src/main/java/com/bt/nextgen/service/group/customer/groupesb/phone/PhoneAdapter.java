package com.bt.nextgen.service.group.customer.groupesb.phone;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.PhoneAddressContactMethod;
import com.bt.nextgen.service.integration.domain.AddressKey;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.AddressType;
import com.bt.nextgen.service.integration.domain.Phone;

import static com.bt.nextgen.service.group.customer.groupesb.phone.v7.CustomerPhoneV7Converter.convertResponseInAddressMedium;
import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Adapter class to convert a {@code PhoneAddressContactMethod} instance (from GESB service SVC0258) into the
 * {@code Phone} interface.
 */
public abstract class PhoneAdapter implements Phone {

    public abstract String getPhoneCategory();

    public abstract String getModificationSeq();
}
