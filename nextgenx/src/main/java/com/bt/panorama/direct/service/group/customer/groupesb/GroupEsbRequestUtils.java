package com.bt.panorama.direct.service.group.customer.groupesb;

import au.com.westpac.gn.common.xsd.identifiers.v1.CommunicationIdentifier;
import au.com.westpac.gn.common.xsd.identifiers.v1.DocumentTemplateIdentifier;
import au.com.westpac.gn.communicationmanagement.services.communicationdispatch.common.xsd.v1.ObjectFactory;
import au.com.westpac.gn.communicationmanagement.services.communicationdispatch.common.xsd.v1.Communication;
import au.com.westpac.gn.communicationmanagement.services.communicationdispatch.common.xsd.v1.EmailAddress;
import au.com.westpac.gn.communicationmanagement.services.communicationdispatch.common.xsd.v1.SpecifiedContent;
import au.com.westpac.gn.communicationmanagement.services.communicationdispatch.common.xsd.v1.StandardTextSpecification;
import com.bt.nextgen.core.tracking.TrackingReferenceLocator;
import com.bt.panorama.direct.api.email.model.PortfolioDetailDto;
import com.bt.panorama.direct.service.group.customer.GroupEsbConstants;


public final class GroupEsbRequestUtils {

    private GroupEsbRequestUtils(){

    }

    public static Communication makeCommunicationObject(PortfolioDetailDto portfolioDetailDto){
        ObjectFactory factory = new ObjectFactory();
        //TODO - need to get static properties from properties files.
        Communication communication = factory.createCommunication();
        communication.setCommunicationMedium(GroupEsbConstants.COMMUNICATION_MEDIUM);

        CommunicationIdentifier identifier = new CommunicationIdentifier();
        identifier.setCommunicationId(TrackingReferenceLocator.locate().getTrackingReference());
        communication.setInternalIdentifier(identifier);
        SpecifiedContent specifiedContent = new SpecifiedContent();
        DocumentTemplateIdentifier templateIdentifier = new DocumentTemplateIdentifier();
        templateIdentifier.setTemplateId(GroupEsbConstants.COMMUNICATION_TEMPLATE);
        specifiedContent.setDocumentTemplateIdentifier(templateIdentifier);
        //testing data
        specifiedContent.getIsBasedOnSpecification().add(getStandardTextSpecification("subject",
                portfolioDetailDto.getCustomerName() + ", so you’re considering BT Invest?"));
        specifiedContent.getIsBasedOnSpecification().add(getStandardTextSpecification("header_left_img", "BTLogo1"));
        specifiedContent.getIsBasedOnSpecification().add(getStandardTextSpecification("firstname", portfolioDetailDto.getCustomerName()));
        specifiedContent.getIsBasedOnSpecification().add(getStandardTextSpecification("footer_right_img", "BTLogo2"));
        specifiedContent.getIsBasedOnSpecification()
                .add(getStandardTextSpecification("html_body_content1", "Thank you for your interest in BT Invest." +
                        "You have selected the BT Indexed "+ PortfolioType.forCode(portfolioDetailDto.getPortfolioType()).getDescription()+" Portfolio." +
                        "Here are some things to consider."));

        communication.setHasInformationContent(specifiedContent);
//        XmlDataTypeConverter.createXMLGregorianCalendar(new Date());
        EmailAddress fromAddress = factory.createEmailAddress();
        fromAddress.setAlias(GroupEsbConstants.FROM_ADDRESS_ALIS);
        fromAddress.setEmailAddress(GroupEsbConstants.FROM_ADDRESS);
        communication.setIsSentFromAddress(fromAddress);

        //to email address
        EmailAddress toAddress = factory.createEmailAddress();
        toAddress.setAlias(GroupEsbConstants.FROM_ADDRESS_ALIS);
        toAddress.setEmailAddress(portfolioDetailDto.getEmail());
        communication.setIsSentToAddress(toAddress);
        return communication;
    }

    private static StandardTextSpecification getStandardTextSpecification(String key, String value){
        ObjectFactory factory = new ObjectFactory();
        StandardTextSpecification sts = factory.createStandardTextSpecification();
        sts.setKey(key);
        sts.setValue(value);

        return sts;
    }
}
