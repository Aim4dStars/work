package com.bt.nextgen.corporateaction.service;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionBaseDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListResult;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionDto;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionConverter;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionDirectAccountService;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionListDtoServiceImpl;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionServices;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionGroup;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionType;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionListDtoServiceImplTest {
	@InjectMocks
	private CorporateActionListDtoServiceImpl corporateActionListDtoServiceImpl;

	@Mock
	private CorporateActionConverter converter;

	@Mock
	private UserProfileService userProfileService;

	@Mock
	private CorporateActionDirectAccountService corporateActionDirectAccountService;

	@Mock
	private CorporateActionServices corporateActionServices;

	@Mock
	private CorporateActionListDto adviserCorporateActionListDto;

	@Mock
	private CorporateActionListDto imDgCorporateActionListDto;

	@Mock
	private ApiSearchCriteria startDateSearchCriteria;

	@Mock
	private ApiSearchCriteria endDateSearchCriteria;

	@Mock
	private ApiSearchCriteria caTypeSearchCriteria;

	@Mock
	private ApiSearchCriteria accountIdSearchCriteria;

	@Mock
	private ApiSearchCriteria portfolioModelSearchCriteria;

	@Before
	public void setup() {
		when(startDateSearchCriteria.getProperty()).thenReturn(Attribute.START_DATE);
		when(startDateSearchCriteria.getValue()).thenReturn("2012-12-01");

		when(endDateSearchCriteria.getProperty()).thenReturn(Attribute.END_DATE);
		when(endDateSearchCriteria.getValue()).thenReturn("2012-12-20");

		when(caTypeSearchCriteria.getProperty()).thenReturn(Attribute.CA_TYPE);
		when(caTypeSearchCriteria.getValue()).thenReturn(CorporateActionType.MULTI_BLOCK.getCode());

		when(accountIdSearchCriteria.getProperty()).thenReturn(Attribute.ACCOUNT_ID);
		when(accountIdSearchCriteria.getValue()).thenReturn("978713637E8D218BAFED132FFD8A632F");

		when(portfolioModelSearchCriteria.getProperty()).thenReturn(Attribute.PORTFOLIO_MODEL);
		when(portfolioModelSearchCriteria.getValue()).thenReturn(null);

		CorporateActionBaseDto corporateActionDto = mock(CorporateActionDto.class);
		CorporateActionBaseDto imCorporateActionDto = mock(ImCorporateActionDto.class);

		when(adviserCorporateActionListDto.getCorporateActions()).thenReturn(Arrays.asList(corporateActionDto));
		when(imDgCorporateActionListDto.getCorporateActions()).thenReturn(Arrays.asList(imCorporateActionDto));

		when(converter.toCorporateActionListDto(any(CorporateActionGroup.class), any(CorporateActionListResult.class), anyString(), any(
				ServiceErrors.class))).thenReturn(adviserCorporateActionListDto);

		when(converter.toCorporateActionListDtoForIm(any(CorporateActionGroup.class), any(CorporateActionListResult.class), anyString(),
				any(ServiceErrors.class))).thenReturn(imDgCorporateActionListDto);
	}

	@Test
	public void testSearch_whenIsAdviserSearch_thenReturnObjectOfTypeCorporateActionDto() {
		when(userProfileService.isDealerGroup()).thenReturn(Boolean.FALSE);
		when(userProfileService.isInvestmentManager()).thenReturn(Boolean.FALSE);
		when(userProfileService.isInvestor()).thenReturn(Boolean.FALSE);
        when(userProfileService.isPortfolioManager()).thenReturn(Boolean.FALSE);

		List<CorporateActionBaseDto> corporateActionListDto = corporateActionListDtoServiceImpl.search(null, null);

		assertNotNull(corporateActionListDto);
		assertFalse(corporateActionListDto.isEmpty());
		assertTrue(corporateActionListDto.get(0) instanceof CorporateActionDto);
	}

	@Test
	public void testSearch_whenIsAdviserSearchAndHasSearchCriteria_thenMethodCallShouldNotCrashAndShouldReturnObjectOfTypeCorporateActionDto() {
		when(userProfileService.isDealerGroup()).thenReturn(Boolean.FALSE);
		when(userProfileService.isInvestmentManager()).thenReturn(Boolean.FALSE);
		when(userProfileService.isInvestor()).thenReturn(Boolean.FALSE);
        when(userProfileService.isPortfolioManager()).thenReturn(Boolean.FALSE);

		List<CorporateActionBaseDto> corporateActionListDto = corporateActionListDtoServiceImpl
				.search(Arrays.asList(startDateSearchCriteria, endDateSearchCriteria, caTypeSearchCriteria, accountIdSearchCriteria,
						portfolioModelSearchCriteria), null);

		assertNotNull(corporateActionListDto);
		assertFalse(corporateActionListDto.isEmpty());
	}

	@Test
	public void testSearch_whenIsInvestorSearch_thenReturnObjectOfTypeCorporateActionDto() {
		when(userProfileService.isDealerGroup()).thenReturn(Boolean.FALSE);
		when(userProfileService.isInvestmentManager()).thenReturn(Boolean.FALSE);
		when(userProfileService.isInvestor()).thenReturn(Boolean.TRUE);
        when(userProfileService.isPortfolioManager()).thenReturn(Boolean.FALSE);

		List<CorporateActionBaseDto> corporateActionListDto = corporateActionListDtoServiceImpl.search(null, null);

		assertNotNull(corporateActionListDto);
		assertFalse(corporateActionListDto.isEmpty());
		assertTrue(corporateActionListDto.get(0) instanceof CorporateActionDto);
	}

	@Test
	public void testSearch_whenIsInvestmentManagerSearch_thenReturnObjectOfTypeImCorporateActionDto() {
		when(userProfileService.isDealerGroup()).thenReturn(Boolean.FALSE);
		when(userProfileService.isInvestmentManager()).thenReturn(Boolean.TRUE);
		when(userProfileService.isInvestor()).thenReturn(Boolean.FALSE);
        when(userProfileService.isPortfolioManager()).thenReturn(Boolean.FALSE);

		List<CorporateActionBaseDto> corporateActionListDto = corporateActionListDtoServiceImpl.search(null, null);

		assertNotNull(corporateActionListDto);
		assertFalse(corporateActionListDto.isEmpty());
		assertTrue(corporateActionListDto.get(0) instanceof ImCorporateActionDto);
	}

	@Test
	public void testSearch_whenIsDealerGroupSearch_thenReturnObjectOfTypeImCorporateActionDto() {
		when(userProfileService.isDealerGroup()).thenReturn(Boolean.TRUE);
		when(userProfileService.isInvestmentManager()).thenReturn(Boolean.FALSE);
		when(userProfileService.isInvestor()).thenReturn(Boolean.FALSE);
        when(userProfileService.isPortfolioManager()).thenReturn(Boolean.FALSE);

		List<CorporateActionBaseDto> corporateActionListDto = corporateActionListDtoServiceImpl.search(null, null);

		assertNotNull(corporateActionListDto);
		assertFalse(corporateActionListDto.isEmpty());
		assertTrue(corporateActionListDto.get(0) instanceof ImCorporateActionDto);
	}

    @Test
    public void testSearch_whenIsPortfolioManagerSearch_thenReturnObjectOfTypeImCorporateActionDto() {
        when(userProfileService.isDealerGroup()).thenReturn(Boolean.FALSE);
        when(userProfileService.isInvestmentManager()).thenReturn(Boolean.FALSE);
        when(userProfileService.isInvestor()).thenReturn(Boolean.FALSE);
        when(userProfileService.isPortfolioManager()).thenReturn(Boolean.TRUE);

        List<CorporateActionBaseDto> corporateActionListDto = corporateActionListDtoServiceImpl.search(null, null);

        assertNotNull(corporateActionListDto);
        assertFalse(corporateActionListDto.isEmpty());
        assertTrue(corporateActionListDto.get(0) instanceof ImCorporateActionDto);
    }
}
