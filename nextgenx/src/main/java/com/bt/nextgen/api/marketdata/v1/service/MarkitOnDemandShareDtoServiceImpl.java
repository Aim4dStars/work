package com.bt.nextgen.api.marketdata.v1.service;

import com.bt.nextgen.api.marketdata.v1.model.MarkitShareNotificationType;
import com.bt.nextgen.api.marketdata.v1.model.ShareNotificationsDto;
import com.bt.nextgen.core.domain.key.StringIdKey;
import com.bt.nextgen.core.type.ConsistentEncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.core.security.integration.account.PersonKey;
import com.bt.nextgen.service.integration.messages.*;
import com.btfin.panorama.core.security.integration.messages.NotificationResolutionBaseKey;
import com.btfin.panorama.core.security.integration.messages.ResolutionGroup;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@SuppressWarnings({"squid:S1199","squid:S1151"})
public class MarkitOnDemandShareDtoServiceImpl implements MarkitOnDemandShareDtoService {

    private static final Logger logger = LoggerFactory.getLogger(MarkitOnDemandShareDtoServiceImpl.class);

    @Autowired
    private NotificationIntegrationService notificationIntegrationService;

    private static enum NotificationCategory {
        ACCOUNT,CLIENT;
    }

    @Override
    public ShareNotificationsDto create(ShareNotificationsDto shareNotificationsDto, ServiceErrors serviceErrors) {

        List<com.btfin.panorama.core.security.integration.messages.NotificationAddRequest> notifications = new ArrayList();

        if (CollectionUtils.isNotEmpty(shareNotificationsDto.getConsistentlyEncryptedClientKeys())) {

            createNotificationForGivenKey(shareNotificationsDto,notifications,NotificationCategory.CLIENT,
                    shareNotificationsDto.getConsistentlyEncryptedClientKeys());

        } else if (CollectionUtils.isNotEmpty(shareNotificationsDto.getConsistentlyEncryptedAccountKeys())) {

            createNotificationForGivenKey(shareNotificationsDto, notifications,
                    NotificationCategory.ACCOUNT, shareNotificationsDto.getConsistentlyEncryptedAccountKeys());
        }
        String status = notificationIntegrationService.addNotifications(notifications, serviceErrors);
        ShareNotificationsDto response = new ShareNotificationsDto();
        response.setStatus(status);
        response.setKey(StringUtils.join(shareNotificationsDto.getConsistentlyEncryptedClientKeys(), ','));
        return response;
    }

    private void createNotificationForGivenKey(ShareNotificationsDto shareNotificationsDto,
                                               List<com.btfin.panorama.core.security.integration.messages.NotificationAddRequest> notifications,
                                               NotificationCategory category,
                                               List<String> encryptedKeys) {

        for (String encryptedKey : encryptedKeys) {
            //create new impl that implements the adapter pattern for this. Should use the fields url/urltest/personalisation message to create the getMessageContext (insert XML into this field)
            NotificationAddRequestImpl notificationAddRequest = new NotificationAddRequestImpl();

            setNotificationResolutionBaseKey(category, encryptedKey, notificationAddRequest);

            setNotificationType(shareNotificationsDto, notificationAddRequest);

            notificationAddRequest.setMessageContext("");
            notificationAddRequest.setType(shareNotificationsDto.getType());
            notificationAddRequest.setUrl(shareNotificationsDto.getUrl());
            notificationAddRequest.setUrlText(shareNotificationsDto.getUrlText());
            notificationAddRequest.setPersonalizedMessage(shareNotificationsDto.getPersonalizedMessage());

            notificationAddRequest.setTriggeringObjectKey(new StringIdKey(ConsistentEncodedString.toPlainText(encryptedKey)));
            notifications.add(notificationAddRequest);
        }
    }

    private void setNotificationResolutionBaseKey(NotificationCategory category, String encryptedKey,
                                                  NotificationAddRequestImpl notificationAddRequest) {

        switch (category) {

            case  ACCOUNT: {
                notificationAddRequest.setNotificationResolutionBaseKey(new com.btfin.panorama.core.security.integration.messages.NotificationResolutionBaseKey(
                        AccountKey.valueOf(ConsistentEncodedString.toPlainText(encryptedKey))));
                break;
            }
            case CLIENT: {

                NotificationResolutionBaseKey clientNotificationKey = new NotificationResolutionBaseKey(PersonKey.valueOf(ConsistentEncodedString.toPlainText(encryptedKey)));
                clientNotificationKey.setResolutionGroup(ResolutionGroup.PERSON);
                notificationAddRequest.setNotificationResolutionBaseKey(clientNotificationKey);
                break;
            }
            default: {
                logger.error("Wrong category type found.....");
            }
        }
    }

    private void setNotificationType(ShareNotificationsDto createNotificationDto, NotificationAddRequestImpl notificationAddRequest) {

        switch (MarkitShareNotificationType.fromCode(createNotificationDto.getType())) {
            case ASX :  {
                notificationAddRequest.setNotificationEventType(com.btfin.panorama.core.security.integration.messages.NotificationEventType.ASX_EMAIL);
                break;
            }
            case NEWS: {
                notificationAddRequest.setNotificationEventType(com.btfin.panorama.core.security.integration.messages.NotificationEventType.NEWS_EMAIL);
                break;
            }
            default: {
                logger.error("Unsupported notification type rquested : " + createNotificationDto.getType() );
                throw new IllegalArgumentException("Wrong notification type found : " +
                        createNotificationDto.getType());
            }
        }
    }
}