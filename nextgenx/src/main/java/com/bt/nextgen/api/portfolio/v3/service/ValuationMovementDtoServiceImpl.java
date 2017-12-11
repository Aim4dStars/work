package com.bt.nextgen.api.portfolio.v3.service;

import com.bt.nextgen.api.portfolio.v3.model.DateRangeAccountKey;
import com.bt.nextgen.api.portfolio.v3.model.movement.GrowthItemDto;
import com.bt.nextgen.api.portfolio.v3.model.movement.ValuationMovementDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.portfolio.PortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.ValuationMovement;
import com.bt.nextgen.service.integration.portfolio.movement.GrowthItemType;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service("ValuationMovementDtoServiceV3")
@Transactional(value = "springJpaTransactionManager")
public class ValuationMovementDtoServiceImpl implements ValuationMovementDtoService {

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;

    @Autowired
    @Qualifier("avaloqPortfolioIntegrationService")
    private PortfolioIntegrationService portfolioIntegrationService;

    @Autowired
    private BrokerHelperService brokerHelperService;

    @Autowired
    private GrowthItemDtoService growthDtoService;

    @Override
    public ValuationMovementDto find(DateRangeAccountKey key, ServiceErrors serviceErrors) {
        AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId()));
        ValuationMovement valuationMovement = portfolioIntegrationService.loadValuationMovement(accountKey, key.getStartDate(),
                key.getEndDate(), serviceErrors);

        return toValuationMovementDto(key, valuationMovement, serviceErrors);
    }

    protected ValuationMovementDto toValuationMovementDto(DateRangeAccountKey key, ValuationMovement valuationMovement,
            ServiceErrors serviceErrors) {
        DateTime periodStartDate = null;
        DateTime periodEndDate = null;
        BigDecimal openingBalance = null;
        BigDecimal closingBalance = null;

        List<GrowthItemDto> growthItems = new ArrayList<>();

        if (valuationMovement != null) {
            periodStartDate = valuationMovement.getPeriodStartDate();
            periodEndDate = valuationMovement.getPeriodEndDate();
            openingBalance = valuationMovement.getOpeningBalance();
            closingBalance = valuationMovement.getClosingBalance();

            growthItems.addAll(growthDtoService.loadGrowthItems(
                    AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId())), valuationMovement.getGrowthItems(),
                    serviceErrors));
        }
        AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId()));
        WrapAccount account = accountIntegrationService.loadWrapAccountWithoutContainers(accountKey, serviceErrors);
        if (brokerHelperService.isDirectInvestor(account, serviceErrors)) {
            for (Iterator<GrowthItemDto> iterator = growthItems.iterator(); iterator.hasNext();) {
                GrowthItemDto dto = iterator.next();
                if (GrowthItemType.EXPENSE.getCode().equals(dto.getCode())) {
                    removeAdviceExpense(dto.getGrowthItems());
                    removeSmsfFees(dto.getGrowthItems());
                }
            }
        }

        return new ValuationMovementDto(key, periodStartDate, periodEndDate, openingBalance, closingBalance, growthItems);
    }

    private void removeAdviceExpense(List<GrowthItemDto> children) {
        for (Iterator<GrowthItemDto> iterator = children.iterator(); iterator.hasNext();) {
            if (GrowthItemType.ADV_FEE.getCode().equals(iterator.next().getCode())) {
                iterator.remove();
            }
        }
    }

    /**
     * @param expenseItems
     *            Remove SMSF Fees if not received from Avalog for the account
     */
    private void removeSmsfFees(List<GrowthItemDto> expenseItems) {
        for (GrowthItemDto expenseItem : expenseItems) {
            if (GrowthItemType.SMSF_FEES.getCode().equals(expenseItem.getCode()) && expenseItem.getBalance() == null) {
                expenseItems.remove(expenseItem);
            }
        }
    }
}
