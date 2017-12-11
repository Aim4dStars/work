package com.bt.nextgen.api.ips.service;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.ips.model.InvestmentPolicyStatementDto;
import com.bt.nextgen.api.ips.model.InvestmentPolicyStatementKey;
import com.bt.nextgen.api.ips.model.IpsFeeDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.fees.FeesType;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementIntegrationService;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementInterface;
import com.bt.nextgen.service.integration.ips.IpsFee;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.ips.IpsTariff;
import com.bt.nextgen.service.integration.ips.IpsTariffBoundary;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioSummary;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioSummaryIntegrationService;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Investment policy statement dto service implementation
 */
@Service
public class InvestmentPolicyStatementDtoServiceImpl implements InvestmentPolicyStatementDtoService {
    @Autowired
    private InvestmentPolicyStatementIntegrationService ipsIntegrationService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private ModelPortfolioSummaryIntegrationService modelPortfolioSummaryService;

    @Override
    public List<InvestmentPolicyStatementDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        Map<IpsKey, InvestmentPolicyStatementInterface> ipsMap = ipsIntegrationService
                .getInvestmentPolicyStatements(serviceErrors);
        List<InvestmentPolicyStatementDto> ipsList = new ArrayList<>(ipsMap.size());

        String imOeId = userProfileService.getPositionId();
        for (InvestmentPolicyStatementInterface ips : ipsMap.values()) {
            // Only return IPS that the IM has access to
            if (imOeId.equals(ips.getInvestmentManagerPersonId())) {
                ipsList.add(new InvestmentPolicyStatementDto(ips.getIpsKey().getId(), ips.getInvestmentName(), ips.getCode(), ips
                        .getApirCode()));
            }
        }

        // Need to append results with any TMP model(s).
        if (userProfileService.isDealerGroup()) {
            ipsList.addAll(getTailoredPortfolioModelIps(ipsMap, serviceErrors));
        }

        return ipsList;
    }

    private List<InvestmentPolicyStatementDto> getTailoredPortfolioModelIps(
            Map<IpsKey, InvestmentPolicyStatementInterface> ipsMap, ServiceErrors serviceErrors) {

        Broker broker = userProfileService.getInvestmentManager(serviceErrors);
        List<ModelPortfolioSummary> results = modelPortfolioSummaryService.loadModels(broker.getKey(), serviceErrors);

        List<InvestmentPolicyStatementDto> ipsList = new ArrayList<>(ipsMap.size());
        for (ModelPortfolioSummary s : results) {
            InvestmentPolicyStatementInterface ips = ipsMap.get(s.getModelKey());
            ipsList.add(new InvestmentPolicyStatementDto(ips.getIpsKey().getId(), ips.getInvestmentName(), ips.getCode(), ips
                    .getApirCode()));
        }
        return ipsList;
    }

    @Override
    public InvestmentPolicyStatementDto find(InvestmentPolicyStatementKey key, ServiceErrors serviceErrors) {

        IpsKey ipsKey = IpsKey.valueOf(key.getIpsId());
        InvestmentPolicyStatementInterface ips = ipsIntegrationService.getSelectiveInvestmentPolicyStatements(
                Collections.singletonList(ipsKey), serviceErrors).get(ipsKey);

        return new InvestmentPolicyStatementDto(key, ips.getInvestmentName(), ips.getCode(), ips.getApirCode(),
                ips.getFeeList() == null || ips.getFeeList().isEmpty() ? Collections.emptyList() : toIpsFeeList(ips.getFeeList()));

    }

    private List<IpsFeeDto> toIpsFeeList(List<IpsFee> feeList) {

        List<IpsFee> ipsPortfMgmtFeeList = Lambda.select(feeList,
                Lambda.having(Lambda.on(IpsFee.class).getBookKind(), Matchers.equalTo(FeesType.PORTFOLIO_MANAGEMENT_FEE)));

        if (!ipsPortfMgmtFeeList.isEmpty() && !ipsPortfMgmtFeeList.get(0).getTariffList().isEmpty()) {
            IpsTariff tariff = ipsPortfMgmtFeeList.get(0).getTariffList().get(0);
            if (tariff.getTariffBndList() != null && !tariff.getTariffBndList().isEmpty()) {
                List<IpsTariffBoundary> tariffBndList = tariff.getTariffBndList();
                return Lambda.convert(tariffBndList, new Converter<IpsTariffBoundary, IpsFeeDto>() {
                    @Override
                    public IpsFeeDto convert(IpsTariffBoundary tariffBnd) {

                        return IpsFeeConverter.toTieredFeeDto(tariffBnd);

                    }
                });

            } else {

                return Collections.singletonList(new IpsFeeDto(tariff.getBoundFrom(), tariff.getBoundTo(), tariff
                        .getTariffFactor().multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP)));
            }
        }
        return Collections.emptyList();

    }
}
