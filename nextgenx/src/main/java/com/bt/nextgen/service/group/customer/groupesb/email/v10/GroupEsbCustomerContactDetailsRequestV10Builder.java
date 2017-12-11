package com.bt.nextgen.service.group.customer.groupesb.email.v10;

import au.com.westpac.gn.common.xsd.identifiers.v1.ContactMethodIdentifier;
import au.com.westpac.gn.common.xsd.identifiers.v1.IdentificationScheme;
import au.com.westpac.gn.common.xsd.identifiers.v1.InvolvedPartyIdentifier;
import au.com.westpac.gn.common.xsd.identifiers.v1.RowSetItemIdentifier;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.Action;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.EmailAddress;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.EmailAddressContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.InvolvedPartyName;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.InvolvedPartyType;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.MaintainIPContactMethodsRequest;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.MaintenanceAuditContext;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.ObjectFactory;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.PhoneAddressContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsResponse;
import com.bt.nextgen.core.type.DateUtil;
import com.bt.nextgen.service.group.customer.ServiceConstants;
import com.bt.nextgen.service.group.customer.groupesb.CustomerData;
import com.bt.nextgen.service.group.customer.groupesb.RoleType;
import com.bt.nextgen.service.group.customer.groupesb.phone.v10.CustomerPhoneUpdateV10Converter;
import com.bt.nextgen.service.integration.domain.Email;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@SuppressWarnings("squid:S1200")
public final class GroupEsbCustomerContactDetailsRequestV10Builder {

    private GroupEsbCustomerContactDetailsRequestV10Builder() {}

    private static final Logger logger = LoggerFactory.getLogger(GroupEsbCustomerContactDetailsRequestV10Builder.class);

    public static MaintainIPContactMethodsRequest createContactDetailsModificationRequest(
            CustomerData customerData, RetrieveDetailsAndArrangementRelationshipsForIPsResponse cachedResponse) {

        ObjectFactory objectFactory = new ObjectFactory();
        MaintainIPContactMethodsRequest maintainIPContactMethodsRequest = objectFactory.createMaintainIPContactMethodsRequest();

        List<InvolvedPartyIdentifier> identifiers = maintainIPContactMethodsRequest.getInvolvedPartyIdentifier();
        InvolvedPartyIdentifier involvedPartyIdentifier = getInvolvedPartyIdentifier(customerData);
        identifiers.add(involvedPartyIdentifier);

        Map<String, au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.
retrievedetailsandarrangementrelationshipsforips.v10.svc0258.EmailAddressContactMethod> cachedEmailContexts =
                applyRoleBasedSettings(customerData, cachedResponse, maintainIPContactMethodsRequest);

        int i=0;
        for (Email  email : customerData.getEmails()) {
            if(email instanceof CustomerEmailV10 && ((CustomerEmailV10) email).getAction() != null) {
                i += 1;
                String rowSetIdentifier = String.format("%010d", i);
                CustomerEmailV10 customerEmail = (CustomerEmailV10) email;
                EmailAddressContactMethod emailAddressContactMethod = objectFactory.createEmailAddressContactMethod();

                switch (customerEmail.getAction()) {
                    case ADD:
                        requestSettingsForEmailAdd(emailAddressContactMethod, cachedResponse, rowSetIdentifier, objectFactory);
                        break;

                    case MODIFY:
                        requestSettingsForEmailModify(emailAddressContactMethod, cachedEmailContexts.get(customerEmail.getOldAddress()), rowSetIdentifier, objectFactory);
                        break;

                    case DELETE:
                        requestSettingsForEmailDelete(emailAddressContactMethod, cachedEmailContexts.get(customerEmail.getEmail()), rowSetIdentifier, objectFactory);
                        break;

                    default:
                        logger.error("Not supportive operation for gcm email update");
                }
                EmailAddress emailAddress = objectFactory.createEmailAddress();
                emailAddress.setEmailAddress(email.getEmail());
                emailAddressContactMethod.setHasAddress(emailAddress);
                maintainIPContactMethodsRequest.getHasEmailAddressContactMethod().add(emailAddressContactMethod);
            }
        }

        //set phone numbers in the request for Add/Modify/Delete
        if(!customerData.getPhoneNumbers().isEmpty()){
            List<PhoneAddressContactMethod> phones = createRequestForPhoneAddress(customerData, cachedResponse);
            if(!phones.isEmpty()){
                maintainIPContactMethodsRequest.getHasPhoneAddressContactMethod().addAll(phones);
            }
        }

        return maintainIPContactMethodsRequest;
    }

