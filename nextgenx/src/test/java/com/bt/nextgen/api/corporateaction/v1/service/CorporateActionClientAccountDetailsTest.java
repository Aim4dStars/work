package com.bt.nextgen.api.corporateaction.v1.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionClientAccountDetailsTest {

	private CorporateActionClientAccountDetails corporateActionClientAccountDetails;

	@Before
	public void setup() {
		corporateActionClientAccountDetails = new CorporateActionClientAccountDetails(null, null, null);
	}

	@Test
	public void testCorporateActionClientAccountDetails() {
		corporateActionClientAccountDetails = new CorporateActionClientAccountDetails();
		Assert.assertNull(corporateActionClientAccountDetails.getAccountBalancesMap());
		Assert.assertNull(corporateActionClientAccountDetails.getClientsMap());
		Assert.assertNull(corporateActionClientAccountDetails.getAccountsMap());
	}
}
