package com.bt.nextgen.api.account.v1.service;

import com.bt.nextgen.api.account.v1.model.DateRangeAccountKey;
import com.bt.nextgen.api.account.v1.model.GrowthItemDto;
import com.bt.nextgen.api.account.v1.model.ValuationMovementDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.portfolio.PortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.ValuationMovement;
import com.bt.nextgen.service.integration.portfolio.movement.GrowthItem;
import com.bt.nextgen.service.integration.portfolio.movement.GrowthItemType;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @deprecated Use V2
 */
@Deprecated
@Service("ValuationMovementDtoServiceV1")
@Transactional(value = "springJpaTransactionManager")
public class ValuationMovementDtoServiceImpl implements ValuationMovementDtoService
{
	@Autowired
	@Qualifier("avaloqPortfolioIntegrationService")
    private PortfolioIntegrationService portfolioIntegrationService;

	@Override
	public ValuationMovementDto find(DateRangeAccountKey key, ServiceErrors serviceErrors)
	{
		AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId()));
        ValuationMovement valuationMovement = portfolioIntegrationService.loadValuationMovement(accountKey,
			key.getStartDate(),
			key.getEndDate(),
			serviceErrors);

		return toValuationMovementDto(key, valuationMovement);
	}

	protected ValuationMovementDto toValuationMovementDto(DateRangeAccountKey key, ValuationMovement valuationMovement)
	{
		DateTime periodStartDate = null;
		DateTime periodEndDate = null;
		BigDecimal openingBalance = null;
		BigDecimal closingBalance = null;

		List <GrowthItemDto> growthItems = new ArrayList <>();

		if (valuationMovement != null)
		{
			periodStartDate = valuationMovement.getPeriodStartDate();
			periodEndDate = valuationMovement.getPeriodEndDate();
			openingBalance = valuationMovement.getOpeningBalance();
			closingBalance = valuationMovement.getClosingBalance();
			for (GrowthItem growthItem : valuationMovement.getGrowthItems())
			{
				GrowthItemDto growthItemDto = getGrowthItem(growthItem);
				growthItems.add(growthItemDto);
			}
		}

		return new ValuationMovementDto(key, periodStartDate, periodEndDate, openingBalance, closingBalance, growthItems);
	}

	private GrowthItemDto getGrowthItem(GrowthItem growthItem)
	{
		GrowthItemDto growthItemDto = new GrowthItemDto(growthItem.getBalance(),
			growthItem.getCode(),
			growthItem.getDisplayName(),
			new ArrayList <GrowthItemDto>());

		for (GrowthItem children : growthItem.getGrowthItems())
		{
			if (!isTaxOrExpense(children.getCode())
                    || (isTaxOrExpense(children.getCode()) && children.getBalance().compareTo(BigDecimal.ZERO) != 0))
			{
				growthItemDto.getGrowthItems().add(getGrowthItem(children));
			}

		}

		return growthItemDto;
	}

    private boolean isTaxOrExpense(String code) {
        GrowthItemType[] expenses = { GrowthItemType.ESTAB_FEE, GrowthItemType.LICENSEE_FEE, GrowthItemType.ONE_OFF_ADV_FEE,
                GrowthItemType.ONG_ADV_FEE, GrowthItemType.TFN_WTAX, GrowthItemType.FRN_WTAX, GrowthItemType.STAMP_DUTY, };

        GrowthItemType type = GrowthItemType.forCode(code);

        for (GrowthItemType expense : expenses) {
            if (type == expense)
                return true;
        }
        return false;
    }
}