    /**
     * Method to do all the transformation for creation of address update request
     * @param customerData
     * @param cachedResponse
     * @return
     */
    private static List<PhoneAddressContactMethod> createRequestForPhoneAddress(CustomerData customerData, RetrieveDetailsAndArrangementRelationshipsForIPsResponse cachedResponse){
        return CustomerPhoneUpdateV10Converter.convertResponseInPhone(customerData, cachedResponse);
    }


    private static void requestSettingsForEmailDelete(EmailAddressContactMethod emailAddressContactMethod, au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.
retrievedetailsandarrangementrelationshipsforips.v10.svc0258.EmailAddressContactMethod cachedEmailContexts,
            String rowSeq, ObjectFactory objectFactory) {

        final au.com.westpac.gn.common.xsd.identifiers.v1.ObjectFactory factory = new au.com.westpac.gn.common.xsd.identifiers.v1.ObjectFactory();
        RowSetItemIdentifier rowIde = factory.createRowSetItemIdentifier();
        rowIde.setSequenceNumber(rowSeq);
        emailAddressContactMethod.setRowSetIdIdentifier(rowIde);
        emailAddressContactMethod.setRequestedAction(Action.DELETE);

        MaintenanceAuditContext maintenanceAudiContext = objectFactory.createMaintenanceAuditContext();
        if(cachedEmailContexts.getAuditContext() != null && StringUtils.isNotBlank(cachedEmailContexts.getAuditContext().getVersionNumber())){
            maintenanceAudiContext.setVersionNumber(cachedEmailContexts.getAuditContext().getVersionNumber());
        }
        maintenanceAudiContext.setIsActive(Boolean.FALSE);
        emailAddressContactMethod.setAuditContext(maintenanceAudiContext);

        ContactMethodIdentifier contactMethodIdentifier = factory.createContactMethodIdentifier();
        contactMethodIdentifier.setContactMethodId(cachedEmailContexts.getContactMethodIdentifier().getContactMethodId());
        emailAddressContactMethod.setInternalIdentifier(contactMethodIdentifier);

        emailAddressContactMethod.setUsageId("OTH");
        if(cachedEmailContexts.getStartDate() != null) {
            emailAddressContactMethod.setStartDate(cachedEmailContexts.getStartDate().getValue());
        }
        emailAddressContactMethod.setEndDate(DateUtil.convertDateInGregorianCalendar(new Date()));
        emailAddressContactMethod.setValidityStatus(cachedEmailContexts.getValidityStatus());
        emailAddressContactMethod.setIsActive("false");

        InvolvedPartyName involvedPartyName = objectFactory.createInvolvedPartyName();
        if (null != cachedEmailContexts.getAddressee() && null != cachedEmailContexts.getAddressee().getFullName()) {
            involvedPartyName.setFullName(cachedEmailContexts.getAddressee().getFullName());
            emailAddressContactMethod.setAddressee(involvedPartyName);
        }

    }

