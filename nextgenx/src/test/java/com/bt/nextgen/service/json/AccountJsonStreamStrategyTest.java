package com.bt.nextgen.service.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AccountJsonStreamStrategyTest {
	
	@InjectMocks
	AccountJsonStreamStrategy accountJsonStreamStrategy;

	@Test
    public void processNameTest() {
    	String name="account_id";
    	String sample = accountJsonStreamStrategy.processName(name);
        assertEquals("accountId", sample);
        assertTrue(accountJsonStreamStrategy.isEncodingRequired());
    }

	@Test
    public void processNumberTestWithNoEncodingRequired() {
    	String number= "number";
    	accountJsonStreamStrategy.setEncodingRequired(false);
    	String sample=accountJsonStreamStrategy.processNumber(number);
    	assertEquals(number, sample); 	
    }
}
