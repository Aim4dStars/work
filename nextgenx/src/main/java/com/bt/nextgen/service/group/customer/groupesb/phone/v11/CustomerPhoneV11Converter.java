package com.bt.nextgen.service.group.customer.groupesb.phone.v11;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.PhoneAddressContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsResponse;
import com.bt.nextgen.service.group.customer.groupesb.CustomerData;
import com.bt.nextgen.service.group.customer.groupesb.CustomerDataImpl;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequest;
import com.bt.nextgen.service.group.customer.groupesb.RoleType;
import com.bt.nextgen.service.group.customer.groupesb.phone.CustomerPhone;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Phone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by F057654 on 1/09/2015.
 */
@SuppressWarnings({"squid:S1200", "squid:MethodCyclomaticComplexity", "squid:S1142"})
public final class CustomerPhoneV11Converter {

    private CustomerPhoneV11Converter(){}

    private static final Logger logger = LoggerFactory.getLogger(CustomerPhoneV11Converter.class);

    private static final String GESB_MOBILE_CONSTANT = "MOBILE";
    private static final String GESB_PHONE_CONSTANT = "Phone";
    private static final String GESB_WORK_PHONE_CONSTANT = "WRK";
    private static final String GESB_BUSINESS_PHONE_CONSTANT = "BUS";
    private static final String GESB_HOME_PHONE_CONSTANT = "HOM";
    private static final String GESB_OTHER_CONSTANT = "OTH";
    public static final String OBSOLETE = "O";

    /**
     * Method to convert service258 retrieved phone numbers into Phone object
     * @param response
     * @param req
     * @return List of Phone
     */
    public static CustomerData convertResponseInPhone(RetrieveDetailsAndArrangementRelationshipsForIPsResponse response, CustomerManagementRequest req) {
        CustomerData data = new CustomerDataImpl();
        List<Phone> phoneListRetrievedFromResponse = new ArrayList<>();
        List<PhoneAddressContactMethod> contactNumbers;

        if(req.getInvolvedPartyRoleType().equals(RoleType.INDIVIDUAL)){
            contactNumbers = response.getIndividual().getHasPhoneAddressContactMethod();
        }else{
            contactNumbers = response.getOrganisation().getHasPhoneAddressContactMethod();
        }

        for(PhoneAddressContactMethod phone : contactNumbers){
            if((phone.getContactMedium().equalsIgnoreCase(GESB_MOBILE_CONSTANT)
                    || phone.getContactMedium().equalsIgnoreCase(GESB_PHONE_CONSTANT)) && !OBSOLETE.equalsIgnoreCase(phone.getValidityStatus())){
                phoneListRetrievedFromResponse.add(new PhoneAdapterV11(phone));
            }
        }

        data.setPhoneNumbers(phoneListRetrievedFromResponse);
       return data;
    }

    public static AddressMedium convertResponseInAddressMedium(PhoneAddressContactMethod phone){
        final String contactMedium = phone.getContactMedium();
        if(GESB_MOBILE_CONSTANT.equalsIgnoreCase(contactMedium)){
            return AddressMedium.MOBILE_PHONE_SECONDARY;
        }else if(GESB_PHONE_CONSTANT.equalsIgnoreCase(contactMedium)){
            switch (phone.getUsage())
            {
                case GESB_HOME_PHONE_CONSTANT:
                    return AddressMedium.PERSONAL_TELEPHONE;
                case GESB_WORK_PHONE_CONSTANT:
                case GESB_BUSINESS_PHONE_CONSTANT:
                    return AddressMedium.BUSINESS_TELEPHONE;
                case GESB_OTHER_CONSTANT:
                    return AddressMedium.OTHER;

                default:
                    logger.warn("No relevant mapping found for Phone Address usage {} : returning Other", phone.getUsage());
                    return AddressMedium.OTHER;
            }
        }

        return null;
    }

}

