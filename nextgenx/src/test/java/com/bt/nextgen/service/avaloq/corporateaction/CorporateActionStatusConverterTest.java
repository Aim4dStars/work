package com.bt.nextgen.service.avaloq.corporateaction;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionStatusConverterTest {

	@InjectMocks
	private CorporateActionStatusConverter corporateActionStatusConverter;

	@Mock
	private StaticIntegrationService staticCodeService;

	@Before
	public void setup() {
	}

	@Test
	public void testCorporateActionStatus_whenOpenStatus_thenReturnCorporateActionStatus_OPEN() {
		CodeImpl code = new CodeImpl("codeId", "userId", "name", "open");

		Mockito.when(staticCodeService.loadCode(Mockito.any(CodeCategory.class), Mockito.anyString(), Mockito.any(ServiceErrors.class)))
				.thenReturn(code);

		CorporateActionStatus status = corporateActionStatusConverter.convert("41");

		Assert.assertNotNull(status);
		Assert.assertEquals(CorporateActionStatus.OPEN, status);
		Assert.assertEquals(status.getCode(), "OPEN");
		Assert.assertEquals(status.getId(), "open");
	}

	@Test
	public void testCorporateActionStatus_whenClosedStatus_thenReturnCorporateActionStatus_CLOSED() {
		CodeImpl code = new CodeImpl("codeId", "userId", "name", "closed");

		Mockito.when(staticCodeService.loadCode(Mockito.any(CodeCategory.class), Mockito.anyString(), Mockito.any(ServiceErrors.class)))
				.thenReturn(code);

		CorporateActionStatus status = corporateActionStatusConverter.convert("2");

		Assert.assertNotNull(status);
		Assert.assertEquals(CorporateActionStatus.CLOSED, status);
	}

	@Test
	public void testCorporateActionStatus_whenNullStatus_thenReturnNullStatus() {
		CodeImpl code = new CodeImpl("codeId", "userId", "name", null);

		Mockito.when(staticCodeService.loadCode(Mockito.any(CodeCategory.class), Mockito.anyString(), Mockito.any(ServiceErrors.class)))
				.thenReturn(code);

		CorporateActionStatus status = corporateActionStatusConverter.convert("-1");

		Assert.assertNull(status);
	}
}
