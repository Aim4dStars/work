package com.bt.nextgen.api.userpreference.controller;

import com.bt.nextgen.api.userpreference.model.UserPreferenceDto;
import com.bt.nextgen.api.userpreference.model.UserPreferenceDtoKey;
import com.bt.nextgen.api.userpreference.service.UserPreferenceDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.exception.NotFoundException;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.ResultListDto;
import com.bt.nextgen.service.ServiceErrors;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class UserPreferenceApiControllerTest {

    private static final String USER_ID = "user1";
    private static final String DEFAULT_ROLE = "defaultrole";
    private static final String USER_TYPE = "job";

    @InjectMocks
    private UserPreferenceApiController userPreferenceApiController;

    @Mock
    private UserPreferenceDtoService userPreferenceService;

    private List<UserPreferenceDto> result = new ArrayList();

    @Test
    public void testFindUserPreferenceSuccess() throws Exception {
        Mockito.when(userPreferenceService.search(Mockito.any(UserPreferenceDtoKey.class),
            Mockito.any(ServiceErrors.class))).thenReturn(Arrays.asList(new UserPreferenceDto(USER_TYPE,
            DEFAULT_ROLE, "paraplanner")));
        ApiResponse response = userPreferenceApiController.find(USER_ID, DEFAULT_ROLE);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getApiVersion(), ApiVersion.CURRENT_VERSION);
        Assert.assertEquals(((ResultListDto<UserPreferenceDto>) response.getData()).getResultList().size(), 1);
        Assert.assertEquals(((ResultListDto<UserPreferenceDto>) response.getData()).getResultList().get(0).getKey()
            .getUserType(), USER_TYPE);
        Assert.assertEquals(((ResultListDto<UserPreferenceDto>) response.getData()).getResultList().get(0).getKey()
            .getPreferenceId(), DEFAULT_ROLE);
        Assert.assertEquals(((ResultListDto<UserPreferenceDto>) response.getData()).getResultList().get(0).getValue(),
            "paraplanner");
    }

    @Test
    public void testFindUserPreferenceNoMatch() throws Exception {
        Mockito.when(userPreferenceService.search(Mockito.any(UserPreferenceDtoKey.class),
            Mockito.any(ServiceErrors.class))).thenReturn(result);
        ApiResponse response = userPreferenceApiController.find(USER_ID, DEFAULT_ROLE);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getApiVersion(), ApiVersion.CURRENT_VERSION);
        Assert.assertEquals(((ResultListDto<UserPreferenceDto>) response.getData()).getResultList().size(), 0);
    }

    @Test
    public void testFindUserPreferenceMultipleResults() throws Exception {
        Mockito.when(userPreferenceService.search(Mockito.any(UserPreferenceDtoKey.class),
            Mockito.any(ServiceErrors.class))).thenReturn(Arrays.asList(new UserPreferenceDto(USER_TYPE, "pref1",
                "value1"), new UserPreferenceDto(USER_TYPE, "pref2", "value2"),
            new UserPreferenceDto(USER_TYPE, "pref3", "value3")));
        ApiResponse response = userPreferenceApiController.find(USER_ID, "pref1,pref2,pref3");
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getApiVersion(), ApiVersion.CURRENT_VERSION);
        Assert.assertEquals(((ResultListDto<UserPreferenceDto>) response.getData()).getResultList().size(), 3);
    }

    @Test
    public void testUpdateSuccess() throws Exception {
        UserPreferenceDto dto = new UserPreferenceDto(USER_TYPE, "pref1", "value1");
        Mockito.when(userPreferenceService.update(Mockito.any(UserPreferenceDto.class),
            Mockito.any(ServiceErrors.class))).thenReturn(dto);
        ApiResponse response = userPreferenceApiController.update(dto);
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getApiVersion(), ApiVersion.CURRENT_VERSION);
        Assert.assertEquals(((UserPreferenceDto) response.getData()).getKey().getPreferenceId(), "pref1");
        Assert.assertEquals(((UserPreferenceDto) response.getData()).getValue(), "value1");
    }
}
