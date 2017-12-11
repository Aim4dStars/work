package com.bt.nextgen.api.account.v1.controller.transitions;

import com.bt.nextgen.api.account.v1.model.AccountBalanceDto;
import com.bt.nextgen.api.account.v1.model.AccountKey;
import com.bt.nextgen.api.account.v1.model.transitions.TransitionAssetDto;
import com.bt.nextgen.api.account.v1.service.AssetTransferService;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.AssetCluster;
import org.joda.time.DateTime;
import org.junit.Before;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by L069552 on 14/10/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class AssetTransferApiControllerTest {


    @InjectMocks
    AssetTransferApiController  assetTransferApiController;

    @Mock
    AssetTransferService assetTransferService;

    private MockHttpServletRequest mockHttpServletRequest;
    private MockHttpServletResponse mockHttpServletResponse;

    @Mock
    private static AnnotationMethodHandlerAdapter annotationMethodHandler;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {

        mockHttpServletRequest = new MockHttpServletRequest(RequestMethod.GET.name(), "/");
        mockHttpServletResponse = new MockHttpServletResponse();
        annotationMethodHandler = new AnnotationMethodHandlerAdapter();
        HttpMessageConverter[] messageConverters =
                {
                        new MappingJackson2HttpMessageConverter()
                };
        annotationMethodHandler.setMessageConverters(messageConverters);

    }

    @Test
    public final void testGetAssetTransferDetails() throws Exception
    {
       List<TransitionAssetDto> listtransitionAssetList = new ArrayList<>();
       com.bt.nextgen.api.account.v2.model.AccountKey accountKeyVal = new com.bt.nextgen.api.account.v2.model.AccountKey("34523654");
       TransitionAssetDto transitionAssetDto1 = new TransitionAssetDto(accountKeyVal);
        transitionAssetDto1.setAssetCluster(AssetCluster.MANAGED_FUND.name());
        transitionAssetDto1.setAssetName("CNA0805AU Macquarie International Infrastructure Securities Fund");
        transitionAssetDto1.setAssetCode("121308");
        transitionAssetDto1.setConsiderationAmt(new BigDecimal(567));
        transitionAssetDto1.setSubmittedTimestamp(new DateTime());
        transitionAssetDto1.setOrderId("12345");
        transitionAssetDto1.setTransitionStatus("Complete");
        transitionAssetDto1.setAccountName("Tom Peters");
        transitionAssetDto1.setAccountNumber("120011366");
        transitionAssetDto1.setProductName("DNR Aus Equities Socially Resp Portfolio");
        transitionAssetDto1.setAccountType("Individual");
        transitionAssetDto1.setQuantity(new BigDecimal(34));
        listtransitionAssetList.add(transitionAssetDto1);

       Mockito.when(assetTransferService.search(Mockito.anyListOf(ApiSearchCriteria.class),
               Mockito.any(ServiceErrors.class))).thenReturn(listtransitionAssetList);

        mockHttpServletRequest.setParameter(UriMappingConstants.ACCOUNT_ID_URI_MAPPING, "account-id");
        mockHttpServletRequest.setRequestURI("/secure/api/v1_0/accounts/901CBD11D6E7365E4DFA7994B2DE32A9C6073F5E7186AE3F/assetTransferStatus");
        mockHttpServletRequest.setMethod("GET");
        annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, assetTransferApiController);

    }

}