    private static void requestSettingsForEmailModify(EmailAddressContactMethod emailAddressContactMethod, au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.
retrievedetailsandarrangementrelationshipsforips.v10.svc0258.EmailAddressContactMethod cachedContextMethod,
            String roSeqNo, ObjectFactory objectFactory) {

        RowSetItemIdentifier rowSetItemIdentifier = new RowSetItemIdentifier();
        rowSetItemIdentifier.setSequenceNumber(roSeqNo);
        emailAddressContactMethod.setRowSetIdIdentifier(rowSetItemIdentifier);

        emailAddressContactMethod.setRequestedAction(Action.MODIFY);

        MaintenanceAuditContext maintenanceAuditContext = objectFactory.createMaintenanceAuditContext();
        if(cachedContextMethod.getAuditContext() != null && StringUtils.isNotBlank(cachedContextMethod.getAuditContext().getVersionNumber())){
            maintenanceAuditContext.setVersionNumber(cachedContextMethod.getAuditContext().getVersionNumber());
        }
        maintenanceAuditContext.setIsActive(Boolean.TRUE);
        emailAddressContactMethod.setAuditContext(maintenanceAuditContext);

        final au.com.westpac.gn.common.xsd.identifiers.v1.ObjectFactory factory = new au.com.westpac.gn.common.xsd.identifiers.v1.ObjectFactory();
        ContactMethodIdentifier contactMethodIdentifier =  factory.createContactMethodIdentifier();
        contactMethodIdentifier.setContactMethodId(cachedContextMethod.getContactMethodIdentifier().getContactMethodId());
        emailAddressContactMethod.setInternalIdentifier(contactMethodIdentifier);

        emailAddressContactMethod.setStartDate(DateUtil.convertDateInGregorianCalendar(new Date()));
        emailAddressContactMethod.setValidityStatus("C");
        emailAddressContactMethod.setIsActive("true");
        emailAddressContactMethod.setUsageId(cachedContextMethod.getUsage());

        InvolvedPartyName involvedPartyName = objectFactory.createInvolvedPartyName();
        if (null != cachedContextMethod.getAddressee() && null != cachedContextMethod.getAddressee().getFullName()) {
            involvedPartyName.setFullName(cachedContextMethod.getAddressee().getFullName());
            emailAddressContactMethod.setAddressee(involvedPartyName);
        }

        /* Set old values */
        setOldValues(emailAddressContactMethod, cachedContextMethod, objectFactory);
    }

    private static void setOldValues(EmailAddressContactMethod emailAddressContactMethod,
                                     au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.
retrievedetailsandarrangementrelationshipsforips.v10.svc0258.
                                             EmailAddressContactMethod cachedContextMethod, ObjectFactory objectFactory) {
        EmailAddressContactMethod oldContext = objectFactory.createEmailAddressContactMethod();
        oldContext.setRequestedAction(Action.DELETE);
        oldContext.setStartDate(cachedContextMethod.getStartDate().getValue());
        oldContext.setEndDate(DateUtil.convertDateInGregorianCalendar(new Date()));
        if (null != oldContext.getAuditContext()) {
            oldContext.getAuditContext().setVersionNumber(cachedContextMethod.getAuditContext().getVersionNumber());
            oldContext.getAuditContext().setIsActive(Boolean.FALSE);
        }
        if (null != oldContext.getInternalIdentifier()) {
            oldContext.getInternalIdentifier().setContactMethodId(cachedContextMethod.getContactMethodIdentifier().getContactMethodId());
        }
        oldContext.setValidityStatus("C");
        oldContext.setIsActive("true");
        oldContext.setUsageId(cachedContextMethod.getUsage());

        if (null != cachedContextMethod.getAddressee()) {
            InvolvedPartyName involvedPartyName = objectFactory.createInvolvedPartyName();
            involvedPartyName.setFullName(cachedContextMethod.getAddressee().getFullName());
            oldContext.setAddressee(involvedPartyName);
        }
        if (null != cachedContextMethod.getHasAddress()) {
            EmailAddress emailAddressOld = objectFactory.createEmailAddress();
            emailAddressOld.setEmailAddress(cachedContextMethod.getHasAddress().getEmailAddress());
            oldContext.setHasAddress(emailAddressOld);
        }
        emailAddressContactMethod.setHasOldValues(oldContext);
    }

    private static void requestSettingsForEmailAdd(EmailAddressContactMethod emailAddressContactMethod,
                                                   RetrieveDetailsAndArrangementRelationshipsForIPsResponse cachedResponse, String rowSeq, ObjectFactory objectFactory) {


        final au.com.westpac.gn.common.xsd.identifiers.v1.ObjectFactory factory = new au.com.westpac.gn.common.xsd.identifiers.v1.ObjectFactory();
        RowSetItemIdentifier rowIde = factory.createRowSetItemIdentifier();
        rowIde.setSequenceNumber(rowSeq);
        emailAddressContactMethod.setRowSetIdIdentifier(rowIde);

        emailAddressContactMethod.setRequestedAction(Action.ADD);

        MaintenanceAuditContext maintenanceAudiContext = objectFactory.createMaintenanceAuditContext();
        maintenanceAudiContext.setIsActive(Boolean.TRUE);
        maintenanceAudiContext.setLastUpdateTimestamp(DateUtil.convertDateInGregorianCalendar(new Date()));
        emailAddressContactMethod.setAuditContext(maintenanceAudiContext);

        emailAddressContactMethod.setIsActive("true");
        emailAddressContactMethod.setUsageId("OTH");
        emailAddressContactMethod.setStartDate(DateUtil.convertDateInGregorianCalendar(new Date()));
        emailAddressContactMethod.setValidityStatus("C");

        InvolvedPartyName involvedPartyName = objectFactory.createInvolvedPartyName();
        involvedPartyName.setFullName(getFullNameFromCachedResponse(cachedResponse));
        if(StringUtils.isNotBlank(involvedPartyName.getFullName())) {
            emailAddressContactMethod.setAddressee(involvedPartyName);
        }
    }

