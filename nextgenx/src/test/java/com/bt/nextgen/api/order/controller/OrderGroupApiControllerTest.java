/**
 *
 */
package com.bt.nextgen.api.order.controller;

import com.bt.nextgen.api.asset.model.ManagedFundAssetDto;
import com.bt.nextgen.api.asset.model.ManagedPortfolioAssetDto;
import com.bt.nextgen.api.asset.model.ShareAssetDto;
import com.bt.nextgen.api.asset.model.TermDepositAssetDto;
import com.bt.nextgen.api.draftaccount.FormDataValidator;
import com.bt.nextgen.api.draftaccount.controller.ClientApplicationDtoDeserializer;
import com.bt.nextgen.api.order.model.OrderGroupDto;
import com.bt.nextgen.api.order.model.OrderGroupKey;
import com.bt.nextgen.api.order.model.OrderItemDto;
import com.bt.nextgen.api.order.service.OrderDtoService;
import com.bt.nextgen.api.order.service.OrderDtoServiceV2;
import com.bt.nextgen.api.order.service.OrderGroupDtoService;
import com.bt.nextgen.api.order.service.OrderInProgressDtoService;
import com.bt.nextgen.api.order.validation.OrderGroupDtoErrorMapper;
import com.bt.nextgen.config.JsonObjectMapper;
import com.bt.nextgen.config.SecureJsonObjectMapper;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.core.security.api.model.PermissionAccountKey;
import com.bt.nextgen.core.security.api.model.PermissionsDto;
import com.bt.nextgen.core.security.api.service.PermissionAccountDtoService;
import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.fees.FeesComponentType;
import com.bt.nextgen.service.avaloq.fees.FeesType;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;

