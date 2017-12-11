package com.bt.nextgen.api.account.v1.service;

import com.bt.nextgen.api.account.v1.model.DateRangeAccountKey;
import com.bt.nextgen.api.account.v1.model.ValuationMovementDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.portfolio.movement.GrowthItemImpl;
import com.bt.nextgen.service.avaloq.portfolio.movement.ValuationMovementImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.portfolio.PortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.movement.GrowthItem;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ValuationMovementDtoServiceTest
{
	@InjectMocks
	private ValuationMovementDtoServiceImpl valuationMovementDTOService;

	@Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private PortfolioIntegrationService portfolioIntegrationService;

	DateRangeAccountKey dateRangeKey = new DateRangeAccountKey("975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0",
		new DateTime().minusMonths(3),
		new DateTime());

	ValuationMovementImpl valuationMovement;
	ServiceErrors serviceErrors;

	@Before
	public void setup() throws Exception
	{
		List <GrowthItem> growthItems = new ArrayList <GrowthItem>();
		GrowthItemImpl parent = new GrowthItemImpl(new BigDecimal("1000.11"),
			"incPortfVal",
			"Inflows",
			new ArrayList <GrowthItem>());
		GrowthItemImpl child1 = new GrowthItemImpl(new BigDecimal("700.09"),
			"depot",
			"Deposits to BT Cash",
			new ArrayList <GrowthItem>());
		GrowthItemImpl child2 = new GrowthItemImpl(new BigDecimal("300.02"),
			"assetXferIn",
			"Asset transfers in",
			new ArrayList <GrowthItem>());
		parent.getGrowthItems().add(child1);
		parent.getGrowthItems().add(child2);
		growthItems.add(parent);

		valuationMovement = new ValuationMovementImpl();
		valuationMovement.setAccountId(dateRangeKey.getAccountId());
		valuationMovement.setPeriodStartDate(dateRangeKey.getStartDate());
		valuationMovement.setPeriodEndDate(dateRangeKey.getEndDate());
		valuationMovement.setOpeningBalance(new BigDecimal(10001.11));
		valuationMovement.setClosingBalance(new BigDecimal(50001.55));
		valuationMovement.setGrowthItems(growthItems);

        Mockito.when(portfolioIntegrationService.loadValuationMovement(Mockito.any(AccountKey.class),
			Mockito.any(DateTime.class),
			Mockito.any(DateTime.class),
			Mockito.any(ServiceErrors.class))).thenReturn(valuationMovement);
	}

	@Test
	public void testToValuationMovementDto_sizeMatches()
	{
		ValuationMovementDto valuationMovementDto = valuationMovementDTOService.toValuationMovementDto(dateRangeKey,
			valuationMovement);
		assertNotNull(valuationMovementDto);
		Assert.assertEquals(1, valuationMovementDto.getGrowthItems().size());
	}

	@Test
	public void testToValuationMovementDto_valueMatches_whenValuationMovement_passed()
	{
		ValuationMovementDto valuationMovementDto = valuationMovementDTOService.toValuationMovementDto(dateRangeKey,
			valuationMovement);
		assertNotNull(valuationMovementDto);

		Assert.assertEquals(1, valuationMovementDto.getGrowthItems().size());
		Assert.assertEquals(valuationMovement.getGrowthItems().get(0).getBalance(), valuationMovementDto.getGrowthItems()
			.get(0)
			.getBalance());
		Assert.assertEquals(valuationMovement.getGrowthItems().get(0).getCode(), valuationMovementDto.getGrowthItems()
			.get(0)
			.getCode());
		Assert.assertEquals(valuationMovement.getGrowthItems().get(0).getDisplayName(), valuationMovementDto.getGrowthItems()
			.get(0)
			.getDisplayName());

		Assert.assertEquals(2, valuationMovementDto.getGrowthItems().get(0).getGrowthItems().size());

		Assert.assertEquals(valuationMovement.getGrowthItems().get(0).getGrowthItems().get(0).getBalance(),
			valuationMovementDto.getGrowthItems().get(0).getGrowthItems().get(0).getBalance());
		Assert.assertEquals(valuationMovement.getGrowthItems().get(0).getGrowthItems().get(0).getCode(),
			valuationMovementDto.getGrowthItems().get(0).getGrowthItems().get(0).getCode());
		Assert.assertEquals(valuationMovement.getGrowthItems().get(0).getGrowthItems().get(0).getDisplayName(),
			valuationMovementDto.getGrowthItems().get(0).getGrowthItems().get(0).getDisplayName());

		Assert.assertEquals(valuationMovement.getGrowthItems().get(0).getGrowthItems().get(1).getBalance(),
			valuationMovementDto.getGrowthItems().get(0).getGrowthItems().get(1).getBalance());
		Assert.assertEquals(valuationMovement.getGrowthItems().get(0).getGrowthItems().get(1).getCode(),
			valuationMovementDto.getGrowthItems().get(0).getGrowthItems().get(1).getCode());
		Assert.assertEquals(valuationMovement.getGrowthItems().get(0).getGrowthItems().get(1).getDisplayName(),
			valuationMovementDto.getGrowthItems().get(0).getGrowthItems().get(1).getDisplayName());

		Assert.assertEquals(valuationMovement.getOpeningBalance(), valuationMovementDto.getOpeningBalance());
		Assert.assertEquals(valuationMovement.getClosingBalance(), valuationMovementDto.getClosingBalance());
		Assert.assertEquals(valuationMovement.getPeriodStartDate(), valuationMovementDto.getPeriodStartDate());
		Assert.assertEquals(valuationMovement.getPeriodEndDate(), valuationMovementDto.getPeriodEndDate());
		Assert.assertEquals(valuationMovement.getGrowthItems().size(), valuationMovementDto.getGrowthItems().size());
	}
}
