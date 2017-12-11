package com.bt.nextgen.service.gesb.locationmanagement.v1;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.soap.client.SoapFaultClientException;

import au.com.westpac.gn.locationmanagement.services.locationmanagement.xsd.retrievepostaladdress.v1.svc0454.AddressType;
import au.com.westpac.gn.locationmanagement.services.locationmanagement.xsd.retrievepostaladdress.v1.svc0454.PostalAddressContactMethod;
import au.com.westpac.gn.locationmanagement.services.locationmanagement.xsd.retrievepostaladdress.v1.svc0454.Provider;
import au.com.westpac.gn.locationmanagement.services.locationmanagement.xsd.retrievepostaladdress.v1.svc0454.ProviderAttribute;
import au.com.westpac.gn.locationmanagement.services.locationmanagement.xsd.retrievepostaladdress.v1.svc0454.RetrievePostalAddressRequest;
import au.com.westpac.gn.locationmanagement.services.locationmanagement.xsd.retrievepostaladdress.v1.svc0454.RetrievePostalAddressResponse;
import au.com.westpac.gn.locationmanagement.services.locationmanagement.xsd.retrievepostaladdress.v1.svc0454.StandardPostalAddress;

import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.config.WebServiceProviderConfig;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.bt.nextgen.core.service.ErrorHandlerUtil;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.gesb.locationmanagement.PostalAddress;
import com.bt.nextgen.service.group.customer.ServiceConstants;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawDataImpl;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Created by F030695 on 24/10/2016.
 */
