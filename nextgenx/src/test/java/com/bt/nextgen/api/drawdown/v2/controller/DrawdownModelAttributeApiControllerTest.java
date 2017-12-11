package com.bt.nextgen.api.drawdown.v2.controller;

import com.bt.nextgen.api.drawdown.v2.model.AssetPriorityDto;
import com.bt.nextgen.api.drawdown.v2.model.DrawdownDetailsDto;
import com.bt.nextgen.api.drawdown.v2.service.DrawdownDetailsDtoService;
import com.bt.nextgen.api.drawdown.v2.service.DrawdownDtoService;
import com.bt.nextgen.api.drawdown.v2.validation.DrawdownErrorMapper;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.profile.UserProfileService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;

@RunWith(MockitoJUnitRunner.class)
public class DrawdownModelAttributeApiControllerTest {

    @InjectMocks
    private DrawdownApiController drawdownApiController;

    @Mock
    static DrawdownDetailsDtoService drawdownDetailsDtoService;

    @Mock
    static UserProfileService profileService;

    private MockMvc mockMvc;
    private AssetPriorityDto apDto;
    private final int STATUS_SUCCESS = 200;

    @EnableWebMvc
    @ComponentScan(basePackages = { "com.bt.nextgen.api.drawdown.v2.controller" })
    public static class TestConfiguration {

        @Bean(name = "PropertyPlaceholderConfigurer")
        PropertyPlaceholderConfigurer propertyPlaceholderConfigurerBean() {
            PropertyPlaceholderConfigurer pc = new PropertyPlaceholderConfigurer();
            pc.setLocation(new ClassPathResource("/com/bt/nextgen/api/drawdown/v2/UriConfig.properties"));
            return pc;
        }

        @Bean(name = "DrawdownDetailsDtoService")
        DrawdownDetailsDtoService drawdownDetailsDtoServiceBean() {
            return drawdownDetailsDtoService;
        }

        @Bean(name = "DrawdownDtoService")
        DrawdownDtoService drawdownDtoServiceBean() {
            return null;
        }

        @Bean(name = "UserProfileService")
        UserProfileService userProfileServiceBean() {
            return profileService;
        }

        @Bean(name = "DrawdownErrorMapper")
        DrawdownErrorMapper drawdownErrorMapperBean() {
            return null;
        }
    }

    @Before
    public void setup() {
        Mockito.when(profileService.isEmulating()).thenReturn(false);
        mockMvc = MockMvcBuilders.annotationConfigSetup(TestConfiguration.class).build();
        
        apDto = mock(AssetPriorityDto.class);
        when(apDto.getAssetId()).thenReturn("110523");
        when(apDto.getAssetName()).thenReturn("BHP Billiton Limited");
        when(apDto.getAssetCode()).thenReturn("BHP");
        when(apDto.getStatus()).thenReturn("Open");
        when(apDto.getDrawdownPriority()).thenReturn(Integer.valueOf(1));
    }

    @Test
    public void testModelAttribute_drawDownCreatePriorityDrawdown() throws Exception {
        Mockito.when(drawdownDetailsDtoService.submit(Mockito.any(DrawdownDetailsDto.class), Mockito.any(ServiceErrors.class)))
                .thenAnswer(new Answer<DrawdownDetailsDto>() {
                    @Override
                    public DrawdownDetailsDto answer(InvocationOnMock invocation) throws Throwable {
                        DrawdownDetailsDto dto = (DrawdownDetailsDto) invocation.getArguments()[0];

                        // Verify dto-fields have been mapped accurately
                        Assert.assertEquals("individual_assets", dto.getDrawdownType());
                        Assert.assertEquals(1, dto.getPriorityDrawdownList().size());

                        // AssetPriority mapping
                        AssetPriorityDto mapAssetDto = dto.getPriorityDrawdownList().get(0);
                        Assert.assertEquals(apDto.getAssetId(), mapAssetDto.getAssetId());
                        Assert.assertEquals(apDto.getAssetName(), mapAssetDto.getAssetName());
                        Assert.assertEquals(apDto.getAssetCode(), mapAssetDto.getAssetCode());
                        Assert.assertEquals(apDto.getStatus(), mapAssetDto.getStatus());
                        Assert.assertEquals(apDto.getDrawdownPriority(), mapAssetDto.getDrawdownPriority());

                        // DomainApiErrorDto mapping
                        Assert.assertEquals("warning", dto.getWarnings().get(0).getErrorType());
                        Assert.assertEquals("errorMessage", dto.getWarnings().get(0).getMessage());
                        return dto;
                    }
                });

        String url = "/secure/api/drawdown/v2_0/accounts/08013568C9B565BA4D79E9ACA34E3B11CC18C669F54CB36C/assetpriority/submit";
        int statusInt = mockMvc.perform(
                        post(url).param("drawdownType", "individual_assets")
                                 .param("key.accountId","08013568C9B565BA4D79E9ACA34E3B11CC18C669F54CB36C")
                                 .param("priorityDrawdownList[0].assetId", apDto.getAssetId())
                                 .param("priorityDrawdownList[0].assetName", apDto.getAssetName())
                                 .param("priorityDrawdownList[0].assetCode", apDto.getAssetCode())
                                 .param("priorityDrawdownList[0].status", apDto.getStatus())
                                 .param("priorityDrawdownList[0].drawdownPriority", apDto.getDrawdownPriority().toString())
                                 .param("warnings[0].message", "errorMessage")
                                 .param("warnings[0].errorType", "warning")
                ).andReturn().getResponse().getStatus();//.andDo(MockMvcResultHandlers.print());

        // URL cannot be mapped, status = 404.
        // Any controller error, status = 500.
        Assert.assertEquals(STATUS_SUCCESS, statusInt);
    }
}
