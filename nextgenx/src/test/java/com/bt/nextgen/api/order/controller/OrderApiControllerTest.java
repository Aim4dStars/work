/**
 *
 */
package com.bt.nextgen.api.order.controller;

import com.bt.nextgen.api.asset.model.ShareAssetDto;
import com.bt.nextgen.api.draftaccount.FormDataValidator;
import com.bt.nextgen.api.draftaccount.controller.ClientApplicationDtoDeserializer;
import com.bt.nextgen.api.order.model.GeneralOrderDto;
import com.bt.nextgen.api.order.model.OrderDto;
import com.bt.nextgen.api.order.model.OrderInProgressDto;
import com.bt.nextgen.api.order.model.OrderKey;
import com.bt.nextgen.api.order.model.ShareOrderDto;
import com.bt.nextgen.api.order.service.OrderDtoService;
import com.bt.nextgen.api.order.service.OrderDtoServiceV2;
import com.bt.nextgen.api.order.service.OrderGroupDtoService;
import com.bt.nextgen.api.order.service.OrderInProgressDtoService;
import com.bt.nextgen.api.order.validation.OrderGroupDtoErrorMapper;
import com.bt.nextgen.config.JsonObjectMapper;
import com.bt.nextgen.config.SecureJsonObjectMapper;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.core.security.api.model.PermissionAccountKey;
import com.bt.nextgen.core.security.api.model.PermissionsDto;
import com.bt.nextgen.core.security.api.service.PermissionAccountDtoService;
import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.order.OrderImpl;
import com.bt.nextgen.service.integration.order.Order;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.any;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;