@Service
public class GroupEsbLocationManagementServiceImpl implements LocationManagementIntegrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupEsbLocationManagementServiceImpl.class);
    public static final String ALL_POSTAL_DELIVERY_TYPES_TYPE = "All postal delivery types (Type)";
    public static final String ALL_POSTAL_DELIVERY_TYPES_NUMBER = "All postal delivery types (Number)";
    public static final String PAF_BUILDING_LEVEL_NUMBER = "PAF Building level (Number)";
    public static final String G_NAF_BUILDING_LEVEL_NUMBER = "G-NAF Building level (Number)";
    public static final String PAF_BUILDING_NAME = "PAF Building name";
    public static final String G_NAF_BUILDING_NAME = "G-NAF Building name";
    public static final String PAF_UNIT_NUMBER = "PAF Flat/Unit (Number)";
    public static final String G_NAF_UNIT_NUMBER = "G-NAF Flat/Unit (Number)";
    public static final String BLANK = " ";

    @Autowired
    private WebServiceProvider provider;

    @Resource(name = "userDetailsService")
    private BankingAuthorityService userSamlService;

    @Autowired
    private CmsService cmsService;

    @Override
    public PostalAddress retrievePostalAddress(String addressId, ServiceErrors serviceErrors) {
        RetrievePostalAddressRequest requestPayload = getRetrievePostalAddressRequest(addressId);
        RetrievePostalAddressResponse response = retrievePostalAddressFromWebservice(requestPayload, serviceErrors);
        return createResultFromResponse(response);
    }

    private RetrievePostalAddressRequest getRetrievePostalAddressRequest(String addressId) {
        LOGGER.info("retrieving address for {}", addressId);
        RetrievePostalAddressRequest request = new RetrievePostalAddressRequest();
        request.setAddressType(AddressType.D);
        request.setKey(addressId);
        return request;
    }
    
    @Override
    public CustomerRawData retrievePostalAddressForGCM(RetrievePostalAddressRequest requestPayload, ServiceErrors serviceErrors) {
        RetrievePostalAddressResponse response = retrievePostalAddressFromWebservice(requestPayload, serviceErrors);
        CustomerRawData customerRawData = null;
        try {
            customerRawData = new CustomerRawDataImpl(response);
        } catch (JsonProcessingException ex) {
            LOGGER.error("createArrangementAndRelationShip Error converting object to json", ex);
        }
        return customerRawData;
    }

    private RetrievePostalAddressResponse retrievePostalAddressFromWebservice(RetrievePostalAddressRequest requestPayload,
                                                                              ServiceErrors serviceErrors) {
        CorrelatedResponse correlatedResponse;
        try {
            LOGGER.info("Calling web service to retrieve postal address.");
            correlatedResponse = provider.sendWebServiceWithSecurityHeaderAndResponseCallback(userSamlService.getSamlToken(),
                    WebServiceProviderConfig.GROUP_ESB_RETRIEVE_POSTAL_ADDRESS.getConfigName(), requestPayload, serviceErrors);
        } catch (SoapFaultClientException sfe) {
            ErrorHandlerUtil.parseSoapFaultException(serviceErrors, sfe, ServiceConstants.SERVICE_454);
            return null;
        }
        RetrievePostalAddressResponse response = (RetrievePostalAddressResponse) correlatedResponse.getResponseObject();
        String status = response.getServiceStatus().getStatusInfo().get(0).getLevel().toString();

        if (!status.equalsIgnoreCase(Attribute.SUCCESS_MESSAGE)) {
            LOGGER.error("There was an error retrieving an address from svc0454.");
            ErrorHandlerUtil.parseErrors(response.getServiceStatus(), serviceErrors, ServiceConstants.SERVICE_454,
                    correlatedResponse.getCorrelationIdWrapper(), cmsService);
            return new RetrievePostalAddressResponse();
        }
        LOGGER.info("Successfully retrieved postal address from svc0454.");
        return response;
    }

    private PostalAddress createResultFromResponse(RetrievePostalAddressResponse response) {
        LOGGER.info("Converting response into PostalAddress object.");
        StandardPostalAddress addressResponse = getPostalAddressFromResponse(response);
        PostalAddress postalAddress = new PostalAddress();
        if (null != addressResponse) {
            List<ProviderAttribute> providerAddressAttribute = getProviderAddressAttribute(response);
            updateFieldsUsingProviderAttributes(addressResponse,providerAddressAttribute, postalAddress);
            
            postalAddress.setStreetNumber(addressResponse.getStreetNumber());
            postalAddress.setStreetType(addressResponse.getStreetType());
            postalAddress.setCity(addressResponse.getCity());
            postalAddress.setPostcode(addressResponse.getPostCode());
            postalAddress.setState(addressResponse.getState());
            LOGGER.info("Returning converted results.");
        }
        return postalAddress;
    }

    @SuppressWarnings({ "squid:MethodCyclomaticComplexity", "squid:S1142" })
    private void updateFieldsUsingProviderAttributes(StandardPostalAddress addressResponse, List<ProviderAttribute> providerAttributes, PostalAddress postalAddress) {
        String postalDeliveryType = null, postalDeliveryNumber = null;
        StringBuilder preTextValues = new StringBuilder();
        for (ProviderAttribute providerAttribute : providerAttributes) {
            switch (providerAttribute.getAttributeName()) {
                case PAF_BUILDING_LEVEL_NUMBER:
                case G_NAF_BUILDING_LEVEL_NUMBER:
                    if(StringUtils.isNotEmpty(providerAttribute.getAttributeValue())) {
                        postalAddress.setFloor(providerAttribute.getAttributeValue());
                    }
                    break;
                case PAF_BUILDING_NAME:
                case G_NAF_BUILDING_NAME:
                    if(StringUtils.isNotEmpty(providerAttribute.getAttributeValue())) {
                      postalAddress.setBuildingName(providerAttribute.getAttributeValue());
                    }
                    break;
                case PAF_UNIT_NUMBER:
                case G_NAF_UNIT_NUMBER:
                    if(StringUtils.isNotEmpty(providerAttribute.getAttributeValue())) {
                        postalAddress.setUnitNumber(providerAttribute.getAttributeValue());
                    }
                    break;
                case ALL_POSTAL_DELIVERY_TYPES_TYPE:
                    postalDeliveryType = providerAttribute.getAttributeValue();
                    break;
                case ALL_POSTAL_DELIVERY_TYPES_NUMBER:
                    postalDeliveryNumber = providerAttribute.getAttributeValue();
                    break;
                case StringUtils.EMPTY:
                    if(StringUtils.isNotEmpty(providerAttribute.getAttributeValue())) {
                        preTextValues.append(providerAttribute.getAttributeValue()).append(BLANK);
                    }
                    break;
                default: break;

            }
        }
        if(StringUtils.isNotEmpty(preTextValues.toString())){
            if(StringUtils.isNotEmpty(postalAddress.getBuilding())){
                String buildingName = preTextValues.toString() + postalAddress.getBuilding();
                postalAddress.setBuildingName(buildingName);
            }else{
                postalAddress.setBuildingName(preTextValues.toString().trim());
            }

        }
        updateStreetName(addressResponse, postalAddress, postalDeliveryType, postalDeliveryNumber);
    }

    private void updateStreetName(StandardPostalAddress addressResponse, PostalAddress postalAddress, String postalDeliveryType, String postalDeliveryNumber) {
        String streetName = addressResponse.getStreetName();
        if(StringUtils.isNotEmpty(streetName)){
            postalAddress.setStreetName(addressResponse.getStreetName());
        } else {
            postalAddress.setStreetName(postalDeliveryType + " " + postalDeliveryNumber);
        }
    }

    private StandardPostalAddress getPostalAddressFromResponse(RetrievePostalAddressResponse response) {
        for (PostalAddressContactMethod address : response.getHasPostalAddressContactMethod()) {
            if (address.getHasAddress() instanceof StandardPostalAddress) {
                return (StandardPostalAddress) address.getHasAddress();
            }
        }
        LOGGER.error("Couldn't find standard postal address in response from svc0454.");
        return null;
    }

    private List<ProviderAttribute> getProviderAddressAttribute(RetrievePostalAddressResponse response) {
        Provider providerAddress = response.getHasProviderAddress();
        return providerAddress.getHasProviderAddressAttribute();
    }
}
