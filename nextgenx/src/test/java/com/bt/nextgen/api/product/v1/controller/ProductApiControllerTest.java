package com.bt.nextgen.api.product.v1.controller;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.broker.model.BrokerKey;
import com.bt.nextgen.api.product.v1.model.AccountProductDocumentDto;
import com.bt.nextgen.api.product.v1.model.BrokerProductDocumentDto;
import com.bt.nextgen.api.product.v1.model.BrokerProductKey;
import com.bt.nextgen.api.product.v1.model.ProductDto;
import com.bt.nextgen.api.product.v1.model.ProductKey;
import com.bt.nextgen.api.product.v1.service.AccountProductDocumentDtoService;
import com.bt.nextgen.api.product.v1.service.BrokerProductDocumentDtoService;
import com.bt.nextgen.api.product.v1.service.ProductDtoService;
import com.bt.nextgen.api.product.v1.service.ProductSearchDtoService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.product.ProductLevel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

/**
 * Since the ProductApiController is largely a pass through to dto services, these tests focus on ensuring that URIs remain
 * correct and the pass through integrity is maintained (e.g. keys passed reflect request params).
 * 
 * @author M040005
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ProductApiControllerTest {

    @EnableWebMvc
    @ComponentScan(basePackages = { "com.bt.nextgen.api.product.v1.controller" },
            excludeFilters = @ComponentScan.Filter(value = ProductFeeApiController.class, type = FilterType.ASSIGNABLE_TYPE))
    public static class TestConfiguration {

        @Bean(name = "PropertyPlaceholderConfigurer")
        PropertyPlaceholderConfigurer propertyPlaceholderConfigurerBean() {
            final PropertyPlaceholderConfigurer pc = new PropertyPlaceholderConfigurer();
            pc.setLocation(new ClassPathResource("/com/bt/nextgen/api/product/v1/UriConfig.properties"));
            return pc;
        }

        @Bean
        ProductDtoService productDtoService() {
            return productDtoService;
        }

        @Bean
        ProductSearchDtoService productSearchDtoService() {
            return productSearchDtoService;
        }

        @Bean
        BrokerProductDocumentDtoService brokerProductDocumentsDtoService() {
            return brokerProductDocumentsDtoService;
        }

        @Bean
        AccountProductDocumentDtoService accountProductDocumentDtoService() {
            return accountProductDocumentDtoService;
        }

    }

    private org.springframework.test.web.server.MockMvc mockMvc;

    @InjectMocks
    ProductApiController productApiController;

    @Mock
    private static ProductDtoService productDtoService;

    @Mock
    private static ProductSearchDtoService productSearchDtoService;

    @Mock
    private static BrokerProductDocumentDtoService brokerProductDocumentsDtoService;

    @Mock
    private static AccountProductDocumentDtoService accountProductDocumentDtoService;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.annotationConfigSetup(TestConfiguration.class).build();

        final ProductDto productDto = new ProductDto();
        productDto.setKey(new ProductKey("omo"));
        productDto.setActive(true);
        productDto.setProductLevel(ProductLevel.WHITE_LABEL.name());
        Mockito.when(productDtoService.find(Mockito.any(ProductKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(productDto);
        Mockito.when(productDtoService.findAll(Mockito.any(ServiceErrors.class)))
                .thenReturn(Collections.singletonList(productDto));
        Mockito.when(productDtoService.search(Mockito.any(List.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(Collections.singletonList(productDto));

        Mockito.when(productSearchDtoService.findAll(Mockito.any(ServiceErrors.class)))
                .thenReturn(Collections.singletonList(productDto));
        final BrokerProductDocumentDto brokerProductDocumentDto = new BrokerProductDocumentDto(new BrokerProductKey("777", "123"), null, null,
                null, false);
        final AccountProductDocumentDto accountProductDocumentDto = new AccountProductDocumentDto(new AccountKey("333"), null,
                null, null, false);
        Mockito.when(brokerProductDocumentsDtoService.findAll(Mockito.any(ServiceErrors.class)))
                .thenReturn(Arrays.asList(brokerProductDocumentDto));
        Mockito.when(accountProductDocumentDtoService.find(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(accountProductDocumentDto);

    }

    @Test
    public final void testGetProduct_whenTheCorrectUriIsRequested_thenTheCorrectMethodsAreInvoked() throws Exception {
        this.mockMvc.perform(get("/secure/api/products/v1_0/omo").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().mimeType("application/json")).andExpect(jsonPath("$.data.key").exists());
        Mockito.verify(productDtoService).find(Mockito.eq(new ProductKey("omo")), Mockito.any(ServiceErrors.class));
    }

    @Test
    public final void testGetProducts_whenTheCorrectUriIsRequested_thenTheCorrectMethodsAreInvoked() throws Exception {
        this.mockMvc.perform(get("/secure/api/products/v1_0/").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().mimeType("application/json")).andExpect(jsonPath("$.data.resultList").exists());
        Mockito.verify(productDtoService).findAll(Mockito.any(ServiceErrors.class));
    }

    @Test
    public final void getClientListProducts_whenTheCorrectUriIsRequested_thenTheCorrectMethodsAreInvoked() throws Exception {
        this.mockMvc.perform(get("/secure/api/products/v1_0/clients").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().mimeType("application/json"))
                .andExpect(jsonPath("$.data.resultList").exists());
        Mockito.verify(productSearchDtoService).findAll(Mockito.any(ServiceErrors.class));
    }

    @Test
    public final void getAdviserProducts_whenTheCorrectUriIsRequested_thenTheCorrectMethodsAreInvoked() throws Exception {
        this.mockMvc.perform(get("/secure/api/products/v1_0/adviser/bob").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().mimeType("application/json"))
                .andExpect(jsonPath("$.data.resultList").exists());
        Mockito.verify(productDtoService).search(Mockito.any(List.class), Mockito.any(ServiceErrors.class));
    }

    @Test
    public final void getClientProducts_whenTheCorrectUriIsRequested_thenTheCorrectMethodsAreInvoked() throws Exception {
        this.mockMvc.perform(get("/secure/api/products/v1_0/client/mary").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().mimeType("application/json"))
                .andExpect(jsonPath("$.data.resultList").exists());
        Mockito.verify(productDtoService).search(Mockito.any(List.class), Mockito.any(ServiceErrors.class));
    }

    @Test
    public final void getBrokerProductDocument_whenTheCorrectUriIsRequested_thenTheCorrectMethodsAreInvoked() throws Exception {
        this.mockMvc.perform(get("/secure/api/products/v1_0/documents").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().mimeType("application/json"))
                .andExpect(jsonPath("$.data.resultList").exists()).andDo(print());
        Mockito.verify(brokerProductDocumentsDtoService).findAll(Mockito.any(ServiceErrors.class));
    }

    @Test
    public final void getAccountProductDocument_whenTheCorrectUriIsRequested_thenTheCorrectMethodsAreInvoked() throws Exception {
        this.mockMvc.perform(get("/secure/api/products/v1_0/account/333/documents").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().mimeType("application/json"))
                .andExpect(jsonPath("$.data.key").exists()).andDo(print());
        Mockito.verify(accountProductDocumentDtoService).find(Mockito.eq(new AccountKey("333")),
                Mockito.any(ServiceErrors.class));
    }

}