/**
 * @author L072463
 *
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class OrderGroupApiControllerTest {

    @InjectMocks
    private final OrderGroupApiController orderGroupApiController = new OrderGroupApiController();;

    @Mock
    private static OrderGroupDtoService orderGroupDtoService;
    @Mock
    private static OrderGroupDtoErrorMapper orderGroupDtoErrorMapper;
    @Mock
    private static PermissionAccountDtoService permissionAccountDtoService;
    @Mock
    private static UserProfileService profileService;
    @Mock
    private static PermissionsDto permissionsDto;

    @Mock
    private static OrderDtoService orderDtoService;

    @Mock
    private static OrderDtoServiceV2 orderDtoServiceV2;

    @Mock
    private static OrderInProgressDtoService orderInProgressDtoService;

    @Mock
    private static AnnotationMethodHandlerAdapter annotationMethodHandler;

    @Mock
    private static FeatureTogglesService featureTogglesService;

    @Mock
    private static FeatureToggles featureToggles;

    private MockHttpServletRequest mockHttpServletRequest;
    private MockHttpServletResponse mockHttpServletResponse;

    @Mock
    private static ObjectMapper mapper;

    @Mock
    private static ClientApplicationDtoDeserializer clientApplicationDtoDeserializer;

    @Mock
    private static FormDataValidator formDataValidator;

    private MockMvc mockMvc;

    OrderGroupDto orderGroupDto;

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


        @Bean(name = "OrderDtoServiceV2")
        OrderDtoServiceV2 orderDtoServiceV2() {
            return orderDtoServiceV2;
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

    @Before
    public void setUp() throws Exception {
        mockHttpServletRequest = new MockHttpServletRequest(RequestMethod.GET.name(), "/");
        mockHttpServletResponse = new MockHttpServletResponse();
        annotationMethodHandler = new AnnotationMethodHandlerAdapter();
        HttpMessageConverter[] messageConverters = { new MappingJackson2HttpMessageConverter() };
        annotationMethodHandler.setMessageConverters(messageConverters);

        orderGroupDto = new OrderGroupDto();
        orderGroupDto.setKey(new OrderGroupKey("abc", "123"));

        Mockito.when(profileService.isEmulating()).thenReturn(false);
        PermissionsDto permissions = Mockito.mock(PermissionsDto.class);
        Mockito.when(permissions.hasPermission(Mockito.anyString())).thenReturn(true);
        Mockito.when(permissionAccountDtoService.find(Mockito.any(PermissionAccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(permissions);

        Mockito.when(orderGroupDtoService.create(Mockito.any(OrderGroupDto.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(orderGroupDto);
        Mockito.when(orderGroupDtoService.update(Mockito.any(OrderGroupDto.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(orderGroupDto);
        Mockito.when(orderGroupDtoService.validate(Mockito.any(OrderGroupDto.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(orderGroupDto);
        Mockito.when(orderGroupDtoService.submit(Mockito.any(OrderGroupDto.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(orderGroupDto);

    }

    @Test
    public void testCreate_whenParamsPassed_thenDtoMappedCorrectly() throws Exception {
        mockMvc = MockMvcBuilders.annotationConfigSetup(TestConfiguration.class).build();

        String url = "/secure/api/v1_0/accounts/0465D5F8D68A1A43612FF477E9254F55358ED2CB3BF5E9F0/order-groups";
        int statusInt = mockMvc.perform(post(url).param("orderGroup",
                "{\"orders\":[{\"orderType\":\"sell\",\"amount\":50,\"asset\":{\"assetId\":\"110740\",\"type\":\"ShareAsset\"},\"sellAll\":false,\"incomePreference\":null,\"assetType\":\"Listed security\",\"distributionMethod\":\"Cash\",\"units\":50,\"price\":5.255,\"fundsAllocation\":[{\"accountId\":\"26CC637BC1941BD35B6215CFEBCE6BBE60DBED6B107E0A6F\",\"allocation\":1}],\"expiry\":\"GFD\",\"priceType\":\"mkt\"},{\"orderType\":\"sell\",\"amount\":24.44,\"asset\":{\"assetId\":\"187735\",\"type\":\"ManagedFundAsset\"},\"sellAll\":true,\"incomePreference\":null,\"assetType\":\"Managed fund\",\"distributionMethod\":\"Cash\",\"units\":24.44,\"price\":3.8223,\"fundsAllocation\":[{\"accountId\":\"26CC637BC1941BD35B6215CFEBCE6BBE60DBED6B107E0A6F\",\"allocation\":1}]},{\"orderType\":\"sell\",\"subAccountId\":\"BAFBB8039A5F695A76EB6810BCC4D082C9B526FC69DFC78A\",\"amount\":5000,\"asset\":{\"assetId\":\"147057\",\"type\":\"ManagedPortfolioAsset\"},\"sellAll\":false,\"incomePreference\":\"reinvest\",\"assetType\":\"Managed portfolio\",\"units\":5000,\"fundsAllocation\":[{\"accountId\":\"26CC637BC1941BD35B6215CFEBCE6BBE60DBED6B107E0A6F\",\"allocation\":1}]},{\"orderType\":\"buy\",\"amount\":50,\"asset\":{\"assetId\":\"110523\",\"type\":\"ShareAsset\"},\"sellAll\":false,\"incomePreference\":null,\"assetType\":\"Listed security\",\"distributionMethod\":\"Reinvest\",\"units\":50,\"price\":51,\"fundsAllocation\":[{\"accountId\":\"26CC637BC1941BD35B6215CFEBCE6BBE60DBED6B107E0A6F\",\"allocation\":1}],\"expiry\":\"GTC\",\"priceType\":\"lim\"},{\"orderType\":\"buy\",\"amount\":40000,\"asset\":{\"assetId\":\"163214\",\"type\":\"ManagedFundAsset\"},\"sellAll\":false,\"incomePreference\":null,\"assetType\":\"Managed fund\",\"distributionMethod\":\"Cash\",\"units\":40000,\"price\":2.2146,\"fundsAllocation\":[{\"accountId\":\"26CC637BC1941BD35B6215CFEBCE6BBE60DBED6B107E0A6F\",\"allocation\":1}]},{\"orderType\":\"buy\",\"amount\":60000,\"asset\":{\"assetId\":\"111832\",\"type\":\"ManagedPortfolioAsset\"},\"sellAll\":false,\"incomePreference\":\"reinvest\",\"assetType\":\"Managed portfolio\",\"units\":60000,\"fundsAllocation\":[{\"accountId\":\"26CC637BC1941BD35B6215CFEBCE6BBE60DBED6B107E0A6F\",\"allocation\":1}]},{\"orderType\":\"buy\",\"amount\":5000,\"asset\":{\"assetId\":\"879509\",\"type\":\"TermDepositAsset\"},\"sellAll\":false,\"incomePreference\":null,\"assetType\":\"Term deposit\",\"intRate\":0.0245,\"units\":5000,\"fundsAllocation\":[{\"accountId\":\"26CC637BC1941BD35B6215CFEBCE6BBE60DBED6B107E0A6F\",\"allocation\":1}]}],\"reference\":\"test ref\",\"lastUpdateDate\":\"2017-08-09T04:12:12.840Z\"}"))
                .andReturn().getResponse().getStatus();

        ArgumentCaptor<OrderGroupDto> argument = ArgumentCaptor.forClass(OrderGroupDto.class);
        verify(orderGroupDtoService, atLeastOnce()).create(argument.capture(), Mockito.any(ServiceErrors.class));
        OrderGroupDto orderGroup = argument.getValue();
        assertEquals("test ref", orderGroup.getReference());

        OrderItemDto order1 = orderGroup.getOrders().get(0);
        assertEquals("sell", order1.getOrderType());
        assertEquals(new BigDecimal("50"), order1.getAmount());
        assertEquals("110740", order1.getAsset().getAssetId());
        assertEquals(ShareAssetDto.class, order1.getAsset().getClass());
        assertEquals(false, order1.getSellAll());
        assertEquals("Listed security", order1.getAssetType());
        assertEquals("Cash", order1.getDistributionMethod());
        assertEquals(new BigInteger("50"), order1.getUnits());
        assertEquals(new BigDecimal("5.255"), order1.getPrice());
        assertEquals("GFD", order1.getExpiry());
        assertEquals("mkt", order1.getPriceType());
        assertEquals("26CC637BC1941BD35B6215CFEBCE6BBE60DBED6B107E0A6F", order1.getFundsAllocation().get(0).getAccountId());
        assertEquals(new BigDecimal("1"), order1.getFundsAllocation().get(0).getAllocation());

        OrderItemDto order2 = orderGroup.getOrders().get(1);
        assertEquals("sell", order2.getOrderType());
        assertEquals(new BigDecimal("24.44"), order2.getAmount());
        assertEquals("187735", order2.getAsset().getAssetId());
        assertEquals(ManagedFundAssetDto.class, order2.getAsset().getClass());
        assertEquals(true, order2.getSellAll());
        assertEquals("Managed fund", order2.getAssetType());
        assertEquals("Cash", order2.getDistributionMethod());
        assertEquals(new BigInteger("24"), order2.getUnits());
        assertEquals(new BigDecimal("3.8223"), order2.getPrice());
        assertEquals("26CC637BC1941BD35B6215CFEBCE6BBE60DBED6B107E0A6F", order2.getFundsAllocation().get(0).getAccountId());
        assertEquals(new BigDecimal("1"), order2.getFundsAllocation().get(0).getAllocation());

        OrderItemDto order3 = orderGroup.getOrders().get(2);
        assertEquals("sell", order3.getOrderType());
        assertEquals(new BigDecimal("5000"), order3.getAmount());
        assertEquals("147057", order3.getAsset().getAssetId());
        assertEquals(ManagedPortfolioAssetDto.class, order3.getAsset().getClass());
        assertEquals(false, order3.getSellAll());
        assertEquals("Managed portfolio", order3.getAssetType());
        assertEquals("reinvest", order3.getIncomePreference());
        assertEquals(new BigInteger("5000"), order3.getUnits());
        assertEquals("26CC637BC1941BD35B6215CFEBCE6BBE60DBED6B107E0A6F", order3.getFundsAllocation().get(0).getAccountId());
        assertEquals(new BigDecimal("1"), order3.getFundsAllocation().get(0).getAllocation());

        OrderItemDto order4 = orderGroup.getOrders().get(3);
        assertEquals("buy", order4.getOrderType());
        assertEquals(new BigDecimal("50"), order4.getAmount());
        assertEquals("110523", order4.getAsset().getAssetId());
        assertEquals(ShareAssetDto.class, order4.getAsset().getClass());
        assertEquals(false, order4.getSellAll());
        assertEquals("Listed security", order4.getAssetType());
        assertEquals("Reinvest", order4.getDistributionMethod());
        assertEquals(new BigInteger("50"), order4.getUnits());
        assertEquals(new BigDecimal("51"), order4.getPrice());
        assertEquals("GTC", order4.getExpiry());
        assertEquals("lim", order4.getPriceType());
        assertEquals("26CC637BC1941BD35B6215CFEBCE6BBE60DBED6B107E0A6F", order4.getFundsAllocation().get(0).getAccountId());
        assertEquals(new BigDecimal("1"), order4.getFundsAllocation().get(0).getAllocation());

        OrderItemDto order5 = orderGroup.getOrders().get(4);
        assertEquals("buy", order5.getOrderType());
        assertEquals(new BigDecimal("40000"), order5.getAmount());
        assertEquals("163214", order5.getAsset().getAssetId());
        assertEquals(ManagedFundAssetDto.class, order5.getAsset().getClass());
        assertEquals(false, order5.getSellAll());
        assertEquals("Managed fund", order5.getAssetType());
        assertEquals("Cash", order5.getDistributionMethod());
        assertEquals(new BigInteger("40000"), order5.getUnits());
        assertEquals(new BigDecimal("2.2146"), order5.getPrice());
        assertEquals("26CC637BC1941BD35B6215CFEBCE6BBE60DBED6B107E0A6F", order5.getFundsAllocation().get(0).getAccountId());
        assertEquals(new BigDecimal("1"), order5.getFundsAllocation().get(0).getAllocation());

        OrderItemDto order6 = orderGroup.getOrders().get(5);
        assertEquals("buy", order6.getOrderType());
        assertEquals(new BigDecimal("60000"), order6.getAmount());
        assertEquals("111832", order6.getAsset().getAssetId());
        assertEquals(ManagedPortfolioAssetDto.class, order6.getAsset().getClass());
        assertEquals(false, order6.getSellAll());
        assertEquals("Managed portfolio", order6.getAssetType());
        assertEquals(new BigInteger("60000"), order6.getUnits());
        assertEquals("26CC637BC1941BD35B6215CFEBCE6BBE60DBED6B107E0A6F", order6.getFundsAllocation().get(0).getAccountId());
        assertEquals(new BigDecimal("1"), order6.getFundsAllocation().get(0).getAllocation());

        OrderItemDto order7 = orderGroup.getOrders().get(6);
        assertEquals("buy", order7.getOrderType());
        assertEquals(new BigDecimal("5000"), order7.getAmount());
        assertEquals("879509", order7.getAsset().getAssetId());
        assertEquals(TermDepositAssetDto.class, order7.getAsset().getClass());
        assertEquals(false, order7.getSellAll());
        assertEquals("Term deposit", order7.getAssetType());
        assertEquals(new BigDecimal("0.0245"), order7.getIntRate());
        assertEquals(new BigInteger("5000"), order7.getUnits());
        assertEquals("26CC637BC1941BD35B6215CFEBCE6BBE60DBED6B107E0A6F", order7.getFundsAllocation().get(0).getAccountId());
        assertEquals(new BigDecimal("1"), order7.getFundsAllocation().get(0).getAllocation());

        // check that params passed in that aren't whitelisted are null 
        assertNull(orderGroup.getOwnerName());

        // URL cannot be mapped, status = 404.
        // Any controller error, status = 500.
        Assert.assertEquals(200, statusInt);
    }

    /**
     * Test method for
     * {@link com.bt.nextgen.api.order.controller.OrderGroupApiController#create(java.lang.String, java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public void testCreate_whenNoPermission_thenAccessedDeniedExceptionThrown() throws Exception {
        mockMvc = MockMvcBuilders.annotationConfigSetup(TestConfiguration.class).build();

        Mockito.when(permissionsDto.hasPermission(Mockito.anyString())).thenReturn(false);
        Mockito.when(permissionAccountDtoService.find(Mockito.any(PermissionAccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(permissionsDto);

        try {
            String url = "/secure/api/v1_0/accounts/0465D5F8D68A1A43612FF477E9254F55358ED2CB3BF5E9F0/order-groups";
            mockMvc.perform(post(url).param("orderGroup",
                    "{\"ownerName\":\"bojack\", \"orders\":[{\"orderType\":\"buy\",\"amount\":3333,\"asset\":{\"assetId\":\"abcd\",\"type\":\"TermDepositAsset\"},\"sellAll\":false},{\"orderType\":\"buy\",\"amount\":22222,\"asset\":{\"assetId\":\"28100\",\"type\":\"TermDepositAsset\"},\"sellAll\":false,\"assetType\":\"Term deposit\",\"fundsAllocation\":[{\"accountId\":\"6F7AE7E41CBD180BA3245476B77FECE1BF7E1E388109A387\",\"allocation\":1}]}]}"))
                    .andReturn().getResponse().getStatus();
        } catch (AccessDeniedException e1) {
            assert (true);
        } catch (Exception e) {
            fail("Access Denied Exception Not thrown");
        }
    }

    @Test
    public void testCreate_whenEmulating_thenAccessedDeniedExceptionThrown() throws Exception {
        mockMvc = MockMvcBuilders.annotationConfigSetup(TestConfiguration.class).build();
        Mockito.when(profileService.isEmulating()).thenReturn(true);

        try {
            String url = "/secure/api/v1_0/accounts/0465D5F8D68A1A43612FF477E9254F55358ED2CB3BF5E9F0/order-groups";
            mockMvc.perform(post(url).param("orderGroup",
                    "{\"ownerName\":\"bojack\", \"orders\":[{\"orderType\":\"buy\",\"amount\":3333,\"asset\":{\"assetId\":\"abcd\",\"type\":\"TermDepositAsset\"},\"sellAll\":false},{\"orderType\":\"buy\",\"amount\":22222,\"asset\":{\"assetId\":\"28100\",\"type\":\"TermDepositAsset\"},\"sellAll\":false,\"assetType\":\"Term deposit\",\"fundsAllocation\":[{\"accountId\":\"6F7AE7E41CBD180BA3245476B77FECE1BF7E1E388109A387\",\"allocation\":1}]}]}"))
                    .andReturn().getResponse().getStatus();
        } catch (AccessDeniedException e1) {
            assert (true);
        } catch (Exception e) {
            fail("Access Denied Exception Not thrown");
        }
    }

    @Test
    public void testValidate_whenParamsPassed_thenDtoMappedCorrectly() throws Exception {
        mockMvc = MockMvcBuilders.annotationConfigSetup(TestConfiguration.class).build();

        String url = "/secure/api/v1_0/accounts/0465D5F8D68A1A43612FF477E9254F55358ED2CB3BF5E9F0/order-groups";

        int statusInt = mockMvc.perform(post(url).param("x-ro-validate-only", "true").param("orderGroup",
                "{\"orders\":[{\"orderType\":\"buy\",\"amount\":10000,\"asset\":{\"assetId\":\"1039959\",\"type\":\"ManagedPortfolioAsset\"},\"sellAll\":false,\"incomePreference\":\"reinvest\",\"assetType\":\"Tailored portfolio\",\"units\":10000,\"fundsAllocation\":[{\"accountId\":\"26CC637BC1941BD35B6215CFEBCE6BBE60DBED6B107E0A6F\",\"allocation\":1}],\"fees\":[{\"structure\":\"SLIDING_SCALE_FEE\",\"percentFee\":{\"rate\":0},\"slidingFee\":{\"tiers\":[{\"upperBound\":500,\"rate\":0.01},{\"upperBound\":1000,\"rate\":0.02},{\"upperBound\":\"\",\"rate\":0.03}]},\"feeType\":\"PORTFOLIO_MANAGEMENT_FEE\"}]}]}"))
                .andReturn().getResponse().getStatus();

        ArgumentCaptor<OrderGroupDto> argument = ArgumentCaptor.forClass(OrderGroupDto.class);
        verify(orderGroupDtoService, atLeastOnce()).validate(argument.capture(), Mockito.any(ServiceErrors.class));
        OrderGroupDto orderGroup = argument.getValue();
        OrderItemDto order = orderGroup.getOrders().get(0);
        assertEquals("buy", order.getOrderType());
        assertEquals(new BigDecimal("10000"), order.getAmount());
        assertEquals("1039959", order.getAsset().getAssetId());
        assertEquals(ManagedPortfolioAssetDto.class, order.getAsset().getClass());
        assertEquals(false, order.getSellAll());
        assertEquals("reinvest", order.getIncomePreference());
        assertEquals(AssetType.TAILORED_PORTFOLIO.getDisplayName(), order.getAssetType());
        assertEquals(new BigInteger("10000"), order.getUnits());
        assertEquals("26CC637BC1941BD35B6215CFEBCE6BBE60DBED6B107E0A6F", order.getFundsAllocation().get(0).getAccountId());
        assertEquals(new BigDecimal("1"), order.getFundsAllocation().get(0).getAllocation());
        assertEquals(FeesComponentType.SLIDING_SCALE_FEE, order.getFees().get(0).getStructure());
        assertEquals(FeesType.PORTFOLIO_MANAGEMENT_FEE, order.getFees().get(0).getFeeType());
        assertEquals(new BigDecimal("500"), order.getFees().get(0).getSlidingFee().getTiers().get(0).getUpperBound());
        assertEquals(new BigDecimal("0.01"), order.getFees().get(0).getSlidingFee().getTiers().get(0).getRate());
        assertEquals(new BigDecimal("0"), order.getFees().get(0).getPercentFee().getRate());

        assertNull(orderGroup.getOwnerName());

        Assert.assertEquals(200, statusInt);
    }

    @Test
    public void testSubmit_whenParamsPassed_thenDtoMappedCorrectly() throws Exception {
        mockMvc = MockMvcBuilders.annotationConfigSetup(TestConfiguration.class).build();

        String url = "/secure/api/v1_0/accounts/0465D5F8D68A1A43612FF477E9254F55358ED2CB3BF5E9F0/order-groups";

        int statusInt = mockMvc.perform(post(url).param("orderGroup",
                "{\"status\":\"submit\",\"orders\":[{\"orderType\":\"sell\",\"amount\":50,\"asset\":{\"assetId\":\"110740\",\"type\":\"ShareAsset\"},\"sellAll\":false,\"incomePreference\":null,\"assetType\":\"Listed security\",\"distributionMethod\":\"Cash\",\"units\":50,\"price\":5.255,\"fundsAllocation\":[{\"accountId\":\"26CC637BC1941BD35B6215CFEBCE6BBE60DBED6B107E0A6F\",\"allocation\":1}],\"expiry\":\"GFD\",\"priceType\":\"mkt\"},{\"orderType\":\"sell\",\"amount\":24.44,\"asset\":{\"assetId\":\"187735\",\"type\":\"ManagedFundAsset\"},\"sellAll\":true,\"incomePreference\":null,\"assetType\":\"Managed fund\",\"distributionMethod\":\"Cash\",\"units\":24.44,\"price\":3.8223,\"fundsAllocation\":[{\"accountId\":\"26CC637BC1941BD35B6215CFEBCE6BBE60DBED6B107E0A6F\",\"allocation\":1}]},{\"orderType\":\"sell\",\"subAccountId\":\"BAFBB8039A5F695A76EB6810BCC4D082C9B526FC69DFC78A\",\"amount\":5000,\"asset\":{\"assetId\":\"147057\",\"type\":\"ManagedPortfolioAsset\"},\"sellAll\":false,\"incomePreference\":\"reinvest\",\"assetType\":\"Managed portfolio\",\"units\":5000,\"fundsAllocation\":[{\"accountId\":\"26CC637BC1941BD35B6215CFEBCE6BBE60DBED6B107E0A6F\",\"allocation\":1}]},{\"orderType\":\"buy\",\"amount\":50,\"asset\":{\"assetId\":\"110523\",\"type\":\"ShareAsset\"},\"sellAll\":false,\"incomePreference\":null,\"assetType\":\"Listed security\",\"distributionMethod\":\"Reinvest\",\"units\":50,\"price\":50,\"fundsAllocation\":[{\"accountId\":\"26CC637BC1941BD35B6215CFEBCE6BBE60DBED6B107E0A6F\",\"allocation\":1}],\"expiry\":\"GTC\",\"priceType\":\"lim\"},{\"orderType\":\"buy\",\"amount\":40000,\"asset\":{\"assetId\":\"163214\",\"type\":\"ManagedFundAsset\"},\"sellAll\":false,\"incomePreference\":null,\"assetType\":\"Managed fund\",\"distributionMethod\":\"Cash\",\"units\":40000,\"price\":2.2146,\"fundsAllocation\":[{\"accountId\":\"26CC637BC1941BD35B6215CFEBCE6BBE60DBED6B107E0A6F\",\"allocation\":1}]},{\"orderType\":\"buy\",\"amount\":60000,\"asset\":{\"assetId\":\"111832\",\"type\":\"ManagedPortfolioAsset\"},\"sellAll\":false,\"incomePreference\":\"reinvest\",\"assetType\":\"Managed portfolio\",\"units\":60000,\"fundsAllocation\":[{\"accountId\":\"26CC637BC1941BD35B6215CFEBCE6BBE60DBED6B107E0A6F\",\"allocation\":1}]},{\"orderType\":\"buy\",\"amount\":5000,\"asset\":{\"assetId\":\"879509\",\"type\":\"TermDepositAsset\"},\"sellAll\":false,\"incomePreference\":null,\"assetType\":\"Term deposit\",\"intRate\":0.0245,\"units\":5000,\"fundsAllocation\":[{\"accountId\":\"26CC637BC1941BD35B6215CFEBCE6BBE60DBED6B107E0A6F\",\"allocation\":1}]}],\"key\":{\"orderGroupId\":\"9591856\"},\"transactionSeq\":2}"))
                .andReturn().getResponse().getStatus();

        ArgumentCaptor<OrderGroupDto> argument = ArgumentCaptor.forClass(OrderGroupDto.class);
        verify(orderGroupDtoService, atLeastOnce()).submit(argument.capture(), Mockito.any(ServiceErrors.class));
        OrderGroupDto orderGroup = argument.getValue();
        assertEquals("9591856", orderGroup.getKey().getOrderGroupId());
        assertEquals(new BigInteger("2"), orderGroup.getTransactionSeq());

        OrderItemDto order1 = orderGroup.getOrders().get(0);
        assertEquals("sell", order1.getOrderType());
        assertEquals(new BigDecimal("50"), order1.getAmount());
        assertEquals("110740", order1.getAsset().getAssetId());
        assertEquals(ShareAssetDto.class, order1.getAsset().getClass());
        assertEquals(false, order1.getSellAll());
        assertEquals("Listed security", order1.getAssetType());
        assertEquals("Cash", order1.getDistributionMethod());
        assertEquals(new BigInteger("50"), order1.getUnits());
        assertEquals(new BigDecimal("5.255"), order1.getPrice());
        assertEquals("GFD", order1.getExpiry());
        assertEquals("mkt", order1.getPriceType());
        assertEquals("26CC637BC1941BD35B6215CFEBCE6BBE60DBED6B107E0A6F", order1.getFundsAllocation().get(0).getAccountId());
        assertEquals(new BigDecimal("1"), order1.getFundsAllocation().get(0).getAllocation());

        assertNull(orderGroup.getOwnerName());

        Assert.assertEquals(200, statusInt);
    }

    @Test
    public void testUpdate_whenParamsPassed_thenDtoMappedCorrectly() throws Exception {
        mockMvc = MockMvcBuilders.annotationConfigSetup(TestConfiguration.class).build();

        String url = "/secure/api/v1_0/accounts/0465D5F8D68A1A43612FF477E9254F55358ED2CB3BF5E9F0/order-groups/12345";
        int statusInt = mockMvc.perform(post(url).param("orderGroup",
                "{\"key\": {\"orderGroupId\": \"12345\", \"accountId\": \"abcd\"}, \"transactionSeq\":2, \"ownerName\":\"bojack\", \"orders\":[{\"orderType\":\"buy\",\"amount\":3333,\"asset\":{\"assetId\":\"abcd\",\"type\":\"TermDepositAsset\"},\"sellAll\":false},{\"orderType\":\"buy\",\"amount\":22222,\"asset\":{\"assetId\":\"28100\",\"type\":\"TermDepositAsset\"},\"sellAll\":false,\"assetType\":\"Term deposit\",\"fundsAllocation\":[{\"accountId\":\"6F7AE7E41CBD180BA3245476B77FECE1BF7E1E388109A387\",\"allocation\":1}]}]}"))
                .andReturn().getResponse().getStatus();

        ArgumentCaptor<OrderGroupDto> argument = ArgumentCaptor.forClass(OrderGroupDto.class);
        verify(orderGroupDtoService, atLeastOnce()).update(argument.capture(), Mockito.any(ServiceErrors.class));
        OrderGroupDto orderGroup = argument.getValue();
        OrderItemDto order = orderGroup.getOrders().get(0);
        assertEquals("buy", order.getOrderType());
        assertEquals(new BigDecimal("3333"), order.getAmount());
        assertEquals("abcd", order.getAsset().getAssetId());
        assertEquals(TermDepositAssetDto.class, order.getAsset().getClass());
        assertEquals(false, order.getSellAll());
        assertNull(orderGroup.getOwnerName());

        Assert.assertEquals(200, statusInt);
    }

    @Test
    public void testValidateSaved_whenParamsPassed_thenDtoMappedCorrectly() throws Exception {
        mockMvc = MockMvcBuilders.annotationConfigSetup(TestConfiguration.class).build();

        String url = "/secure/api/v1_0/accounts/0465D5F8D68A1A43612FF477E9254F55358ED2CB3BF5E9F0/order-groups/12345";

        int statusInt = mockMvc.perform(post(url).param("x-ro-validate-only", "true").param("orderGroup",
                "{\"ownerName\":\"bojack\", \"orders\":[{\"orderType\":\"buy\",\"amount\":3333,\"asset\":{\"assetId\":\"abcd\",\"type\":\"TermDepositAsset\"},\"sellAll\":false},{\"orderType\":\"buy\",\"amount\":22222,\"asset\":{\"assetId\":\"28100\",\"type\":\"TermDepositAsset\"},\"sellAll\":false,\"assetType\":\"Term deposit\",\"fundsAllocation\":[{\"accountId\":\"6F7AE7E41CBD180BA3245476B77FECE1BF7E1E388109A387\",\"allocation\":1}]}]}"))
                .andReturn().getResponse().getStatus();

        ArgumentCaptor<OrderGroupDto> argument = ArgumentCaptor.forClass(OrderGroupDto.class);
        verify(orderGroupDtoService, atLeastOnce()).validate(argument.capture(), Mockito.any(ServiceErrors.class));
        OrderGroupDto orderGroup = argument.getValue();
        OrderItemDto order = orderGroup.getOrders().get(0);
        assertEquals("buy", order.getOrderType());
        assertEquals(new BigDecimal("3333"), order.getAmount());
        assertEquals("abcd", order.getAsset().getAssetId());
        assertEquals(TermDepositAssetDto.class, order.getAsset().getClass());
        assertEquals(false, order.getSellAll());
        assertNull(orderGroup.getOwnerName());

        Assert.assertEquals(200, statusInt);
    }

    @Test
    public void testSubmitSaved_whenParamsPassed_thenDtoMappedCorrectly() throws Exception {
        mockMvc = MockMvcBuilders.annotationConfigSetup(TestConfiguration.class).build();

        String url = "/secure/api/v1_0/accounts/0465D5F8D68A1A43612FF477E9254F55358ED2CB3BF5E9F0/order-groups/12345";

        int statusInt = mockMvc.perform(post(url).param("orderGroup",
                "{\"status\":\"submit\", \"ownerName\":\"bojack\", \"orders\":[{\"orderType\":\"buy\",\"amount\":3333,\"asset\":{\"assetId\":\"abcd\",\"type\":\"TermDepositAsset\"},\"sellAll\":false},{\"orderType\":\"buy\",\"amount\":22222,\"asset\":{\"assetId\":\"28100\",\"type\":\"TermDepositAsset\"},\"sellAll\":false,\"assetType\":\"Term deposit\",\"fundsAllocation\":[{\"accountId\":\"6F7AE7E41CBD180BA3245476B77FECE1BF7E1E388109A387\",\"allocation\":1}]}]}"))
                .andReturn().getResponse().getStatus();

        ArgumentCaptor<OrderGroupDto> argument = ArgumentCaptor.forClass(OrderGroupDto.class);
        verify(orderGroupDtoService, atLeastOnce()).submit(argument.capture(), Mockito.any(ServiceErrors.class));
        OrderGroupDto orderGroup = argument.getValue();
        OrderItemDto order = orderGroup.getOrders().get(0);
        assertEquals("buy", order.getOrderType());
        assertEquals(new BigDecimal("3333"), order.getAmount());
        assertEquals("abcd", order.getAsset().getAssetId());
        assertEquals(TermDepositAssetDto.class, order.getAsset().getClass());
        assertEquals(false, order.getSellAll());
        assertNull(orderGroup.getOwnerName());

        Assert.assertEquals(200, statusInt);
    }

    /**
     * Test method for {@link com.bt.nextgen.api.order.controller.OrderGroupApiController#search(java.lang.String)} .
     */
    @Test
    public final void testSearch() throws Exception {
        List<OrderGroupDto> orderGroupDtos = new ArrayList<>();
        OrderGroupDto orderGroupDto = new OrderGroupDto();
        orderGroupDto.setAccountName("Sample");
        orderGroupDtos.add(orderGroupDto);

        Mockito.when(orderGroupDtoService.search(Mockito.anyListOf(ApiSearchCriteria.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(orderGroupDtos);

        mockHttpServletRequest.setRequestURI("/secure/api/v1_0/accounts/sample-account-id/order-groups");
        mockHttpServletRequest.setParameter("account-id", "sample-account-id");
        mockHttpServletRequest.setMethod("GET");

        annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, orderGroupApiController);

    }

    /**
     * Test method for {@link com.bt.nextgen.api.order.controller.OrderGroupApiController#searchOrders(java.lang.String)} .
     */
    @Test
    public final void testSearchOrders() throws Exception {
        List<OrderGroupDto> orderGroupDtos = new ArrayList<>();
        OrderGroupDto orderGroupDto = new OrderGroupDto();
        orderGroupDto.setAccountName("Sample");
        orderGroupDtos.add(orderGroupDto);

        Mockito.when(orderGroupDtoService.findAll(Mockito.any(ServiceErrors.class))).thenReturn(orderGroupDtos);

        mockHttpServletRequest.setRequestURI("/secure/api/v1_0/orders/order-groups");
        mockHttpServletRequest.setParameter("order-by", "ASC");
        mockHttpServletRequest.setMethod("GET");

        annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, orderGroupApiController);
    }

    /**
     * Test method for
     * {@link com.bt.nextgen.api.order.controller.OrderGroupApiController#load(java.lang.String, java.lang.String)} .
     */
    @Test
    public final void testLoad() throws Exception {

        OrderGroupDto orderGroupDto = new OrderGroupDto();
        orderGroupDto.setAccountName("Sample");

        Mockito.when(profileService.isEmulating()).thenReturn(false);

        Mockito.when(orderGroupDtoService.find(Mockito.any(OrderGroupKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(orderGroupDto);

        mockHttpServletRequest.setRequestURI("/secure/api/v1_0/accounts/sample-account-id/order-groups/order-id=234324");
        mockHttpServletRequest.setMethod("GET");

        annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, orderGroupApiController);
    }

    @Test
    public final void testLoad_whenEmulating_thenAccessDeniedExceptionThrown() throws Exception {

        OrderGroupDto orderGroupDto = new OrderGroupDto();
        orderGroupDto.setAccountName("Sample");

        Mockito.when(profileService.isEmulating()).thenReturn(true);

        Mockito.when(orderGroupDtoService.find(Mockito.any(OrderGroupKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(orderGroupDto);

        mockHttpServletRequest.setRequestURI("/secure/api/v1_0/accounts/sample-account-id/order-groups/order-id=234324");
        mockHttpServletRequest.setMethod("GET");

        try {
            annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, orderGroupApiController);
        } catch (AccessDeniedException e1) {
            assert (true);
        } catch (Exception e) {
            fail("Access Denied Exception Not thrown");
        }
    }

    /**
     * Test method for
     * {@link com.bt.nextgen.api.order.controller.OrderGroupApiController#delete(java.lang.String, java.lang.String)} .
     */
    @Test
    public final void testDelete() throws Exception {

        OrderGroupDto orderGroupDto = new OrderGroupDto();
        orderGroupDto.setAccountName("Sample");

        Mockito.when(profileService.isEmulating()).thenReturn(false);

        Mockito.doNothing().when(orderGroupDtoService).delete(Mockito.any(OrderGroupKey.class), Mockito.any(ServiceErrors.class));

        Mockito.when(permissionAccountDtoService.find(Mockito.any(PermissionAccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(permissionsDto);

        mockHttpServletRequest.setRequestURI("/secure/api/v1_0/accounts/sample-account-id/order-groups/order-id=234324/delete");
        mockHttpServletRequest.setMethod("POST");
        mockHttpServletRequest.setParameter("order-id", "sample-order-id");
        mockHttpServletRequest.setParameter("account-id", "sample-account-id");

        annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, orderGroupApiController);
    }

    @Test
    public final void testDelete_whenEmulating_thenAccessDeniedExceptionThrown() throws Exception {
        OrderGroupDto orderGroupDto = new OrderGroupDto();
        orderGroupDto.setAccountName("Sample");

        Mockito.when(profileService.isEmulating()).thenReturn(true);
        Mockito.doNothing().when(orderGroupDtoService).delete(Mockito.any(OrderGroupKey.class), Mockito.any(ServiceErrors.class));

        mockHttpServletRequest.setRequestURI("/secure/api/v1_0/accounts/sample-account-id/order-groups/order-id=234324/delete");
        mockHttpServletRequest.setMethod("POST");
        mockHttpServletRequest.setParameter("order-id", "sample-order-id");
        mockHttpServletRequest.setParameter("account-id", "sample-account-id");

        try {
            annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, orderGroupApiController);
        } catch (AccessDeniedException e1) {
            assert (true);
        } catch (Exception e) {
            fail("Access Denied Exception Not thrown");
        }
    }
}
