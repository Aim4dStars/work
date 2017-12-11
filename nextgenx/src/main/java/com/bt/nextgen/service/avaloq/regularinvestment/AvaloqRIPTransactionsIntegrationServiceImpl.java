package com.bt.nextgen.service.avaloq.regularinvestment;

import com.btfin.panorama.core.validation.Validator;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.Template;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.regularinvestment.RIPTransactionsIntegrationService;
import com.bt.nextgen.service.integration.regularinvestment.RegularInvestmentTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("avaloqRIPTransactionsIntegrationService")
public class AvaloqRIPTransactionsIntegrationServiceImpl extends AbstractAvaloqIntegrationService implements
        RIPTransactionsIntegrationService {

    @Autowired
    private Validator validator;

    @Autowired
    private AvaloqExecute avaloqExecute;

    @Override
    public List<RegularInvestmentTransaction> loadRegularInvestments(final AccountKey accountKey, final ServiceErrors serviceErrors) {
        final List<RegularInvestmentTransaction> regularInvestments = new ArrayList<>();
        new IntegrationOperation("loadRegularInvestments", serviceErrors) {
            @Override
            public void performOperation() {
                RegularInvestmentResponseImpl response = avaloqExecute.executeReportRequestToDomain(new AvaloqReportRequest(
                        Template.REGULAR_INVESTMENTS_ACCOUNT.getName()).forAccountListId(accountKey.getId()),
                        RegularInvestmentResponseImpl.class, serviceErrors);

                if (response != null && response.getRegularInvestments() != null) {
                    regularInvestments.addAll(response.getRegularInvestments());
                    validator.validate(regularInvestments, serviceErrors);
                }

            }

        }.run();

        return regularInvestments;
    }

}
