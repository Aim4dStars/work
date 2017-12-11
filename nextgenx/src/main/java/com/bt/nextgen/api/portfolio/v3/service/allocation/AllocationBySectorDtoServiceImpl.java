package com.bt.nextgen.api.portfolio.v3.service.allocation;

import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.AggregatedAllocationBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.AllocationBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.KeyedAllocBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.DatedValuationKey;
import com.bt.nextgen.api.portfolio.v3.model.valuation.ParameterisedDatedValuationKey;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.portfolio.PortfolioIntegrationServiceFactory;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

@Service("AllocationBySectorDtoServiceV3")
@Transactional(value = "springJpaTransactionManager")
public class AllocationBySectorDtoServiceImpl implements AllocationBySectorDtoService {
    @Autowired
    private PortfolioIntegrationServiceFactory portfolioIntegrationServiceFactory;

    @Autowired
    public SectorAggregator sectorAggregator;

    @Override
    @Transactional(value = "springJpaTransactionManager", readOnly = true)
    public KeyedAllocBySectorDto find(DatedValuationKey key, ServiceErrors serviceErrors) {
        // Retrieve valuations
        AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId()));

        String accountServiceType = "";

        if (key instanceof ParameterisedDatedValuationKey) {
            Map<String, String> parameters = ((ParameterisedDatedValuationKey) key).getParameters();
            accountServiceType = parameters.get("serviceType");
        }

        WrapAccountValuation valuation = portfolioIntegrationServiceFactory.getInstance(accountServiceType)
                .loadWrapAccountValuation(accountKey, key.getEffectiveDate(), key.getIncludeExternal(), serviceErrors);

        KeyedAllocBySectorDto emptyResult = new KeyedAllocBySectorDto(null, new ArrayList<AllocationBySectorDto>(), key);
        if (valuation == null || valuation.getSubAccountValuations().isEmpty())
            return emptyResult;

        BigDecimal totalPortfBal = valuation.getBalance();

        AggregatedAllocationBySectorDto totalAllocation = sectorAggregator.aggregateAllocations(accountKey,
                valuation.getSubAccountValuations(), totalPortfBal);

        KeyedAllocBySectorDto resultDto = new KeyedAllocBySectorDto(totalAllocation, key, valuation.getHasExternal());
        return resultDto;
    }
}
