package com.bt.nextgen.service.btesb.supernotification;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.btesb.gateway.WebServiceHandler;
import com.bt.nextgen.service.integration.supermatch.Member;
import com.bt.nextgen.service.integration.supermatch.SuperFundAccount;
import com.bt.nextgen.service.integration.supernotification.SuperNotificationResponseHolder;
import com.bt.nextgen.service.integration.supernotification.SuperNotificationIntegrationService;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import ns.btfin_com.party.v3_0.CustomerNoAllIssuerType;
import ns.btfin_com.party.v3_0.CustomerNoBaseIssuerType;
import ns.btfin_com.party.v3_0.CustomerNumberIdentifier;
import ns.btfin_com.product.superannuationretirement.superannuationnotification.superannuationnotificationservice.superannuationnotificationrequest.v1_0.NotificationRequestContextType;
import ns.btfin_com.product.superannuationretirement.superannuationnotification.superannuationnotificationservice.superannuationnotificationrequest.v1_0.NotifyCustomerDeliveryMethodType;
import ns.btfin_com.product.superannuationretirement.superannuationnotification.superannuationnotificationservice.superannuationnotificationrequest.v1_0.NotifyCustomerNotificationType;
import ns.btfin_com.product.superannuationretirement.superannuationnotification.superannuationnotificationservice.superannuationnotificationrequest.v1_0.NotifyCustomerNotificationsType;
import ns.btfin_com.product.superannuationretirement.superannuationnotification.superannuationnotificationservice.superannuationnotificationrequest.v1_0.NotifyCustomerRequestMsgType;
import ns.btfin_com.product.superannuationretirement.superannuationnotification.superannuationnotificationservice.superannuationnotificationrequest.v1_0.ObjectFactory;
import ns.btfin_com.product.superannuationretirement.superannuationnotification.v1_0.NotificationType;
import ns.btfin_com.product.superannuationretirement.superannuationnotification.v1_0.NotifyDocumentPropertiesType;
import ns.btfin_com.product.superannuationretirement.superannuationnotification.v1_0.NotifyDocumentPropertyType;
import ns.btfin_com.product.superannuationretirement.superannuationnotification.v1_0.NotifyDocumentPropertyValueType;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SuperNotificationIntegrationServiceImpl extends AbstractAvaloqIntegrationService implements SuperNotificationIntegrationService {

    @Autowired
    @Qualifier("btEsbWebServiceHandler")
    private WebServiceHandler webServiceHandler;

    private static final String NOTIFY_CUSTOMER = "notifyCustomer";
    private static final String STATUS_ERROR = "Error";
    private static final String TRACKING_VERSION = "1_0";
    private static final String CHANNEL = "Online";
    private static final String EMAIL_CHANNEL = "Email";
    private static final String BRAND = "WPAC";
    private static final String DOCUMENT_TYPE = "SGPDFNotifyMe";

    private static final Logger logger = LoggerFactory.getLogger(SuperNotificationIntegrationServiceImpl.class);

    /**
     * Triggers a notification in ECO to send out the SG letter to the user
     *
     * @param customerId       - customer identifier
     * @param superFundAccount - Super fund account
     * @param serviceErrors    - Object to capture service errors
     */
    @Override
    public boolean notifyCustomer(String customerId, SuperFundAccount superFundAccount, ServiceErrors serviceErrors) {
        final ObjectFactory objectFactory = new ObjectFactory();
        NotifyCustomerRequestMsgType requestMsgType = objectFactory.createNotifyCustomerRequestMsgType();

        requestMsgType.setContext(createRequestContext(objectFactory, customerId));
        requestMsgType.setNotifications(createRequestForNotifications(objectFactory, superFundAccount));

        final SuperNotificationResponseHolder response = webServiceHandler.sendToWebServiceAndParseResponseToDomain(NOTIFY_CUSTOMER, requestMsgType,
                SuperNotificationResponseHolderImpl.class, serviceErrors);

        if (STATUS_ERROR.equals(response.getStatus())) {
            logger.error("Error in Eco notifyCustomer service with trackingId:{} - {}", requestMsgType.getContext().getTrackingID(),
                    new ServiceErrorImpl(response.getError().getDescription()));
            return false;
        }
        return true;
    }

    private NotifyCustomerNotificationsType createRequestForNotifications(ObjectFactory objectFactory, SuperFundAccount superFundAccount) {
        final NotifyCustomerNotificationsType notifyCustomerNotificationsType = objectFactory.createNotifyCustomerNotificationsType();

        if (CollectionUtils.isNotEmpty(superFundAccount.getMembers())) {
            final Member member = superFundAccount.getMembers().get(0);
            notifyCustomerNotificationsType.setCustomerNumberIdentifier(createCustomerIdentifierRequest(objectFactory, member));
            notifyCustomerNotificationsType.getNotification().add(getNotification(objectFactory, member, superFundAccount));
        }
        return notifyCustomerNotificationsType;
    }

    private NotificationType getNotification(ObjectFactory objectFactory, Member member, SuperFundAccount superFundAccount) {
        final NotifyCustomerDeliveryMethodType deliveryMethod = objectFactory.createNotifyCustomerDeliveryMethodType();
        deliveryMethod.setOnline(EMAIL_CHANNEL);

        final NotifyCustomerNotificationType notificationType = objectFactory.createNotifyCustomerNotificationType();
        notificationType.setDocumentType(DOCUMENT_TYPE);
        notificationType.setDeliveryMethod(deliveryMethod);
        notificationType.setDocumentProperties(getDocumentProperties(objectFactory, member, superFundAccount.getAccountNumber(), superFundAccount.getUsi()));
        return notificationType;
    }

    private NotifyDocumentPropertiesType getDocumentProperties(ObjectFactory objectFactory, Member member, String accountNumber, String usi) {
        // Set document properties
        final NotifyDocumentPropertiesType notificationDocumentProps = objectFactory.createNotifyCustomerDocPropertiesType();
        final List<NotifyDocumentPropertyType> props = notificationDocumentProps.getDocumentProperty();
        final DateTimeFormatter formatter = DateTimeFormat.forPattern("DD/MM/YYYY");

        props.add(createDocumentProperty(objectFactory, "DOB", formatter.print(member.getDateOfBirth())));
        props.add(createDocumentProperty(objectFactory, "Channel", CHANNEL));
        props.add(createDocumentProperty(objectFactory, "Brand", BRAND));
        props.add(createDocumentProperty(objectFactory, "MemberID", accountNumber));
        props.add(createDocumentProperty(objectFactory, "USI", usi));
        props.add(createDocumentProperty(objectFactory, "GivenName", member.getFirstName()));
        props.add(createDocumentProperty(objectFactory, "LastName", member.getLastName()));
        props.add(createDocumentProperty(objectFactory, "PreferredEmailAddress", member.getEmailAddresses().get(0)));

        return notificationDocumentProps;
    }

    private NotifyDocumentPropertyType createDocumentProperty(ObjectFactory objectFactory, String key, String value) {
        final NotifyDocumentPropertyValueType propertyValue = objectFactory.createNotifyCustomerDocPropertyValueType();
        propertyValue.setDocumentStringValue(value);

        final NotifyDocumentPropertyType property = objectFactory.createNotifyCustomerDocPropertyType();
        property.setDocumentPropertyName(key);
        property.setDocumentPropertyValue(propertyValue);

        return property;
    }

    private CustomerNumberIdentifier createCustomerIdentifierRequest(ObjectFactory objectFactory, Member member) {
        final CustomerNoBaseIssuerType baseIssuerType = CustomerNoBaseIssuerType.fromValue(member.getIssuer());
        final CustomerNumberIdentifier customerNumberIdentifier = objectFactory.createNotifyCustomerNumberIdentificationType();

        customerNumberIdentifier.setCustomerNumber(member.getCustomerId());
        customerNumberIdentifier.setCustomerNumberIssuer(CustomerNoAllIssuerType.fromValue(baseIssuerType));

        return customerNumberIdentifier;
    }

    private NotificationRequestContextType createRequestContext(ObjectFactory objectFactory, String customerId) {

        final NotificationRequestContextType context = objectFactory.createNotificationRequestContextType();
        context.setVersion(TRACKING_VERSION);
        context.setResponseVersion(TRACKING_VERSION);
        context.setSubmitter(customerId);
        context.setRequester(customerId);
        context.setRequestingSystem(CHANNEL);
        context.setTrackingID(UUID.randomUUID().toString());
        return context;
    }
}
