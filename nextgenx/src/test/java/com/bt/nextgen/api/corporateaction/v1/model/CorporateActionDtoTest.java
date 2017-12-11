package com.bt.nextgen.api.corporateaction.v1.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionDtoTest {
	private CorporateActionDto corporateActionDto;
	private ObjectMapper mapper;

	@Before
	public void setup() throws Exception {
		corporateActionDto = new CorporateActionDto();
		mapper = new ObjectMapper();
	}

	@Test
	public void testToAssetDto_assetsEmpty() {
	}
}
