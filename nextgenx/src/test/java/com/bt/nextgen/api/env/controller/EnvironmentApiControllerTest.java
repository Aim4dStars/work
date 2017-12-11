package com.bt.nextgen.api.env.controller;

import com.bt.nextgen.api.env.model.EnvironmentDto;
import com.bt.nextgen.api.env.service.EnvironmentDtoService;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.service.ServiceErrors;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EnvironmentApiControllerTest {
    @InjectMocks
    private EnvironmentApiController environmentApiController;

    @Mock
    private EnvironmentDtoService environmentDtoService;

    @Test
    public void testEnvController() throws Exception {
        EnvironmentDto mockresponse = getEnvironmentDetails("DEV1", "cms.westpac.com.au");
        when(environmentDtoService.findOne(any(ServiceErrors.class))).thenReturn(mockresponse);
        MockHttpServletRequest request = new MockHttpServletRequest();
        ApiResponse response = environmentApiController.getEnvironmentDetails(request, new MockHttpServletResponse());
        Assert.assertNotNull(response);
        EnvironmentDto controllerResponse = (EnvironmentDto) response.getData();
        Assert.assertEquals(mockresponse, controllerResponse);
        verify(environmentDtoService, times(1)).findOne(any(ServiceErrors.class));
    }

    private EnvironmentDto getEnvironmentDetails(String environment, String cmsHostForAem) {
        EnvironmentDto dto = new EnvironmentDto();
        dto.setEnvironment(environment);
        dto.setCmsHostForAem(cmsHostForAem);
        return dto;
    }
}
