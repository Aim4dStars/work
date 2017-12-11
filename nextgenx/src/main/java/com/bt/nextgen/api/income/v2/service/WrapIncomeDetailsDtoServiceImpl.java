package com.bt.nextgen.api.income.v2.service;

import com.bt.nextgen.api.income.v2.model.AbstractIncomeDto;
import com.bt.nextgen.api.income.v2.model.IncomeDetailsKey;
import com.bt.nextgen.api.income.v2.model.IncomeDetailsType;
import com.bt.nextgen.api.income.v2.model.IncomeDto;
import com.bt.nextgen.api.income.v2.model.IncomeValuesDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.income.IncomeIntegrationService;
import com.bt.nextgen.service.integration.income.IncomeIntegrationServiceFactory;
import com.bt.nextgen.service.integration.income.IncomeType;
import com.bt.nextgen.service.integration.income.SubAccountIncomeDetails;
import com.bt.nextgen.service.integration.income.WrapAccountIncomeDetails;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by L067221 on 23/08/2017.
 */

@Service("IncomeDetailsDtoServiceV2")
@Profile({"WrapOffThreadImplementation"})
@Transactional(value = "springJpaTransactionManager")
public class WrapIncomeDetailsDtoServiceImpl implements IncomeDetailsDtoService {

    @Autowired
    private IncomeIntegrationService incomeIntegrationService;

    @Autowired
    private IncomeSubAccountIncomeAggregator incomeSubAccountIncomeAggregator;

    @Autowired
    private IncomeValueDtoBuilder incomeValueDtoBuilder;

    @Autowired
    private IncomeIntegrationServiceFactory incomeIntegrationServiceFactory;

    @Override
    public IncomeValuesDto find(IncomeDetailsKey key, ServiceErrors serviceErrors) {

        final AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId()));
        List<WrapAccountIncomeDetails> accountIncomes;

        if (IncomeDetailsType.RECEIVED.equals(key.getType())) {
            accountIncomes = incomeIntegrationServiceFactory.loadIncomeReceivedDetails(accountKey,
                    key.getStartDate(), key.getEndDate(), serviceErrors);
        } else {
            accountIncomes = incomeIntegrationService.loadIncomeAccruedDetails(accountKey,
                    key.getEndDate(), serviceErrors);
        }

        final List<IncomeDto> investmentTypes = buildInvestmentTypes(accountIncomes, accountKey, serviceErrors);
        return new IncomeValuesDto(key, investmentTypes);
    }

    private List<IncomeDto> buildInvestmentTypes(List<WrapAccountIncomeDetails> accountIncomes, AccountKey accountKey,
                                                 ServiceErrors serviceErrors) {

        if (accountIncomes != null && !accountIncomes.isEmpty()
                && accountIncomes.get(0).getSubAccountIncomeDetailsList() != null) {
            final List<SubAccountIncomeDetails> incomes = accountIncomes.get(0).getSubAccountIncomeDetailsList();

            final Map<AssetType, Map<IncomeType, List<AbstractIncomeDto>>> investmentMapIncome = incomeSubAccountIncomeAggregator
                    .buildInvestmentMapFromSubAccount(incomes, accountKey, serviceErrors);

            return incomeValueDtoBuilder.buildIncomeTypesDtoList(investmentMapIncome);
        } else {
            return new ArrayList<>();
        }
    }

}
