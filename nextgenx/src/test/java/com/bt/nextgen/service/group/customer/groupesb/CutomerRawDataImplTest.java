package com.bt.nextgen.service.group.customer.groupesb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;

/**
 * Created by l069679 on 14/02/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class CutomerRawDataImplTest {


    @Mock
    ObjectMapper objectMapper;

    @Test
    public void testGetJson () {
        Object obj = "Test Object";
        assertNotNull(CustomerRawDataImpl.getJson(obj));
    }

    @Test
    public void testConstructor(){
        Object obj = "Test Object";
        CustomerRawData customerRawData = null;
        try {
            customerRawData = (CustomerRawData)new CustomerRawDataImpl(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        assertNotNull(customerRawData);
    }
}