/**
 * @author L072463
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class OrderApiControllerTest {

    @InjectMocks
    private final OrderApiController orderApiController = new OrderApiController();

    @Mock
    private static OrderDtoService orderDtoService;

    @Mock
    private OrderGroupDtoErrorMapper orderDtoErrorMapper;

    @Mock
    private static OrderDtoServiceV2 orderDtoServiceV2;

    @Mock
    private static OrderInProgressDtoService orderInProgressDtoService;

    @Mock
    private static UserProfileService profileService;

    @Mock
    private static PermissionAccountDtoService permissionAccountDtoService;

    private MockHttpServletRequest mockHttpServletRequest;
    private MockHttpServletResponse mockHttpServletResponse;

    @Mock
    private static AnnotationMethodHandlerAdapter annotationMethodHandler;

    @Mock
    private static OrderGroupDtoService orderGroupDtoService;

    @Mock
    private static OrderGroupDtoErrorMapper orderGroupDtoErrorMapper;

    @Mock
    private static ObjectMapper mapper;

    @Mock
    private static ClientApplicationDtoDeserializer clientApplicationDtoDeserializer;

    @Mock
    private static FormDataValidator formDataValidator;

    @Mock
    private static FeatureTogglesService featureTogglesService;

    @Mock
    private static FeatureToggles featureToggles;

    private MockMvc mockMvc;

    List<Order> orderList;
    OrderImpl order1;

    @EnableWebMvc
    @ComponentScan(basePackages = { "com.bt.nextgen.api.order.controller" })
    public static class TestConfiguration {

        @Bean(name = "PropertyPlaceholderConfigurer")
        PropertyPlaceholderConfigurer propertyPlaceholderConfigurerBean() {
            PropertyPlaceholderConfigurer pc = new PropertyPlaceholderConfigurer();
            return pc;
        }

        @Bean(name = "OrderGroupDtoService")
        OrderGroupDtoService orderGroupDtoService() {
            return orderGroupDtoService;
        }

        @Bean(name = "PermissionAccountDtoService")
        PermissionAccountDtoService permissionAccountDtoService() {
            return permissionAccountDtoService;
        }

        @Bean(name = "UserProfileService")
        UserProfileService userProfileServiceBean() {
            return profileService;
        }

        @Bean(name = "OrderGroupDtoErrorMapper")
        OrderGroupDtoErrorMapper orderGroupDtoErrorMapper() {
            return orderGroupDtoErrorMapper;
        }

        @Bean(name = "OrderDtoService")
        OrderDtoService orderDtoService() {
            return orderDtoService;
        }

        @Bean(name = "OrderDtoServiceV2")
        OrderDtoServiceV2 orderDtoServiceV2() {
            return orderDtoServiceV2;
        }

        @Bean(name = "OrderInProgressDtoService")
        OrderInProgressDtoService orderInProgressDtoService() {
            return orderInProgressDtoService;
        }

        @Bean(name = "jsonObjectMapper")
        ObjectMapper mapper() {
            return new JsonObjectMapper();
        }

        @Bean(name = "SecureJsonObjectMapper")
        ObjectMapper secureMapper() {
            return new SecureJsonObjectMapper();
        }

        @Bean(name = "ClientApplicationDtoDeserializer")
        ClientApplicationDtoDeserializer clientApplicationDtoDeserializer() {
            return clientApplicationDtoDeserializer;
        }

        @Bean(name = "FormDataValidator")
        FormDataValidator formDataValidator() {
            return formDataValidator;
        }

        @Bean(name= "FeatureTogglesService")
        FeatureTogglesService featureTogglesService(){
            return featureTogglesService;
        }

        @Bean(name="FeatureToggles")
        FeatureToggles featureToggles(){
            return featureToggles;
        }
    }

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

        ShareOrderDto orderDto = new ShareOrderDto();
        orderDto.setAccountName("Mary");
        orderDto.setStatus("In progress");
        orderDto.setKey(new OrderKey("41235"));

        PermissionsDto permissions = Mockito.mock(PermissionsDto.class);
        when(permissions.hasPermission(Mockito.anyString())).thenReturn(true);
        when(permissionAccountDtoService.find(any(PermissionAccountKey.class), any(ServiceErrors.class)))
                .thenReturn(permissions);

        when(orderDtoService.update(any(ShareOrderDto.class), any(ServiceErrors.class)))
                .thenReturn(orderDto);
        when(featureTogglesService.findOne(any(ServiceErrors.class))).thenReturn(featureToggles);
    }

    @Test
    public void testAmendOrder_whenParamsPassed_thenDtoMappedCorrectly() throws Exception {
        mockMvc = MockMvcBuilders.annotationConfigSetup(TestConfiguration.class).build();
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);

        String url = "/secure/api/v1_0/orders";
        int statusInt = mockMvc.perform(post(url).param("order",
                "{\"submitDate\":\"2017-07-19T07:33:40.000Z\",\"displayOrderId\":\"9429056\",\"origin\":\"Back office direct\",\"accountKey\":\"12EFE5419BBA504886B0817C56985FBCF8BCE3DE829A231A\",\"accountName\":\"Trading indi cient2\",\"accountNumber\":\"120134440\",\"orderType\":\"Buy\",\"status\":\"In progress\",\"cancellable\":true,\"asset\":{\"type\":\"ShareAsset\",\"assetId\":\"110523\",\"assetName\":\"BHP Billiton Limited\",\"assetType\":\"Listed security\",\"assetCluster\":\"ls\",\"status\":\"Open\",\"isin\":\"AU000000BHP4\",\"assetCode\":\"BHP\",\"assetClass\":\"Australian shares\",\"groupClass\":\"Australian shares\",\"issuerId\":\"109916\",\"issuerName\":\"BHP Billiton Limited\",\"prePensionRestricted\":false,\"price\":25.11,\"industryType\":\"Materials\",\"industrySector\":\"Materials\",\"investmentHoldingLimit\":30,\"investmentHoldingLimitBuffer\":35,\"key\":\"110523\"},\"lastTranSeqId\":\"5\",\"quantity\":1500,\"amendable\":true,\"contractNotes\":false,\"external\":false,\"brokerage\":-33.15,\"filledQuantity\":0,\"priceType\":\"Market\",\"expiryType\":\"GFD\",\"limitPrice\":\"1.234\",\"cancellationCount\":0,\"maxCancellationCount\":3,\"key\":{\"orderId\":\"9429056\"},\"type\":\"ShareOrder\",\"statusicon\":\"icon-refresh\",\"price\":\"25.110\"}"))
                .andReturn().getResponse().getStatus();

        ArgumentCaptor<OrderDto> argument = ArgumentCaptor.forClass(OrderDto.class);
        verify(orderDtoService, atLeastOnce()).update(argument.capture(), any(ServiceErrors.class));
        ShareOrderDto order = (ShareOrderDto) argument.getValue();
        assertEquals("9429056", order.getKey().getOrderId());
        assertEquals("5", order.getLastTranSeqId());
        assertEquals("In progress", order.getStatus());
        assertEquals(Boolean.TRUE, order.getCancellable());
        assertEquals("Market", order.getPriceType());
        assertEquals("GFD", order.getExpiryType());
        assertEquals(new BigDecimal("1.234"), order.getLimitPrice());
        assertEquals(ShareAssetDto.class, order.getAsset().getClass());
        assertEquals(AssetType.SHARE.getDisplayName(), order.getAsset().getAssetType());

        // check that params passed in that aren't whitelisted are null 
        assertNull(order.getOrderType());

        // URL cannot be mapped, status = 404.
        // Any controller error, status = 500.
        Assert.assertEquals(200, statusInt);
    }

    @Test
    public void testAmendOrder_whenEmulating_thenAccessedDeniedExceptionThrown() throws Exception {
        mockMvc = MockMvcBuilders.annotationConfigSetup(TestConfiguration.class).build();
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);
        when(profileService.isEmulating()).thenReturn(true);

        try {
            String url = "/secure/api/v1_0/orders";
            mockMvc.perform(post(url).param("order",
                    "{\"submitDate\":\"2017-07-19T07:33:40.000Z\",\"displayOrderId\":\"9429056\",\"origin\":\"Back office direct\",\"accountKey\":\"12EFE5419BBA504886B0817C56985FBCF8BCE3DE829A231A\",\"accountName\":\"Trading indi cient2\",\"accountNumber\":\"120134440\",\"orderType\":\"Buy\",\"status\":\"In progress\",\"cancellable\":true,\"asset\":{\"type\":\"ShareAsset\",\"assetId\":\"110523\",\"assetName\":\"BHP Billiton Limited\",\"assetType\":\"Listed security\",\"assetCluster\":\"ls\",\"status\":\"Open\",\"isin\":\"AU000000BHP4\",\"assetCode\":\"BHP\",\"assetClass\":\"Australian shares\",\"groupClass\":\"Australian shares\",\"issuerId\":\"109916\",\"issuerName\":\"BHP Billiton Limited\",\"prePensionRestricted\":false,\"price\":25.11,\"industryType\":\"Materials\",\"industrySector\":\"Materials\",\"investmentHoldingLimit\":30,\"investmentHoldingLimitBuffer\":35,\"key\":\"110523\"},\"lastTranSeqId\":\"5\",\"quantity\":1500,\"amendable\":true,\"contractNotes\":false,\"external\":false,\"brokerage\":-33.15,\"filledQuantity\":0,\"priceType\":\"Market\",\"expiryType\":\"GFD\",\"limitPrice\":\"1.234\",\"cancellationCount\":0,\"maxCancellationCount\":3,\"key\":{\"orderId\":\"9429056\"},\"type\":\"ShareOrder\",\"statusicon\":\"icon-refresh\",\"price\":\"25.110\"}"))
                    .andReturn().getResponse().getStatus();
        } catch (AccessDeniedException e1) {
            assert (true);
        } catch (Exception e) {
            fail("Access Denied Exception Not thrown");
        }
    }

    @Test
    public void testUpdateAccountOrder_whenParamsPassed_thenDtoMappedCorrectly() throws Exception {
        mockMvc = MockMvcBuilders.annotationConfigSetup(TestConfiguration.class).build();
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);
        String url = "/secure/api/v1_0/accounts/0465D5F8D68A1A43612FF477E9254F55358ED2CB3BF5E9F0/orders/update";
        int statusInt = mockMvc.perform(post(url).param("order",
                "{\"submitDate\":\"2017-07-19T07:33:40.000Z\",\"displayOrderId\":\"9429056\",\"origin\":\"Back office direct\",\"accountKey\":\"12EFE5419BBA504886B0817C56985FBCF8BCE3DE829A231A\",\"accountName\":\"Trading indi cient2\",\"accountNumber\":\"120134440\",\"orderType\":\"Buy\",\"status\":\"In progress\",\"cancellable\":true,\"asset\":{\"type\":\"ShareAsset\",\"assetId\":\"110523\",\"assetName\":\"BHP Billiton Limited\",\"assetType\":\"Listed security\",\"assetCluster\":\"ls\",\"status\":\"Open\",\"isin\":\"AU000000BHP4\",\"assetCode\":\"BHP\",\"assetClass\":\"Australian shares\",\"groupClass\":\"Australian shares\",\"issuerId\":\"109916\",\"issuerName\":\"BHP Billiton Limited\",\"prePensionRestricted\":false,\"price\":25.11,\"industryType\":\"Materials\",\"industrySector\":\"Materials\",\"investmentHoldingLimit\":30,\"investmentHoldingLimitBuffer\":35,\"key\":\"110523\"},\"lastTranSeqId\":\"5\",\"quantity\":1500,\"amendable\":true,\"contractNotes\":false,\"external\":false,\"brokerage\":-33.15,\"filledQuantity\":0,\"priceType\":\"Market\",\"expiryType\":\"GFD\",\"limitPrice\":\"1.234\",\"cancellationCount\":0,\"maxCancellationCount\":3,\"key\":{\"orderId\":\"9429056\"},\"type\":\"ShareOrder\",\"statusicon\":\"icon-refresh\",\"price\":\"25.110\"}"))
                .andReturn().getResponse().getStatus();

        ArgumentCaptor<OrderDto> argument = ArgumentCaptor.forClass(OrderDto.class);
        verify(orderDtoService, atLeastOnce()).update(argument.capture(), any(ServiceErrors.class));
        ShareOrderDto order = (ShareOrderDto) argument.getValue();
        assertEquals("9429056", order.getKey().getOrderId());
        assertEquals("5", order.getLastTranSeqId());
        assertEquals("In progress", order.getStatus());
        assertEquals(Boolean.TRUE, order.getCancellable());
        assertEquals("Market", order.getPriceType());
        assertEquals("GFD", order.getExpiryType());
        assertEquals(new BigDecimal("1.234"), order.getLimitPrice());
        assertEquals(ShareAssetDto.class, order.getAsset().getClass());
        assertEquals(AssetType.SHARE.getDisplayName(), order.getAsset().getAssetType());

        // check that params passed in that aren't whitelisted are null 
        assertNull(order.getOrderType());

        // URL cannot be mapped, status = 404.
        // Any controller error, status = 500.
        Assert.assertEquals(200, statusInt);
    }

    @Test
    public void testUpdateAccountOrder_whenNoPermission_thenAccessedDeniedExceptionThrown() throws Exception {
        mockMvc = MockMvcBuilders.annotationConfigSetup(TestConfiguration.class).build();
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);
        PermissionsDto permissionsDto = Mockito.mock(PermissionsDto.class);
        when(permissionsDto.hasPermission(Mockito.anyString())).thenReturn(false);
        when(permissionAccountDtoService.find(any(PermissionAccountKey.class), any(ServiceErrors.class)))
                .thenReturn(permissionsDto);

        try {
            String url = "/secure/api/v1_0/accounts/0465D5F8D68A1A43612FF477E9254F55358ED2CB3BF5E9F0/orders/update";
            mockMvc.perform(post(url).param("order",
                    "{\"submitDate\":\"2017-07-19T07:33:40.000Z\",\"displayOrderId\":\"9429056\",\"origin\":\"Back office direct\",\"accountKey\":\"12EFE5419BBA504886B0817C56985FBCF8BCE3DE829A231A\",\"accountName\":\"Trading indi cient2\",\"accountNumber\":\"120134440\",\"orderType\":\"Buy\",\"status\":\"In progress\",\"cancellable\":true,\"asset\":{\"type\":\"ShareAsset\",\"assetId\":\"110523\",\"assetName\":\"BHP Billiton Limited\",\"assetType\":\"Listed security\",\"assetCluster\":\"ls\",\"status\":\"Open\",\"isin\":\"AU000000BHP4\",\"assetCode\":\"BHP\",\"assetClass\":\"Australian shares\",\"groupClass\":\"Australian shares\",\"issuerId\":\"109916\",\"issuerName\":\"BHP Billiton Limited\",\"prePensionRestricted\":false,\"price\":25.11,\"industryType\":\"Materials\",\"industrySector\":\"Materials\",\"investmentHoldingLimit\":30,\"investmentHoldingLimitBuffer\":35,\"key\":\"110523\"},\"lastTranSeqId\":\"5\",\"quantity\":1500,\"amendable\":true,\"contractNotes\":false,\"external\":false,\"brokerage\":-33.15,\"filledQuantity\":0,\"priceType\":\"Market\",\"expiryType\":\"GFD\",\"limitPrice\":\"1.234\",\"cancellationCount\":0,\"maxCancellationCount\":3,\"key\":{\"orderId\":\"9429056\"},\"type\":\"ShareOrder\",\"statusicon\":\"icon-refresh\",\"price\":\"25.110\"}"))
                    .andReturn().getResponse().getStatus();
        } catch (AccessDeniedException e1) {
            assert (true);
        } catch (Exception e) {
            fail("Access Denied Exception Not thrown");
        }
    }

    @Test
    public void testUpdateAccountOrder_whenEmulating_thenAccessedDeniedExceptionThrown() throws Exception {
        mockMvc = MockMvcBuilders.annotationConfigSetup(TestConfiguration.class).build();
        when(profileService.isEmulating()).thenReturn(true);

        try {
            String url = "/secure/api/v1_0/accounts/0465D5F8D68A1A43612FF477E9254F55358ED2CB3BF5E9F0/orders/update";
            mockMvc.perform(post(url).param("order",
                    "{\"submitDate\":\"2017-07-19T07:33:40.000Z\",\"displayOrderId\":\"9429056\",\"origin\":\"Back office direct\",\"accountKey\":\"12EFE5419BBA504886B0817C56985FBCF8BCE3DE829A231A\",\"accountName\":\"Trading indi cient2\",\"accountNumber\":\"120134440\",\"orderType\":\"Buy\",\"status\":\"In progress\",\"cancellable\":true,\"asset\":{\"type\":\"ShareAsset\",\"assetId\":\"110523\",\"assetName\":\"BHP Billiton Limited\",\"assetType\":\"Listed security\",\"assetCluster\":\"ls\",\"status\":\"Open\",\"isin\":\"AU000000BHP4\",\"assetCode\":\"BHP\",\"assetClass\":\"Australian shares\",\"groupClass\":\"Australian shares\",\"issuerId\":\"109916\",\"issuerName\":\"BHP Billiton Limited\",\"prePensionRestricted\":false,\"price\":25.11,\"industryType\":\"Materials\",\"industrySector\":\"Materials\",\"investmentHoldingLimit\":30,\"investmentHoldingLimitBuffer\":35,\"key\":\"110523\"},\"lastTranSeqId\":\"5\",\"quantity\":1500,\"amendable\":true,\"contractNotes\":false,\"external\":false,\"brokerage\":-33.15,\"filledQuantity\":0,\"priceType\":\"Market\",\"expiryType\":\"GFD\",\"limitPrice\":\"1.234\",\"cancellationCount\":0,\"maxCancellationCount\":3,\"key\":{\"orderId\":\"9429056\"},\"type\":\"ShareOrder\",\"statusicon\":\"icon-refresh\",\"price\":\"25.110\"}"))
                    .andReturn().getResponse().getStatus();
        } catch (AccessDeniedException e1) {
            assert (true);
        } catch (Exception e) {
            fail("Access Denied Exception Not thrown");
        }
    }

    /**
     * Test method for
     * {@link com.bt.nextgen.api.order.controller.OrderApiController#getOrders(java.lang.String, java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public final void testGetOrdersEmptyCriteria() throws Exception {
        String paging = "{\"startIndex\":0,\"maxResults\":\"50\"}";

        List<OrderDto> orderDTOs = new ArrayList<>();
        GeneralOrderDto orderDto = new GeneralOrderDto();
        orderDto.setAccountName("sample");
        orderDto.setStatus("Open");
        orderDTOs.add(orderDto);

        when(orderDtoService.search(Mockito.anyListOf(ApiSearchCriteria.class), any(ServiceErrors.class)))
                .thenReturn(orderDTOs);
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);

        ApiResponse apiResponse = orderApiController.getOrders(null, paging, "80A063E31114DEA03439E0BD2F82ABFCB056BED9910DDAEA");
        assertThat(apiResponse, is(notNullValue()));

        Mockito.verify(orderDtoService).search(Mockito.anyListOf(ApiSearchCriteria.class), any(ServiceErrorsImpl.class));
    }

    @Test
    public final void testGetOrdersCriteria() throws Exception {
        String paging = "{\"startIndex\":0,\"maxResults\":\"50\"}";
        String searchCriteria = "[{\"prop\":\"lastUpdateDate\",\"op\":\"~<\",\"val\":\"2015-02-04\",\"type\":\"date\"},{\"prop\":\"lastUpdateDate\",\"op\":\"~>\",\"val\":\"2015-03-06\",\"type\":\"date\"}]";

        List<OrderDto> orderDTOs = new ArrayList<>();
        GeneralOrderDto orderDto = new GeneralOrderDto();
        orderDto.setAccountName("sample");
        orderDto.setStatus("Open");
        orderDTOs.add(orderDto);

        when(orderDtoService.search(Mockito.anyListOf(ApiSearchCriteria.class), any(ServiceErrors.class)))
                .thenReturn(orderDTOs);
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);

        ApiResponse apiResponse = orderApiController.getOrders(searchCriteria, paging,
                "80A063E31114DEA03439E0BD2F82ABFCB056BED9910DDAEA");
        assertThat(apiResponse, is(notNullValue()));

        Mockito.verify(orderDtoService).search(Mockito.anyListOf(ApiSearchCriteria.class), any(ServiceErrorsImpl.class));
    }

    /**
     * Test method for
     * {@link com.bt.nextgen.api.order.controller.OrderApiController#getAccountOrders(java.lang.String, java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public final void testGetAccountOrders() throws Exception {

        List<OrderDto> orderDTOs = new ArrayList<>();
        GeneralOrderDto orderDto = new GeneralOrderDto();
        orderDto.setAccountName("sample");
        orderDto.setStatus("Open");
        orderDTOs.add(orderDto);

        when(orderDtoService.search(Mockito.anyListOf(ApiSearchCriteria.class), any(ServiceErrors.class)))
                .thenReturn(orderDTOs);
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);

        mockHttpServletRequest.setParameter("search-criteria",
                "[{\"prop\":\"lastUpdateDate\",\"op\":\"~<\",\"val\":\"2015-02-04\",\"type\":\"date\"},{\"prop\":\"lastUpdateDate\",\"op\":\"~>\",\"val\":\"2015-03-06\",\"type\":\"date\"}]&paging={\"startIndex\":0,\"maxResults\":200}");

        mockHttpServletRequest.setRequestURI("/secure/api/v1_0/accounts/80A063E31114DEA03439E0BD2F82ABFCB056BED9910DDAEA/orders");
        mockHttpServletRequest.setMethod("GET");
        annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, orderApiController);

    }

    /**
     * Test method for {@link com.bt.nextgen.api.order.controller.OrderApiController#getOrder(java.lang.String)} .
     */
    @Test
    public final void testGetOrder() throws Exception {
        GeneralOrderDto orderDto = new GeneralOrderDto();
        orderDto.setKey(new OrderKey("123"));
        orderDto.setAccountName("sample");
        orderDto.setStatus("Open");

        when(orderDtoService.find(any(OrderKey.class), any(ServiceErrors.class))).thenReturn(orderDto);
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);

        ApiResponse apiResponse = orderApiController.getOrder("123", false);
        assertThat(apiResponse, is(notNullValue()));

        Mockito.verify(orderDtoService).find(any(OrderKey.class), any(ServiceErrorsImpl.class));
    }

    /**
     * Test method for {@link com.bt.nextgen.api.order.controller.OrderApiController#getOrder(java.lang.String)}.
     */
    @Test
    public final void testSearchOrders() throws Exception {
        List<OrderDto> orderDTOs = new ArrayList<>();
        GeneralOrderDto orderDto = new GeneralOrderDto();
        orderDto.setKey(new OrderKey("123"));
        orderDto.setAccountName("sample");
        orderDto.setStatus("Open");
        orderDTOs.add(orderDto);

        when(orderDtoService.search(any(OrderKey.class), any(ServiceErrors.class))).thenReturn(orderDTOs);
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);

        ApiResponse apiResponse = orderApiController.getOrder("123", true);
        assertThat(apiResponse, is(notNullValue()));

        Mockito.verify(orderDtoService).search(any(OrderKey.class), any(ServiceErrorsImpl.class));
    }

    /**
     * Test method for {@link com.bt.nextgen.api.order.controller.OrderApiController#getOrdersInProgress(java.lang.String)}.
     */
    @Test
    public final void testGetOrdersInProgress() throws Exception {
        List<OrderInProgressDto> orderDTOs = new ArrayList<>();
        OrderInProgressDto orderInProgressDto = new OrderInProgressDto("orderType", new BigDecimal(100));
        orderDTOs.add(orderInProgressDto);

        when(
                orderInProgressDtoService.search(Mockito.anyListOf(ApiSearchCriteria.class), any(ServiceErrors.class)))
                .thenReturn(orderDTOs);
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);

        mockHttpServletRequest.setRequestURI("/secure/api/v1_0/accounts/account-id/orderinprogress");
        mockHttpServletRequest.setMethod("GET");
        annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, orderApiController);

    }

    @Test
    public final void testGetOrder_Include_ExternalOrders() throws Exception {
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(true);
        GeneralOrderDto orderDto = new GeneralOrderDto();
        orderDto.setKey(new OrderKey("123" ));
        orderDto.setAccountName("sample");
        orderDto.setStatus("Open");

        when(orderDtoService.find(any(OrderKey.class), any(ServiceErrors.class))).thenReturn(orderDto);
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(true);

        ApiResponse apiResponse = orderApiController.getOrder("123", true);
        assertThat(apiResponse, is(notNullValue()));

        Mockito.verify(orderDtoServiceV2).search(any(OrderKey.class), any(ServiceErrorsImpl.class));
    }

    /**
     * Test method for
     * {@link com.bt.nextgen.api.order.controller.OrderApiController#getOrders(java.lang.String, java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public final void testGetOrdersEmptyCriteria_WithTD() throws Exception {
        String paging = "{\"startIndex\":0,\"maxResults\":\"50\"}";
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(true);
        List<OrderDto> orderDTOs = new ArrayList<>();
        GeneralOrderDto orderDto = new GeneralOrderDto();
        orderDto.setAccountName("sample");
        orderDto.setStatus("Open");
        orderDTOs.add(orderDto);

        Mockito.when(orderDtoServiceV2.search(Mockito.anyListOf(ApiSearchCriteria.class), any(ServiceErrors.class)))
                .thenReturn(orderDTOs);

        ApiResponse apiResponse = orderApiController.getOrders(null, paging, "80A063E31114DEA03439E0BD2F82ABFCB056BED9910DDAEA");
        assertThat(apiResponse, is(notNullValue()));

        Mockito.verify(orderDtoServiceV2).search(Mockito.anyListOf(ApiSearchCriteria.class), any(ServiceErrorsImpl.class));
    }

    @Test
    public final void testGetOrdersCriteria_ForTD() throws Exception {
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(true);
        String paging = "{\"startIndex\":0,\"maxResults\":\"50\"}";
        String searchCriteria = "[{\"prop\":\"lastUpdateDate\",\"op\":\"~<\",\"val\":\"2015-02-04\",\"type\":\"date\"},{\"prop\":\"lastUpdateDate\",\"op\":\"~>\",\"val\":\"2015-03-06\",\"type\":\"date\"}]";

        List<OrderDto> orderDTOs = new ArrayList<>();
        GeneralOrderDto orderDto = new GeneralOrderDto();
        orderDto.setAccountName("sample");
        orderDto.setStatus("Open");
        orderDTOs.add(orderDto);

        Mockito.when(orderDtoServiceV2.search(Mockito.anyListOf(ApiSearchCriteria.class), any(ServiceErrors.class)))
                .thenReturn(orderDTOs);

        ApiResponse apiResponse = orderApiController.getOrders(searchCriteria, paging,
                "80A063E31114DEA03439E0BD2F82ABFCB056BED9910DDAEA");
        assertThat(apiResponse, is(notNullValue()));

        Mockito.verify(orderDtoServiceV2).search(Mockito.anyListOf(ApiSearchCriteria.class), any(ServiceErrorsImpl.class));
    }


    /**
     * Test method for
     * {@link com.bt.nextgen.api.order.controller.OrderApiController#getAccountOrders(java.lang.String, java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public final void testGetAccountOrdersForTD() throws Exception {
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(true);
        List<OrderDto> orderDTOs = new ArrayList<>();
        GeneralOrderDto orderDto = new GeneralOrderDto();
        orderDto.setAccountName("sample");
        orderDto.setStatus("Open");
        orderDTOs.add(orderDto);

        Mockito.when(orderDtoServiceV2.search(Mockito.anyListOf(ApiSearchCriteria.class), any(ServiceErrors.class)))
                .thenReturn(orderDTOs);

        mockHttpServletRequest.setParameter("search-criteria",
                "[{\"prop\":\"lastUpdateDate\",\"op\":\"~<\",\"val\":\"2015-02-04\",\"type\":\"date\"},{\"prop\":\"lastUpdateDate\",\"op\":\"~>\",\"val\":\"2015-03-06\",\"type\":\"date\"}]&paging={\"startIndex\":0,\"maxResults\":200}");

        mockHttpServletRequest.setRequestURI("/secure/api/v1_0/accounts/80A063E31114DEA03439E0BD2F82ABFCB056BED9910DDAEA/orders");
        mockHttpServletRequest.setMethod("GET");
        annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, orderApiController);

    }

    /**
     * Test method for {@link com.bt.nextgen.api.order.controller.OrderApiController#getOrder(java.lang.String)} .
     */
    @Test
    public final void testGetOrderForTD() throws Exception {
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(true);
        GeneralOrderDto orderDto = new GeneralOrderDto();
        orderDto.setKey(new OrderKey("123" ));
        orderDto.setAccountName("sample");
        orderDto.setStatus("Open");

        Mockito.when(orderDtoServiceV2.find(any(OrderKey.class), any(ServiceErrors.class))).thenReturn(orderDto);

        ApiResponse apiResponse = orderApiController.getOrder("123", false);
        assertThat(apiResponse, is(notNullValue()));

        Mockito.verify(orderDtoServiceV2).find(any(OrderKey.class), any(ServiceErrorsImpl.class));
    }

}
