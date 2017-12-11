package com.bt.nextgen.draftaccount.repository;

import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.config.JsonObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ClientApplicationTest {

    private ClientApplication clientApplication = new ClientApplication();

    @Configuration
    static class EmptyConfig {
        //empty configuration
    }

    @Before
    public void setup() {
        //setup Spring context with 'jsonObjectMapper'
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        Mockito.when(applicationContext.getBean("jsonObjectMapper")).thenReturn(new JsonObjectMapper());
        clientApplication.setApplicationContext(applicationContext);
    }

    static final String TEST_FORM_JSON =
            "{" +
                    "\"a_string\":\"Some String\"," +
                    "\"a_boolean\":true," +
                    "\"an_integer\":9," +
                    "\"an_object\":{" +
                        "\"nested_attribute\":\"is possible\"" +
                    "}," +
                    "\"a_list\":[1,2,3]" +
            "}";

    @Test
    public void shouldReturnFormDataJsonAsClientApplicationForm() throws Exception {
        Map formData = new HashMap();
        formData.put("accountType", "individual");
        clientApplication.setFormData(formData);
        IClientApplicationForm clientApplicationForm = clientApplication.getClientApplicationForm();
        assertEquals(IClientApplicationForm.AccountType.INDIVIDUAL, clientApplicationForm.getAccountType());
    }

    @Test
    public void shouldSetFormDataMapAsJson() throws Exception {
        Map<String, Object> formDataMap = new LinkedHashMap<>();
        formDataMap.put("a_string", "Some String");
        formDataMap.put("a_boolean", Boolean.TRUE);
        formDataMap.put("an_integer", Integer.valueOf(9));
        Map<String, Object> object = new HashMap<>();
        object.put("nested_attribute", "is possible");
        formDataMap.put("an_object", object);
        formDataMap.put("a_list", Arrays.asList(1,2,3));

        clientApplication.setFormData(formDataMap);
        assertEquals(TEST_FORM_JSON, clientApplication.getFormData());

    }
}
