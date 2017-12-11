package com.bt.nextgen.corporateaction.service;

import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListDtoKey;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListResult;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionApprovalListDtoServiceImpl;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionConverter;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionServices;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionGroup;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionType;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionApprovalListDtoServiceImplTest {
	@InjectMocks
	private CorporateActionApprovalListDtoServiceImpl trusteeCorporateActionListDtoServiceImpl;

	@Mock
	private CorporateActionConverter converter;

	@Mock
	private CorporateActionServices corporateActionServices;

	private ServiceErrors serviceErrors;

	@Before
	public void setup() {
		serviceErrors = new ServiceErrorsImpl();

		when(converter.toCorporateActionApprovalListDto(any(CorporateActionGroup.class), any(CorporateActionListResult.class), any(ServiceErrors.class))).
				thenReturn(null);

		when(corporateActionServices.loadVoluntaryCorporateActionsForApproval(
				any(org.joda.time.DateTime.class),
				any(org.joda.time.DateTime.class),
				any(ServiceErrors.class))).thenReturn(null);
	}

	@Test
	public void testFind_whenNullDates()
	{
		CorporateActionListDtoKey corporateActionListDtoKey = new CorporateActionListDtoKey(
				null,
				null,
				CorporateActionType.SHARE_PURCHASE_PLAN.getCode(),
				null,
				null);

		CorporateActionListDto caListDto = trusteeCorporateActionListDtoServiceImpl.find(corporateActionListDtoKey, serviceErrors);

		Assert.assertNull(caListDto);
	}

	@Test
	public void testFind_whenNotNullDates()
	{
		CorporateActionListDtoKey corporateActionListDtoKey = new CorporateActionListDtoKey(
				new DateTime().toString(),
				new DateTime().toString(),
				CorporateActionType.SHARE_PURCHASE_PLAN.getCode(),
				null,
				null);

		CorporateActionListDto caListDto = trusteeCorporateActionListDtoServiceImpl.find(corporateActionListDtoKey, serviceErrors);

		Assert.assertNull(caListDto);
	}
}
