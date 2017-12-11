package com.bt.nextgen.config;

/**
 * Created by m035652 on 6/02/14.
 */
public enum WebServiceProviderConfig {

    GROUP_ESB_GENERATE_SECURITY_CREDENTIAL("gesb-generate-credentials"),
    GROUP_ESB_MODIFY_CHANNEL_ACCESS_CREDENTIAL("gesb-modify-username"),
    GROUP_ESB_MAINTAIN_CHANNEL_ACCESS_CREDENTIAL("gesb-modify-password"),
    GROUP_ESB_RETRIEVE_CHANNEL_ACCESS_CREDENTIAL("gesb-retrieve-credentials"),
    GROUP_ESB_RETRIEVE_CHANNEL_ACCESS_CREDENTIAL_V5("gesb-retrieve-credentialsV5"),
    GROUP_ESB_MAINTAIN_MFA_DEVICE_ARRANGEMENTS("gesb-maintain-mfadevicearrangements"),
    GROUP_ESB_GENERATE_COMMUNICATION_DETAILS("gesb-generate-communication"),
    GROUP_ESB_RETRIEVE_CUSTOMER_DETAILS("gesb-retrieve-customerdetails"),
    GROUP_ESB_RETRIEVE_CUSTOMER_DETAILS_V10("gesb-retrieve-customerdetailsV10"),
    GROUP_ESB_RETRIEVE_CUSTOMER_DETAILS_V11("gesb-retrieve-customerdetailsV11"),
    GROUP_ESB_UPDATE_CUSTOMER_DETAILS("gesb-update-customerdetails"),
    GROUP_ESB_UPDATE_CUSTOMER_DETAILS_INDIVIDUAL_V5("gesb-update-customerdetails-individual-V5"),
    GROUP_ESB_UPDATE_CUSTOMER_DETAILS_ORGANISATION_V5("gesb-update-customerdetails-organisation-V5"),
    GROUP_ESB_UPDATE_REGISTER_STATE("gesb-update-registeredstate"),
    GROUP_ESB_UPDATE_CUSTOMER_ADDRESS_DETAILS("gesb-update-customeraddressdetails"),
    GROUP_ESB_PRM("gesb-prm"),
    FINANCIAL_MARKET_INSTRUMENT("financialMarketInstrument"),
    GROUP_ESB_RETRIEVE_POSTAL_ADDRESS("gesb-retrieve-postaladdress"),
    GROUP_ESB_RETRIEVE_TFN("gesb-retrieve-tfn"),
    GROUP_ESB_MAINTAIN_ARRANGEMENT_AND_RELATIONSHIP_V1("gesb-maintain-arrangement-and-relationshipV1"),
    GROUP_ESB_RETRIVE_IP_TO_IP_RELATIONSHIP_V4("gesb-retrive-ip-to-ip-relationshipV4"), 
    GROUP_ESB_RETRIVE_IDV_DETAILS_V6("gesb-retrive-idv-detailsV6"),
    GROUP_ESB_MAINTAIN_IDV_DETAIL_V5("gesb-maintain-idv-detailV5"),
    GROUP_MAINTAIN_IP_CONTACTS_METHOD_V1("gesb-maintain-ip-contacts-methodV1"),
    GROUP_ESB_CREATE_ORAGANISATION_V5("gesb-create-organisationV5"),
    GROUP_ESB_CREATE_INDIVIDUAL_IP_V5("gesb-create-individual-ipV5"),
    GROUP_ESB_MAINTAIN_IP_TO_IP_RELATIONSHIP_V1("gesb-maintain-ip-to-ip-relationshipV1");
    
    private String configName;

    WebServiceProviderConfig(String name) {
        this.configName = name;
    }

    public String getConfigName() {
        return configName;
    }
}
