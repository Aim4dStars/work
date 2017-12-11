package com.bt.nextgen.api.product.v1.controller;

import com.bt.nextgen.api.product.v1.model.ProductFeeDto;
import com.bt.nextgen.api.product.v1.service.ProductFeeService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
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

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

/**
 * Since the ProductFeeApiController is largely a pass through to dto services, these tests focus on ensuring that URIs remain
 * correct and the pass through integrity is maintained (e.g. keys passed reflect request params).
 * 
 * @author M040005
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ProductFeeApiControllerTest {

    @EnableWebMvc
    @ComponentScan(basePackages = { "com.bt.nextgen.api.product.v1.controller" },
            excludeFilters = @ComponentScan.Filter(value = ProductApiController.class, type = FilterType.ASSIGNABLE_TYPE))
    public static class TestConfiguration {

        @Bean(name = "PropertyPlaceholderConfigurer")
        PropertyPlaceholderConfigurer propertyPlaceholderConfigurerBean() {
            final PropertyPlaceholderConfigurer pc = new PropertyPlaceholderConfigurer();
            pc.setLocation(new ClassPathResource("/com/bt/nextgen/api/product/v1/UriConfig.properties"));
            return pc;
        }

        @Bean
        ProductFeeService productFeeService() {
            return productFeeService;
        }

    }

    private org.springframework.test.web.server.MockMvc mockMvc;

    @InjectMocks
    ProductFeeApiController productApiController;

    @Mock
    private static ProductFeeService productFeeService;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.annotationConfigSetup(TestConfiguration.class).build();

        Mockito.when(productFeeService.findProductFee(Mockito.anyString(), Mockito.anyString(), Mockito.any(ServiceErrors.class)))
                .thenReturn(new ProductFeeDto());

    }

    @Test
    public final void testGetProduct_whenTheCorrectUriIsRequested_thenTheCorrectMethodsAreInvoked() throws Exception {
        final String adviserId = EncodedString.fromPlainText("asdf").toString();
        final String productId = EncodedString.fromPlainText("hjkl").toString();
        this.mockMvc
                .perform(get("/secure/api/products/v1_0/" + productId + "/adviser/" + adviserId + "/fee")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().mimeType("application/json"));
        Mockito.verify(productFeeService).findProductFee(Mockito.eq("asdf"), Mockito.eq("hjkl"),
                Mockito.any(ServiceErrors.class));
    }

}
