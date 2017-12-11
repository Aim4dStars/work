package com.bt.nextgen.api.drawdown.v2.controller;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.drawdown.v2.model.DrawdownDetailsDto;
import com.bt.nextgen.api.drawdown.v2.service.DrawdownDetailsDtoService;
import com.bt.nextgen.api.drawdown.v2.service.DrawdownDtoService;
import com.bt.nextgen.config.JsonObjectMapper;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.core.security.api.model.PermissionsDto;
import com.bt.nextgen.core.security.api.service.PermissionAccountDtoService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.drawdownstrategy.DrawdownStrategy;
import com.btfin.panorama.core.security.profile.UserProfileService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DrawdownApiControllerTest {
    @InjectMocks
    private DrawdownApiController drawdownApiController;

    @Mock
    private DrawdownDtoService drawdownDtoService;

    @Mock
    private PermissionAccountDtoService permissionAccountDtoService;

    @Mock
    private UserProfileService profileService;

    @Mock
    private PermissionsDto permissionsDto;

    @Mock
    private DrawdownDetailsDtoService priorityDtoService;

    @Mock
    private JsonObjectMapper objectMapper;

    private DrawdownDetailsDto mockDrawdownDto;

    private static final String drawdownPriorityJsonObject = "{\"drawdownType\":\"individual_assets\",\"key\":{\"accountId\":\"83DD6F9EBA702FE9D2B8DE23EF8F9933404D197CBE512D9B\"},\"priorityDrawdownList\":[{\"assetId\":\"110523\",\"drawdownPriority\":1}],\"type\":\"Drawdown\"}";


    @Before
    public void setUp() throws Exception {
        mockDrawdownDto = Mockito.mock(DrawdownDetailsDto.class);
    }

    @Test
    public final void testGetDrawdown() throws Exception {
        DrawdownDetailsDto drawdownDto = new DrawdownDetailsDto(new AccountKey("accountId"), "prorata", null);
        Mockito.when(drawdownDtoService.find(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class))).thenReturn(
                drawdownDto);
        KeyedApiResponse<AccountKey> response = drawdownApiController.getDrawdown("accountId");
        Assert.assertTrue(response != null);

    }

    @Test(expected = AccessDeniedException.class)
    public final void testUpdateAccessDeniedWhenEmulation() {
        Mockito.when(profileService.isEmulating()).thenReturn(true);
        Mockito.when(permissionsDto.hasPermission(Mockito.anyString())).thenReturn(true);

        drawdownApiController.updateDrawdownStrategy("accountId", "drawdown");
    }

    @Test
    public void test_updateDrawdownStrategy() {
        DrawdownDetailsDto drawdownDto = new DrawdownDetailsDto(new AccountKey("accountId"), "prorata", null);
        Mockito.when(drawdownDtoService.update(any(DrawdownDetailsDto.class), any(ServiceErrors.class))).thenReturn(drawdownDto);
        KeyedApiResponse<AccountKey> response = drawdownApiController.updateDrawdownStrategy("accountId",
                DrawdownStrategy.PRORATA.getDisplayName());
        Assert.assertNotNull(response);
    }

    @Test
    public void testRetrieveAssetPriorityDrawdown() {
        AccountKey key = new AccountKey("accountId");
        DrawdownDetailsDto aDto = mock(DrawdownDetailsDto.class);
        when(aDto.getDrawdownType()).thenReturn(DrawdownStrategy.ASSET_PRIORITY.getIntlId());
        when(aDto.getKey()).thenReturn(key);

        when(priorityDtoService.find(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(aDto);
        KeyedApiResponse response = drawdownApiController.getPriorityDrawdown("accountId");
        Assert.assertTrue(response != null);
    }

    @Test(expected = AccessDeniedException.class)
    public final void test_validate_whenEmulation() throws IOException {
        Mockito.when(profileService.isEmulating()).thenReturn(true);
        Mockito.when(permissionsDto.hasPermission(Mockito.anyString())).thenReturn(true);

        drawdownApiController.createPriorityDrawdown("accountId", Boolean.TRUE.toString(), mockDrawdownDto);
    }

    @Test(expected = AccessDeniedException.class)
    public final void test_submit_whenEmulation() throws IOException {
        Mockito.when(profileService.isEmulating()).thenReturn(true);
        Mockito.when(permissionsDto.hasPermission(Mockito.anyString())).thenReturn(true);

        drawdownApiController.createPriorityDrawdown("accountId", Boolean.FALSE.toString(), mockDrawdownDto);
    }

    @Test
    public void test_validateDrawdown_withAssetPriority() throws Exception {
        Mockito.when(profileService.isEmulating()).thenReturn(false);
        Mockito.when(permissionsDto.hasPermission(Mockito.anyString())).thenReturn(true);

        JsonObjectMapper mapper = new JsonObjectMapper();
        DrawdownDetailsDto dto = mapper.readValue(drawdownPriorityJsonObject, DrawdownDetailsDto.class);

        Mockito.when(priorityDtoService.validate(any(DrawdownDetailsDto.class), any(ServiceErrors.class))).thenReturn(dto);

        KeyedApiResponse<AccountKey> response = drawdownApiController.createPriorityDrawdown(
                "83DD6F9EBA702FE9D2B8DE23EF8F9933404D197CBE512D9B", Boolean.TRUE.toString(), dto);

        Assert.assertNotNull(response);
        Assert.assertEquals("83DD6F9EBA702FE9D2B8DE23EF8F9933404D197CBE512D9B", response.getId().getAccountId());
    }

    @Test
    public void test_submitDrawdown_withAssetPriority() throws Exception {
        Mockito.when(profileService.isEmulating()).thenReturn(false);
        Mockito.when(permissionsDto.hasPermission(Mockito.anyString())).thenReturn(true);

        JsonObjectMapper mapper = new JsonObjectMapper();
        DrawdownDetailsDto dto = mapper.readValue(drawdownPriorityJsonObject, DrawdownDetailsDto.class);
        Mockito.when(priorityDtoService.submit(any(DrawdownDetailsDto.class), any(ServiceErrors.class))).thenReturn(dto);

        KeyedApiResponse<AccountKey> response = drawdownApiController.createPriorityDrawdown(
                "83DD6F9EBA702FE9D2B8DE23EF8F9933404D197CBE512D9B", Boolean.FALSE.toString(), dto);

        Assert.assertNotNull(response);
        Assert.assertEquals("83DD6F9EBA702FE9D2B8DE23EF8F9933404D197CBE512D9B", response.getId().getAccountId());
    }
}
