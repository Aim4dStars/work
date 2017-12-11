package com.bt.nextgen.api.corporateaction.v1.controller;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionBaseDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDetailsBaseDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDtoKey;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListDtoKey;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionNotificationDto;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionDetailsDtoService;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionListDtoService;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionListWithMetadataDtoService;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionNotificationDtoService;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.ResultListDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionApiControllerTest {
	@InjectMocks
	private CorporateActionApiController corporateActionApiController;

	@Mock
	private CorporateActionListDtoService corporateActionListDtoService;

	@Mock
	private CorporateActionListWithMetadataDtoService corporateActionListWithMetadataDtoService;

	@Mock
	private CorporateActionDetailsDtoService corporateActionDetailsDtoService;

	@Mock
	private CorporateActionNotificationDtoService corporateActionNotificationDtoService;

	@Before
	public void setup() {
		List<CorporateActionBaseDto> corporateActionDtos = new ArrayList<>();
		List<CorporateActionNotificationDto> notificationDtos = new ArrayList<>();

		CorporateActionListDto corporateActionListDto = new CorporateActionListDto(Boolean.FALSE, null);
		CorporateActionDetailsDto corporateActionDetailsDto = new CorporateActionDetailsDto();

		when(corporateActionListWithMetadataDtoService.find(any(CorporateActionListDtoKey.class), any(ServiceErrors.class)))
				.thenReturn(corporateActionListDto);
		when(corporateActionListDtoService.search(anyListOf(ApiSearchCriteria.class), any(ServiceErrors.class))).thenReturn(
				corporateActionDtos);
		when(corporateActionDetailsDtoService.find(any(CorporateActionDtoKey.class), any(ServiceErrors.class)))
				.thenReturn(corporateActionDetailsDto);
		when(corporateActionNotificationDtoService.search(anyListOf(ApiSearchCriteria.class), any(ServiceErrors.class)))
				.thenReturn(notificationDtos);
	}

	/**
	 * Test retrieval of corporate action DTOs
	 * <p/>
	 * Note that this is not a very useful test as the API controller implementation is very basic.  The core test is CorporateActionDtoServiceImpl,
	 * which is already covered by CorporateActionDtoServiceImplTest
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetCorporateActions() throws Exception {
		ApiResponse response = corporateActionApiController.getCorporateActions("", "", "", "", "", "false");

		assertNotNull(response.getData());
		assertTrue(response.getData() instanceof ResultListDto);

		response = corporateActionApiController.getCorporateActions("", "", "", "", "", "true");
		assertNotNull(response.getData());
		assertTrue(response.getData() instanceof CorporateActionListDto);
	}

	@Test
	public void testGetRoaCorporateActions() {
		ApiResponse response = corporateActionApiController.getRoaCorporateActions("", "");
		assertNotNull(response.getData());
	}

	@Test
	public void testGetCorporateActionDetails() throws Exception {
		CorporateActionDetailsBaseDto cadDto = new CorporateActionDetailsBaseDto();
		Mockito.when(corporateActionDetailsDtoService.find(Mockito.any(CorporateActionDtoKey.class), Mockito.any(FailFastErrorsImpl.class)))
				.thenReturn(cadDto);

		ApiResponse response = corporateActionApiController.getCorporateActionDetails("B8D8D9E0016FC0B35E02BDDE397BB19E9FC9867609F745AF",
				"B8D8D9E0016FC0B35E02BDDE397BB19E9FC9867609F745AF", "", "false");

		Assert.assertNotNull(response.getData());

		response = corporateActionApiController
				.getCorporateActionDetails("B8D8D9E0016FC0B35E02BDDE397BB19E9FC9867609F745AF", null, "", "false");

		Assert.assertNotNull(response.getData());
	}

	@Test
	public void testGetCorporateEventsNotificationCount() throws Exception {
		ApiResponse response = corporateActionApiController.getCorporateEventsNotificationCount("", "");

		assertNotNull(response.getData());
	}
}
