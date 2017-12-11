package com.bt.nextgen.api.user.v1.controller;

import com.bt.nextgen.api.user.v1.model.UserNoticesDto;
import com.bt.nextgen.api.user.v1.model.UserNoticesDtoKey;
import com.bt.nextgen.api.user.v1.service.UserNoticesDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindAll;
import com.bt.nextgen.core.api.operation.Update;
import com.btfin.panorama.core.security.profile.UserProfileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * This API is used to retrieve available updates for a user
 * <p/>
 * GET: secure/api/user/v1_0/notices
 * <p/>
 * POST: secure/api/user/v1_0/notices/{notice-id}/{version}
 */
@Api("Provides services to fetch and modify user updates/notices")
@Controller("UserUpdatesApiControllerV1")
public class UserNoticesApiController {

    @Autowired
    @Qualifier("UserUpdatesDtoServiceV1")
    private UserNoticesDtoService userNoticesDtoService;

    @Autowired
    private UserProfileService userProfileService;

    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value="Retrieves user notices for the current user", response = UserNoticesDto.class, responseContainer = "List")
    @RequestMapping(method = RequestMethod.GET, value = "${api.user.v1.uri.notices}")
    public
    @ResponseBody
    ApiResponse getUserUpdates() {
        return new FindAll<>(ApiVersion.CURRENT_VERSION, userNoticesDtoService).performOperation();
    }

    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value="Modifies user notices for the current user")
    @RequestMapping(method = RequestMethod.POST, value = "${api.user.v1.uri.updatenotices}")
    public
    @ResponseBody
    ApiResponse modifyUserUpdates(@PathVariable("notice-id") @ApiParam(value="Notice ID for the user notice to update") String noticeId,
                                  @PathVariable("version") @ApiParam(value="Version of the user notice to update") Integer version) {
        final UserNoticesDtoKey userNoticesDtoKey = new UserNoticesDtoKey(userProfileService.getGcmId(), noticeId, version);
        return new Update<>(ApiVersion.CURRENT_VERSION, userNoticesDtoService, null, new UserNoticesDto(userNoticesDtoKey)).performOperation();
    }
}
