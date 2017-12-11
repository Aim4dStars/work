package com.bt.nextgen.api.account.v3.controller;

import com.bt.nextgen.api.account.v3.model.AccountAssetKey;
import com.bt.nextgen.api.account.v3.model.DistributionAccountDto;
import com.bt.nextgen.api.account.v3.service.DistributionAccountDtoService;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.service.ServiceErrors;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

@RunWith(MockitoJUnitRunner.class)
@Ignore("dodgy mock uri method handler doesn't have full spring capabilities")
public class ReinvestApiControllerTest {
    @InjectMocks
    private ReinvestApiController reinvestApiController;

    @Mock
    private DistributionAccountDtoService distributionDtoService;

    private MockHttpServletRequest mockHttpServletRequest;
    private MockHttpServletResponse mockHttpServletResponse;

    @Mock
    private static AnnotationMethodHandlerAdapter annotationMethodHandler;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

        mockHttpServletRequest = new MockHttpServletRequest(RequestMethod.GET.name(), "/");
        mockHttpServletResponse = new MockHttpServletResponse();
        annotationMethodHandler = new AnnotationMethodHandlerAdapter();
        HttpMessageConverter[] messageConverters = { new MappingJackson2HttpMessageConverter() };
        annotationMethodHandler.setMessageConverters(messageConverters);

    }

    @Test
    public final void testPostManagedFunds() throws Exception {
        DistributionAccountDto mfa = new DistributionAccountDto(new AccountAssetKey("1111", null), "Cash");

        Mockito.when(distributionDtoService.update(Mockito.any(DistributionAccountDto.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(mfa);

        mockHttpServletRequest.setParameter(UriMappingConstants.ACCOUNT_ID_URI_MAPPING, "1111");

        mockHttpServletRequest.setRequestURI("/secure/api/accounts/v3_0/1111/managed-funds/2222");
        mockHttpServletRequest.addParameter("distributionOption", "Cash");
        mockHttpServletRequest.setMethod("POST");
        annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, reinvestApiController);
    }

}
