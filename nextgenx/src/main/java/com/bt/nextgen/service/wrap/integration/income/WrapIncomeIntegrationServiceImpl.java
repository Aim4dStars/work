package com.bt.nextgen.service.wrap.integration.income;


import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.income.SubAccountIncomeDetails;
import com.btfin.panorama.wrap.model.Income;
import com.btfin.panorama.wrap.service.InvestmentIncomeService;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Retrieve various investment income from wrap
 * Created by L067221 on 1/08/2017.
 */
@Service
@Profile({"WrapOffThreadImplementation"})
public class WrapIncomeIntegrationServiceImpl implements WrapIncomeIntegrationService {
    private static final String INCOME_RECEIVED_TYPE = "Cash Receipt";
    @Autowired
    @Qualifier("InvestmentIncomeServiceRestClient")
    private InvestmentIncomeService investmentIncomeService;

    @Autowired
    private WrapIncomeConverter incomeConverter;

    @Override
    public List<SubAccountIncomeDetails> loadIncomeReceivedDetails(String clientId, DateTime startDate,
                                                                   DateTime endDate, ServiceErrors serviceErrors) {

        List<Income> incomes = investmentIncomeService.getInvestmentIncome(clientId, startDate.toDate(),
                endDate.toDate(), INCOME_RECEIVED_TYPE, serviceErrors);

        final List<SubAccountIncomeDetails> incomeDetailsList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(incomes)) {
            incomeDetailsList.addAll(incomeConverter.convert(incomes, serviceErrors));
        }

        return incomeDetailsList;
    }
}