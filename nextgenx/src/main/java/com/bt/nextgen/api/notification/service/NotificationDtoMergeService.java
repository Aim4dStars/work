package com.bt.nextgen.api.notification.service;

import com.bt.nextgen.api.notification.model.NotificationCountDto;
import com.bt.nextgen.api.notification.model.NotificationDto;
import com.bt.nextgen.api.notification.model.NotificationListDto;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.ResultListDto;
import com.bt.nextgen.core.api.operation.ChainedControllerOperation;
import com.bt.nextgen.core.api.operation.ControllerOperation;

/**
 * This merge service combines the results of two separate calls into one response.
 * The two calls are get list of notifications and get priority and unread counts.
 */

public class NotificationDtoMergeService<T extends BaseDto> extends ChainedControllerOperation {
    private ControllerOperation countResponse;

    public NotificationDtoMergeService(ControllerOperation listResponse, ControllerOperation countResponse) {
        super(listResponse);
        this.countResponse = countResponse;
    }

    @Override
    protected ApiResponse performChainedOperation(ApiResponse response) {
        NotificationCountDto counts = (NotificationCountDto) countResponse.performOperation().getData();
        ResultListDto<NotificationDto> results = (ResultListDto<NotificationDto>) response.getData();
        NotificationListDto mergedResponse = new NotificationListDto();
        mergedResponse.setPriorityCount(counts.getPriorityCount());
        mergedResponse.setUnreadCount(counts.getUnreadCount());
        mergedResponse.setNotificationList(results.getResultList());

        return new ApiResponse(response.getApiVersion(), response.getStatus(), mergedResponse, response.getError(),
            response.getPaging());
    }
}
