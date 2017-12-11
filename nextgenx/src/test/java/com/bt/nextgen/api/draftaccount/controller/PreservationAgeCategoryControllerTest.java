package com.bt.nextgen.api.draftaccount.controller;

import com.bt.nextgen.api.draftaccount.FormDataValidator;
import com.bt.nextgen.api.draftaccount.model.PreservationAgeDto;
import com.bt.nextgen.api.draftaccount.service.PreservationAgeService;
import com.bt.nextgen.config.JsonObjectMapper;
import com.bt.nextgen.config.SecureJsonObjectMapper;
import com.bt.nextgen.service.ServiceErrors;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.server.MockMvc;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by L069552 on 21/08/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class PreservationAgeCategoryControllerTest {

    @InjectMocks
    private PreservationAgeCategoryController preservationAgeCategoryController;

    @Mock
    private static ClientApplicationDtoDeserializer clientApplicationDtoDeserializer;

    @Mock
    private static PreservationAgeService preservationAgeService;

    @Mock
    private static AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter;

    @Mock
    private static FormDataValidator formDataValidator;

    private MockHttpServletRequest mockHttpServletRequest;
    private MockHttpServletResponse mockHttpServletResponse;
    private MockMvc mockMvc;
    List<PreservationAgeDto> preservationAgeDtoList;


    @EnableWebMvc
    @ComponentScan(basePackages = { "com.bt.nextgen.api.draftaccount.controller" },resourcePattern = "**/PreservationAgeCategoryController.class")
    public static class TestConfiguration {

        @Bean(name = "PropertyPlaceholderConfigurer")
        PropertyPlaceholderConfigurer propertyPlaceholderConfigurerBean() {
            PropertyPlaceholderConfigurer pc = new PropertyPlaceholderConfigurer();
            return pc;
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


        @Bean(name="PreservationAgeService")
        PreservationAgeService preservationAgeService(){
            return preservationAgeService;
        }

    }

    @Before
    public void setUp(){

        mockHttpServletRequest = new MockHttpServletRequest(RequestMethod.GET.name(), "/");
        mockHttpServletResponse = new MockHttpServletResponse();
        annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
        HttpMessageConverter[] messageConverters = { new MappingJackson2HttpMessageConverter() };
        annotationMethodHandlerAdapter.setMessageConverters(messageConverters);
        preservationAgeDtoList = new ArrayList<>();
        PreservationAgeDto preservationAgeDto = new PreservationAgeDto();
        preservationAgeDto.setAge(56);
        preservationAgeDto.setBirthDateFrom("01-JUL-1960");
        preservationAgeDto.setBirthDateTo("30-JUN-1961");
        preservationAgeDtoList.add(preservationAgeDto);
    }

    @Test
    public void testGetPreservationAge_ForBTInvest() throws Exception {
        String preservationAgeUrl = "/onboard/api/v1_0/client_application/preservation_age";
        mockMvc = MockMvcBuilders.annotationConfigSetup(TestConfiguration.class).build();
        when(preservationAgeService.findAll(any(ServiceErrors.class))).thenReturn(preservationAgeDtoList);
        mockMvc.perform(get(preservationAgeUrl)).andExpect(status().isOk()).
                andExpect(jsonPath("$.data.resultList[0].age").exists()).
                andExpect(jsonPath("$.data.resultList[0].birthDateFrom").exists()).
                andExpect(jsonPath("$data.resultList[0].age").value(56)).
                andExpect(jsonPath("$data.resultList[0].birthDateFrom").value("01-JUL-1960")).
                andExpect(jsonPath("$.data.resultList[0].birthDateTo").exists()).
                andExpect(jsonPath("$data.resultList[0].birthDateTo").value("30-JUN-1961"));
    }

    @Test
    public void testGetPreservationAge_PanoramaUser() throws Exception {
        String preservationAgeUrl = "/secure/api/v1_0/client_application/preservation_age";
        mockMvc = MockMvcBuilders.annotationConfigSetup(TestConfiguration.class).build();
        when(preservationAgeService.findAll(any(ServiceErrors.class))).thenReturn(preservationAgeDtoList);
        mockMvc.perform(get(preservationAgeUrl)).andExpect(status().isOk());

    }


    @Test
    public void testGetPreservationAge_InvalidURL() throws Exception {
        String preservationAgeUrl = "/onboard/api/v1_0/client_application/preservation_ages";
        mockMvc = MockMvcBuilders.annotationConfigSetup(TestConfiguration.class).build();
        when(preservationAgeService.findAll(any(ServiceErrors.class))).thenReturn(preservationAgeDtoList);
        mockMvc.perform(get(preservationAgeUrl)).andExpect(status().isNotFound());

    }
}