    private static String getFullNameFromCachedResponse(RetrieveDetailsAndArrangementRelationshipsForIPsResponse cachedResponse) {

        for (au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.
retrievedetailsandarrangementrelationshipsforips.v10.svc0258.EmailAddressContactMethod emailAddressContactMethod :
                cachedResponse.getIndividual().getHasEmailAddressContactMethod()) {

            if (null != emailAddressContactMethod.getAddressee() && null != emailAddressContactMethod.getAddressee().getFullName()) {
                return emailAddressContactMethod.getAddressee().getFullName();
            }
        }
        logger.error("Null addressee found for customer");
        return null;
    }

    private static Map<String, au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.
retrievedetailsandarrangementrelationshipsforips.v10.svc0258.EmailAddressContactMethod>
    applyRoleBasedSettings(CustomerData customerData, RetrieveDetailsAndArrangementRelationshipsForIPsResponse cachedResponse,
                           MaintainIPContactMethodsRequest maintainIPContactMethodsRequest) {

        Map<String, au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.
retrievedetailsandarrangementrelationshipsforips.v10.svc0258.EmailAddressContactMethod> cachedEmailContexts
                = null;

        if (RoleType.INDIVIDUAL.equals(customerData.getRequest().getInvolvedPartyRoleType())) {
            maintainIPContactMethodsRequest.setInvolvedPartyType(InvolvedPartyType.INDIVIDUAL);
            cachedEmailContexts = getCachedEmailContextsIntoAMap(cachedResponse.getIndividual().getHasEmailAddressContactMethod());
        } else if (RoleType.ORGANISATION.equals(customerData.getRequest().getInvolvedPartyRoleType())) {
            maintainIPContactMethodsRequest.setInvolvedPartyType(InvolvedPartyType.ORGANISATION);
            cachedEmailContexts = getCachedEmailContextsIntoAMap(cachedResponse.getOrganisation().getHasEmailAddressContactMethod());
        }
        return cachedEmailContexts;
    }

    private static InvolvedPartyIdentifier getInvolvedPartyIdentifier(CustomerData customerData) {
        final au.com.westpac.gn.common.xsd.identifiers.v1.ObjectFactory factory = new au.com.westpac.gn.common.xsd.identifiers.v1.ObjectFactory();
        InvolvedPartyIdentifier involvedPartyIdentifier = factory.createInvolvedPartyIdentifier();
        involvedPartyIdentifier.setIdentificationScheme(IdentificationScheme.CIS_KEY);
        involvedPartyIdentifier.setInvolvedPartyId(customerData.getRequest().getCISKey().getId());
        involvedPartyIdentifier.setSourceSystem(ServiceConstants.UCM);
        return involvedPartyIdentifier;
    }

    private static Map<String, au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.
retrievedetailsandarrangementrelationshipsforips.v10.svc0258.EmailAddressContactMethod> getCachedEmailContextsIntoAMap(
            List<au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.
retrievedetailsandarrangementrelationshipsforips.v10.svc0258.EmailAddressContactMethod> contextList) {

        Map<String, au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.
retrievedetailsandarrangementrelationshipsforips.v10.svc0258.EmailAddressContactMethod> retrievedEmailsMap =
                new HashMap<>();
        for (au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.
retrievedetailsandarrangementrelationshipsforips.v10.svc0258.EmailAddressContactMethod
                cachedEmailContexts : contextList) {
            retrievedEmailsMap.put(cachedEmailContexts.getHasAddress().getEmailAddress(), cachedEmailContexts);
        }
        return retrievedEmailsMap;
    }
}
