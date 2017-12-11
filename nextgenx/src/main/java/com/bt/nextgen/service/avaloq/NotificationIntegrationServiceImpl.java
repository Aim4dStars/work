package com.bt.nextgen.service.avaloq;

import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.avaloq.notification.NotificationCountCacheService;
import com.btfin.panorama.core.security.integration.messages.Notification;
import com.btfin.panorama.core.security.integration.messages.NotificationAddRequest;
import com.bt.nextgen.service.integration.messages.NotificationAddRequestImpl;
import com.bt.nextgen.service.integration.messages.NotificationIntegrationService;
import com.btfin.panorama.core.security.integration.messages.NotificationResponse;
import com.btfin.panorama.core.security.integration.messages.NotificationUnreadCount;
import com.btfin.panorama.core.security.integration.messages.NotificationUnreadCountResponse;
import com.btfin.panorama.core.security.integration.messages.NotificationUpdateRequest;
import com.btfin.abs.trxservice.ntfcn.v1_0.NtfcnReq;
import com.btfin.abs.trxservice.ntfcn.v1_0.NtfcnRsp;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Service Implementation for Notification Service.
 */
@SuppressWarnings("squid:S1200")
@Service
public class NotificationIntegrationServiceImpl extends AbstractAvaloqIntegrationService implements
        NotificationIntegrationService {
    @Autowired
    private AvaloqExecute avaloqExecute;
    @Autowired
    private AvaloqGatewayHelperService webserviceClient;
    @Autowired
    private UserProfileService userProfile;

    @Autowired
    @Qualifier("notificationCountCacheService")
    private NotificationCountCacheService notificationCountCacheService;

   	private static final Logger logger = LoggerFactory.getLogger(NotificationIntegrationServiceImpl.class);


    private final String NOTIFICATION_PAYLOAD_TEMPLATE = "invURL|%s||MsgTitle|%s||AdviserMsg|%s";

    @Override
    public List<Notification> loadNotifications(List job_profile_id, DateTime startDate, DateTime endDate, ServiceErrors serviceErrors) {

        logger.debug("Entered loadNotifications Method");
        NotificationResponse response = avaloqExecute.executeReportRequestToDomain(new AvaloqReportRequest(
                Template.GET_NOTIFICATION_MESSAGES.getName()).forProfileIds(job_profile_id).fromCreationTimestamp(startDate.toString())
                        .toCreationTimestamp(endDate.toString()),
                NotificationResponseImpl.class,
                serviceErrors);

        /* We have to set back the additional notification details coming as an XML to the notification instance */
        if (!CollectionUtils.isEmpty(response.getNotification())) {
             setAdditionalNotificationFieldsForParsedNotificationsJSON(response.getNotification());
        }

            return (response != null && null != response.getNotification()) ? response.getNotification() : new ArrayList<Notification>();
        }

    private void setAdditionalNotificationFieldsForParsedNotificationsJSON(List<Notification> notifications) {

        try {
            for (int i =0;i<notifications.size();i++) {

                NotificationImpl notification = (NotificationImpl) notifications.get(i);
                if (StringUtils.isNotEmpty(notification.getNotificationMessage()) &&
                        notification.getNotificationMessage().contains("AdviserMsg") ) {

                    String[] pairArray = notification.getNotificationMessage().split(Pattern.quote("||"));
                    for (String pair : pairArray) {
                        logger.debug("Keyvalue Pair : {}", pair);
                        String [] keyValue = pair.split(Pattern.quote("|"));
                        if (keyValue.length == 2) {
                            String key = keyValue[0];
                            String value = keyValue[1];
                            setNotificationDetails(notification, key, value);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error occurred in parsing json", e);
        }
    }

    private void setNotificationDetails(NotificationImpl notification, String key, String value) {
        switch (key) {
            case "SharedInfoType" : {
                notification.setType(value);
                break;
            }
            case "invURL" : {
                notification.setUrl(value);
                break;
            }
            case "MsgTitle" : {
                notification.setUrlText(value);
                break;
            }
            case "AdviserMsg" : {
                notification.setPersonalizedMessage(value);
                break;
            }
            default: {
                logger.error("Unknown key {} and value {} found",key,value);
                throw new RuntimeException("Not supported shared notification type found");
            }
        }
    }

    @Override
    public NotificationUnreadCount getUnReadNotification(ServiceErrors serviceErrors) {
        logger.debug("Entered getUnReadNotification Method");
        NotificationUnreadCount unreadCountResponse = notificationCountCacheService.getNotificationCount(serviceErrors);
        return unreadCountResponse;
    }

    @Override
    public NotificationUnreadCountResponse getDetailedUnReadNotification(ServiceErrors serviceErrors) {
        logger.debug("Entered getDetailedUnReadNotification Method");
        NotificationUnreadCountResponse unreadCountResponse = notificationCountCacheService.getDetailedNotificationCount(serviceErrors);
        return unreadCountResponse;
    }

        @Override
        public String updateNotification ( final NotificationUpdateRequest request, final ServiceErrors serviceErrors){
            logger.debug("Entered updateNotification Method");
            return new IntegrationSingleOperation<String>("updateNotification", serviceErrors) {
                @Override
                public String performOperation() {
                    NtfcnReq ntfcnReq = AvaloqUtils.makeUpdateMultipleNotificationRequestFor(Arrays.asList(request));
                    NtfcnRsp ntfcnRsp = webserviceClient.sendToWebService(ntfcnReq, AvaloqOperation.NTFCN_REQ, serviceErrors);
                    String status = ntfcnRsp.getRspText().value();
                    logger.debug("Notification Update Status is: {} ", status);
                    return status;
                }
            }.run();

        }

        @Override
        public String updateNotifications ( final List<NotificationUpdateRequest> notificationUpdateRequest,
        final ServiceErrors serviceErrors){

            logger.debug("Entered updateNotification Method");
            return new IntegrationSingleOperation<String>("updateNotification", serviceErrors) {
                @Override
                public String performOperation() {
                    NtfcnReq ntfcnReq = AvaloqUtils.makeUpdateMultipleNotificationRequestFor(notificationUpdateRequest);
                    NtfcnRsp ntfcnRsp = webserviceClient.sendToWebService(ntfcnReq, AvaloqOperation.NTFCN_REQ, serviceErrors);
                    String status = ntfcnRsp.getRspText().value();
                    logger.debug("Notifications Update Status is: {} ", status);
                    return status;
                }
            }.run();
        }

        @Override
        public String addNotification ( final NotificationAddRequest notificationAddRequest,
        final ServiceErrors serviceErrors){

            return new IntegrationSingleOperation<String>("addNotification", serviceErrors) {
                @Override
                public String performOperation() {
                    NtfcnReq notifiAddReq = AvaloqUtils.makeAddNotificationRequestFor(Arrays.asList(notificationAddRequest));
                    NtfcnRsp ntfcnRsp = webserviceClient.sendToWebService(notifiAddReq, AvaloqOperation.NTFCN_REQ, serviceErrors);
                    String status = ntfcnRsp.getRspText().value();
                    logger.debug("Notification Add Status is: {} ", status);
                    return status;
                }
            }.run();
        }

        @Override
        public String addNotifications ( final List<NotificationAddRequest> notificationAddRequest,
        final ServiceErrors serviceErrors){
            return new IntegrationSingleOperation<String>("addNotification", serviceErrors) {
                @Override
                public String performOperation() {
                    setNotificationMessageContext(notificationAddRequest);
                    NtfcnReq notifiAddReq = AvaloqUtils.makeAddNotificationRequestFor(notificationAddRequest);
                    NtfcnRsp ntfcnRsp = webserviceClient.sendToWebService(notifiAddReq, AvaloqOperation.NTFCN_REQ, serviceErrors);
                    String status = ntfcnRsp.getRspText().value();
                    logger.debug("Notifications Add Status is: {} ", status);
                    return status;
                }
            }.run();
        }

    private void setNotificationMessageContext(List<NotificationAddRequest> notificationAddRequestList) {

        for (NotificationAddRequest notificationAddRequestInstance : notificationAddRequestList ) {
            NotificationAddRequestImpl notificationAddRequestImpl = (NotificationAddRequestImpl) notificationAddRequestInstance;
            String payload = String.format(NOTIFICATION_PAYLOAD_TEMPLATE,
                    notificationAddRequestImpl.getUrl(),notificationAddRequestImpl.getUrlText(),
                    notificationAddRequestImpl.getPersonalizedMessage() != null ? notificationAddRequestImpl.getPersonalizedMessage() : "");
            notificationAddRequestImpl.setMessageContext(payload);
        }
    }
}
