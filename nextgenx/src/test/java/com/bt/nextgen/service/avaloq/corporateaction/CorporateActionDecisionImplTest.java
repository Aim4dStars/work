package com.bt.nextgen.service.avaloq.corporateaction;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionDecisionImplTest {

	private CorporateActionDecisionImpl decision;

	@Before
	public void setup() {
		decision = new CorporateActionDecisionImpl();
		decision.setKey("123");
		decision.setValue("456.0");
	}

	@Test
	public void test_modelGettersAndSetters()
	{
		Assert.assertNotNull(decision);
		Assert.assertEquals("123", decision.getKey());
		Assert.assertEquals("456.0", decision.getValue());
		Assert.assertEquals(true, decision.hasValue());
		BigDecimal diff = decision.getBigDecimalValue().subtract(BigDecimal.valueOf(465.0));
		Assert.assertEquals(true, diff.compareTo(BigDecimal.ONE) < 0);
		decision.setValue(null);
		Assert.assertNull(decision.getBigDecimalValue());
	}
}
